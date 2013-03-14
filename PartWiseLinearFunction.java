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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;

import org.finroc.core.datatype.Unit;


/**
 * @author Max Reichardt
 *
 * Efficient implementation of a part-wise linear function.
 *
 * Writing to this class is not thread-safe.
 * Reading is not thread-safe during writes.
 */
public class PartWiseLinearFunction extends RRLibSerializableImpl implements Function {

    /** list with nodes... invariant: there is only one node with the same x value */
    protected final List<Node> nodes;

    /** list with nodes for reuse */
    protected final List<Node> reuseBuffer;

    /** temporary node - one for each accessing thread */
    protected final ThreadLocal<Node> tempNode = new ThreadLocal<Node>();

    /** units */
    protected Unit xUnit = Unit.NO_UNIT, yUnit = Unit.NO_UNIT;

    public PartWiseLinearFunction() {
        nodes = new ArrayList<Node>();
        reuseBuffer = new ArrayList<Node>();
    }

    /**
     * @param initialNodeCount initial number of nodes
     */
    public PartWiseLinearFunction(int initialNodeCount) {
        nodes = new ArrayList<Node>(initialNodeCount);
        reuseBuffer = new ArrayList<Node>(initialNodeCount);
    }

    public double getMaxX() {
        if (nodes.size() == 0) {
            return Double.NaN;
        }
        return nodes.get(nodes.size() - 1).x;
    }

    public double getMinX() {
        if (nodes.size() == 0) {
            return Double.NaN;
        }
        return nodes.get(0).x;
    }

    public Node getTempNode(double xVal) {
        Node n = tempNode.get();
        if (n == null) {
            n = new Node(xVal, 0);
            tempNode.set(n);
        } else {
            n.x = xVal;
        }
        return n;
    }

    public double getY(double x) {

        // should be fairly efficient... therefore use binary search for upper and lower bound
        int index = Collections.binarySearch(nodes, getTempNode(x));
        if (index >= 0) {
            return nodes.get(index).y;
        }

        int insertionpoint = -(index + 1);

        // index outside ? abort
        if (insertionpoint == 0 || insertionpoint >= nodes.size()) {
            return Double.NaN;
        }

        // index lies between two nodes... use linear interpolation
        Node lower = nodes.get(insertionpoint - 1);
        Node upper = nodes.get(insertionpoint);
        double alpha = (x - lower.x) / (upper.x - lower.x);

        return (1 - alpha) * lower.y + alpha * upper.y;
    }

    public void addEntry(double x, double y) {
        Node n = reuseBuffer.size() == 0 ? new Node(x, y) : reuseBuffer.remove(0).set(x, y);
        int index = Collections.binarySearch(nodes, n);
        if (index >= 0) {  // replace node with same x value
            nodes.set(index, n);
            return;
        }

        int insertionpoint = -(index + 1);
        nodes.add(insertionpoint, n);
    }

    public void reset() {
        reuseBuffer.addAll(nodes);
        nodes.clear();
    }

    /**
     * @author Max Reichardt
     *
     * One Point/Node of linear function
     */
    public static class Node implements Comparable<Node>, Serializable {

        /** UID */
        private static final long serialVersionUID = -4263342627261153428L;

        public double x, y;

        public Node(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Node set(double x2, double y2) {
            x = x2;
            y = y2;
            return this;
        }

        public int compareTo(Node n) {
            return Double.compare(x, n.x);
        }
    }

    public int getInsertIndex(double d) {
        // should we fairly efficient... therefore use binary search for upper and lower bound
        int index = Collections.binarySearch(nodes, getTempNode(d));
        if (index >= 0) {
            return index;
        }

        return -(index + 1);
    }

    public static void main(String[] args) {
        // test
        PartWiseLinearFunction pwlf = new PartWiseLinearFunction();
        pwlf.addEntry(0, 1);
        pwlf.addEntry(3, 6);
        pwlf.addEntry(1, 6);
        pwlf.addEntry(1, 2);
        for (double d = 0; d <= 4; d += 0.5) {
            System.out.println(pwlf.getY(d));
        }
    }

    @Override
    public Function asFunction() {
        return this;
    }

    /**
     * @return the xUnit
     */
    public Unit getXUnit() {
        return xUnit;
    }

    /**
     * @param unit the xUnit to set
     */
    public void setXUnit(Unit unit) {
        xUnit = unit;
    }

    /**
     * @return the yUnit
     */
    public Unit getYUnit() {
        return yUnit;
    }

    /**
     * @param unit the yUnit to set
     */
    public void setYUnit(Unit unit) {
        yUnit = unit;
    }

    @Override
    public void deserialize(InputStreamBuffer is) {
        throw new RuntimeException("Currently unsupported");
    }

    @Override
    public void serialize(OutputStreamBuffer os) {
        throw new RuntimeException("Currently unsupported");
    }
}
