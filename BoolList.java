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

import org.rrlib.serialization.BinaryInputStream;
import org.rrlib.serialization.BinaryOutputStream;
import org.rrlib.serialization.BinarySerializable;
import org.rrlib.serialization.StringInputStream;
import org.rrlib.serialization.StringOutputStream;
import org.rrlib.serialization.StringSerializable;
import org.rrlib.serialization.rtti.DataType;

/**
 * @author Max Reichardt
 *
 * Boolean list
 */
public class BoolList implements BinarySerializable, StringSerializable {

    public final static DataType<BoolList> TYPE = new DataType<BoolList>(BoolList.class, "List<bool>", false);

    /** Buffer backend */
    private boolean[] buffer = new boolean[10];

    /** Current size */
    private int size = 0;

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            sb.append(getValue(i) ? '1' : '0');
        }
        return sb.toString();
    }

    private boolean getValue(int i) {
        return buffer[i];
    }

    /**
     * @return Number of bools in buffer
     */
    public int size() {
        return size;
    }

    @Override
    public void deserialize(BinaryInputStream is) {
        size = is.readInt();
        if (size > buffer.length) {
            buffer = new boolean[size];
        }
        boolean constType = is.readBoolean();
        assert(constType);
        for (int i = 0; i < size; i++) {
            buffer[i] = is.readBoolean();
        }
    }

    @Override
    public void serialize(BinaryOutputStream os) {
        os.writeInt(size());
        os.writeBoolean(true);
        for (int i = 0; i < size; i++) {
            os.writeBoolean(getValue(i));
        }
    }

    public void set(int index, boolean value) {
        assert(index < size());
        buffer[index] = value;
    }

    @Override
    public void serialize(StringOutputStream os) {
        os.append(toString());
    }

    @Override
    public void deserialize(StringInputStream sis) throws Exception {
        String s = sis.readLine();
        size = s.length();
        if (size > buffer.length) {
            buffer = new boolean[size];
        }
        for (int i = 0; i < size; i++) {
            buffer[i] = s.charAt(i) != '0';
        }
    }

}
