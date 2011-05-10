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
import org.finroc.plugin.blackboard.BlackboardBuffer;
import org.finroc.plugin.datatype.ContainsStrings;
import org.finroc.serialization.DataTypeBase;

/**
 * @author max
 *
 * String blackboard buffer
 */
@JavaOnly
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
