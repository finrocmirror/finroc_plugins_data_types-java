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
package org.finroc.plugins.data_types;

import org.rrlib.finroc_core_utils.jc.annotation.JavaOnly;
import org.finroc.plugins.blackboard.BlackboardPlugin;
import org.rrlib.finroc_core_utils.rtti.DataType;
import org.rrlib.finroc_core_utils.rtti.DataTypeBase;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;

/**
 * @author max
 *
 * MCA Style behaviour info - see tBehaviourInfo.h
 */
@JavaOnly
public class BehaviourInfo extends RRLibSerializableImpl {

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
    public void serialize(OutputStreamBuffer os) {
        os.writeShort(beh_id);
        os.writeFloat(activity);
        os.writeFloat(target_rating);
        os.writeFloat(activation);
        os.writeBoolean(auto_mode);
        os.writeBoolean(enabled);
    }

    @Override
    public void deserialize(InputStreamBuffer is) {
        beh_id = is.readShort();
        activity = is.readFloat();
        target_rating = is.readFloat();
        activation = is.readFloat();
        auto_mode = is.readBoolean();
        enabled = is.readBoolean();
    }

}
