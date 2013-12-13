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
import org.rrlib.serialization.rtti.DataTypeBase;

/**
 * @author Max Reichardt
 *
 * Float blackboard buffer
 */
public class FloatBlackboardBuffer extends MCABlackboardBuffer {

    public static class Elem extends BlackboardBuffer {}
    public final static DataTypeBase TYPE = getMcaBlackboardType(FloatBlackboardBuffer.class, Elem.class, "Float");

    public FloatBlackboardBuffer() {
        super(TYPE);
    }

    public int size() {
        return (getBuffer().getElements() * getBuffer().getElementSize()) / 4;
    }

    public float getValue(int index) {
        return getBuffer().getBuffer().getFloat(4 * index);
    }

}
