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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import org.finroc.core.portdatabase.DataType;
import org.finroc.jc.jni.StructBase;
import org.finroc.jc.stream.FixedBuffer;
import org.finroc.plugin.blackboard.BlackboardBuffer;
import org.finroc.plugin.blackboard.BlackboardPlugin;
import org.finroc.plugin.datatype.Paintable;
import org.finroc.plugin.datatype.Vector2D;

public class GeometryBlackboard extends BlackboardBuffer implements Paintable {

    public static DataType TYPE = BlackboardPlugin.registerBlackboardType(GeometryBlackboard.class, "Geometry Entries");
    public static DataType MTYPE = TYPE.getRelatedType();

    /** Temporary variables for rendering */
    private ThreadLocal<RenderingThreadLocals> renderLocals = new ThreadLocal<RenderingThreadLocals>();

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

        void setBuffer(FixedBuffer b) {
            StructBase.setBuffer(b, header, header.color, header.pose, line, line.dir, line.point, rectangle, rectangle.dir1, rectangle.dir2, rectangle.point,
                                 circle, circle.point, text, text.point, text.position, lineSeg, lineSeg.start, lineSeg.end, lineSeg.point);
        }
    }

    @Override
    public void paint(Graphics2D g) {

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
        final FixedBuffer buf = getBuffer();
        int pos = buf.getByte(0) == 0 ? 1 : getElementOffset(1) + 1;
        r.setBuffer(buf);

        //dumpToFile("/home/max/geom.bb");

        while (true) {

            // read header
            r.header.setRelAddress(pos);
            pos += r.header.getSize();
            double entryX = r.header.pose.x.get();
            double entryY = -r.header.pose.y.get();
            double entryYaw = r.header.pose.yaw.get();
            Color entryColor = new Color(r.header.color.r.get(), r.header.color.g.get(), r.header.color.b.get());
            int shapeCount = r.header.dimension.get();
            int type = r.header.type.get();

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
                    if (r.rectangle.filled.get()) {
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
                    if (r.circle.filled.get()) {
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
                    pos += r.text.getSize();

                    Font tmp = g.getFont();
                    g.setFont(tmp.deriveFont(size));
                    g.drawString(text, (int)x, (int)y);
                    g.setFont(tmp); // reset font
                }
                break;

            default:
                System.out.println("warning: Unknown entry type " + type + " in Geometry blackboard... skipping the rest");
                // reset graphics object
                g.setTransform(at);
                return;
            }

            // reset graphics object
            g.setTransform(at);
        }
    }
}
