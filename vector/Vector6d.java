//
// You received this file as part of Finroc
// A Framework for intelligent robot control
//
// Copyright (C) Finroc GbR (finroc.org)
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
//----------------------------------------------------------------------
package org.finroc.plugins.data_types.vector;

import org.rrlib.finroc_core_utils.rtti.DataType;

/**
 * @author Max Reichardt
 *
 * Java equivalent to tVec6i
 */
public class Vector6d extends Vector3d {

    public final static DataType<Vector6d> TYPE = new DataType<Vector6d>(Vector6d.class);

    public Vector6d() {
        super(6);
    }
}
