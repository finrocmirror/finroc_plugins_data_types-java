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
package org.finroc.plugins.data_types.mca;

import org.finroc.plugins.blackboard.BlackboardPlugin;
import org.rrlib.serialization.BinaryInputStream;
import org.rrlib.serialization.BinaryOutputStream;
import org.rrlib.serialization.BinarySerializable;
import org.rrlib.serialization.rtti.DataType;
import org.rrlib.serialization.rtti.DataTypeBase;

/**
 * @author Max Reichardt
 *
 * MCA Style behaviour info - see tBehaviourInfo.h
 */
public class BehaviourInfo implements BinarySerializable {

    public final static DataType<BehaviourInfo> TYPE = new DataType<BehaviourInfo>(BehaviourInfo.class);
    public final static DataTypeBase BB_TYPE = BlackboardPlugin.registerBlackboardType(TYPE);

    //Copied from tBegaviourInfo.h
    public short beh_id;
    // activation
    public float activity;
    // target rating
    public float target_rating;
    public float activation;
    public boolean auto_mode;
    public boolean enabled;

    @Override
    public void serialize(BinaryOutputStream os) {
        os.writeShort(beh_id);
        os.writeFloat(activity);
        os.writeFloat(target_rating);
        os.writeFloat(activation);
        os.writeBoolean(auto_mode);
        os.writeBoolean(enabled);
    }

    @Override
    public void deserialize(BinaryInputStream is) {
        beh_id = is.readShort();
        activity = is.readFloat();
        target_rating = is.readFloat();
        activation = is.readFloat();
        auto_mode = is.readBoolean();
        enabled = is.readBoolean();
    }

}
