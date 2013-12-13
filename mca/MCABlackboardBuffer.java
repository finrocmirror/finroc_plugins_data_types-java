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
import org.finroc.plugins.blackboard.BlackboardPlugin;
import org.rrlib.serialization.PortDataListImpl;
import org.rrlib.serialization.rtti.DataType;
import org.rrlib.serialization.rtti.DataTypeBase;

/**
 * @author Max Reichardt
 *
 * Image-Blackboard
 */
public class MCABlackboardBuffer extends PortDataListImpl<BlackboardBuffer> {

    public MCABlackboardBuffer(DataTypeBase blackboardType) {
        super(blackboardType.getElementType());
        resize(1);
    }

    public BlackboardBuffer getBuffer() {
        return get(0);
    }

    protected static <T, E> DataType<T> getMcaBlackboardType(Class<T> bbType, Class<E> elementType, String elementTypeName) {
        DataType<E> elemType = new DataType<E>(elementType, elementTypeName, false);
        DataType<T> blackboardType = new DataType<T>(bbType, "List<" + elementTypeName + ">");
        elemType.getInfo().listType = blackboardType;
        blackboardType.getInfo().elementType = elemType;
        BlackboardPlugin.registerBlackboardType(elemType);
        return blackboardType;
    }

    public void resize(int length, int length2, int i, boolean b) {
        getBuffer().resize(length, length2, i, b);
    }
}

