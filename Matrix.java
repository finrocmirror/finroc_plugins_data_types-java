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

import org.rrlib.logging.Log;
import org.rrlib.logging.LogLevel;
import org.rrlib.serialization.BinaryInputStream;
import org.rrlib.serialization.BinaryOutputStream;
import org.rrlib.serialization.BinarySerializable;
import org.rrlib.serialization.Serialization;
import org.rrlib.serialization.StringInputStream;
import org.rrlib.serialization.StringOutputStream;
import org.rrlib.serialization.StringSerializable;
import org.rrlib.serialization.rtti.Copyable;

/**
 * @author Max Reichardt
 *
 * rrlib::math::tMatrix template Java equivalent
 */
public class Matrix implements Copyable<Matrix>, BinarySerializable, StringSerializable {

    /** Matrix values (first row, then the second, then the third) */
    public final double[] values;

    /** Number of rows and columns */
    public final int rows, cols;

    public Matrix(int rows, int cols) {
        values = new double[rows * cols];
        this.rows = rows;
        this.cols = cols;
    }

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
    public void copyFrom(Matrix o) {
        if (rows != o.rows || cols != o.cols) {
            Log.log(LogLevel.WARNING, "Matrices have different sizes. Not copying.");
        } else {
            System.arraycopy(o.values, 0, values, 0, values.length);
        }
    }

    @Override
    public void serialize(StringOutputStream stream) {
        stream.append("[ ");
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                stream.append(values[row * col + col]).append(" ");
            }
            stream.append(row == rows - 1 ? "]" : ";\n");
        }
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof Matrix) {
            Matrix other = (Matrix)o;
            return rows == other.rows && cols == other.cols && Arrays.equals(values, other.values);
        }
        return false;
    }
}
