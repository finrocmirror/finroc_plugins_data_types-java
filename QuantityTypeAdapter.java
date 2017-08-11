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

import org.finroc.core.datatype.CoreNumber;
import org.finroc.core.datatype.SIUnit;
import org.finroc.core.datatype.Unit;
import org.finroc.core.remote.RemoteType;
import org.finroc.core.remote.RemoteTypeAdapter;
import org.rrlib.logging.Log;
import org.rrlib.logging.LogLevel;
import org.rrlib.serialization.BinaryInputStream;
import org.rrlib.serialization.BinaryOutputStream;
import org.rrlib.serialization.Serialization;
import org.rrlib.serialization.rtti.GenericObject;

/**
 * @author Max Reichardt
 *
 * Adapts rrlib::si_units::tQuantity template types to CoreNumbers
 */
public class QuantityTypeAdapter extends RemoteTypeAdapter {

    public static final QuantityTypeAdapter INSTANCE = new QuantityTypeAdapter();

    private QuantityTypeAdapter() {
        super(10);
    }

    private String[] LOOKUP = {
        "Length", "m",
        "Mass", "kg",
        "Time", "s",
        "ElectricCurrent", "A",
        "Temperature", "K",
        "AmountOfSubstance", "mol",
        "LuminousIntensity", "cd",
        "Frequency", "Hz",
        "Force", "N",
        "Pressure", "Pa",
        "Velocity", "m/s",
        "Acceleration", "m/s^2",
        "AngularVelocity", "rad/s",
        "AngularAcceleration", "rad/s^2"
    };

    @Override
    public boolean handlesType(RemoteType remoteType, Info adapterInfo) {
        String typeName = remoteType.getName();
        boolean siUnitNamespace = remoteType.getName().startsWith("rrlib.si_units.");
        if (siUnitNamespace) {
            typeName = typeName.substring("rrlib.si_units.".length());
        }
        if (typeName.startsWith("Quantity<") && typeName.endsWith(">")) {
            String[] arguments = typeName.substring("Quantity<".length(), typeName.length() - 1).split(",");
            if (arguments.length == 2) {
                try {
                    adapterInfo.customAdapterData1 = SIUnit.getInstance(arguments[0]);
                    adapterInfo.customAdapterData2 = (arguments[1].trim().equalsIgnoreCase("float")) ? Float.class : Double.class;
                    adapterInfo.localType = CoreNumber.TYPE;
                    adapterInfo.networkEncoding = Serialization.DataEncoding.BINARY;
                    if (arguments[1].trim().equalsIgnoreCase("angle") && adapterInfo.customAdapterData1 == SIUnit.HERTZ) {
                        adapterInfo.customAdapterData1 = SIUnit.RAD_PER_SECOND;
                    }
                    return true;
                } catch (Exception e) {
                    Log.log(LogLevel.WARNING, "Cannot find suitable SI unit for string '" + arguments[0] + "': ", e);
                }
            } else if (remoteType.getName().endsWith("rrlib.math.Angle<double, rrlib.math.angle.Radian, rrlib.math.angle.NoWrap>>")) { // TODO: this hard-coded string is a workaround and not really nice
                adapterInfo.customAdapterData1 = SIUnit.RAD_PER_SECOND;
                adapterInfo.customAdapterData2 = Double.class;
                adapterInfo.localType = CoreNumber.TYPE;
                adapterInfo.networkEncoding = Serialization.DataEncoding.BINARY;
                return true;
            }
        }
        if (siUnitNamespace) {
            for (int i = 0; i < LOOKUP.length; i += 2) {
                if (LOOKUP[i].equals(typeName)) {
                    try {
                        adapterInfo.customAdapterData1 = SIUnit.getInstance(LOOKUP[i + 1]);
                        adapterInfo.customAdapterData2 = Double.class;
                        adapterInfo.localType = CoreNumber.TYPE;
                        adapterInfo.networkEncoding = Serialization.DataEncoding.BINARY;
                        return true;
                    } catch (Exception e) {
                        Log.log(LogLevel.WARNING, "Cannot find suitable SI unit for predefined string '" + LOOKUP[i + 1] + "': ", e);
                    }
                }
            }
            Log.log(LogLevel.WARNING, "Cannot find suitable SI unit for '" + remoteType.getName() + "': ");
        }
        return false;
    }

    @Override
    public void deserialize(BinaryInputStream stream, GenericObject object, RemoteType type, Info adapterInfo) throws Exception {
        ((CoreNumber)object.getData()).setValue(adapterInfo.customAdapterData2 == Float.class ? stream.readFloat() : stream.readDouble(), (SIUnit)adapterInfo.customAdapterData1);
    }

    @Override
    public void serialize(BinaryOutputStream stream, GenericObject object, RemoteType type, Info adapterInfo) {
        SIUnit targetUnit = (SIUnit)adapterInfo.customAdapterData1;
        double value = ((CoreNumber)object.getData()).doubleValue();
        Unit sourceUnit = ((CoreNumber)object.getData()).getUnit();
        if (sourceUnit != null && sourceUnit != targetUnit) {
            if (sourceUnit.convertibleTo(targetUnit)) {
                value = sourceUnit.convertTo(value, targetUnit);
            } else {
                Log.log(LogLevel.WARNING, "Cannot convert unit " + sourceUnit.toString() + " to target unit " + targetUnit.toString() + ". Forwarding unmodified value.");
            }
        }
        if (adapterInfo.customAdapterData2 == Float.class) {
            stream.writeFloat((float)value);
        } else {
            stream.writeDouble(value);
        }
    }
}
