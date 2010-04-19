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
package org.finroc.plugin.datatype;

import org.finroc.jc.annotation.JavaOnly;
import org.finroc.core.buffer.CoreInput;
import org.finroc.core.buffer.CoreOutput;
import org.finroc.core.datatype.Unit;
import org.finroc.core.port.std.PortDataImpl;
import org.finroc.core.portdatabase.DataType;
import org.finroc.core.portdatabase.DataTypeRegister;

/**
 * @author max
 *
 * This is an interface for any function double=>double
 */
@JavaOnly
public interface Function extends HasFunctionRepresentation {

    static DataType TYPE = DataTypeRegister.getInstance().getDataType(Function.class);

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
    public class Empty extends PortDataImpl implements Function {

        static DataType TYPE = DataTypeRegister.getInstance().getDataType(Empty.class, "EmptyFunction");

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
        public void deserialize(CoreInput is) {
        }

        @Override
        public void serialize(CoreOutput os) {
        }
    }
}
