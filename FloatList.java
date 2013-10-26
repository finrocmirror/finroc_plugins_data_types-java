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

import org.rrlib.finroc_core_utils.rtti.DataType;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.MemoryBuffer;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.StringInputStream;
import org.rrlib.finroc_core_utils.serialization.StringOutputStream;

/**
 * @author Max Reichardt
 *
 * Float list (as used in finroc blackboards)
 */
public class FloatList extends MemoryBuffer implements ContainsStrings {

    public final static DataType<FloatList> TYPE = new DataType<FloatList>(FloatList.class, "List<float>", false);

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(getValue(i));
        }
        return sb.toString();
    }

    private float getValue(int i) {
        return super.getBuffer().getFloat(i * 4);
    }

    /**
     * @return Number of floats in buffer
     */
    public int size() {
        return super.getSize() / 4;
    }

    @Override
    public void deserialize(InputStreamBuffer is) {
        int size = is.readInt();
        boolean constType = is.readBoolean();
        assert(constType);
        setSize(size);
        is.readFully(super.getBuffer(), 0, size * 4);
    }

    @Override
    public void serialize(OutputStreamBuffer os) {
        os.writeInt(size());
        os.writeBoolean(true);
        os.write(super.getBuffer(), 0, super.curSize);
    }

    public void set(int index, float value) {
        assert(index < size());
        getBuffer().getBuffer().putFloat(4 * index, value);
    }

    @Override
    public int stringCount() {
        return size();
    }

    @Override
    public CharSequence getString(int index) {
        return "" + getValue(index);
    }

    @Override
    public void setString(int index, CharSequence newString) {
        set(index, Float.parseFloat(newString.toString()));
    }

    @Override
    public void setSize(int newSize) {
        super.ensureCapacity(newSize * 4, false, 0);
        super.curSize = newSize * 4;
    }

    @Override
    public void serialize(StringOutputStream os) {
        ContainsStrings.Util.serialize(os, this, "[", "]", ",");
    }

    /* (non-Javadoc)
     * @see org.finroc.serialization.RRLibSerializableImpl#deserialize(org.finroc.serialization.StringInputStream)
     */
    @Override
    public void deserialize(StringInputStream s) throws Exception {
        ContainsStrings.Util.deserialize(s, this, "[", "]", ",");
    }

}
