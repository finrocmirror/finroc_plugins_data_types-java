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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.finroc.serialization.DataType;

public class StringList extends ArrayList<String> {

    public final static DataType<StringList> TYPE = new DataType<StringList>(StringList.class);

    /** UID */
    private static final long serialVersionUID = -8572604096904164788L;

    public StringList(String s) {
        setEntries(s);
    }

    public StringList() {
    }

    public StringList(String[] strings) {
        setEntries(strings);
    }

    private void setEntries(String[] strings) {
        clear();
        this.addAll(Arrays.asList(strings));
    }

    public String toString() {
        String text = "";
        for (String s : this) {
            text += s + "\n";
        }
        return text;
    }

    public List<String> getStrings() {
        return this;
    }

    public void setEntries(String s) {
        clear();
        String[] strings = s.split("\n");
        this.addAll(Arrays.asList(strings));
    }

    public static String toSingleString(List<String> list) {
        StringBuffer sb = new StringBuffer();
        for (String s : list) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }
}
