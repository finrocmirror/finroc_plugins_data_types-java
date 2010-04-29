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
import org.finroc.core.portdatabase.Copyable;
import org.finroc.core.portdatabase.DataType;
import org.finroc.core.portdatabase.DataTypeRegister;

/**
 * @author max
 *
 * MCA Style behaviour info - see tBehaviourInfo.h
 */
@JavaOnly
public interface BehaviourInfo extends PortData {

    static DataType TYPE = DataTypeRegister.getInstance().getDataType(BehaviourInfo.class);

    /**
     * @return Number of entries
     */
    public int size();

    /**
     * @param size New Number of entries
     */
    public void setSize(int size);

    /**
     * @param index index
     * @return Single entry in behaviour info list
     */
    public Entry getEntry(int index);

    /**
     * Single entry in behaviour info list
     */
    @JavaOnly
    public interface Entry {

        public short getBehId();
        public void setBehId(short beh_id);
        public float getActivation();
        public void setActivation(float a);
        public float getTargetRating();
        public void setTargetRating(float r);
        public float getActivity();
        public void setActivity(float jota);
        public boolean getAutoMode();
        public void setAutoMode(boolean auto_mode);
        public boolean isEnabled();
        public void setEnabled(boolean enabled);
    }

    /**
     * Dummy/Empty behaviour info
     */
    @JavaOnly
    public static class Empty extends PortDataImpl implements BehaviourInfo {

        static DataType TYPE = DataTypeRegister.getInstance().getDataType(Empty.class, "EmptyBehaviour");

        @Override
        public Entry getEntry(int index) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public void setSize(int size) {}

        @Override
        public void deserialize(CoreInput is) {
        }

        @Override
        public void serialize(CoreOutput os) {
        }
    }

    @JavaOnly
    public static class Util {

        @SuppressWarnings("unchecked")
        public static void copy(BehaviourInfo src, BehaviourInfo dest) {
            if (src.getClass() == dest.getClass() && src instanceof Copyable) {
                try {
                    ((Copyable)dest).copyFrom(src);
                    return;
                } catch (Exception e) {}
            }

            // copy by hand
            dest.setSize(src.size());
            for (int i = 0; i < src.size(); i++) {
                Entry se = src.getEntry(i);
                Entry de = dest.getEntry(i);
                de.setActivation(se.getActivation());
                de.setAutoMode(de.getAutoMode());
                de.setBehId(se.getBehId());
                de.setEnabled(se.isEnabled());
                de.setActivity(se.getActivity());
                de.setTargetRating(se.getTargetRating());
            }
        }
    }
}
