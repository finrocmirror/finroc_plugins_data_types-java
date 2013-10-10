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

import org.rrlib.finroc_core_utils.rtti.DataType;
import org.rrlib.finroc_core_utils.rtti.DataTypeBase;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;

/**
 * @author Max Reichardt
 *
 * Java equivalent to rrlib::ib2c::tStatus.
 * Contains information about a behavior's status relevant for tooling.
 */
public class BehaviorStatus extends RRLibSerializableImpl {

    public final static DataTypeBase TYPE = new DataType<BehaviorStatus>(BehaviorStatus.class, "finroc.ib2c.Status");

    enum StimulationMode {
        AUTO,
        ENABLED,
        DISABLED
    };

    public String name;
    public int moduleHandle;
    public StimulationMode stimulationMode;
    public double activity;
    public double targetRating;
    public double activation;

    @Override
    public void serialize(OutputStreamBuffer stream) {
        stream.writeString(name);
        stream.writeInt(moduleHandle);
        stream.writeEnum(stimulationMode);
        stream.writeDouble(activity);
        stream.writeDouble(targetRating);
        stream.writeDouble(activation);
    }

    @Override
    public void deserialize(InputStreamBuffer stream) {
        name = stream.readString();
        moduleHandle = stream.readInt();
        stimulationMode = stream.readEnum(StimulationMode.class);
        activity = stream.readDouble();
        targetRating = stream.readDouble();
        activation = stream.readDouble();
    }
}
