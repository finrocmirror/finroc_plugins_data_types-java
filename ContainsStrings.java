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
package org.finroc.plugins.data_types;

import org.rrlib.finroc_core_utils.jc.annotation.JavaOnly;
import org.rrlib.finroc_core_utils.rtti.DataType;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializable;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;
import org.rrlib.finroc_core_utils.serialization.StringInputStream;
import org.rrlib.finroc_core_utils.serialization.StringOutputStream;

/**
 * @author max
 *
 * Class that contains Strings
 */
@JavaOnly
public interface ContainsStrings extends RRLibSerializable {

    public final static DataType<ContainsStrings> TYPE = new DataType<ContainsStrings>(ContainsStrings.class);

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
     * @param newSize New size
     */
    public void setSize(int newSize);

    /**
     * Empty String List
     */
    @JavaOnly
    public class Empty extends RRLibSerializableImpl implements ContainsStrings {

        public final static DataType<Empty> TYPE = new DataType<Empty>(Empty.class, "EmptyStrings");

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
        public void serialize(OutputStreamBuffer os) {
        }

        @Override
        public void deserialize(InputStreamBuffer is) {
        }

        @Override
        public void setSize(int newSize) {
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

        /**
         * Serialize string list to string output stream
         *
         * @param sos String output stream
         * @param cs ContainsString object
         * @param start Start character
         * @param end End character
         * @param delim Delimiter
         */
        public static void serialize(StringOutputStream sos, ContainsStrings cs, String start, String end, String delim) {
            sos.append(start);
            for (int i = 0, n = cs.stringCount(); i < n; i++) {
                if (i > 0) {
                    sos.append(delim).append(" ");
                }
                String s = cs.getString(i).toString();
                assert(!s.contains(delim));
                sos.append(s);
            }
            sos.append(end);
        }

        /**
         * Deserialize string list from string input stream
         *
         * @param sos String input stream
         * @param cs ContainsString object
         * @param start Start character
         * @param end End character
         * @param delim Delimiter
         */
        public static void deserialize(StringInputStream sis, ContainsStrings cs, String start, String end, String delim) {
            String s = sis.readAll().trim();
            assert(s.startsWith(start) && s.endsWith(end));
            s = s.substring(1, s.length() - 1).trim();
            String[] sa = s.split(delim);
            cs.setSize(sa.length);
            for (int i = 0, n = sa.length; i < n; i++) {
                cs.setString(i, sa[i].trim());
            }
        }
    }
}
