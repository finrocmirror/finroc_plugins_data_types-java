/**
 * You received this file as part of an advanced experimental
 * robotics framework prototype ('finroc')
 *
 * Copyright (C) 2007-2010 Max Reichardt,
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
package org.finroc.plugin.datatype;

import org.finroc.jc.annotation.JavaOnly;
import org.finroc.plugin.datatype.mca.FloatBlackboardBuffer;
import org.finroc.serialization.DataType;
import org.finroc.serialization.InputStreamBuffer;
import org.finroc.serialization.OutputStreamBuffer;

/**
 * @author max
 *
 * Float list (as used in finroc blackboards)
 */
@JavaOnly
public class FloatList extends FloatBlackboardBuffer implements ContainsStrings {

    public final static DataType<FloatList> TYPE = new DataType<FloatList>(FloatList.class, "List<float>");

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(get(i));
        }
        return sb.toString();
    }

    @Override
    public void deserialize(InputStreamBuffer is) {
        int size = is.readInt();
        boolean constType = is.readBoolean();
        assert(constType);
        resize(size, size, 4, false);
        for (int i = 0; i < size; i++) {
            set(i, is.readFloat());
        }
    }

    @Override
    public void serialize(OutputStreamBuffer os) {
        os.writeInt(size());
        os.writeBoolean(true);
        for (int i = 0; i < size(); i++) {
            os.writeFloat(get(i));
        }
    }

    public void set(int index, float value) {
        getBuffer().putFloat(4 * index, value);
    }

    @Override
    public int stringCount() {
        return size();
    }

    @Override
    public CharSequence getString(int index) {
        return "" + get(index);
    }

    @Override
    public void setString(int index, CharSequence newString) {
        set(index, Float.parseFloat(newString.toString()));
    }
}
