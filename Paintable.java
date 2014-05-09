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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.finroc.plugins.data_types.util.FastBufferedImage;

/**
 * @author Max Reichardt
 *
 * Marks objects that are paintable through a Java Graphics2D interface
 */
public interface Paintable {

    /**
     * Paint object
     *
     * @param g Graphics2D Object to blit to
     * @param imageBuffer Image buffer that this object is painted to (optional, may be null).
     *                    Specifying this can allow increased rendering performance by bypassing the Graphics2D object
     *                    when rendering to an image buffer.
     *                    If specified, it must contain a 32 bit RGB(A) integer buffer.
     */
    public void paint(Graphics2D g, FastBufferedImage imageBuffer);

    /**
     * @return Bounds of paintable object (null if nothing is drawn)
     */
    public Rectangle2D getBounds();

    /**
     * Typically, the y axis in cartesian coordinate systems points upwards.
     * However, with screen coordinates and e.g. images it points downwards.
     *
     * @return True if y axis is pointing downwards
     */
    public boolean isYAxisPointingDownwards();
}
