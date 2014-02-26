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

import org.finroc.core.portdatabase.FinrocTypeInfo;
import org.rrlib.serialization.BinaryInputStream;
import org.rrlib.serialization.BinaryOutputStream;
import org.rrlib.serialization.BinarySerializable;
import org.rrlib.serialization.Serialization;
import org.rrlib.serialization.StringInputStream;
import org.rrlib.serialization.StringOutputStream;
import org.rrlib.serialization.StringSerializable;
import org.rrlib.serialization.rtti.Copyable;
import org.rrlib.serialization.rtti.DataType;

/**
 * @author Max Reichardt
 *
 * rrlib::math::tMat3x3d Java equivalent
 */
public class Matrix3x3d implements Copyable<Matrix3x3d>, BinarySerializable, StringSerializable {

    /** Data Type */
    public final static DataType<Matrix3x3d> TYPE = new DataType<Matrix3x3d>(Matrix3x3d.class);

    static {
        FinrocTypeInfo.get(TYPE).init(FinrocTypeInfo.Type.CC);
    }

    /** Matrix values (first row, then the second, then the third) */
    public final double[] values = new double[9];

    @Override
    public void serialize(BinaryOutputStream os) {
        for (int i = 0; i < values.length; i++) {
            os.writeDouble(values[i]);
        }
    }

    @Override
    public void deserialize(BinaryInputStream is) {
        for (int i = 0; i < values.length; i++) {
            values[i] = is.readDouble();
        }
    }

    @Override
    public void copyFrom(Matrix3x3d o) {
        System.arraycopy(o.values, 0, values, 0, values.length);
    }

    @Override
    public void serialize(StringOutputStream stream) {
        stream.append("[ " + values[0] + " " + values[1] + " " + values[2] + " ;\n" + values[3] + " " + values[4] + " " + values[5] + " ;\n" + values[6] + " " + values[7] + " " + values[8] + " ]");
    }

    @Override
    public void deserialize(StringInputStream stream) throws Exception {
        String[] numbers = stream.readAll().split("[;\\[\\]\\s]+");
        int indexOffset = numbers[0].trim().length() == 0 ? 1 : 0;
        for (int i = 0; i < Math.min(numbers.length, values.length); i++) {
            values[i] = Double.parseDouble(numbers[indexOffset + i]);
        }
    }

    @Override
    public String toString() {
        return Serialization.serialize(this);
    }
}
