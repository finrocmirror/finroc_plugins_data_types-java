//
// You received this file as part of Finroc
// A Framework for intelligent robot control
//
// Copyright (C) Finroc GbR (finroc.org)
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
//----------------------------------------------------------------------
package org.finroc.plugins.data_types;

import java.util.ArrayList;

import org.rrlib.finroc_core_utils.rtti.DataType;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;
import org.rrlib.finroc_core_utils.xml.XMLNode;

/**
 * @author Max Reichardt
 *
 * Class to interpret C++ std::string lists
 */
public class StdStringList extends RRLibSerializableImpl implements ContainsStrings {

    public final static DataType<StdStringList> TYPE = new DataType<StdStringList>(StdStringList.class, "List<String>");

    private final ArrayList<String> wrapped = new ArrayList<String>();

    /**
     * @return Number of Strings this class contains
     */
    public int stringCount() {
        return wrapped.size();
    }

    /**
     * @param index Index of String
     * @return String at index
     */
    public CharSequence getString(int index) {
        return wrapped.get(index);
    }

    /**
     * @param index Index of String
     * @param newString String at this index
     */
    public void setString(int index, CharSequence newString) {
        wrapped.set(index, newString.toString());
    }


    @Override
    public void serialize(OutputStreamBuffer os) {
        os.writeInt(wrapped.size());
        os.writeBoolean(true);
        for (int i = 0; i < wrapped.size(); i++) {
            os.writeString(wrapped.get(i));
        }
    }

    @Override
    public void deserialize(InputStreamBuffer is) {
        int size = is.readInt();
        boolean constType = is.readBoolean();
        assert(constType);
        wrapped.clear();
        for (int i = 0; i < size; i++) {
            wrapped.add(is.readString());
        }
    }

    @Override
    public void serialize(XMLNode node) throws Exception {
        for (String s : wrapped) {
            node.addChildNode("element").setContent(s);
        }
    }

    @Override
    public void deserialize(XMLNode node) throws Exception {
        wrapped.clear();
        for (XMLNode.ConstChildIterator e = node.getChildrenBegin(); e.get() != node.getChildrenEnd(); e.next()) {
            wrapped.add(e.get().hasTextContent() ? e.get().getTextContent() : "");
        }
    }

    @Override
    public void setSize(int newSize) {
        while (wrapped.size() > newSize) {
            wrapped.remove(wrapped.size()  - 1);
        }
        while (wrapped.size() < newSize) {
            wrapped.add("");
        }
    }

    /**
     * Adds string to list
     *
     * @param string String to add
     */
    public void add(String string) {
        wrapped.add(string);
    }
}
