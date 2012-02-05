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
 * Java equivalent to tVec2d
 */
public class Vector2d extends Vector {

    public final static DataType<Vector2d> TYPE = new DataType<Vector2d>(Vector2d.class);

    public Vector2d() {
        super(2, Type.DOUBLE);
    }

    /** for subclasses */
    protected Vector2d(int dim) {
        super(dim, Type.DOUBLE);
    }

    public Vector2d(double x, double y) {
        this();
        set(x, y);
    }

    public double getX() {
        return getDouble(0);
    }
    public double getY() {
        return getDouble(1);
    }
    public void setX(double value) {
        set(0, value);
    }
    public void setY(double value) {
        set(1, value);
    }

    public double length() {
        return Math.sqrt(getX() * getX() + getY() * getY());
    }

    public double polarAngleRad() {
        return Math.atan2(getY(), getX());
    }

    public void set(double x, double y) {
        setX(x);
        setX(y);
    }
}
