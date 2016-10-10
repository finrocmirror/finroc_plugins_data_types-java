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
 * Adapts rrlib::math::tAngle template types to Angle type
 */
public class AngleTypeAdapter extends RemoteTypeAdapter {

    public static final AngleTypeAdapter INSTANCE = new AngleTypeAdapter();

    private AngleTypeAdapter() {
        super(10);
    }

    @Override
    public boolean handlesType(RemoteType remoteType, Info adapterInfo) {
        if (remoteType.getName().startsWith("rrlib.math.Angle<double,") && remoteType.getName().endsWith(">")) {
            adapterInfo.localType = Angle.class;
            adapterInfo.networkEncoding = Serialization.DataEncoding.BINARY;
            adapterInfo.customAdapterData1 = remoteType.getName().contains("rrlib.math.angle.Degree,");
            adapterInfo.customAdapterData2 = remoteType.getName().contains("rrlib.math.angle.Unsigned");
            return true;
        }
        return false;
    }

    @Override
    public void deserialize(BinaryInputStream stream, GenericObject object, RemoteType type, Info adapterInfo) throws Exception {
        double value = stream.readDouble();
        if (((Boolean)adapterInfo.customAdapterData1).booleanValue()) {
            ((Angle)object.getData()).setDeg(value);
        } else {
            ((Angle)object.getData()).setRad(value);
        }
    }

    @Override
    public void serialize(BinaryOutputStream stream, GenericObject object, RemoteType type, Info adapterInfo) {
        boolean deg = ((Boolean)adapterInfo.customAdapterData1).booleanValue();
        boolean unsigned = ((Boolean)adapterInfo.customAdapterData2).booleanValue();
        Angle a = (Angle)object.getData();
        double value = deg ? (unsigned ? a.getUnsignedDeg() : a.getSignedDeg()) : (unsigned ? a.getUnsignedRad() : a.getSignedRad());
        stream.writeDouble(value);
    }
}
