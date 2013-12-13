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

import org.rrlib.serialization.BinaryInputStream;
import org.rrlib.serialization.BinaryOutputStream;
import org.rrlib.serialization.BinarySerializable;
import org.rrlib.serialization.StringInputStream;
import org.rrlib.serialization.StringOutputStream;
import org.rrlib.serialization.StringSerializable;
import org.rrlib.serialization.rtti.DataType;
import org.rrlib.serialization.rtti.DataTypeBase;

/**
 * @author Max Reichardt
 *
 * Java equivalent to rrlib::ib2c::tStatus.
 * Contains information about a behavior's status relevant for tooling.
 */
public class BehaviorStatus implements BinarySerializable, StringSerializable {

    public final static DataTypeBase TYPE = new DataType<BehaviorStatus>(BehaviorStatus.class, "finroc.ib2c.Status");

    public enum StimulationMode {
        Auto,
        Enabled,
        Disabled
    };

    public String name;
    public int moduleHandle;
    public StimulationMode stimulationMode;
    public double activity;
    public double targetRating;
    public double activation;

    @Override
    public void serialize(BinaryOutputStream stream) {
        stream.writeString(name);
        stream.writeInt(moduleHandle);
        stream.writeEnum(stimulationMode);
        stream.writeDouble(activity);
        stream.writeDouble(targetRating);
        stream.writeDouble(activation);
    }

    @Override
    public void deserialize(BinaryInputStream stream) {
        name = stream.readString();
        moduleHandle = stream.readInt();
        stimulationMode = stream.readEnum(StimulationMode.class);
        activity = stream.readDouble();
        targetRating = stream.readDouble();
        activation = stream.readDouble();
    }

    @Override
    public void serialize(StringOutputStream stream) {
        stream.append("Activity: " + activity + ", Activation: " + activation + ", Target Rating: " + targetRating + ", Stimulation Mode: " + stimulationMode + ", Name: " + name + ", ModuleHandle: " + moduleHandle);
    }

    @Override
    public void deserialize(StringInputStream stream) throws Exception {
        throw new Exception("You cannot set the behavior status via finstruct");
    }
}
