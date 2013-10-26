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

/**
 * @author Max Reichardt
 *
 * List of points (in arbitrary space).
 */
public interface PointList {

    /**
     * @return Number of dimensions of space/points
     */
    public int getDimensionCount();

    /**
     * @param index Dimension index (must be < getDimensionCount())
     * @return Dimension with specified index
     */
    public Dimension getDimension(int index);

    /**
     * @return Number of points
     */
    public int getPointCount();

    /**
     * @param pointIndex Index (must be < getPointCount()) of point of interest
     * @param dimensionIndex Index (must be < getDimensionCount()) of interest
     * @return Coordinate
     */
    public double getPointCoordinate(int pointIndex, int dimensionIndex);

    /**
     * @param pointIndex Index (must be < getPointCount()) of point of interest
     * @param resultBuffer Buffer to write point coordinates to
     */
    public void getPoint(int pointIndex, double[] resultBuffer);
}
