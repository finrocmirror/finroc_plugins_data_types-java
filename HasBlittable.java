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
import org.rrlib.finroc_core_utils.serialization.RRLibSerializable;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;

/**
 * @author max
 *
 * Data Type that in some way can be rendered to an image buffer.
 */
public interface HasBlittable extends RRLibSerializable {

    public final static DataType<HasBlittable> TYPE = new DataType<HasBlittable>(HasBlittable.class);

    /**
     * @return number of blittable objects in this objects (Image lists may have multiple)
     */
    public int getNumberOfBlittables();

    /**
     * @param Index of blittable
     * @return Blittable object (null if index is out of bounds);
     */
    public Blittable getBlittable(int index);

    /**
     * Empty Blittable
     */
    public class Empty extends RRLibSerializableImpl implements HasBlittable {

        public final static DataType<Empty> TYPE = new DataType<Empty>(Empty.class, "DummyBlittable");

        @Override
        public Blittable getBlittable(int index) {
            return org.finroc.plugins.data_types.Blittable.Empty.instance;
        }

        @Override
        public void serialize(OutputStreamBuffer os) {
        }

        @Override
        public void deserialize(InputStreamBuffer is) {
        }

        @Override
        public int getNumberOfBlittables() {
            return 0;
        }
    }
}
