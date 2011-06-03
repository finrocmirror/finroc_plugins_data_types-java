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
package org.finroc.plugin.datatype;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import org.finroc.serialization.DataType;
import org.finroc.serialization.InputStreamBuffer;
import org.finroc.serialization.OutputStreamBuffer;
import org.finroc.serialization.RRLibSerializable;
import org.finroc.serialization.RRLibSerializableImpl;

/**
 * @author max
 *
 * Marks objects that are paintable through Graphics interface
 */
public interface PaintablePortData extends Paintable, RRLibSerializable {

    public final static DataType<PaintablePortData> TYPE = new DataType<PaintablePortData>(PaintablePortData.class);

    public void paint(Graphics2D g);

    /**
     * Empty Paintable
     */
    public class Empty extends RRLibSerializableImpl implements PaintablePortData {

        public final static DataType<Empty> TYPE = new DataType<Empty>(Empty.class, "DummyPaintable");

        @Override
        public void paint(Graphics2D g) {}

        @Override
        public void serialize(OutputStreamBuffer os) {
        }

        @Override
        public void deserialize(InputStreamBuffer is) {
        }

        @Override
        public Rectangle getBounds() {
            return null;
        }
    }
}
