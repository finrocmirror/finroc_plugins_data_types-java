//
// You received this file as part of Finroc
// A framework for intelligent robot control
//
// Copyright (C) Finroc GbR (finroc.org)
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License along
// with this program; if not, write to the Free Software Foundation, Inc.,
// 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
//
//----------------------------------------------------------------------
package org.finroc.plugins.data_types;

import java.lang.Math;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import org.finroc.plugins.data_types.util.BezierSpline;
import org.finroc.plugins.data_types.util.BoundsExtractingGraphics2D;
import org.finroc.plugins.data_types.util.FastBufferedImage;
import org.finroc.plugins.data_types.util.GraphicsUtil;
import org.rrlib.logging.Log;
import org.rrlib.logging.LogLevel;
import org.rrlib.serialization.BinaryInputStream;
import org.rrlib.serialization.MemoryBuffer;
import org.rrlib.serialization.rtti.DataType;

/**
 * @author Max Reichardt
 *
 * Java equivalent to rrlib::canvas::tCanvas
 */
public class Canvas extends MemoryBuffer implements PaintablePortData {

    public final static DataType<Canvas> TYPE = new DataType<Canvas>(Canvas.class, "Canvas2D");

    private final static double cARROW_HEAD_SIZE = 5.0;
    private final static double cSTROKE_WIDTH = 1.0;

    enum Opcode {
        // ####### tCanvas-supported opcodes ########

        // Transformation operations
        eSET_TRANSFORMATION,            // [(K+1)x(K+1) matrix]
        eTRANSFORM,                     // [(K+1)x(K+1) matrix]
        eTRANSLATE,                     // [vector]
        eROTATE,                        // [yaw]
        eSCALE,                         // [vector]
        eRESET_TRANSFORMATION,          // []

        // Canvas, Draw & encoding mode
        eSET_COLOR,                     // [RGB: 3 bytes]
        eSET_EDGE_COLOR,                // [RGB: 3 bytes]
        eSET_FILL_COLOR,                // [RGB: 3 bytes]
        eSET_FILL,                      // [bool]
        eSET_ALPHA,                     // [1 byte]

        // Geometry primitives
        eDRAW_POINT,                    // [vector]
        eDRAW_LINE,                     // [vector][vector]
        eDRAW_LINE_SEGMENT,             // [vector][vector]
        eDRAW_LINE_STRIP,               // [number of values: N][vector1]...[vectorN]
        eDRAW_ARROW,                    // [bool][vector][vector]
        eDRAW_BOX,                      // [vector][size1]...[sizeN]
        eDRAW_ELLIPSOID,                // [vector][diameter1]...[diameterN]
        eDRAW_BEZIER_CURVE,             // [degree: N][vector1]...[vectorN+1]
        eDRAW_POLYGON,                  // [number of values: N][vector1]...[vectorN]
        eDRAW_SPLINE,                   // [number of values: N][tension-parameter][vector1]...[vectorN]
        eDRAW_STRING,                   // [vector][null-terminated chars]

        // Custom path/shape
        ePATH_START,                    // [point]
        ePATH_END_OPEN,                 // [point]
        ePATH_END_CLOSED,               // [point]
        ePATH_LINE,                     // [point]
        ePATH_QUADRATIC_BEZIER_CURVE,   // [point][point]
        ePATH_CUBIC_BEZIER_CURVE,       // [point][point][point]

        // ####### tCanvas2D-only opcodes ########

        // Canvas, Draw & encoding mode
        eSET_Z,                         // [value]
        eSET_EXTRUSION,                 // [value]

        // ####### tCanvas3D-only opcodes ########

        eDRAW_COLORED_POINT_CLOUD,      // [number of values: N][6d vector1]...[6d vectorN]
        eDRAW_POINT_CLOUD,              // [number of values: N][vector1]...[vectorN]

        // ####### Opcodes added after Finroc 13.10 ########

        eDEFAULT_VIEWPORT,              // 2d: [left,bottom,width,height]  3d: yet undefined (could be tPose3D)
        eDEFAULT_VIEWPORT_OFFSET        // [int64 absolute offset]
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
    BinaryInputStream tempInputStreamZExtraction = new BinaryInputStream(this);
    double[] tempArrayZExtraction = new double[6];

    public Canvas() {
        super(false);
    }

    @Override
    public void deserialize(BinaryInputStream rv) {
        super.deserialize(rv);
        extractZLevels();
    }

    /**
     * Extract all areas with the same Z-level and store them in zLevels
     */
    private void extractZLevels() {
        zLevels.clear();
        BinaryInputStream is = tempInputStreamZExtraction;
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
            if (opcode != Opcode.ePATH_CUBIC_BEZIER_CURVE && opcode != Opcode.ePATH_QUADRATIC_BEZIER_CURVE && opcode != Opcode.ePATH_LINE) {
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

            case eDRAW_ARROW:              // [bool][2D-point][2D-point]
                is.readBoolean();
            case eDRAW_LINE:               // [2D-point][2D-point]
            case eDRAW_LINE_SEGMENT:       // [2D-point][2D-point]
            case eDRAW_BOX:                // [2D-point][width][height]
            case eDRAW_ELLIPSOID:          // [2D-point][width][height]
            case eDEFAULT_VIEWPORT:        // [left,bottom,width,height]
            case ePATH_QUADRATIC_BEZIER_CURVE:
                skipValues(is, 4);
                break;

            case eDRAW_SPLINE:             // [number of values][2D-vector1]...[2D-vectorN]  (bezier spline)
                is.readFloat();
            case eDRAW_POLYGON:            // [number of values][2D-vector1]...[2D-vectorN]
            case eDRAW_LINE_STRIP:
                int points = is.readShort();
                skipValues(is, points * 2);
                break;

            case eDRAW_STRING:             // [2D-point][null-terminated chars]
                skipValues(is, 2);
                is.readString();
                break;

            case ePATH_CUBIC_BEZIER_CURVE:
                skipValues(is, 6);
                break;

            case eDRAW_BEZIER_CURVE:       // [degree: N][2D-point1]...[2D-pointN+1]
                int degree = is.readShort();
                skipValues(is, (degree + 1) * 2);
                break;

            case ePATH_START:
                skipValues(is, 2);
                is.readBoolean();
                break;

            case eDEFAULT_VIEWPORT_OFFSET:
                is.readLong();
                break;

            default:
                Log.log(LogLevel.WARNING, this, "Opcode " + opcode.toString() + " not supported yet");
                return;
            }
        }
        Collections.sort(zLevels);
    }

    @Override
    public void paint(Graphics2D g, FastBufferedImage imageBuffer) {
        BinaryInputStream is = new BinaryInputStream(this);
        Graphics2D g2d = (g instanceof BoundsExtractingGraphics2D) ? g : (Graphics2D)g.create();
        AffineTransform defaultTransform = g2d.getTransform();

        for (RenderContext lvl : zLevels) {
            if (lvl.commandCount > 0) {
                is.reset(this);
                is.skip(lvl.bufferOffset);
                paintGeometry(g2d, imageBuffer, lvl, is, defaultTransform);
            }
        }
        is.close();
    }

    public static class ScalingFactors {
        public double x;
        public double y;
    }

    /**
     * This function extracts the real scaling factors of the affine transformation
     * used in the given Graphics2D object \arg g. It also set the strokeWidth to
     * be independent from current scaling.
     *
     * @note This method only works if not shear is involved as the resulting
     *       system would be under-determined otherwise.
     *
     * @param g   The graphics object to render
     *
     * @return The current scaling factors for x- and y-axis
     */
    public static ScalingFactors calculateScalingFactorsAndUpdateStrokeWidth(Graphics2D g) {
        ScalingFactors result = new ScalingFactors();

        double[] m = new double[4];
        g.getTransform().getMatrix(m);
        result.x = Math.sqrt(m[0] * m[0] + m[1] * m[1]);
        result.y = Math.sqrt(m[2] * m[2] + m[3] * m[3]);

        g.setStroke(new BasicStroke((float)(cSTROKE_WIDTH / Math.sqrt(result.x * result.x + result.y * result.y))));

        return result;
    }

    public void paintGeometry(Graphics2D g, FastBufferedImage imageBuffer, RenderContext lvl, BinaryInputStream is, AffineTransform defaultTransform) {
        double[] v = new double[1000];
        boolean fill = lvl.fill;
        final boolean drawPrettyPoints = g.getRenderingHint(RenderingHints.KEY_RENDERING) == RenderingHints.VALUE_RENDER_QUALITY;
        Color edgeColor = lvl.edgeColor;
        Color fillColor = lvl.fillColor;
        g.setColor(edgeColor);
        g.setTransform(defaultTransform);
        g.transform(lvl.at);

        // Shapes
        final Line2D.Double line = new Line2D.Double();
        final Ellipse2D.Double ellipse = new Ellipse2D.Double();
        final Rectangle2D.Double rect = new Rectangle2D.Double();
        final Rectangle awtRectangle = new Rectangle();

        // Helper objects for line drawing
        final Point2D.Double p1 = new Point2D.Double();
        final Point2D.Double p2 = new Point2D.Double();
        final Point2D.Double p1t = new Point2D.Double();
        final Point2D.Double p2t = new Point2D.Double();

        // Arrow head
        final Path2D.Double arrowHead = new Path2D.Double();
        arrowHead.moveTo(0.0, 0.0);
        arrowHead.lineTo(-2.0, 1.0);
        arrowHead.lineTo(-2.0, -1.0);
        arrowHead.lineTo(0.0, 0.0);

        ScalingFactors scaling = calculateScalingFactorsAndUpdateStrokeWidth(g);

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
                scaling = calculateScalingFactorsAndUpdateStrokeWidth(g);
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
                scaling = calculateScalingFactorsAndUpdateStrokeWidth(g);
                break;
            case eRESET_TRANSFORMATION:
                g.setTransform(defaultTransform);
                scaling = calculateScalingFactorsAndUpdateStrokeWidth(g);
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
                    g.setColor(edgeColor);
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
                if (imageBuffer != null) {
                    p1.x = v[0];
                    p1.y = v[1];
                    if (g.getClip() == null || g.getClip().contains(p1)) {
                        g.getTransform().transform(p1, p2);
                        int x = (int)p2.x;
                        int y = (int)p2.y;
                        if (x >= 0 && x < imageBuffer.getWidth() && y >= 0 && y < imageBuffer.getHeight()) {
                            imageBuffer.setPixel(x, y, g.getColor().getRGB());
                        }
                    }
                } else if (drawPrettyPoints) {
                    ellipse.x = v[0] - 0.5 / scaling.x;
                    ellipse.y = v[1] - 0.5 / scaling.y;
                    ellipse.width = 1 / scaling.x;
                    ellipse.height = 1 / scaling.y;
                    g.fill(ellipse);
                } else {
                    line.x1 = v[0];
                    line.x2 = v[0];
                    line.y1 = v[1];
                    line.y2 = v[1];
                    g.draw(line);
                }
                break;

            case eDRAW_LINE:              // [2D-point][2D-vector]
                readValues(is, v, 4);
                double x1 = v[0];
                double y1 = v[1];
                double vecx = v[2];
                double vecy = v[3];
                AffineTransform at = g.getTransform();

                if (!(g instanceof BoundsExtractingGraphics2D)) {
                    Rectangle r = g.getClipBounds();
                    if (r == null && imageBuffer != null) {
                        p1.x = 0;
                        p1.y = 0;
                        p2.x = imageBuffer.getWidth();
                        p2.y = imageBuffer.getHeight();
                        try {
                            r = new Rectangle();
                            at.inverseTransform(p1, p1t);
                            at.inverseTransform(p2, p2t);
                            r.x = (int)Math.round(Math.min(p1t.x, p2t.x)) - 1;
                            r.y = (int)Math.round(Math.min(p1t.y, p2t.y)) - 1;
                            r.width = (int)Math.round(Math.abs(p1t.x - p2t.x)) + 2;
                            r.height = (int)Math.round(Math.abs(p1t.y - p2t.y)) + 2;
                        } catch (Exception e) {
                            Log.log(LogLevel.ERROR, e);
                        }
                    }
                    while (r != null) {
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

            case eDRAW_LINE_STRIP: {      // [number of values][2D-vector1]...[2D-vectorN]
                int points = is.readShort();
                Path2D.Double path = new Path2D.Double(Path2D.WIND_NON_ZERO, points);
                if (points * 2 > v.length) {
                    Log.log(LogLevel.WARNING, this, "More than " + (v.length / 2) + " points not supported");
                    return;
                }
                readValues(is, v, points * 2);
                synchronized (path) { // maybe this speeds synchronized calls up?
                    path.moveTo(v[0], v[1]);
                    for (int j = 1; j < points; j++) {
                        path.lineTo(v[j * 2], v[j * 2 + 1]);
                    }
                }
                g.draw(path);
                break;
            }
            case eDRAW_ARROW:      // [bool][2D-point][2D-point]
                boolean undirected = is.readBoolean();
                readValues(is, v, 4);
                line.x1 = v[0];
                line.y1 = v[1];
                line.x2 = v[2];
                line.y2 = v[3];
                g.draw(line);

                AffineTransform currentTransform = g.getTransform();

                double angle = Math.atan2(line.y2 - line.y1, line.x2 - line.x1);

                g.translate(line.x2, line.y2);
                g.rotate(angle);
                g.scale(cARROW_HEAD_SIZE / scaling.x, cARROW_HEAD_SIZE / scaling.y);
                g.fill(arrowHead);

                if (undirected) {
                    g.setTransform(currentTransform);
                    g.translate(line.x1, line.y1);
                    g.rotate(angle + Math.PI);
                    g.scale(cARROW_HEAD_SIZE / scaling.x, cARROW_HEAD_SIZE / scaling.y);
                    g.fill(arrowHead);
                }

                g.setTransform(currentTransform);
                break;

            case eDRAW_BOX:             // [2D-point][width][height]
                readValues(is, v, 4);
                //if (imageBuffer != null && g.getClip() == null && (!GraphicsUtil.isRotation(g.getTransform()))) {  // this would be possible, however, we get double-pixel-lines in grid maps (due to rounding errors)
                if (imageBuffer != null && g.getClip() == null && (!GraphicsUtil.isRotation(g.getTransform())) && fill && edgeColor.equals(fillColor)) {
                    p1.x = v[0];
                    p1.y = v[1];
                    int width = (int)Math.round(v[2] * scaling.x) + 1; /*Math.max(1, (int)Math.round(v[2] * scaling.x))*/;
                    int height = (int)Math.round(v[3] * scaling.y) + 1; /*Math.max(1, (int)Math.round(v[3] * scaling.y))*/;
                    g.getTransform().transform(p1, p2);
                    awtRectangle.x = (int)Math.round(p2.x);
                    awtRectangle.y = ((int)Math.round(p2.y)) - height;
                    awtRectangle.width = width;
                    awtRectangle.height = height;
                    Rectangle awtRectangle2 = imageBuffer.getBounds().intersection(awtRectangle);

                    // fill
                    if (fill) {
                        int destPos = awtRectangle2.y * imageBuffer.getWidth() + awtRectangle2.x;
                        for (int y = 1; y < awtRectangle2.height - 1; y++) {
                            destPos += imageBuffer.getWidth();
                            Arrays.fill(imageBuffer.getBuffer(), destPos, destPos + awtRectangle2.width, fillColor.getRGB());
                        }
                    }

                    // draw border
                    int rgb = g.getColor().getRGB();
                    int destPos = awtRectangle2.y * imageBuffer.getWidth() + awtRectangle2.x;
                    Arrays.fill(imageBuffer.getBuffer(), destPos, destPos + awtRectangle2.width, g.getColor().getRGB());
                    for (int y = 1; y < awtRectangle2.height - 1; y++) {
                        destPos += imageBuffer.getWidth();
                        imageBuffer.getBuffer()[destPos] = g.getColor().getRGB();
                        imageBuffer.getBuffer()[destPos + awtRectangle2.width] = g.getColor().getRGB();
                    }
                    destPos += imageBuffer.getWidth();
                    Arrays.fill(imageBuffer.getBuffer(), destPos, destPos + awtRectangle2.width, g.getColor().getRGB());

                } else {
                    rect.x = v[0];
                    rect.y = v[1];
                    rect.width = v[2];
                    rect.height = v[3];
                    if (fill) {
                        g.setColor(fillColor);
                        g.fill(rect);
                        g.setColor(edgeColor);
                    }
                    g.draw(rect);
                }
                break;

            case eDRAW_ELLIPSOID:               // [2D-point][width][height]
                readValues(is, v, 4);
                ellipse.x = v[0];
                ellipse.y = v[1];
                ellipse.width = v[2];
                ellipse.height = v[3];
                if (fill) {
                    g.setColor(fillColor);
                    g.fill(ellipse);
                    g.setColor(edgeColor);
                }
                g.draw(ellipse);
                break;

            case eDRAW_POLYGON:            // [number of values][2D-vector1]...[2D-vectorN]
                int points = is.readShort();
                Path2D.Double path = new Path2D.Double(Path2D.WIND_NON_ZERO, points + 1);
                if (points * 2 > v.length) {
                    Log.log(LogLevel.WARNING, this, "More than " + (v.length / 2) + " points not supported");
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
                if (fill) {
                    g.setColor(fillColor);
                    g.fill(path);
                    g.setColor(edgeColor);
                }
                g.draw(path);

                break;

            case eDRAW_SPLINE:             // [number of values][2D-vector1]...[2D-vectorN]  (bezier spline)
                float tension = is.readFloat();
                points = is.readShort();
                if (points * 2 > v.length) {
                    Log.log(LogLevel.WARNING, this, "More than " + (v.length / 2) + " points not supported");
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
                // get current transformation
                AffineTransform tmp = g.getTransform();
                tmp.transform(new Point2D.Double(v[0], v[1]), p1t);
                // create a transformation without a rotation
                AffineTransform nonRotated = new AffineTransform();
                nonRotated.translate(p1t.x, p1t.y);
                g.setTransform(nonRotated);
                g.drawString(s, 0, 0);
                // reset transform
                g.setTransform(tmp);
                break;

            case eDRAW_BEZIER_CURVE: // [degree: N][2D-point1]...[2D-pointN+1]
                short degree = is.readShort();
                points = degree + 1;
                if (points * 2 > v.length) {
                    Log.log(LogLevel.WARNING, this, "Degree greater than " + (v.length / 2 - 1) + " points not supported");
                    return;
                }
                readValues(is, v, points * 2);
                double twist_threshold = 1.0 / Math.sqrt(Math.pow(scaling.x, 2) + Math.pow(scaling.y, 2));
                drawBezierCurve(g, degree, v, twist_threshold);
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
                        } else if (opcode == Opcode.ePATH_QUADRATIC_BEZIER_CURVE) {
                            readValues(is, v, 4);
                            path.quadTo(v[0], v[1], v[2], v[3]);
                        } else if (opcode == Opcode.ePATH_CUBIC_BEZIER_CURVE) {
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

            case eDEFAULT_VIEWPORT:
                readValues(is, v, 4);
                break;

            case eDEFAULT_VIEWPORT_OFFSET:
                is.readLong();
                break;

            default:
                Log.log(LogLevel.WARNING, this, "Opcode " + opcode.toString() + " not supported yet");
                return;
            }
        }
    }

    private void skipValues(BinaryInputStream is, int valueCount) {
        NumberTypeEnum t = is.readEnum(NumberTypeEnum.class);
        int bytes = numberTypeBytes[t.ordinal()];
        is.skip(bytes * valueCount);
    }

    private double[] readValues(BinaryInputStream is, double[] buffer, int valueCount) {
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
        if (getSize() > 9) {
            long offset = getBuffer().getByte(0) == Opcode.eDEFAULT_VIEWPORT_OFFSET.ordinal() ? (getBuffer().getLong(1) + 9) : 0;
            if (getBuffer().getByte((int)offset) == Opcode.eDEFAULT_VIEWPORT.ordinal()) {
                double[] v = new double[4];
                BinaryInputStream stream = new BinaryInputStream(this);
                stream.skip((int)offset + 1);
                readValues(stream, v, 4);
                return new Rectangle2D.Double(v[0], v[1], v[2], v[3]);
            }
        }
        return BoundsExtractingGraphics2D.getBounds(this);
    }

    private void drawBezierCurve(Graphics2D g, int degree, double[] v, double twist_threshold) {
        int points = degree + 1;
        int values = 2 * points;

        double twist = 0.0;
        for (int i = 0; i < degree - 1; ++i) {
            int offset_0 = 2 * i;
            int offset_1 = offset_0 + 2;
            int offset_2 = offset_1 + 2;
            double second_forward_difference_x = (v[offset_2] - v[offset_1]) - (v[offset_1] - v[offset_0]);
            double second_forward_difference_y = (v[offset_2 + 1] - v[offset_1 + 1]) - (v[offset_1 + 1] - v[offset_0 + 1]);

            twist = Math.max(twist, Math.sqrt(Math.pow(second_forward_difference_x, 2) + Math.pow(second_forward_difference_y, 2)));
        }
        twist *= degree * (degree - 1);

        if (twist < twist_threshold) {
            Line2D.Double line = new Line2D.Double();
            line.x1 = v[0];
            line.y1 = v[1];
            line.x2 = v[values - 2];
            line.y2 = v[values - 1];
            g.draw(line);
            return;
        }

        double[] first_half = new double[values];
        double[] second_half = new double[values];
        double[] temp_points = new double[values];
        for (int i = 0; i < values; ++i) {
            temp_points[i] = v[i];
        }

        first_half[0] = temp_points[0];
        first_half[1] = temp_points[1];
        second_half[values - 2] = temp_points[values - 2];
        second_half[values - 1] = temp_points[values - 1];

        int k = 0;
        while (k < degree) {
            for (int i = 0; i < degree - k; ++i) {
                int point_x = 2 * i;
                int point_y = 2 * i + 1;
                temp_points[point_x] = (temp_points[point_x] + temp_points[point_x + 2]) * 0.5;
                temp_points[point_y] = (temp_points[point_y] + temp_points[point_y + 2]) * 0.5;
            }
            ++k;
            first_half[2 * k] = temp_points[0];
            first_half[2 * k + 1] = temp_points[1];
            second_half[2 * (degree - k)] = temp_points[2 * (degree - k)];
            second_half[2 * (degree - k) + 1] = temp_points[2 * (degree - k) + 1];
        }

        drawBezierCurve(g, degree, first_half, twist_threshold);
        drawBezierCurve(g, degree, second_half, twist_threshold);
    }

    @Override
    public void copyFrom(MemoryBuffer source) {
        super.copyFrom(source);
        extractZLevels();
    }

    @Override
    public boolean isYAxisPointingDownwards() {
        return false;
    }
}
