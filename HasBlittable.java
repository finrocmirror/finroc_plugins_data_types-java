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

import org.rrlib.serialization.BinaryInputStream;
import org.rrlib.serialization.BinaryOutputStream;
import org.rrlib.serialization.BinarySerializable;
import org.rrlib.serialization.rtti.DataType;

/**
 * @author Max Reichardt
 *
 * Data Type that in some way can be rendered to an image buffer.
 */
public interface HasBlittable extends BinarySerializable {

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
    public class Empty implements HasBlittable {

        public final static DataType<Empty> TYPE = new DataType<Empty>(Empty.class, "DummyBlittable");

        @Override
        public Blittable getBlittable(int index) {
            return org.finroc.plugins.data_types.Blittable.Empty.instance;
        }

        @Override
        public void serialize(BinaryOutputStream os) {
        }

        @Override
        public void deserialize(BinaryInputStream is) {
        }

        @Override
        public int getNumberOfBlittables() {
            return 0;
        }
    }
}
