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
 * tPose3D Java equivalent
 */
@JavaOnly
public class Pose3D extends CCPortDataImpl {

    /** Data Type */
    public static DataType TYPE = DataTypeRegister.getInstance().getDataType(Pose3D.class);

    /** values */
    public double x, y, z, roll, pitch, yaw;

    public Pose3D() {
        type = TYPE;
    }

    @Override
    public DataType getType() {
        return TYPE;
    }

    @Override
    public void serialize(CoreOutput os) {
        os.writeDouble(x);
        os.writeDouble(y);
        os.writeDouble(z);
        os.writeDouble(roll);
        os.writeDouble(pitch);
        os.writeDouble(yaw);
    }

    @Override
    public void deserialize(CoreInput is) {
        x = is.readDouble();
        y = is.readDouble();
        z = is.readDouble();
        roll = is.readDouble();
        pitch = is.readDouble();
        yaw = is.readDouble();
    }

    @Override
    public void assign(CCPortData other) {
        if (!(other instanceof Pose3D)) {
            return;
        }
        Pose3D o = (Pose3D)other;
        x = o.x;
        y = o.y;
        z = o.z;
        roll = o.roll;
        pitch = o.pitch;
        yaw = o.yaw;
    }

    @Override
    public String serialize() {
        return "(" + x + ", " + y + ", " + z + ", " + roll + ", " + pitch + ", " + yaw + ")";
    }

    @Override
    public void deserialize(String s) throws Exception {
        s = s.trim();
        if (s.startsWith("(") && s.endsWith(")")) {
            s = s.substring(1, s.length() - 1);
            String[] nums = s.split(",");
            if (nums.length == 6) {
                x = Double.parseDouble(nums[0]);
                y = Double.parseDouble(nums[1]);
                z = Double.parseDouble(nums[2]);
                roll = Double.parseDouble(nums[3]);
                pitch = Double.parseDouble(nums[4]);
                yaw = Double.parseDouble(nums[5]);
                return;
            }
        }
        throw new Exception("Cannot parse " + s);
    }
}
