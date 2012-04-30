/**
 * You received this file as part of an advanced experimental
 * robotics framework prototype ('finroc')
 *
 * Copyright (C) 2011 Max Reichardt,
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

import org.finroc.core.portdatabase.CCType;
import org.finroc.core.portdatabase.FinrocTypeInfo;
import org.rrlib.finroc_core_utils.jc.annotation.JavaOnly;
import org.rrlib.finroc_core_utils.rtti.Copyable;
import org.rrlib.finroc_core_utils.rtti.DataType;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;
import org.rrlib.finroc_core_utils.serialization.StringInputStream;
import org.rrlib.finroc_core_utils.serialization.StringOutputStream;

/**
 * @author max
 *
 * tTime Java equivalent
 */
@JavaOnly
public class Time extends RRLibSerializableImpl implements Copyable<Time>, CCType {

    /** Data Type */
    public final static DataType<Time> TYPE = new DataType<Time>(Time.class);

    /** values */
    public int sec, usec;

    static {
        FinrocTypeInfo.get(TYPE).init(FinrocTypeInfo.Type.CC);
    }

    public Time() {
    }

    @Override
    public void serialize(OutputStreamBuffer os) {
        os.writeLong(sec);
        os.writeLong(usec);
    }

    @Override
    public void deserialize(InputStreamBuffer is) {
        sec = (int)is.readLong();
        usec = (int)is.readLong();
    }

    @Override
    public void serialize(StringOutputStream os) {
        os.append("(").append(sec).append(", ").append(usec).append(")");
    }

    @Override
    public void deserialize(StringInputStream is) throws Exception {
        String s = is.readAll();
        s = s.trim();
        if (s.startsWith("(") && s.endsWith(")")) {
            s = s.substring(1, s.length() - 1);
            String[] nums = s.split(",");
            if (nums.length == 2) {
                sec = Integer.parseInt(nums[0].trim());
                usec = Integer.parseInt(nums[1].trim());
                return;
            }
        }
        throw new Exception("Cannot parse " + s);
    }

    @Override
    public void copyFrom(Time o) {
        sec = o.sec;
        usec = o.usec;
    }
}
