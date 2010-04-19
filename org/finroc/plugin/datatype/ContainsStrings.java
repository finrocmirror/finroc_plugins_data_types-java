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
import org.finroc.core.buffer.CoreInput;
import org.finroc.core.buffer.CoreOutput;
import org.finroc.core.port.std.PortData;
import org.finroc.core.port.std.PortDataImpl;
import org.finroc.core.portdatabase.DataType;
import org.finroc.core.portdatabase.DataTypeRegister;

/**
 * @author max
 *
 * Class that contains Strings
 */
@JavaOnly
public interface ContainsStrings extends PortData {

    static DataType TYPE = DataTypeRegister.getInstance().getDataType(ContainsStrings.class);

    /**
     * @return Number of Strings this class contains
     */
    public int stringCount();

    /**
     * @param index Index of String
     * @return String at index
     */
    public CharSequence getString(int index);

    /**
     * @param index Index of String
     * @param newString String at this index
     */
    public void setString(int index, CharSequence newString);

    /**
     * Empty String List
     */
    @JavaOnly
    public class Empty extends PortDataImpl implements ContainsStrings {

        static DataType TYPE = DataTypeRegister.getInstance().getDataType(Empty.class, "EmptyStrings");

        @Override
        public CharSequence getString(int index) {
            return null;
        }

        @Override
        public void setString(int index, CharSequence newString) {
        }

        @Override
        public int stringCount() {
            return 0;
        }

        @Override
        public void deserialize(CoreInput is) {
        }

        @Override
        public void serialize(CoreOutput os) {
        }
    }

    @JavaOnly
    public static class Util {

        /**
         * Converts text lines to single string with each line ending with
         * a line change
         *
         * @param cs ContainsStrings object to get String from
         * @return Long String
         */
        public static CharSequence toSingleString(ContainsStrings cs) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0, n = cs.stringCount(); i < n; i++) {
                sb.append(cs.getString(i)).append("\n");
            }
            return sb;
        }
    }
}
