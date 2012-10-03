/**
 * You received this file as part of FinGUI - a universal
 * (Web-)GUI editor for Robotic Systems.
 *
 * Copyright (C) 2007-2010 Max Reichardt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * @author max
 *
 * Abstract superclass for anything that can be blitted
 */
public abstract class Blittable {

    protected abstract void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int srcY, int srcOffset, int width);

    /** Temporary buffer for painting images to Graphics2D java objects */
    private static ThreadLocal<PaintHelper> paintTempBuffer = new ThreadLocal<PaintHelper>();

    /**
     * Copy (part of) image to another image
     * If necessary, rectangles are automatically made smaller
     *
     * @param destination destination image
     * @param destArea destination area (rectangle)
     * @param srcArea source area (rectangle)
     */
    public void blitTo(Destination destination, Point dest, Rectangle sourceArea) {

        // ensure correct bounds
        Rectangle[] areas = ensureCorrectBounds(destination, dest, sourceArea, getSize());
        if (areas == null) {
            return;
        }
        Rectangle srcArea = areas[1];
        Rectangle destArea = areas[0];

        // blit
        int srcPos = srcArea.y * getWidth() + srcArea.x;
        int destPos = destArea.y * destination.getWidth() + destArea.x;
        int[] destBuffer = destination.getBuffer();
        for (int y = 0; y < srcArea.height; y++) {
            //System.arraycopy(getBuffer(), srcPos, destination.getBuffer(), destPos, srcArea.width);
            blitLineToRGB(destBuffer, destPos, srcArea.x, srcArea.y + y, srcPos, srcArea.width);
            srcPos += getWidth();
            destPos += destination.getWidth();
        }
    }

    public void blitTo(Destination buffer, int destX, int destY, int srcX, int srcY, int width, int height) {
        blitTo(buffer, new Point(destX, destY), new Rectangle(srcX, srcY, width, height));
    }

    public void blitTo(Destination buffer) {
        blitTo(buffer, 0, 0, 0, 0, buffer.getWidth(), buffer.getHeight());
    }

    public void blitTo(Destination buffer, Rectangle destArea, Point srcOffset) {
        blitTo(buffer, destArea.getLocation(), new Rectangle(srcOffset, destArea.getSize()));
    }

    public void blitTo(Destination buffer, Rectangle destArea) {
        blitTo(buffer, destArea.getLocation(), new Rectangle(destArea.getSize()));
    }

    private static Rectangle[] ensureCorrectBounds(Destination destination, Point dest, Rectangle srcArea, Dimension srcDimension) {

        Rectangle bounds = new Rectangle(srcDimension);
        if (!srcArea.intersects(bounds)) {
            return null;
        }
        srcArea = srcArea.intersection(bounds);

        // Lies destination in Bounds?
        Dimension destDimension = destination.getSize();
        Rectangle destArea = new Rectangle(dest.x, dest.y, srcArea.width, srcArea.height);
        if (!destArea.intersects(destination.getBounds())) {
            return null;
        }

        // correct bounds
        if (dest.x < 0) {
            srcArea.width += dest.x;
            srcArea.x -= dest.x;
            destArea.x = 0;
        }
        if (destArea.x + srcArea.width > destDimension.width) {
            srcArea.width = destDimension.width - destArea.x;
        }
        if (dest.y < 0) {
            srcArea.height += dest.y;
            srcArea.y -= dest.y;
            destArea.y = 0;
        }
        if (destArea.y + srcArea.height > destDimension.height) {
            srcArea.height = destDimension.height - destArea.y;
        }
        destArea.width = srcArea.width;
        destArea.height = srcArea.height;

        return new Rectangle[] {destArea, srcArea};
    }

    /**
     * May be overridden (for minimal performance gain)
     *
     * @return Size of image
     */
    public Dimension getSize() {
        return new Dimension(getWidth(), getHeight());
    }

    public abstract int getWidth();
    public abstract int getHeight();

    public Rectangle getBounds() {
        return new Rectangle(0, 0, getWidth(), getHeight());
    }

    /**
     * Blit destination. Any 24bit RGB buffer.
     */
    public interface Destination {

        /**
         * @return Buffer to blit to - one int per pixel
         */
        public abstract int[] getBuffer();

        // same as in Blittable
        public abstract int getWidth();
        public abstract int getHeight();
        public Rectangle getBounds();
        public Dimension getSize();
        public BufferedImage getBufferedImage();
    }

    /**
     * Empty Blittable
     */
    public static class Empty extends Blittable {

        public static final Empty instance = new Empty();

        @Override
        protected void blitLineToRGB(int[] destBuffer, int destOffset, int srcX, int srcY, int srcOffset, int width) {
        }

        @Override
        public int getHeight() {
            return 0;
        }

        @Override
        public int getWidth() {
            return 0;
        }
    }

    /**
     * Helper class for painting blittable to Graphics2D
     */
    static class PaintHelper extends BufferedImage implements Destination {

        public PaintHelper(int width, int height) {
            super(width, height, BufferedImage.TYPE_INT_RGB);
        }

        @Override
        public int[] getBuffer() {
            return ((DataBufferInt)getRaster().getDataBuffer()).getData();
        }

        @Override
        public Rectangle getBounds() {
            return new Rectangle(0, 0, getWidth(), getHeight());
        }

        @Override
        public Dimension getSize() {
            return new Dimension(getWidth(), getHeight());
        }

        @Override
        public BufferedImage getBufferedImage() {
            return this;
        }

    }

    /**
     * Standard (slightly inefficient) paint implementation to paint
     * blittable to Graphics2D Java object
     * (blits blittable to temp buffer and then paints temp buffer to graphics2d)
     *
     * @param g Graphics2D object
     */
    public void standardPaintImplementation(Graphics2D g) {
        if (getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        PaintHelper img = paintTempBuffer.get();
        if (img == null || img.getWidth() < getWidth() || img.getHeight() < getHeight()) {
            img = new PaintHelper(getWidth(), getHeight());
            paintTempBuffer.set(img);
        }
        blitTo(img);
        g.drawImage(img, 0, 0, null);
    }
}
