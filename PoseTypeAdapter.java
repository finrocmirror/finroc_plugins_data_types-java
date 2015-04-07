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

import org.finroc.core.remote.RemoteType;
import org.finroc.core.remote.RemoteTypeAdapter;
import org.rrlib.serialization.BinaryInputStream;
import org.rrlib.serialization.BinaryOutputStream;
import org.rrlib.serialization.Serialization;
import org.rrlib.serialization.rtti.GenericObject;

/**
 * @author Max Reichardt
 *
 * Adapts rrlib::localization::tPose default template types to Pose types
 */
public class PoseTypeAdapter extends RemoteTypeAdapter {

    public static final PoseTypeAdapter INSTANCE = new PoseTypeAdapter();

    public static final String STANDARD_2D_POSE_TYPE_NAME = "rrlib.localization.Pose<2u, double, rrlib.si_units.SIUnit<1, 0, 0, 0, 0, 0, 0>, rrlib.si_units.SIUnit<0, 0, 0, 0, 0, 0, 0>, rrlib.math.angle.Signed>";
    public static final String STANDARD_3D_POSE_TYPE_NAME = "rrlib.localization.Pose<3u, double, rrlib.si_units.SIUnit<1, 0, 0, 0, 0, 0, 0>, rrlib.si_units.SIUnit<0, 0, 0, 0, 0, 0, 0>, rrlib.math.angle.Signed>";
    public static final String LEGACY_2D_POSE_TYPE_NAME = "rrlib.math.Pose2D";
    public static final String LEGACY_3D_POSE_TYPE_NAME = "rrlib.math.Pose3D";
    public static final String TWIST_3D_TYPE_NAME = "rrlib.localization.Twist3D";
    public static final String UNCERTAIN_TWIST_3D_TYPE_NAME = "rrlib.localization.UncertainTwist3D";

    private PoseTypeAdapter() {
        super(10);
    }

    @Override
    public boolean handlesType(RemoteType remoteType, Info adapterInfo) {
        if (remoteType.getName().equals(STANDARD_2D_POSE_TYPE_NAME) || remoteType.getName().equals(LEGACY_2D_POSE_TYPE_NAME)) {
            adapterInfo.localType = Pose2D.class;
            adapterInfo.networkEncoding = Serialization.DataEncoding.BINARY;
            return true;
        }
        if (remoteType.getName().equals(STANDARD_3D_POSE_TYPE_NAME) || remoteType.getName().equals(LEGACY_3D_POSE_TYPE_NAME) || remoteType.getName().equals(TWIST_3D_TYPE_NAME) || remoteType.getName().equals(UNCERTAIN_TWIST_3D_TYPE_NAME)) {
            adapterInfo.localType = Pose3D.class;
            adapterInfo.networkEncoding = Serialization.DataEncoding.BINARY;
            return true;
        }
        return false;
    }

    @Override
    public void deserialize(BinaryInputStream stream, GenericObject object, RemoteType type, Info adapterInfo) throws Exception {
        object.deserialize(stream, Serialization.DataEncoding.BINARY);
    }

    @Override
    public void serialize(BinaryOutputStream stream, GenericObject object, RemoteType type, Info adapterInfo) {
        object.serialize(stream, Serialization.DataEncoding.BINARY);
    }
}
