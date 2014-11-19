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

import java.util.Arrays;

import org.rrlib.serialization.BinaryInputStream;
import org.rrlib.serialization.BinaryOutputStream;
import org.rrlib.serialization.StringInputStream;
import org.rrlib.serialization.StringOutputStream;
import org.rrlib.serialization.rtti.DataType;

/**
 * @author Max Reichardt
 *
 * tUncertainPose (3D) Java equivalent
 */
public class UncertainPose3D extends Pose3D {

    /** Data Type */
    public final static DataType<UncertainPose3D> TYPE = new DataType<UncertainPose3D>(UncertainPose3D.class);

    public final Matrix matrix = new Matrix(6, 6);

    @Override
    public void serialize(BinaryOutputStream os) {
        super.serialize(os);
        matrix.serialize(os);
    }

    @Override
    public void deserialize(BinaryInputStream is) {
        super.deserialize(is);
        matrix.deserialize(is);
    }

    @Override
    public void serialize(StringOutputStream os) {
        super.serialize(os);
        os.append("\n").append(matrix.toString());
    }

    @Override
    public void deserialize(StringInputStream is) throws Exception {
        String s = is.readAll();
        int index = s.indexOf(')') + 1;
        if (index >= 0) {
            super.deserialize(new StringInputStream(s.substring(0, index)));
            matrix.deserialize(new StringInputStream(s.substring(index)));
            return;
        }
        throw new Exception("Cannot parse " + s);
    }

    @Override
    public void copyFrom(Pose2D o) {
        super.copyFrom(o);
        if (o instanceof UncertainPose3D) {
            matrix.copyFrom(((UncertainPose3D) o).matrix);
        } else {
            Arrays.fill(matrix.values, 0);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof UncertainPose3D) {
            UncertainPose3D o = (UncertainPose3D)other;
            return super.equals(o) && matrix.equals(o.matrix);
        }
        return false;
    }
}
