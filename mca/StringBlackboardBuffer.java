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
package org.finroc.plugins.data_types.mca;

import org.finroc.plugins.blackboard.BlackboardBuffer;
import org.finroc.plugins.data_types.ContainsStrings;
import org.rrlib.serialization.rtti.DataTypeBase;

/**
 * @author Max Reichardt
 *
 * String blackboard buffer
 */
public class StringBlackboardBuffer extends MCABlackboardBuffer implements ContainsStrings {

    public static class Elem extends BlackboardBuffer {}
    public final static DataTypeBase TYPE = getMcaBlackboardType(StringBlackboardBuffer.class, Elem.class, "Signed Char");

    public StringBlackboardBuffer() {
        super(TYPE);
    }

    @Override
    public CharSequence getString(int index) {
        return getBuffer().getBuffer().getString(getBuffer().getElementOffset(index));
    }

    @Override
    public void setString(int index, CharSequence newString) {
        getBuffer().getBuffer().putString(getBuffer().getElementOffset(index), newString.toString());
    }

    @Override
    public int stringCount() {
        return getBuffer().getElements();
    }

    @Override
    public void setSize(int newSize) {
        throw new RuntimeException("Unsupported");
    }
}
