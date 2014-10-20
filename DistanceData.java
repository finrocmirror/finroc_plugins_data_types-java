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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;

import org.finroc.core.datatype.SIUnit;
import org.finroc.core.datatype.Unit;
import org.finroc.plugins.blackboard.BlackboardPlugin;
import org.finroc.plugins.data_types.Canvas;
import org.finroc.plugins.data_types.Dimension;
import org.finroc.plugins.data_types.Paintable;
import org.finroc.plugins.data_types.PaintablePortData;
import org.finroc.plugins.data_types.PointList;
import org.finroc.plugins.data_types.Pose3D;
import org.finroc.plugins.data_types.Time;
import org.finroc.plugins.data_types.util.BoundsExtractingGraphics2D;
import org.finroc.plugins.data_types.util.FastBufferedImage;
import org.rrlib.logging.Log;
import org.rrlib.logging.LogLevel;
import org.rrlib.serialization.BinaryInputStream;
import org.rrlib.serialization.BinaryOutputStream;
import org.rrlib.serialization.MemoryBuffer;
import org.rrlib.serialization.PortDataListImpl;
import org.rrlib.serialization.rtti.DataType;
import org.rrlib.serialization.rtti.DataTypeBase;

/**
 * @author Max Reichardt
 *
 * tDistanceData Java equivalent
 */
public class DistanceData implements PaintablePortData, PointList {

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
        public void paint(Graphics2D g, FastBufferedImage imageBuffer) {
            for (int i = 0; i < size(); i++) {
                get(i).paint(g, imageBuffer);
            }
        }

        @Override
        public Rectangle2D getBounds() {
            return size() > 0 ? get(0).getBounds() : null;
        }

        @Override
        public boolean isYAxisPointingDownwards() {
            return false;
        }
    }

    public final static DataType<DistanceData> TYPE = new DataType<DistanceData>(DistanceData.class, DistanceDataList.class, "DistanceData");
    public final static DataTypeBase BB_TYPE = BlackboardPlugin.registerBlackboardType(TYPE);

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

    /** tValueType */
    public static final int
    eVT_POLAR = 0,
    eVT_CARTESIAN = 1,
    eVT_DISTANCE_ONLY = 2,
    eVT_DIMENSION = 3;

    /** tDistanceUnit */
    public static final int
    eDISTANCE_UNIT_MM = 0,
    eDISTANCE_UNIT_CM = 1,
    eDISTANCE_UNIT_DM = 2,
    eDISTANCE_UNIT_M = 3,
    eDISTANCE_UNIT_DIMENSION = 4;

    /** cDistanceDataFormatInfo */
    public static final DistanceData.FormatInfo[] cDistanceDataFormatInfo = new DistanceData.FormatInfo[] {
        new DistanceData.FormatInfo("eDF_POLAR_2D_FLOAT", 2, 4, false, eVT_POLAR),
        new DistanceData.FormatInfo("eDF_POLAR_3D_FLOAT", 3, 4, false, eVT_POLAR),
        new DistanceData.FormatInfo("eDF_CARTESIAN_2D_FLOAT", 2, 4, false, eVT_CARTESIAN),
        new DistanceData.FormatInfo("eDF_CARTESIAN_3D_FLOAT", 3, 4, false, eVT_CARTESIAN),
        new DistanceData.FormatInfo("eDF_POLAR_2D_DOUBLE", 2, 8, false, eVT_POLAR),
        new DistanceData.FormatInfo("eDF_POLAR_3D_DOUBLE", 3, 8, false, eVT_POLAR),
        new DistanceData.FormatInfo("eDF_CARTESIAN_2D_DOUBLE", 2, 8, false, eVT_CARTESIAN),
        new DistanceData.FormatInfo("eDF_CARTESIAN_3D_DOUBLE", 3, 8, false, eVT_CARTESIAN),
        new DistanceData.FormatInfo("eDF_POLAR_REMISSION_2D_FLOAT", 2, 4, false, eVT_POLAR),
        new DistanceData.FormatInfo("eDF_POLAR_REMISSION_2D_DOUBLE", 2, 8, false, eVT_POLAR),
        new DistanceData.FormatInfo("eDF_CARTESIAN_REMISSION_2D_FLOAT", 2, 4, false, eVT_CARTESIAN),
        new DistanceData.FormatInfo("eDF_CARTESIAN_REMISSION_2D_DOUBLE", 2, 8, false, eVT_CARTESIAN),
        new DistanceData.FormatInfo("eDF_POLAR_2D_FLOAT_PLANAR", 2, 4, true, eVT_POLAR),
        new DistanceData.FormatInfo("eDF_POLAR_3D_FLOAT_PLANAR", 3, 4, true, eVT_POLAR),
        new DistanceData.FormatInfo("eDF_CARTESIAN_2D_FLOAT_PLANAR", 2, 4, true, eVT_CARTESIAN),
        new DistanceData.FormatInfo("eDF_CARTESIAN_3D_FLOAT_PLANAR", 3, 4, true, eVT_CARTESIAN),
        new DistanceData.FormatInfo("eDF_DISTANCE_ONLY_FLOAT_PLANAR", 1, 4, true, eVT_DISTANCE_ONLY),
        new DistanceData.FormatInfo("eDF_POLAR_2D_DOUBLE_PLANAR", 2, 8, true, eVT_POLAR),
        new DistanceData.FormatInfo("eDF_POLAR_3D_DOUBLE_PLANAR", 3, 8, true, eVT_POLAR),
        new DistanceData.FormatInfo("eDF_CARTESIAN_2D_DOUBLE_PLANAR", 2, 8, true, eVT_CARTESIAN),
        new DistanceData.FormatInfo("eDF_CARTESIAN_3D_DOUBLE_PLANAR", 3, 8, true, eVT_CARTESIAN),
        new DistanceData.FormatInfo("eDF_DISTANCE_ONLY_DOUBLE_PLANAR", 1, 8, true, eVT_DISTANCE_ONLY),
        new DistanceData.FormatInfo("eDF_DISTANCE_ONLY_UNSIGNED16_PLANAR", 1, 2, true, eVT_DISTANCE_ONLY),
        new DistanceData.FormatInfo("eDF_REMISSION_ONLY_UNSIGNED16_PLANAR", 1, 2, true, eVT_DISTANCE_ONLY),
        new DistanceData.FormatInfo("eDF_REMISSION_ONLY_UNSIGNED8_PLANAR", 1, 1, true, eVT_DISTANCE_ONLY),
        new DistanceData.FormatInfo("eDF_INVALID", 0, 0, false, eVT_DIMENSION)
    };


    /**
     * Utility class to access values of dimensions in data buffer
     */
    class DimensionImpl implements Dimension {

        private int offset, increment;

        private String name;

        public DimensionImpl(int dimIndex) {
            if (formatInfo.valueType == eVT_DISTANCE_ONLY) {
                assert(formatInfo.numberOfValues == 1 && dimIndex == 0);
                name = "distance";
                offset = 0;
                increment = formatInfo.numberOfBytesPerValue;
            } else if (formatInfo.valueType == eVT_CARTESIAN) {
                name = (dimIndex == 0) ? "x" : ((dimIndex == 1) ? "y" : "z");
                offset = dimIndex * formatInfo.numberOfBytesPerValue;
                increment = formatInfo.numberOfBytesPerValue * formatInfo.numberOfValues;
            } else if (formatInfo.valueType == eVT_POLAR) {
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
    private FormatInfo formatInfo = cDistanceDataFormatInfo[0];
    private DimensionImpl[] dimensions = null;

    /** Data Buffer */
    private MemoryBuffer data = new MemoryBuffer(false);
    private MemoryBuffer extraData = new MemoryBuffer(false);

    /**
     * Cartesian points (updated by calculateCartesianPoints() when needed for drawing)
     *
     * Index 0: X coordinate of point 0
     * Index 1: Y coordinate of point 0
     * Index 2: Z coordinate of point 0
     * Index 3: X coordinate of point 1
     * ...
     */
    private double[] cartesianPoints = new double[30];
    private boolean cartesianPointsValid = false;

    /**
     * When rendered to Graphics2D, the cartesian points are projected to a plane
     * This array defines which plane this is:
     * (0,1) XY
     * (0,2) XZ
     * (1,2) YZ
     *
     * (updated by calculateCartesianPoints() when needed for drawing)
     */
    private final int[] viewPlane2dDimensionIndices = new int[2];

    /**
     * Bounding box (updated by calculateCartesianPoints() when needed for drawing)
     * Index 0: minimum X
     * Index 1: maximum X
     * Index 2: minimum Y
     * ...
     */
    private final double[] bounds = new double[6];

    /** Zero pose */
    private static final Pose3D ZERO_POSE = new Pose3D();


    @Override
    public void serialize(BinaryOutputStream os) {
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
    public void deserialize(BinaryInputStream is) {
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
        formatInfo = cDistanceDataFormatInfo[format];
        int size = dimension * formatInfo.numberOfBytesPerValue * formatInfo.numberOfValues;
        data.deserialize(is, size);

        // Read extra data
        extraData.clear();
        extraData.deserialize(is, extraDataSize);

        // calculate internal variables
        if (formatChanged || dimensions == null) {
            dimensions = new DimensionImpl[formatInfo.numberOfValues];
            for (int i = 0; i < dimensions.length; i++) {
                dimensions[i] = new DimensionImpl(i);
            }
        }
        cartesianPointsValid = false;
    }

    public Unit getUnit() {
        switch (unit) {
        case eDISTANCE_UNIT_MM:
            return SIUnit.MILLIMETER;
        case eDISTANCE_UNIT_DM:
            return SIUnit.DECIMETER;
        case eDISTANCE_UNIT_CM:
            return SIUnit.CENTIMETER;
        case eDISTANCE_UNIT_M:
            return SIUnit.METER;
        }
        Log.log(LogLevel.WARNING, this, "Invalid unit " + unit);
        return null;
    }

    public int getValueType() {
        return formatInfo.valueType;
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

    public int getExtraDataSize() {
        return extraDataSize;
    }

    public MemoryBuffer getExtraData() {
        return extraData;
    }

    private synchronized void calculateCartesianPoints() {
        if (!cartesianPointsValid) { // recalculate
            if (cartesianPoints.length < dimension * 3) {
                cartesianPoints = new double[dimension * 3 + 30];
            }

            int cartesianIndex = 0;

            if (formatInfo.valueType == eVT_DISTANCE_ONLY) {
                // assume scan is from -90° to +90°
                double minAngle = -Math.PI / 2;
                double maxAngle = Math.PI / 2;
                double increment = (maxAngle - minAngle) / (dimension - 1);
                double angle = minAngle;
                for (int i = 0; i < dimension; i++) {
                    double distance = getPointCoordinate(i, 0);
                    cartesianPoints[cartesianIndex] = Math.cos(angle) * distance;
                    cartesianPoints[cartesianIndex + 1] = Math.sin(angle) * distance;
                    cartesianPoints[cartesianIndex + 2] = 0;
                    angle += increment;
                    cartesianIndex += 3;
                }
            } else if (formatInfo.valueType == eVT_CARTESIAN) {
                for (int i = 0; i < dimension; i++) {
                    cartesianPoints[cartesianIndex] = getPointCoordinate(i, 0);
                    cartesianPoints[cartesianIndex + 1] = getPointCoordinate(i, 1);
                    cartesianPoints[cartesianIndex + 2] = formatInfo.numberOfValues < 3 ? 0 : getPointCoordinate(i, 2);
                    cartesianIndex += 3;
                }
            } else if (formatInfo.valueType == eVT_POLAR) {
                if (formatInfo.numberOfValues < 3) {
                    for (int i = 0; i < dimension; i++) {
                        double angle = getPointCoordinate(i, 0);
                        double distance = getPointCoordinate(i, 1);
                        double x = Math.cos(angle) * distance;
                        double y = Math.sin(angle) * distance;
                        cartesianPoints[cartesianIndex] = x;
                        cartesianPoints[cartesianIndex + 1] = y;
                        cartesianPoints[cartesianIndex + 2] = 0;
                        cartesianIndex += 3;
                    }
                } else {
                    for (int i = 0; i < dimension; i++) {
                        // TODO: This is not tested - as 3D Polar coordinates do not seem to be used in any components/projects
                        double polarAngle = getPointCoordinate(i, 0);
                        double azimuthalAngle = getPointCoordinate(i, 1);
                        double distance = getPointCoordinate(i, 2);

                        // Formulas from wikipedia
                        double x = Math.sin(azimuthalAngle) * Math.cos(polarAngle) * distance;
                        double y = Math.sin(azimuthalAngle) * Math.sin(polarAngle) * distance;
                        double z = Math.cos(azimuthalAngle) * distance;
                        cartesianPoints[cartesianIndex] = x;
                        cartesianPoints[cartesianIndex + 1] = y;
                        cartesianPoints[cartesianIndex + 2] = z;
                        cartesianIndex += 3;
                    }
                }
            }

            // Calculate bounds
            if (dimension == 0) {
                Arrays.fill(bounds, 0);
                viewPlane2dDimensionIndices[0] = 0;
                viewPlane2dDimensionIndices[1] = 1;
            } else {

                // Set BoundingBox to first point
                for (int i = 0; i < 3; i++) {
                    bounds[i * 2] = cartesianPoints[i];
                    bounds[i * 2 + 1] = cartesianPoints[i];
                }

                // Extend bounding box
                cartesianIndex = 3;
                for (int i = 1; i < dimension; i++, cartesianIndex += 3) {
                    for (int j = 0; j < 3; j++) {
                        bounds[j * 2] = Math.min(bounds[j * 2], cartesianPoints[cartesianIndex + j]);
                        bounds[j * 2 + 1] = Math.max(bounds[j * 2 + 1], cartesianPoints[cartesianIndex + j]);
                    }
                }

                // Decide on viewing plane
                double xDistance = bounds[1] - bounds[0];
                double yDistance = bounds[3] - bounds[2];
                double zDistance = bounds[5] - bounds[4];
                if (zDistance > xDistance * 10 && zDistance > yDistance * 10) {
                    viewPlane2dDimensionIndices[0] = xDistance >= yDistance ? 0 : 1;
                    viewPlane2dDimensionIndices[1] = 2;
                } else if (zDistance > xDistance * 10) {
                    viewPlane2dDimensionIndices[0] = 1;
                    viewPlane2dDimensionIndices[1] = 2;
                } else if (zDistance > yDistance * 10) {
                    viewPlane2dDimensionIndices[0] = 0;
                    viewPlane2dDimensionIndices[1] = 2;
                } else {
                    viewPlane2dDimensionIndices[0] = 0;
                    viewPlane2dDimensionIndices[1] = 1;
                }
            }

            cartesianPointsValid = true;
        }
    }


    @Override
    public void paint(Graphics2D g, FastBufferedImage imageBuffer) {
        if (dimension == 0) {
            return;
        }
        calculateCartesianPoints();

        if (imageBuffer != null) {

            boolean clipped = false;
            if (g.getClip() != null) {
                clipped = !g.getClip().contains(getBounds());
            }
            if (!clipped) {
                // very optimized rendering is possible
                final Point2D.Double source = new Point2D.Double();
                final Point2D.Double destination = new Point2D.Double();
                final AffineTransform transform = g.getTransform();
                final int xdim = viewPlane2dDimensionIndices[0];
                final int ydim = viewPlane2dDimensionIndices[1];
                final int color = g.getColor().getRGB();

                int index = 0;
                for (int i = 0; i < dimension; i++, index += 3) {
                    source.x = cartesianPoints[index + xdim];
                    source.y = cartesianPoints[index + ydim];
                    transform.transform(source, destination);
                    int x = (int)destination.x;
                    int y = (int)destination.y;
                    if (x >= 0 && x < imageBuffer.getWidth() && y >= 0 && y < imageBuffer.getHeight()) {
                        imageBuffer.setPixel((int)destination.x, (int)destination.y, color);
                    }
                }
                return;
            }
        }


        AffineTransform at = g.getTransform(); // backup current transformation
        Stroke oldStroke = g.getStroke();

        applyTransformation(g, robotPose);
        applyTransformation(g, sensorPose);
        applyTransformation(g, sensorPoseDelta);
        final double scalingFactor = Canvas.calculateScalingFactorsAndUpdateStrokeWidth(g).x;
        float strokeWidth = ((BasicStroke)g.getStroke()).getLineWidth();
        final boolean drawPrettyPoints = g.getRenderingHint(RenderingHints.KEY_RENDERING) == RenderingHints.VALUE_RENDER_QUALITY;
        g.setStroke(new BasicStroke(2 * strokeWidth));

        int index = 0;
        int xdim = viewPlane2dDimensionIndices[0];
        int ydim = viewPlane2dDimensionIndices[1];
        if (drawPrettyPoints) {
            Ellipse2D.Double ellipseObject = new Ellipse2D.Double();
            ellipseObject.width = 1 / scalingFactor;
            ellipseObject.height = 1 / scalingFactor;
            final double radius = 0.5 / scalingFactor;
            for (int i = 0; i < dimension; i++, index += 3) {
                drawPrettyPoint(g, cartesianPoints[index + xdim], cartesianPoints[index + ydim], radius, ellipseObject);
            }
        } else {
            Line2D.Double lineObject = new Line2D.Double();
            for (int i = 0; i < dimension; i++, index += 3) {
                drawPoint(g, cartesianPoints[index + xdim], cartesianPoints[index + ydim], lineObject);
            }
        }

        g.setTransform(at);
        g.setStroke(oldStroke);
    }

    /**
     * Applies transformation to graphics using the view plane set in viewPlane2dDimensionIndices
     * (note that only 2D transforms will be applied with XY plane (use case: display in 2D maps) -
     *  everything else would require full 3D calculations which is complicated and does not
     *  necessarily make the 2D view more helpful)
     *
     * @param g Graphics2D
     * @param pose Pose
     */
    private void applyTransformation(Graphics2D g, Pose3D pose) {
        int xdim = viewPlane2dDimensionIndices[0];
        int ydim = viewPlane2dDimensionIndices[1];

        if (xdim == 0 && ydim == 1 && pose.pitch == 0 && pose.roll == 0) {
            pose.applyTransformation(g);
        }
    }

    private void drawPoint(Graphics2D g, double x, double y, Line2D.Double lineObject) {
        lineObject.x1 = x;
        lineObject.x2 = x;
        lineObject.y1 = y;
        lineObject.y2 = y;
        g.draw(lineObject);
    }

    private void drawPrettyPoint(Graphics2D g, double x, double y, double radius, Ellipse2D.Double ellipseObject) {
        ellipseObject.x = x - radius;
        ellipseObject.y = y - radius;
        g.draw(ellipseObject);
    }

    @Override
    public Rectangle2D getBounds() {
        if (dimension == 0) {
            return null;
        }

        if (!cartesianPointsValid) {
            calculateCartesianPoints();
        }

        if (sensorPose.equals(ZERO_POSE) && robotPose.equals(ZERO_POSE) && sensorPoseDelta.equals(ZERO_POSE)) {
            return new Rectangle2D.Double(bounds[0], bounds[2], bounds[1] - bounds[0], bounds[3] - bounds[2]);
        } else {
            return BoundsExtractingGraphics2D.getBounds(this);
        }
    }

    @Override
    public boolean isYAxisPointingDownwards() {
        return false;
    }
}

