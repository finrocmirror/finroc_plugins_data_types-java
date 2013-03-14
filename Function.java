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

import org.rrlib.finroc_core_utils.rtti.DataType;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;
import org.finroc.core.datatype.Unit;

/**
 * @author Max Reichardt
 *
 * This is an interface for any function double=>double
 */
public interface Function extends HasFunctionRepresentation {

    public final static DataType<Function> TYPE = new DataType<Function>(Function.class);

    /**
     * @return Lower bound of domain
     */
    public double getMinX();

    /**
     * @return Upper bound of domain
     */
    public double getMaxX();

    /**
     * @param x X value to return Y value for
     * @return Y value
     */
    public double getY(double x);

    /**
     * @return Unit of x value
     */
    public Unit getXUnit();

    /**
     * @return Unit of y value
     */
    public Unit getYUnit();

    /**
     * Empty Function
     */
    public class Empty extends RRLibSerializableImpl implements Function {

        public final static DataType<Empty> TYPE = new DataType<Empty>(Empty.class, "EmptyFunction");

        @Override
        public double getMaxX() {
            return Double.NaN;
        }

        @Override
        public double getMinX() {
            return Double.NaN;
        }

        @Override
        public double getY(double x) {
            return Double.NaN;
        }

        public static final Empty instance = new Empty();

        @Override
        public Function asFunction() {
            return instance;
        }

        @Override
        public Unit getXUnit() {
            return Unit.NO_UNIT;
        }

        @Override
        public Unit getYUnit() {
            return Unit.NO_UNIT;
        }

        @Override
        public void serialize(OutputStreamBuffer os) {
        }

        @Override
        public void deserialize(InputStreamBuffer is) {
        }
    }
}
