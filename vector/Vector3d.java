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
package org.finroc.plugins.data_types.vector;

import org.rrlib.serialization.rtti.DataType;

/**
 * @author Max Reichardt
 *
 * Java equivalent to tVec3d
 */
public class Vector3d extends Vector2d {

    public final static DataType<Vector3d> TYPE = new DataType<Vector3d>(Vector3d.class);

    public Vector3d() {
        super(3);
    }
    /** for subclasses */
    protected Vector3d(int dim) {
        super(dim);
    }

    public double getZ() {
        return getDouble(2);
    }
    public void setZ(double value) {
        set(2, value);
    }
}
