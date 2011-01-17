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
package org.finroc.plugin.datatype;

import org.finroc.core.buffer.CoreInput;
import org.finroc.core.buffer.CoreOutput;
import org.finroc.core.port.cc.CCPortData;
import org.finroc.core.port.cc.CCPortDataImpl;
import org.finroc.core.portdatabase.DataType;
import org.finroc.core.portdatabase.DataTypeRegister;
import org.finroc.jc.annotation.JavaOnly;

/**
 * @author max
 *
 * tTime Java equivalent
 */
@JavaOnly
public class Time extends CCPortDataImpl {

    /** Data Type */
    public static DataType TYPE = DataTypeRegister.getInstance().getDataType(Time.class);

    /** values */
    public int sec, usec;

    public Time() {
        type = TYPE;
    }

    @Override
    public DataType getType() {
        return TYPE;
    }

    @Override
    public void serialize(CoreOutput os) {
        os.writeInt(sec);
        os.writeInt(usec);
    }

    @Override
    public void deserialize(CoreInput is) {
        sec = is.readInt();
        usec = is.readInt();
    }

    @Override
    public void assign(CCPortData other) {
        if (!(other instanceof Time)) {
            return;
        }
        Time o = (Time)other;
        sec = o.sec;
        usec = o.usec;
    }

    @Override
    public String serialize() {
        return "(" + sec + ", " + usec + ")";
    }

    @Override
    public void deserialize(String s) throws Exception {
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
}
