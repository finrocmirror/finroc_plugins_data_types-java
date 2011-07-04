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

import java.awt.Graphics2D;

import org.finroc.core.portdatabase.FinrocTypeInfo;
import org.rrlib.finroc_core_utils.jc.annotation.JavaOnly;
import org.rrlib.finroc_core_utils.serialization.Copyable;
import org.rrlib.finroc_core_utils.serialization.DataType;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;
import org.rrlib.finroc_core_utils.serialization.StringInputStream;
import org.rrlib.finroc_core_utils.serialization.StringOutputStream;

/**
 * @author max
 *
 * tPose3D Java equivalent
 */
@JavaOnly
public class Pose3D extends RRLibSerializableImpl implements Copyable<Pose3D> {

    /** Data Type */
    public final static DataType<Pose3D> TYPE = new DataType<Pose3D>(Pose3D.class);

    static {
        FinrocTypeInfo.get(TYPE).init(FinrocTypeInfo.Type.CC);
    }

    /** values */
    public double x, y, z, roll, pitch, yaw;

    @Override
    public void serialize(OutputStreamBuffer os) {
        os.writeDouble(x);
        os.writeDouble(y);
        os.writeDouble(z);
        os.writeDouble(roll);
        os.writeDouble(pitch);
        os.writeDouble(yaw);
    }

    @Override
    public void deserialize(InputStreamBuffer is) {
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
    public void copyFrom(Pose3D o) {
        x = o.x;
        y = o.y;
        z = o.z;
        roll = o.roll;
        pitch = o.pitch;
        yaw = o.yaw;
    }

    /**
     * Apply transformation to 2D graphics
     *
     * @param g Graphics2D object to apply transformation to
     */
    public void applyTransformation(Graphics2D g) {
        g.translate(x, y);
        g.rotate(yaw);
    }
}
