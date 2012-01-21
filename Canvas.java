/**
 * You received this file as part of an advanced experimental
 * robotics framework prototype ('finroc')
 *
 * Copyright (C) 2012 Max Reichardt,
 *   Robotics Research Lab, University of Kaiserslautern
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.finroc.plugins.data_types;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;

import org.finroc.plugins.data_types.util.BezierSpline;
import org.finroc.plugins.data_types.util.BoundsExtractingGraphics2D;
import org.rrlib.finroc_core_utils.log.LogLevel;
import org.rrlib.finroc_core_utils.serialization.DataType;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.MemoryBuffer;

/**
 * @author max
 *
 * Java equivalent to rrlib::canvas::tCanvas
 */
public class Canvas extends MemoryBuffer implements PaintablePortData {

    public final static DataType<Canvas> TYPE = new DataType<Canvas>(Canvas.class, "Canvas2D");

    enum Opcode {
        // ####### tCanvas-supported opcodes ########

        // Transformation operations
        eSET_TRANSFORMATION,      // [(K+1)x(K+1) matrix]
        eTRANSFORM,               // [(K+1)x(K+1) matrix]
        eTRANSLATE,               // [vector]
        eROTATE,                  // [yaw]
        eSCALE,                   // [vector]
        eRESET_TRANSFORMATION,    // []

        // Canvas, Draw & encoding mode
        eSET_COLOR,               // [RGB: 3 bytes]
        eSET_EDGE_COLOR,          // [RGB: 3 bytes]
        eSET_FILL_COLOR,          // [RGB: 3 bytes]
        eSET_FILL,                // [bool]
        eSET_ALPHA,               // [1 byte]

        // Geometry primitives
        eDRAW_POINT,              // [vector]
        eDRAW_LINE,               // [vector][vector]
        eDRAW_LINE_SEGMENT,       // [vector][vector]
        eDRAW_BOX,                // [vector][size1]...[sizeN]
        eDRAW_ELLIPSOID,          // [vector][diameter1]...[diameterN]
        eDRAW_POLYGON,            // [number of values: N][vector1]...[vectorN]
        eDRAW_SPLINE,             // [number of values: N][tension-parameter][vector1]...[vectorN]  (uniform b-spline)
        eDRAW_CUBIC_BEZIER_CURVE, // [vector][vector][vector][vector]
        eDRAW_STRING,             // [vector][null-terminated chars]

        // Custom path/shape
        ePATH_START,              // [point]
        ePATH_END_OPEN,           // [point]
        ePATH_END_CLOSED,         // [point]
        ePATH_LINE,               // [point]
        ePATH_QUADRATIC_CURVE,    // [point][point]
        ePATH_CUBIC_CURVE,        // [point][point][point]

        // ####### tCanvas2D-only opcodes ########

        // Canvas, Draw & encoding mode
        eSET_Z,                   // [value]
        eSET_EXTRUSION            // [value]

        // ####### tCanvas3D-only opcodes ########
    }

    enum NumberTypeEnum {
        eFLOAT,
        eDOUBLE,
        eZEROES,
        eINT8,
        eUINT8,
        eINT16,
        eUINT16,
        eINT32,
        eUINT32,
        eINT64,
        eUINT64
    };

    /*! Bytes per value of enum above */
    static final int[] numberTypeBytes = new int[] { 4, 8, 0, 1, 1, 2, 2, 4, 4, 8, 8 };

    class RenderContext implements Comparable<RenderContext> {
        Color edgeColor, fillColor;
        boolean fill;
        double z;
        AffineTransform at = new AffineTransform();
        int bufferOffset;
        int commandCount;

        public RenderContext(AffineTransform at, Color edgeColor, Color fillColor, boolean fill, double z, long bufferOffset) {
            this.at.setTransform(at);
            this.edgeColor = edgeColor;
            this.fillColor = fillColor;
            this.fill = fill;
            this.z = z;
            this.bufferOffset = (int)bufferOffset;
            zLevels.add(this);
        }

        @Override
        public int compareTo(RenderContext o) {
            return Double.compare(this.z, o.z);
        }
    }

    /** Render contexts (one for every Z-Level) in current data */
    private ArrayList<RenderContext> zLevels = new ArrayList<RenderContext>();

    /** Temporary variables for Z-Level extraction */
    InputStreamBuffer tempInputStreamZExtraction = new InputStreamBuffer(this);
    double[] tempArrayZExtraction = new double[6];

    @Override
    public void deserialize(InputStreamBuffer rv) {
        super.deserialize(rv);
        extractZLevels();
    }

    /**
     * Extract all areas with the same Z-level and store them in zLevels
     */
    private void extractZLevels() {
        zLevels.clear();
        InputStreamBuffer is = tempInputStreamZExtraction;
        is.reset(this);
        double[] v = tempArrayZExtraction;
        AffineTransform at = new AffineTransform();
        Color edgeColor = Color.BLACK;
        Color fillColor = Color.BLACK;
        boolean fill = false;
        RenderContext current = new RenderContext(at, edgeColor, fillColor, fill, 0, 0);
        zLevels.add(current);
        while (is.moreDataAvailable()) {
            Opcode opcode = is.readEnum(Opcode.class);
            if (opcode != Opcode.ePATH_CUBIC_CURVE && opcode != Opcode.ePATH_QUADRATIC_CURVE && opcode != Opcode.ePATH_LINE) {
                current.commandCount++;
            }
            switch (opcode) {
            case eSET_TRANSFORMATION:
                at.setTransform(new AffineTransform(readValues(is, v, 6)));
                break;
            case eTRANSFORM:
                at.concatenate(new AffineTransform(readValues(is, v, 6)));
                break;
            case eTRANSLATE:      // [2D-vector]
                readValues(is, v, 2);
                at.translate(v[0], v[1]);
                break;
            case eROTATE:         // [yaw]
                readValues(is, v, 1);
                at.rotate(v[0]);
                break;
            case eSCALE:          // [2D-vector]
                readValues(is, v, 2);
                at.scale(v[0], v[1]);
                break;
            case eRESET_TRANSFORMATION:
                at.setToIdentity();
                break;

            case eSET_COLOR:         // [RGB: 3 bytes]
                edgeColor = new Color(is.readByte() & 0xFF, is.readByte() & 0xFF, is.readByte() & 0xFF, edgeColor.getAlpha());
                fillColor = edgeColor;
                break;
            case eSET_EDGE_COLOR:    // [RGB: 3 bytes]
                edgeColor = new Color(is.readByte() & 0xFF, is.readByte() & 0xFF, is.readByte() & 0xFF, edgeColor.getAlpha());
                break;
            case eSET_FILL_COLOR:    // [RGB: 3 bytes]
                fillColor = new Color(is.readByte() & 0xFF, is.readByte() & 0xFF, is.readByte() & 0xFF, fillColor.getAlpha());
                break;
            case eSET_ALPHA:
                int alpha = is.readByte() & 0xFF;
                if (alpha != edgeColor.getAlpha()) {
                    edgeColor = new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), alpha);
                }
                if (alpha != fillColor.getAlpha()) {
                    fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), alpha);
                }
                break;
            case eSET_FILL:          // [bool]
                fill = is.readBoolean();
                break;
            case eSET_Z:             // [value]
                readValues(is, v, 1);
                if (v[0] != current.z) {
                    current = new RenderContext(at, edgeColor, fillColor, fill, v[0], is.getAbsoluteReadPosition());
                }
                break;
            case eSET_EXTRUSION:
                readValues(is, v, 1);
                break;

            case eDRAW_POINT:              // [2D-vector]
            case ePATH_LINE:
                skipValues(is, 2);
                break;

            case eDRAW_LINE:               // [2D-point][2D-point]
            case eDRAW_LINE_SEGMENT:       // [2D-point][2D-point]
            case eDRAW_BOX:                // [2D-point][width][height]
            case eDRAW_ELLIPSOID:          // [2D-point][width][height]
            case ePATH_QUADRATIC_CURVE:
                skipValues(is, 4);
                break;

            case eDRAW_SPLINE:             // [number of values][2D-vector1]...[2D-vectorN]  (bezier spline)
                is.readFloat();
            case eDRAW_POLYGON:            // [number of values][2D-vector1]...[2D-vectorN]
                int points = is.readShort();
                skipValues(is, points * 2);
                break;

            case eDRAW_STRING:             // [2D-point][null-terminated chars]
                skipValues(is, 2);
                is.readString();
                break;

            case ePATH_CUBIC_CURVE:
                skipValues(is, 6);
                break;

            case eDRAW_CUBIC_BEZIER_CURVE: // [2D-point][2D-point][2D-point][2D-point]
                skipValues(is, 8);
                break;

            case ePATH_START:
                skipValues(is, 2);
                is.readBoolean();
                break;

            default:
                DataTypePlugin.logDomain.log(LogLevel.LL_WARNING, "Canvas", "Opcode " + opcode.toString() + " not supported yet");
                return;
            }
        }
        Collections.sort(zLevels);
    }

    @Override
    public void paint(Graphics2D g) {
        InputStreamBuffer is = new InputStreamBuffer(this);
        double[] v = new double[1000];
        Graphics2D g2d = (Graphics2D)g.create();
        g2d.scale(1000, 1000);
        g2d.setStroke(new BasicStroke(0.01f));
        AffineTransform defaultTransform = g2d.getTransform();
        for (RenderContext lvl : zLevels) {
            if (lvl.commandCount > 0) {
                is.reset(this);
                is.skip(lvl.bufferOffset);
                paintGeometry(g2d, lvl, is, v, defaultTransform);
            }
        }
        is.close();
    }

    public void paintGeometry(Graphics2D g, RenderContext lvl, InputStreamBuffer is, double[] v, AffineTransform defaultTransform) {
        boolean fill = lvl.fill;
        Color edgeColor = lvl.edgeColor;
        Color fillColor = lvl.fillColor;
        g.setColor(edgeColor);
        g.setTransform(defaultTransform);
        g.transform(lvl.at);

        // Shapes
        final Line2D.Double line = new Line2D.Double();
        final Ellipse2D.Double ellipse = new Ellipse2D.Double();
        final Rectangle2D.Double rect = new Rectangle2D.Double();
        final CubicCurve2D.Double curve = new CubicCurve2D.Double();

        // Helper objects for line drawing
        final Point2D.Double p1 = new Point2D.Double();
        final Point2D.Double p2 = new Point2D.Double();
        final Point2D.Double p1t = new Point2D.Double();
        final Point2D.Double p2t = new Point2D.Double();

        boolean readNextOpcode = true;
        Opcode opcode = null;
        for (int i = 0; i < lvl.commandCount; i++) {

            if (readNextOpcode) {
                opcode = is.readEnum(Opcode.class);
            } else {
                readNextOpcode = true;
            }
            switch (opcode) {
            case eSET_TRANSFORMATION:
                g.setTransform(defaultTransform);
                g.transform(new AffineTransform(readValues(is, v, 6)));
                break;
            case eTRANSFORM:
                g.transform(new AffineTransform(readValues(is, v, 6)));
                break;
            case eTRANSLATE:      // [2D-vector]
                readValues(is, v, 2);
                g.translate(v[0], v[1]);
                break;
            case eROTATE:         // [yaw]
                readValues(is, v, 1);
                g.rotate(v[0]);
                break;
            case eSCALE:          // [2D-vector]
                readValues(is, v, 2);
                g.scale(v[0], v[1]);
                break;
            case eRESET_TRANSFORMATION:
                g.setTransform(defaultTransform);
                break;

            case eSET_COLOR:         // [RGB: 3 bytes]
                edgeColor = new Color(is.readByte() & 0xFF, is.readByte() & 0xFF, is.readByte() & 0xFF, edgeColor.getAlpha());
                fillColor = edgeColor;
                g.setColor(edgeColor);
                break;
            case eSET_EDGE_COLOR:    // [RGB: 3 bytes]
                edgeColor = new Color(is.readByte() & 0xFF, is.readByte() & 0xFF, is.readByte() & 0xFF, edgeColor.getAlpha());
                g.setColor(edgeColor);
                break;
            case eSET_FILL_COLOR:    // [RGB: 3 bytes]
                fillColor = new Color(is.readByte() & 0xFF, is.readByte() & 0xFF, is.readByte() & 0xFF, fillColor.getAlpha());
                break;
            case eSET_ALPHA:
                int alpha = is.readByte() & 0xFF;
                if (alpha != edgeColor.getAlpha()) {
                    edgeColor = new Color(edgeColor.getRed(), edgeColor.getGreen(), edgeColor.getBlue(), alpha);
                }
                if (alpha != fillColor.getAlpha()) {
                    fillColor = new Color(fillColor.getRed(), fillColor.getGreen(), fillColor.getBlue(), alpha);
                }
                break;
            case eSET_FILL:          // [bool]
                fill = is.readBoolean();
                break;
            case eSET_Z:             // [value]
                readValues(is, v, 1);
                break;
            case eSET_EXTRUSION:
                readValues(is, v, 1);
                break;

            case eDRAW_POINT:              // [2D-vector]
                readValues(is, v, 2);
                line.x1 = v[0];
                line.x2 = v[0];
                line.y1 = v[1];
                line.y2 = v[1];
                g.draw(line);
                break;

            case eDRAW_LINE:              // [2D-point][2D-vector]
                readValues(is, v, 4);
                double x1 = v[0];
                double y1 = v[1];
                double vecx = v[2];
                double vecy = v[3];
                AffineTransform at = g.getTransform();
                Rectangle r = g.getClipBounds();

                if (!(g instanceof BoundsExtractingGraphics2D)) {
                    while (true) {
                        p1.x = x1 - vecx;
                        p1.y = y1 - vecy;
                        p2.x = x1 + vecx;
                        p2.y = y1 + vecy;

                        at.transform(p1, p1t);
                        at.transform(p2, p2t);

                        if (p1.x < r.getMinX() && p2.x > r.getMaxX() || p2.x < r.getMinX() && p1.x > r.getMaxX() ||
                                p1.y < r.getMinY() && p2.y > r.getMaxY() || p2.y < r.getMinY() && p1.y > r.getMaxY()) {
                            break;
                        }

                        vecx *= 8;
                        vecy *= 8;
                    }
                    line.setLine(p1, p2);
                    g.draw(line);
                }

                break;

            case eDRAW_LINE_SEGMENT:      // [2D-point][2D-point]
                readValues(is, v, 4);
                line.x1 = v[0];
                line.y1 = v[1];
                line.x2 = v[2];
                line.y2 = v[3];
                g.draw(line);
                break;

            case eDRAW_BOX:             // [2D-point][width][height]
                readValues(is, v, 4);
                rect.x = v[0];
                rect.y = v[1];
                rect.width = v[2];
                rect.height = v[3];
                g.draw(rect);
                if (fill) {
                    g.setColor(fillColor);
                    g.fill(rect);
                    g.setColor(edgeColor);
                }
                break;

            case eDRAW_ELLIPSOID:               // [2D-point][width][height]
                readValues(is, v, 4);
                ellipse.x = v[0];
                ellipse.y = v[1];
                ellipse.width = v[2];
                ellipse.height = v[3];
                g.draw(ellipse);
                if (fill) {
                    g.setColor(fillColor);
                    g.fill(ellipse);
                    g.setColor(edgeColor);
                }
                break;

            case eDRAW_POLYGON:            // [number of values][2D-vector1]...[2D-vectorN]
                int points = is.readShort();
                Path2D.Double path = new Path2D.Double(Path2D.WIND_NON_ZERO, points + 1);
                if (points * 2 > v.length) {
                    DataTypePlugin.logDomain.log(LogLevel.LL_WARNING, "Canvas", "More than " + (v.length / 2) + " points not supported");
                    return;
                }
                readValues(is, v, points * 2);
                synchronized (path) { // maybe this speeds synchronized calls up?
                    path.moveTo(v[0], v[1]);
                    for (int j = 1; j < points; j++) {
                        path.lineTo(v[j * 2], v[j * 2 + 1]);
                    }
                    path.lineTo(v[0], v[1]);
                }
                g.draw(path);
                if (fill) {
                    g.setColor(fillColor);
                    g.fill(path);
                    g.setColor(edgeColor);
                }

                break;

            case eDRAW_SPLINE:             // [number of values][2D-vector1]...[2D-vectorN]  (bezier spline)
                float tension = is.readFloat();
                points = is.readShort();
                if (points * 2 > v.length) {
                    DataTypePlugin.logDomain.log(LogLevel.LL_WARNING, "Canvas", "More than " + (v.length / 2) + " points not supported");
                    return;
                }
                readValues(is, v, points * 2);
                Point2D.Double[] splinePoints = new Point2D.Double[points];
                for (int j = 0; j < points; j++) {
                    splinePoints[j] = new Point2D.Double(v[j * 2], v[j * 2 + 1]);
                }
                BezierSpline spline = new BezierSpline(splinePoints, tension);
                g.draw(spline);
                break;

            case eDRAW_STRING:             // [2D-point][null-terminated chars]
                readValues(is, v, 2);
                String s = is.readString();
                g.drawString(s, (float)v[0], (float)v[1]);
                break;

            case eDRAW_CUBIC_BEZIER_CURVE: // [2D-point][2D-point][2D-point][2D-point]
                readValues(is, v, 8);
                curve.x1 = v[0];
                curve.y1 = v[1];
                curve.ctrlx1 = v[2];
                curve.ctrly1 = v[3];
                curve.ctrlx2 = v[4];
                curve.ctrly2 = v[5];
                curve.x2 = v[6];
                curve.y2 = v[7];
                g.draw(curve);
                break;

            case ePATH_START:
                path = new Path2D.Double();

                readValues(is, v, 2);
                boolean shape = is.readBoolean();
                double startx = v[0];
                double starty = v[1];
                path.moveTo(startx, starty);

                synchronized (path) {
                    while (is.moreDataAvailable()) {
                        opcode = is.readEnum(Opcode.class);
                        if (opcode == Opcode.ePATH_LINE) {
                            readValues(is, v, 2);
                            path.lineTo(v[0], v[1]);
                        } else if (opcode == Opcode.ePATH_QUADRATIC_CURVE) {
                            readValues(is, v, 4);
                            path.quadTo(v[0], v[1], v[2], v[3]);
                        } else if (opcode == Opcode.ePATH_CUBIC_CURVE) {
                            readValues(is, v, 6);
                            path.curveTo(v[0], v[1], v[2], v[3], v[4], v[5]);
                        } else {
                            readNextOpcode = false;
                            break;
                        }
                    }

                    if (shape) {
                        path.lineTo(startx, starty);
                    }

                    g.draw(path);
                    if (fill) {
                        g.setColor(fillColor);
                        g.fill(path);
                        g.setColor(edgeColor);
                    }
                }
                break;

            default:
                DataTypePlugin.logDomain.log(LogLevel.LL_WARNING, "Canvas", "Opcode " + opcode.toString() + " not supported yet");
                return;
            }
        }
    }

    private void skipValues(InputStreamBuffer is, int valueCount) {
        NumberTypeEnum t = is.readEnum(NumberTypeEnum.class);
        int bytes = numberTypeBytes[t.ordinal()];
        is.skip(bytes * valueCount);
    }

    private double[] readValues(InputStreamBuffer is, double[] buffer, int valueCount) {
        NumberTypeEnum t = is.readEnum(NumberTypeEnum.class);
        switch (t) {
        case eFLOAT:
            for (int i = 0; i < valueCount; i++) {
                buffer[i] = is.readFloat();
            }
            break;
        case eDOUBLE:
            for (int i = 0; i < valueCount; i++) {
                buffer[i] = is.readDouble();
            }
            break;
        case eZEROES:
            for (int i = 0; i < valueCount; i++) {
                buffer[i] = 0;
            }
            break;
        case eINT8:
            for (int i = 0; i < valueCount; i++) {
                buffer[i] = is.readByte();
            }
            break;
        case eUINT8:
            for (int i = 0; i < valueCount; i++) {
                buffer[i] = is.readByte() & 0xFF;
            }
            break;
        case eINT16:
            for (int i = 0; i < valueCount; i++) {
                buffer[i] = is.readShort();
            }
            break;
        case eUINT16:
            for (int i = 0; i < valueCount; i++) {
                buffer[i] = is.readShort() & 0xFFFF;
            }
            break;
        case eINT32:
            for (int i = 0; i < valueCount; i++) {
                buffer[i] = is.readInt();
            }
            break;
        case eUINT32:
            for (int i = 0; i < valueCount; i++) {
                buffer[i] = is.readInt() & 0xFFFFFFFFL;
            }
            break;
        case eINT64:
            for (int i = 0; i < valueCount; i++) {
                buffer[i] = is.readLong();
            }
            break;
        case eUINT64:
            for (int i = 0; i < valueCount; i++) {
                buffer[i] = is.readLong();
            }
            break;
        }
        return buffer;
    }

    @Override
    public Rectangle2D getBounds() {
        return BoundsExtractingGraphics2D.getBounds(this);
    }


}
