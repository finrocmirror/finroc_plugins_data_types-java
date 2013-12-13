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
package org.finroc.plugins.data_types.util;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

import org.rrlib.logging.Log;
import org.rrlib.logging.LogLevel;
import org.finroc.plugins.data_types.Paintable;

/**
 * @author Max Reichardt
 *
 * Graphics2d object that can be used for extracting bounds of paintable
 * object.
 */
public class BoundsExtractingGraphics2D extends Graphics2D {

    /** Graphics2D object */
    private Graphics2D g2d = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics();

    /** Current transform */
    private AffineTransform transform = new AffineTransform();

    /** Current bounds */
    private Rectangle2D bounds = null;

    /** Temporary objects */
    private Point2D tmp1 = new Point2D.Double(), tmp2 = new Point2D.Double();

    /** Thread local instance of BoundsExtractingGraphics2D */
    private static ThreadLocal<BoundsExtractingGraphics2D> threadLocal = new ThreadLocal<BoundsExtractingGraphics2D>();

    /** Reset transform and bounds to reuse this object */
    public void reset() {
        transform.setToIdentity();
        bounds = null;
    }

    /**
     * @return Reset thread-local instance of BoundsExtractingGraphics2D
     */
    public static BoundsExtractingGraphics2D getInstance() {
        BoundsExtractingGraphics2D b = threadLocal.get();
        if (b == null) {
            b = new BoundsExtractingGraphics2D();
            threadLocal.set(b);
        }
        b.reset();
        return b;
    }

    /**
     * Extract bounds from paintable object
     *
     * @param p Paintable object
     * @return Bounds
     */
    public static Rectangle2D getBounds(Paintable p) {
        BoundsExtractingGraphics2D b = getInstance();
        p.paint(b);
        return b.bounds == null ? null : new Rectangle2D.Double(b.bounds.getX(), b.bounds.getY(), b.bounds.getWidth(), b.bounds.getHeight());
    }


    private void addToBounds(Rectangle2D bounds2d) {
        addToBounds(bounds2d.getMinX(), bounds2d.getMinY(), bounds2d.getMaxX(), bounds2d.getMaxY());
    }

    private void addToBounds(double minX, double minY, double maxX, double maxY) {
        addToBounds(minX, minY);
        addToBounds(maxX, minY);
        addToBounds(minX, maxY);
        addToBounds(maxX, maxY);
    }

    private void addToBounds(double minX, double minY) {
        tmp1.setLocation(minX, minY);
        transform.transform(tmp1, tmp2);

        if (bounds == null) {
            bounds = new Rectangle2D.Double(tmp2.getX(), tmp2.getY(), 0, 0);
        } else {
            bounds.add(tmp2);
        }
    }

    @Override
    public void draw(Shape s) {
        addToBounds(s.getBounds2D());
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
        return true;
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
    }

    @Override
    public void drawString(String str, int x, int y) {
        drawString(str, (float)x, (float)y);
    }

    @Override
    public void drawString(String str, float x, float y) {
        int w = g2d.getFontMetrics().stringWidth(str);
        int h = g2d.getFontMetrics().getHeight();
        addToBounds(x, y, x + w, y + h);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
    }

    @Override
    public void fill(Shape s) {
        addToBounds(s.getBounds2D());
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return g2d.hit(rect, s, onStroke);
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return g2d.getDeviceConfiguration();
    }

    @Override
    public void setComposite(Composite comp) {
        g2d.setComposite(comp);
    }

    @Override
    public void setPaint(Paint paint) {
        g2d.setPaint(paint);
    }

    @Override
    public void setStroke(Stroke s) {
        g2d.setStroke(s);
    }

    @Override
    public void setRenderingHint(Key hintKey, Object hintValue) {
        g2d.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public Object getRenderingHint(Key hintKey) {
        return g2d.getRenderingHint(hintKey);
    }

    @Override
    public void setRenderingHints(Map <? , ? > hints) {
        g2d.setRenderingHints(hints);
    }

    @Override
    public void addRenderingHints(Map <? , ? > hints) {
        g2d.addRenderingHints(hints);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return g2d.getRenderingHints();
    }

    @Override
    public void translate(int x, int y) {
        transform.translate(x, y);
    }

    @Override
    public void translate(double tx, double ty) {
        transform.translate(tx, ty);
    }

    @Override
    public void rotate(double theta) {
        transform.rotate(theta);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        transform.rotate(theta, x, y);
    }

    @Override
    public void scale(double sx, double sy) {
        transform.scale(sx, sy);
    }

    @Override
    public void shear(double shx, double shy) {
        transform.shear(shx, shy);
    }

    @Override
    public void transform(AffineTransform Tx) {
        transform.concatenate(Tx);
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        transform.setTransform(Tx);
    }

    @Override
    public AffineTransform getTransform() {
        return new AffineTransform(transform);
    }

    @Override
    public Paint getPaint() {
        return g2d.getPaint();
    }

    @Override
    public Composite getComposite() {
        return g2d.getComposite();
    }

    @Override
    public void setBackground(Color color) {
        g2d.setBackground(color);
    }

    @Override
    public Color getBackground() {
        return g2d.getBackground();
    }

    @Override
    public Stroke getStroke() {
        return g2d.getStroke();
    }

    @Override
    public void clip(Shape s) {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return g2d.getFontRenderContext();
    }

    @Override
    public Graphics create() {
        Log.log(LogLevel.ERROR, this, "Not implemented. Skipping.");
        return null;
    }

    @Override
    public Color getColor() {
        return g2d.getColor();
    }

    @Override
    public void setColor(Color c) {
        g2d.setColor(c);
    }

    @Override
    public void setPaintMode() {
        g2d.setPaintMode();
    }

    @Override
    public void setXORMode(Color c1) {
        g2d.setXORMode(c1);
    }

    @Override
    public Font getFont() {
        return g2d.getFont();
    }

    @Override
    public void setFont(Font font) {
        g2d.setFont(font);
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return g2d.getFontMetrics(f);
    }

    @Override
    public Rectangle getClipBounds() {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
        return null;
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
    }

    @Override
    public Shape getClip() {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
        return null;
    }

    @Override
    public void setClip(Shape clip) {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        addToBounds(x + dx, y + dy, x + dx + width, y + dy + height);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        addToBounds(x1, y1, x2, y2);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        addToBounds(x, y, x + width, y + height);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        addToBounds(x, y, x + width, y + height);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        addToBounds(x, y, x + width, y + height);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        addToBounds(x, y, x + width, y + height);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        addToBounds(x, y, x + width, y + height);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        addToBounds(x, y, x + width, y + height);
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        addToBounds(x, y, x + width, y + height);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        addToBounds(x, y, x + width, y + height);
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        for (int i = 0; i < nPoints; i++) {
            addToBounds(xPoints[i], yPoints[i]);
        }
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        for (int i = 0; i < nPoints; i++) {
            addToBounds(xPoints[i], yPoints[i]);
        }
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        for (int i = 0; i < nPoints; i++) {
            addToBounds(xPoints[i], yPoints[i]);
        }
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        addToBounds(x, y, x + img.getWidth(null), y + img.getHeight(null));
        return true;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        addToBounds(x, y, x + width, y + height);
        return true;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return drawImage(img, x, y, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        return drawImage(img, x, y, width, height, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
        return true;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        Log.log(LogLevel.DEBUG_WARNING, this, "Not implemented. Skipping.");
        return true;
    }

    @Override
    public void dispose() {
    }

//    static class Test implements Paintable {
//
//        @Override
//        public void paint(Graphics2D g) {
//            AffineTransform at = g.getTransform();
//            g.translate(50, 50);
//            g.fillRect(20, 400, 20, 400);
//            g.setTransform(at);
//        }
//
//        @Override
//        public Rectangle2D getBounds() {
//            return BoundsExtractingGraphics2D.getBounds(this);
//        }
//    }
//
//    static class TestPanel extends JPanel {
//
//        final Paintable p;
//        final Rectangle2D bounds;
//
//        public TestPanel(Paintable p) {
//            this.p = p;
//            this.bounds = p.getBounds();
//        }
//
//        @Override
//        protected void paintComponent(Graphics g) {
//            System.out.println(bounds.toString());
//            p.paint((Graphics2D)g);
//            g.setColor(Color.RED);
//            g.drawRect((int)bounds.getMinX(), (int)bounds.getMinY(), (int)bounds.getWidth(), (int)bounds.getHeight());
//        }
//    }
//
//
//
//    public static void main(String[] args) {
//        JFrame jf = new JFrame();
//        TestPanel jp = new TestPanel(new Test());
//        jf.getContentPane().add(jp);
//        jf.setVisible(true);
//    }
}
