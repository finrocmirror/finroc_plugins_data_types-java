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
package org.finroc.plugins.data_types.mca;

import org.finroc.plugins.blackboard.BlackboardBuffer;
import org.finroc.plugins.blackboard.BlackboardPlugin;
import org.rrlib.finroc_core_utils.rtti.DataType;
import org.rrlib.finroc_core_utils.rtti.DataTypeBase;
import org.rrlib.finroc_core_utils.serialization.PortDataListImpl;

/**
 * @author max
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

