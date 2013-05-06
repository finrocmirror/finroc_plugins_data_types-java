//
// You received this file as part of Finroc
// A Framework for intelligent robot control
//
// Copyright (C) Finroc GbR (finroc.org)
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
//----------------------------------------------------------------------
package org.finroc.plugins.data_types;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

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
     */
    public void paint(Graphics2D g);

    /**
     * @return Bounds of paintable object (null if nothing is drawn)
     * (Note: y direction is downwards on screen)
     */
    public Rectangle2D getBounds();
}
