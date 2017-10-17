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
package org.finroc.plugins.data_types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

import org.rrlib.serialization.XMLSerializable;
import org.rrlib.xml.XMLNode;

public class StringList extends ArrayList<String> implements XMLSerializable {

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

    public void removeEmptyEntries() {
        Iterator<String> iter = this.iterator();
        while (iter.hasNext()) {
            String s = iter.next();
            if ((s == null) ||
                    (s.isEmpty())) {
                iter.remove();
            }
        }
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

    @Override
    public void serialize(XMLNode node) throws Exception {
        for (String s : this) {
            node.addChildNode("element").setContent(s);
        }
    }

    @Override
    public void deserialize(XMLNode node) throws Exception {
        this.clear();
        for (XMLNode child : node.children()) {
            this.add(child.hasTextContent() ? child.getTextContent() : "");
        }
    }
}
