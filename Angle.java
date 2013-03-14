/**
 * You received this file as part of an advanced experimental
 * robotics framework prototype ('finroc')
 *
 * Copyright (C) 2012 Max Reichardt,
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

import org.finroc.core.datatype.CoreNumber;
import org.finroc.core.datatype.Unit;
import org.finroc.core.portdatabase.FinrocTypeInfo;
import org.rrlib.finroc_core_utils.rtti.Copyable;
import org.rrlib.finroc_core_utils.rtti.DataType;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.NumericRepresentation;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;
import org.rrlib.finroc_core_utils.serialization.StringInputStream;
import org.rrlib.finroc_core_utils.serialization.StringOutputStream;

/**
 * @author Max Reichardt
 *
 * rrlib::math::tAngleRad Java equivalent
 */
public class Angle extends RRLibSerializableImpl implements Copyable<Angle>, NumericRepresentation {

    /** Data Type */
    public final static DataType<Angle> TYPE = new DataType<Angle>(Angle.class);

    static {
        FinrocTypeInfo.get(TYPE).init(FinrocTypeInfo.Type.CC);
    }

    /** Angle as signed rad */
    public double unsignedRad;

    @Override
    public void serialize(OutputStreamBuffer os) {
        os.writeDouble(unsignedRad);
    }

    @Override
    public void deserialize(InputStreamBuffer is) {
        unsignedRad = is.readDouble();
    }

    @Override
    public void serialize(StringOutputStream os) {
        os.append(toString());
    }

    @Override
    public void deserialize(StringInputStream is) throws Exception {
        CoreNumber cn = new CoreNumber();
        cn.deserialize(is);
        Unit unit = (cn.getUnit() == Unit.NO_UNIT) ? Unit.deg : cn.getUnit();
        unsignedRad = unit.convertTo(cn.doubleValue(), Unit.rad);
        while (unsignedRad < 0) {
            unsignedRad += 2 * Math.PI;
        }
        while (unsignedRad > 2 * Math.PI) {
            unsignedRad -= 2 * Math.PI;
        }
    }

    @Override
    public void copyFrom(Angle o) {
        unsignedRad = o.unsignedRad;
    }

    public double getSignedRad() {
        return unsignedRad > Math.PI ? (unsignedRad - 2 * Math.PI) : unsignedRad;
    }

    public double getUnsignedRad() {
        return unsignedRad;
    }

    public double getSignedDeg() {
        return (getSignedRad() / Math.PI) * 180;
    }

    public double getUnsignedDeg() {
        return (getUnsignedRad() / Math.PI) * 180;
    }

    public String toString() {
        CoreNumber cn = new CoreNumber(getSignedDeg(), Unit.deg);
        return cn.toString();
    }

    @Override
    public Number getNumericRepresentation() {
        return getSignedDeg();
    }

    public void setDeg(double newAngle) {
        setRad((newAngle / 180.0) * Math.PI);
    }

    // TODO implement normalization properly
    public void setRad(double newAngle) {
        unsignedRad = newAngle;
        while (unsignedRad < 0) {
            unsignedRad += 2 * Math.PI;
        }
        while (unsignedRad > 2 * Math.PI) {
            unsignedRad -= 2 * Math.PI;
        }
    }
}
