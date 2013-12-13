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

import org.finroc.core.portdatabase.FinrocTypeInfo;
import org.rrlib.serialization.BinaryInputStream;
import org.rrlib.serialization.BinaryOutputStream;
import org.rrlib.serialization.Serialization;
import org.rrlib.serialization.StringInputStream;
import org.rrlib.serialization.StringOutputStream;
import org.rrlib.serialization.rtti.DataType;

/**
 * @author Max Reichardt
 *
 * tPose3D Java equivalent
 */
public class Pose3D extends Pose2D {

    /** Data Type */
    public final static DataType<Pose3D> TYPE = new DataType<Pose3D>(Pose3D.class);

    static {
        FinrocTypeInfo.get(TYPE).init(FinrocTypeInfo.Type.CC);
    }

    /** values */
    public double z, roll, pitch;

    @Override
    public void serialize(BinaryOutputStream os) {
        os.writeDouble(x);
        os.writeDouble(y);
        os.writeDouble(z);
        os.writeDouble(roll);
        os.writeDouble(pitch);
        os.writeDouble(yaw);
    }

    @Override
    public void deserialize(BinaryInputStream is) {
        x = is.readDouble();
        y = is.readDouble();
        z = is.readDouble();
        roll = is.readDouble();
        pitch = is.readDouble();
        yaw = is.readDouble();
    }

    @Override
    public void serialize(StringOutputStream os) {
        os.append("(").append(x).append(", ").append(y).append(", ").append(z).append(", ").append(roll).append(", ").append(pitch).append(", ").append(yaw).append(")");
    }

    @Override
    public void deserialize(StringInputStream is) throws Exception {
        String s = is.readAll();
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

    @Override
    public void copyFrom(Pose2D o) {
        x = o.x;
        y = o.y;
        yaw = o.yaw;
        if (o instanceof Pose3D) {
            Pose3D o3 = (Pose3D)o;
            z = o3.z;
            roll = o3.roll;
            pitch = o3.pitch;
        } else {
            z = 0;
            roll = 0;
            pitch = 0;
        }
    }

    public String toString() {
        return Serialization.serialize(this);
    }
}
