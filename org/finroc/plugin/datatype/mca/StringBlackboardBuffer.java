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
package org.finroc.plugin.datatype.mca;

import org.finroc.jc.annotation.JavaOnly;
import org.finroc.plugin.blackboard.BlackboardPlugin;
import org.finroc.plugin.blackboard.BlackboardBuffer;
import org.finroc.plugin.datatype.ContainsStrings;
import org.finroc.core.portdatabase.DataType;

/**
 * @author max
 *
 * String blackboard buffer
 */
@JavaOnly
public class StringBlackboardBuffer extends BlackboardBuffer implements ContainsStrings {

    public static DataType TYPE = BlackboardPlugin.registerBlackboardType(StringBlackboardBuffer.class, "Signed Char");
    public static DataType MTYPE = TYPE.getRelatedType();

    @Override
    public CharSequence getString(int index) {
        return getBuffer().getString(getElementOffset(index));
    }

    @Override
    public void setString(int index, CharSequence newString) {
        getBuffer().putString(getElementOffset(index), newString.toString());
    }

    @Override
    public int stringCount() {
        return getElements();
    }
}
