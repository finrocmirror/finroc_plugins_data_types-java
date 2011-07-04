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
import org.rrlib.finroc_core_utils.serialization.DataType;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializable;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;


/**
 * @author max
 *
 * All objects that are representable as a function
 */
@JavaOnly
public interface HasFunctionRepresentation extends RRLibSerializable {

    public final static DataType<HasFunctionRepresentation> TYPE = new DataType<HasFunctionRepresentation>(HasFunctionRepresentation.class);

    /** Function representation of object */
    public Function asFunction();

    /**
     * Empty Function
     */
    @JavaOnly
    public class Empty extends RRLibSerializableImpl implements HasFunctionRepresentation {

        public final static DataType<Empty> TYPE = new DataType<Empty>(Empty.class, "HasEmptyFunction");

        @Override
        public Function asFunction() {
            return Function.Empty.instance;
        }

        @Override
        public void serialize(OutputStreamBuffer os) {
        }

        @Override
        public void deserialize(InputStreamBuffer is) {
        }
    }
}
