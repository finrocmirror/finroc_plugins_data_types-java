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
package org.finroc.plugins.data_types.mca;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import org.finroc.core.datatype.Unit;
import org.rrlib.finroc_core_utils.log.LogLevel;
import org.finroc.plugins.blackboard.BlackboardPlugin;
import org.finroc.plugins.data_types.DataTypePlugin;
import org.finroc.plugins.data_types.Dimension;
import org.finroc.plugins.data_types.Paintable;
import org.finroc.plugins.data_types.PaintablePortData;
import org.finroc.plugins.data_types.PointList;
import org.finroc.plugins.data_types.Pose3D;
import org.finroc.plugins.data_types.Time;
import org.finroc.plugins.data_types.util.BoundsExtractingGraphics2D;
import org.rrlib.finroc_core_utils.rtti.DataType;
import org.rrlib.finroc_core_utils.rtti.DataTypeBase;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.MemoryBuffer;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.PortDataListImpl;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;

/**
 * @author max
 *
 * tDistanceData Java equivalent
 */
public class DistanceData extends RRLibSerializableImpl implements PaintablePortData, PointList {

    public static class DistanceDataList extends PortDataListImpl<DistanceData> implements Paintable, PointList {

        public DistanceDataList() {
            super(DistanceData.TYPE);
        }

        @Override
        public int getDimensionCount() {
            return size() > 0 ? get(0).getDimensionCount() : 0;
        }

        @Override
        public Dimension getDimension(int index) {
            return get(0).getDimension(index);
        }

        @Override
        public int getPointCount() {
            return size() > 0 ? get(0).getPointCount() : 0;
        }

        @Override
        public double getPointCoordinate(int pointIndex, int dimensionIndex) {
            return get(0).getPointCoordinate(pointIndex, dimensionIndex);
        }

        @Override
        public void getPoint(int pointIndex, double[] resultBuffer) {
            get(0).getPoint(pointIndex, resultBuffer);
        }

        @Override
        public void paint(Graphics2D g) {
            for (int i = 0; i < size(); i++) {
                get(i).paint(g);
            }
        }

        @Override
        public Rectangle2D getBounds() {
            return size() > 0 ? get(0).getBounds() : null;
        }
    }

    public final static DataType<DistanceData> TYPE = new DataType<DistanceData>(DistanceData.class, "DistanceData", false);
    public final static DataType<DistanceDataList> LIST_TYPE = new DataType<DistanceDataList>(DistanceDataList.class, "List<DistanceData>", false);
    public final static DataTypeBase BB_TYPE;

    static {
        TYPE.getInfo().listType = LIST_TYPE;
        LIST_TYPE.getInfo().elementType = TYPE;
        BB_TYPE = BlackboardPlugin.registerBlackboardType(TYPE);
    }

    /**
     * Distance format info from tDistanceData.h
     */
    static class FormatInfo {

        /** Stores the string representation of the distance data format. */
        final String name;

        /** Stores the number of values of this distance data format. */
        final int numberOfValues;

        /** Stores the number of bytes each value occupies. */
        final int numberOfBytesPerValue;

        /**
         * Indicates whether the distance data is stored in planar format.
         *
         * Planar in that context means that values are stored blockwise:
         * format      non-planar  planar\n
         * polar 2d  (alpha_1, d_1) (alpha_2, d_2) (alpha_3, d_3)... -> d_1 d_2 d_3... alpha_1 alpha_2 alpha_3...\n
         * cartesian 2d (x_1, y_1) (x_2, y_2) (x_3, y_3)... -> x_1 x_2 x_3... y_1 y_2 y_3...\n
         * \n\nNote that in the case of polar coordinates the distance values are stored
         * before the angles for compatibility reasons to distance only formats.
         * That way algorithms working on distance only data can access
         * polar planar data the same way. For 3d formats the memory layout is analogous.
         */
        final boolean isPlanar;

        /**
         * Stores the value type.
         *
         * This flag is handy for some checks.
         */
        final int valueType;

        public FormatInfo(String name, int numberOfValues, int numberOfBytesPerValue, boolean isPlanar, int valueType) {
            this.name = name;
            this.numberOfValues = numberOfValues;
            this.numberOfBytesPerValue = numberOfBytesPerValue;
            this.isPlanar = isPlanar;
            this.valueType = valueType;
        }

        /**
         * @return Returns the number of bytes which are needed for a single element of the
         * distance data type represented by this info object.
         */
        int CapacityPerElement() {
            return numberOfValues * numberOfBytesPerValue;
        }
    };

    /**
     * Utility class to access values of dimensions in data buffer
     */
    class DimensionImpl implements Dimension {

        private int offset, increment;

        private String name;

        public DimensionImpl(int dimIndex) {
            if (formatInfo.valueType == MCA.eVT_DISTANCE_ONLY) {
                assert(formatInfo.numberOfValues == 1 && dimIndex == 0);
                name = "distance";
                offset = 0;
                increment = formatInfo.numberOfBytesPerValue;
            } else if (formatInfo.valueType == MCA.eVT_CARTESIAN) {
                name = (dimIndex == 0) ? "x" : ((dimIndex == 1) ? "y" : "z");
                offset = dimIndex * formatInfo.numberOfBytesPerValue;
                increment = formatInfo.numberOfBytesPerValue * formatInfo.numberOfValues;
            } else if (formatInfo.valueType == MCA.eVT_POLAR) {
                if (formatInfo.numberOfValues == 2) {
                    name = (dimIndex == 0) ? "alpha" : "distance";
                } else {
                    name = (dimIndex == 0) ? "alpha" : ((dimIndex == 1) ? "beta" : "distance");
                }
                offset = dimIndex * formatInfo.numberOfBytesPerValue;
                increment = formatInfo.numberOfBytesPerValue * formatInfo.numberOfValues;
            }
        }

        @Override
        public Class<?> getValueType() {
            return double.class;
        }

        @Override
        public Unit getUnit() {
            return DistanceData.this.getUnit();
        }

        @Override
        public String getDimensionName() {
            return name;
        }

        public double getValue(int pointIndex) {
            int o = offset + pointIndex * increment;
            switch (formatInfo.numberOfBytesPerValue) {
            case 1:
                return (int)(data.getBuffer().getByte(o) & 0xFF);
            case 2:
                return (int)(data.getBuffer().getShort(o) & 0xFFFF);
            case 4:
                return data.getBuffer().getFloat(o);
            case 8:
                return data.getBuffer().getDouble(o);
            }
            return Double.NaN;
        }
    }


    /** Variables from tDistanceData header */
    private byte format;
    private int capacity;
    private int dimension;
    private byte unit;
    private Pose3D sensorPose = new Pose3D();
    private Pose3D sensorPoseDelta = new Pose3D();
    private Pose3D robotPose = new Pose3D();
    private Time timestamp = new Time();
    private int extraDataSize;

    /** Helper variables derived from header data */
    private FormatInfo formatInfo = MCA.cDistanceDataFormatInfo[0];
    private DimensionImpl[] dimensions = null;
    private int pointDrawSize = 50;

    /** Data Buffer */
    private MemoryBuffer data = new MemoryBuffer();
    private MemoryBuffer extraData = new MemoryBuffer();

    @Override
    public void serialize(OutputStreamBuffer os) {
        os.writeByte(format);
        os.writeInt(capacity);
        os.writeInt(dimension);
        os.writeByte(unit);
        os.writeInt(extraDataSize);

        sensorPose.serialize(os);
        sensorPoseDelta.serialize(os);
        robotPose.serialize(os);
        timestamp.serialize(os);

        // Write data
        int size = dimension * formatInfo.numberOfBytesPerValue * formatInfo.numberOfValues;
        os.write(data.getBuffer(), 0, size);

        // Write extra data
        os.write(extraData.getBuffer(), 0, extraDataSize);
    }

    @Override
    public void deserialize(InputStreamBuffer is) {
        byte tmp = is.readByte();
        boolean formatChanged = tmp != format;
        format = tmp;
        capacity = is.readInt();
        dimension = is.readInt();
        unit = is.readByte();
        extraDataSize = is.readInt();

        sensorPose.deserialize(is);
        sensorPoseDelta.deserialize(is);
        robotPose.deserialize(is);
        timestamp.deserialize(is);

        // Read image data
        data.clear();
        formatInfo = MCA.cDistanceDataFormatInfo[format];
        int size = dimension * formatInfo.numberOfBytesPerValue * formatInfo.numberOfValues;
        data.deserialize(is, size);

        // Read extra data
        extraData.clear();
        extraData.deserialize(is, extraDataSize);

        // calculate internal variables
        if (formatChanged) {
            dimensions = new DimensionImpl[formatInfo.numberOfValues];
            for (int i = 0; i < dimensions.length; i++) {
                dimensions[i] = new DimensionImpl(i);
            }
        }
        pointDrawSize = (int)Unit.cm.convertTo(3, getUnit());
    }

    public Unit getUnit() {
        switch (unit) {
        case MCA.eDISTANCE_UNIT_MM:
            return Unit.mm;
        case MCA.eDISTANCE_UNIT_DM:
            return Unit.dm;
        case MCA.eDISTANCE_UNIT_CM:
            return Unit.cm;
        case MCA.eDISTANCE_UNIT_M:
            return Unit.m;
        }
        DataTypePlugin.logDomain.log(LogLevel.LL_WARNING, "DistanceData", "Invalid unit " + unit);
        return Unit.NO_UNIT;
    }

    @Override
    public int getDimensionCount() {
        return formatInfo.numberOfValues;
    }

    @Override
    public Dimension getDimension(int index) {
        return dimensions[index];
    }

    @Override
    public int getPointCount() {
        return dimension;
    }

    @Override
    public double getPointCoordinate(int pointIndex, int dimensionIndex) {
        assert(pointIndex >= 0 && pointIndex < dimension);
        return dimensions[dimensionIndex].getValue(pointIndex);
    }

    @Override
    public void getPoint(int pointIndex, double[] resultBuffer) {
        assert(resultBuffer.length >= getDimensionCount());
        for (int i = 0; i < getDimensionCount(); i++) {
            resultBuffer[i] = getPointCoordinate(pointIndex, i);
        }
    }

    @Override
    public void paint(Graphics2D g) {
        AffineTransform at = g.getTransform(); // backup current transformation
        robotPose.applyTransformation(g);
        sensorPose.applyTransformation(g);
        sensorPoseDelta.applyTransformation(g);
        if (formatInfo.valueType == MCA.eVT_DISTANCE_ONLY) {
            // assume scan is from -180° to +180°
            double minAngle = -Math.PI / 2;
            double maxAngle = Math.PI / 2;
            double increment = (maxAngle - minAngle) / (dimension - 1);
            double angle = minAngle;
            for (int i = 0; i < dimension; i++) {
                double distance = getPointCoordinate(i, 0);
                double x = Math.cos(angle) * distance;
                double y = Math.sin(angle) * distance;
                drawPoint(g, x, y);
                angle += increment;
            }
        } else if (formatInfo.valueType == MCA.eVT_CARTESIAN) {
            for (int i = 0; i < dimension; i++) {
                drawPoint(g, getPointCoordinate(i, 0), getPointCoordinate(i, 1));
            }
        } else if (formatInfo.valueType == MCA.eVT_POLAR) {
            for (int i = 0; i < dimension; i++) {
                double angle = getPointCoordinate(i, 0);
                double distance = getPointCoordinate(i, formatInfo.numberOfValues - 1);
                double x = Math.cos(angle) * distance;
                double y = Math.sin(angle) * distance;
                drawPoint(g, x, y);
            }
        }
        g.setTransform(at);
    }

    private void drawPoint(Graphics2D g, double x, double y) {
        //g.setColor(Color.BLACK);
        g.fillRect((int)x, (int)y, pointDrawSize, pointDrawSize);
    }

    @Override
    public Rectangle2D getBounds() {
        return BoundsExtractingGraphics2D.getBounds(this);
    }
}

