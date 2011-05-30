/**
 * You received this file as part of an advanced experimental
 * robotics framework prototype ('finroc')
 *
 * Copyright (C) 2011 Max Reichardt,
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

/**
 * @author max
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
