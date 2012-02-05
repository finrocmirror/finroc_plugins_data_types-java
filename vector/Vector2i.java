/**
 * You received this file as part of an advanced experimental
 * robotics framework prototype ('finroc')
 *
 * Copyright (C) 2011 Max Reichardt,
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
package org.finroc.plugins.data_types.vector;

import org.rrlib.finroc_core_utils.rtti.DataType;

/**
 * @author max
 *
 * Java equivalent to tVec2i
 */
public class Vector2i extends Vector {

    public final static DataType<Vector2i> TYPE = new DataType<Vector2i>(Vector2i.class);

    public Vector2i() {
        super(2, Type.INT);
    }

    /** for subclasses */
    protected Vector2i(int dim) {
        super(dim, Type.INT);
    }

    public int getX() {
        return getInt(0);
    }
    public int getY() {
        return getInt(1);
    }
    public void setX(int value) {
        set(0, value);
    }
    public void setY(int value) {
        set(1, value);
    }
}
