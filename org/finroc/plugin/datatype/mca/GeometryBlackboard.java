/**
 * You received this file as part of an advanced experimental
 * robotics framework prototype ('finroc')
 *
 * Copyright (C) 2007-2010 Max Reichardt,
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
package org.finroc.plugin.datatype.mca;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.finroc.jc.jni.StructBase;
import org.finroc.log.LogLevel;
import org.finroc.plugin.blackboard.BlackboardBuffer;
import org.finroc.plugin.datatype.DataTypePlugin;
import org.finroc.plugin.datatype.PaintablePortData;
import org.finroc.plugin.datatype.Vector2D;
import org.finroc.serialization.DataTypeBase;
import org.finroc.serialization.FixedBuffer;

public class GeometryBlackboard extends MCABlackboardBuffer implements PaintablePortData {

    public static class Elem extends BlackboardBuffer {}
    public final static DataTypeBase TYPE = getMcaBlackboardType(GeometryBlackboard.class, Elem.class, "Geometry Entries");

    /** Temporary variables for rendering */
    private ThreadLocal<RenderingThreadLocals> renderLocals = new ThreadLocal<RenderingThreadLocals>();

    public GeometryBlackboard() {
        super(TYPE);
    }

    /**
     * Contains static final Objects for rendering for a single thread.
     */
    private class RenderingThreadLocals {

        private final MCA.tGeometryEntryHeader header = new MCA.tGeometryEntryHeader();
        private final Line2D.Double rLine = new Line2D.Double();
        private final Ellipse2D.Double rCircle = new Ellipse2D.Double();
        private final Rectangle2D.Double rRect = new Rectangle2D.Double();
        private final MCA.tLine line = new MCA.tLine();
        private final MCA.tLineSegment lineSeg = new MCA.tLineSegment();
        private final MCA.tRectangle rectangle = new MCA.tRectangle();
        private final Vector2D d1 = new Vector2D(0, 0), d2 = new Vector2D(0, 0);
        private final MCA.tCircle circle = new MCA.tCircle();
        private final MCA.tText text = new MCA.tText();
        private final MCA.tTriangle triangle = new MCA.tTriangle();
        private final int[] x = new int[3];
        private final int[] y = new int[3];
        private final MCA.tArrow arrow = new MCA.tArrow();

        void setBuffer(FixedBuffer b) {
            StructBase.setBuffer(b, header, header.color, header.pose, line, line.dir, line.point, rectangle, rectangle.dir1, rectangle.dir2, rectangle.point,
                                 circle, circle.point, text, text.point, text.position, lineSeg, lineSeg.start, lineSeg.end, lineSeg.point,
                                 triangle, triangle.point, triangle.point_1, triangle.point_2, triangle.point_3, arrow, arrow.start, arrow.end, arrow.point);
        }
    }

    @Override
    public void paint(Graphics2D g) {

        if (this.getBuffer().getSize() <= 0 || this.getBuffer().getElements() <= 0) {
            return;
        }

        // init graphics object
        AffineTransform at = g.getTransform();

        // init drawing objects
        RenderingThreadLocals rtl = renderLocals.get();
        if (rtl == null) {
            rtl = new RenderingThreadLocals();
            renderLocals.set(rtl);
        }
        final RenderingThreadLocals r = rtl;

        // first byte indicates which area of blackboard to use
        final FixedBuffer buf = getBuffer().getBuffer();
        int pos = buf.getByte(0) == 0 ? 1 : getBuffer().getElementOffset(1) + 1;
        r.setBuffer(buf);

        //dumpToFile("/home/max/geom.bb");

        try {

            while (true) {

                // read header
                r.header.setRelAddress(pos);
                pos += r.header.getSize();
                double entryX = r.header.pose.x.get();
                double entryY = -r.header.pose.y.get();
                double entryYaw = r.header.pose.yaw.get();
                Color entryColor = new Color(r.header.color.r.get(), r.header.color.g.get(), r.header.color.b.get());
                int alpha = r.header.color.a.get();
                int shapeCount = r.header.dimension.get();
                int type = r.header.type.get();
                boolean filled = r.header.filled.get();

                if (type == MCA.eGT_DIMENSION) {
                    return; // end signal
                }

                // transform Graphics object
                g.translate(entryX, entryY);
                g.rotate(-entryYaw);
                g.setColor(entryColor);

                switch (type) {

                case MCA.eGT_POINT: // draw points
                    for (int i = 0; i < shapeCount; i++) {
                        double x = MCA.tVec2._x.getRel(buf, pos);
                        double y = -MCA.tVec2._y.getRel(buf, pos);
                        pos += MCA.tVec2.sizeof;
                        g.drawLine((int)x, (int)y, (int)x, (int)y);
                    }
                    break;

                case MCA.eGT_POINT_3D: // draw 3D points
                    for (int i = 0; i < shapeCount; i++) {
                        double x = MCA.tVec3._x.getRel(buf, pos);
                        double y = -MCA.tVec3._y.getRel(buf, pos);
                        pos += MCA.tVec3.sizeof;
                        g.drawLine((int)x, (int)y, (int)x, (int)y);
                    }
                    break;

                case MCA.eGT_LINE: // draw lines
                    for (int i = 0; i < shapeCount; i++) {
                        r.line.setRelAddress(pos);
                        pos += r.line.getSize();
                        double x = r.line.point.x.get();
                        double y = r.line.point.y.get();
                        r.rLine.setLine(x, -y, x + r.line.dir.x.get(), -y - r.line.dir.y.get());
                        g.draw(r.rLine);
                    }
                    break;

                case MCA.eGT_LINE_SEGMENT: // draw line segments
                    for (int i = 0; i < shapeCount; i++) {
                        r.lineSeg.setRelAddress(pos);
                        pos += r.lineSeg.getSize();
                        double x1 = r.lineSeg.start.x.get();
                        double y1 = r.lineSeg.start.y.get();
                        double x2 = r.lineSeg.end.x.get();
                        double y2 = r.lineSeg.end.y.get();
                        r.rLine.setLine(x1, -y1, x2, -y2);
                        g.draw(r.rLine);
                    }
                    break;


                case MCA.eGT_RECTANGLE: // draw Rectangle
                    for (int i = 0; i < shapeCount; i++) {
                        r.rectangle.setRelAddress(pos);
                        pos += r.rectangle.getSize();

                        r.d1.set(r.rectangle.dir1.x.get(), -r.rectangle.dir1.y.get());
                        r.d2.set(r.rectangle.dir2.x.get(), -r.rectangle.dir2.y.get());
                        double halfWidth = r.d1.length();
                        double halfHeight = r.d2.length();
                        //this.setRect(px - halfWidth, py - halfWidth, halfWidth * 2, halfHeight * 2);
                        double x1 =  r.rectangle.point.x.get() - halfWidth;
                        double x2 = -r.rectangle.point.y.get() - halfHeight;
                        double w = halfWidth * 2;
                        double h = halfHeight * 2;
                        double rotation = r.d1.polarAngleRad();

                        r.rRect.setRect(x1, x2, w, h);
                        g.rotate(rotation);
                        g.draw(r.rRect);
                        g.rotate(-rotation);
                        if (filled) {
                            g.fill(r.rRect);
                        }
                    }
                    break;

                case MCA.eGT_CIRCLE: // draw circles
                    for (int i = 0; i < shapeCount; i++) {
                        r.circle.setRelAddress(pos);
                        pos += r.circle.getSize();

                        double x = r.circle.point.x.get();
                        double y = -r.circle.point.y.get();
                        double radius = r.circle.radius.get();

                        r.rCircle.setFrame(x - radius, y - radius, 2 * radius, 2 * radius);
                        g.draw(r.rCircle);
                        if (filled) {
                            g.fill(r.rCircle);
                        }
                    }
                    break;

                case MCA.eGT_TEXT: // draw texts

                    for (int i = 0; i < shapeCount; i++) {
                        r.text.setRelAddress(pos);

                        double x = r.text.position.x.get();
                        double y = -r.text.position.y.get();
                        String text = buf.getString(pos + MCA.tText._text_0.getOffset()).toString();
                        float size = r.text.size.get();

                        pos += r.text.getSize(); // increment here - because of text reading before

                        Font tmp = g.getFont();
                        g.setFont(tmp.deriveFont(size));
                        g.drawString(text, (int)x, (int)y);
                        g.setFont(tmp); // reset font
                    }
                    break;

                case MCA.eGT_TRIANGLE: // draw triangles

                    for (int i = 0; i < shapeCount; i++) {
                        r.triangle.setRelAddress(pos);
                        pos += r.triangle.getSize();

                        float opacity = 1.0f;
                        Composite oldComp = g.getComposite();
                        if (alpha != 255) {
                            opacity = (float)(((double)alpha) / 255.0);
                            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, opacity));
                        }

                        r.x[0] = (int)r.triangle.point_1.x.get();
                        r.x[1] = (int)r.triangle.point_2.x.get();
                        r.x[2] = (int)r.triangle.point_3.x.get();
                        r.y[0] = -(int)r.triangle.point_1.y.get();
                        r.y[1] = -(int)r.triangle.point_2.y.get();
                        r.y[2] = -(int)r.triangle.point_3.y.get();

                        g.drawPolygon(r.x, r.y, 3);
                        if (filled) {
                            g.fillPolygon(r.x, r.y, 3);
                        }

                        g.setComposite(oldComp);
                    }
                    break;

                case MCA.eGT_ARROW:

                    for (int i = 0; i < shapeCount; i++) {
                        r.arrow.setRelAddress(pos);
                        pos += r.arrow.getSize();

                        double x1 = r.lineSeg.start.x.get();
                        double y1 = r.lineSeg.start.y.get();
                        double x2 = r.lineSeg.end.x.get();
                        double y2 = r.lineSeg.end.y.get();
                        r.rLine.setLine(x1, -y1, x2, -y2);
                        g.draw(r.rLine);

                        // TODO: draw optional arrow heads

//                    if (arrow.head_at_start || arrow.head_at_end)
//                    {
//                      double arrow_length(0.0);
//                      tVec2d dir = (arrow.start - arrow.end).norm(arrow_length);
//                      if (arrow.head_length_relative)
//                        arrow_length *= arrow.head_length;
//                      else
//                        arrow_length = arrow.head_length;
//                      double sin_val(0.0), cos_val(0.0);
//                      sincos(arrow.head_opening_angle, &sin_val, &cos_val);
//                      arrow_length /= cos_val;
//                      tVec2d arrow_dir1(arrow_length * dir.Rotated(-sin_val, cos_val));
//                      tVec2d arrow_dir2(arrow_length * dir.Rotated(sin_val, cos_val));
//
//                      // draw optional arrow head at start of arrow
//                      if (arrow.head_at_start)
//                      {
//                        // the 1st part of the arrow at the line start
//                        tVec2d p1(arrow.start - arrow_dir1);
//                        //AddToBoundingBox(bounding_box, temp_bounding_box, p1);
//                        // the 2nd part of the arrow at the line start
//                        tVec2d p2(arrow.start - arrow_dir2);
//                        //AddToBoundingBox(bounding_box, temp_bounding_box, p2);
//
//                        DrawArrowHead(painter, header, arrow.start, p1, p2, arrow.filled);
//                      } // if (arrow.head_at_start)
//
//                      // draw optional arrow head at end of arrow
//                      if (arrow.head_at_end)
//                      {
//                        // the 1st part of the arrow at the line end
//                        tVec2d p1(arrow.end + arrow_dir1);
//                        //AddToBoundingBox(bounding_box, temp_bounding_box, p1);
//                        // the 2nd part of the arrow at the line end
//                        tVec2d p2(arrow.end + arrow_dir2);
//                        //AddToBoundingBox(bounding_box, temp_bounding_box, p2);
//
//                        DrawArrowHead(painter, header, arrow.end, p1, p2, arrow.filled);
//                      } // if (arrow.head_at_end)
//                    } // if ( arrow.head_at_start || arrow.head_at_end )
//                  } // for
//                } // arrow

                    }
                    break;

                default:
                    DataTypePlugin.logDomain.log(LogLevel.LL_WARNING, "GeometryBlackboard", "warning: Unknown entry type " + type + " in Geometry blackboard... skipping the rest");
                    // reset graphics object
                    g.setTransform(at);
                    return;
                }

                // reset graphics object
                g.setTransform(at);
            }

        } catch (Exception e) {
            DataTypePlugin.logDomain.log(LogLevel.LL_ERROR, "GeometryBlackboard", e);
        }
    }
}
