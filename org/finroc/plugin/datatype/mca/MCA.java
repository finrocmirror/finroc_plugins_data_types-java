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
package org.finroc.plugin.datatype.mca;

import org.finroc.jc.annotation.JavaOnly;
import org.finroc.jc.stream.FixedBuffer;
import org.finroc.jc.jni.JNIInfo;
import org.finroc.jc.jni.Struct;
import org.finroc.jc.jni.StructBase;

@JavaOnly
public class MCA {

    public static class tBehaviourInfo extends Struct {
        public final static int sizeof32 = 16;
        public final static int sizeof64 = 16;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tBehaviourInfo() {}
        public tBehaviourInfo(FixedBuffer dbb) {
            super(dbb);
        }
        public tBehaviourInfo(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final Signed16S _beh_id = new Signed16S(0, 0);
        public final Signed16 beh_id = new Signed16(_beh_id);
        public static final Float32S _activity = new Float32S(2, 2);
        public final Float32 activity = new Float32(_activity);
        public static final Float32S _target_rating = new Float32S(6, 6);
        public final Float32 target_rating = new Float32(_target_rating);
        public static final Float32S _activation = new Float32S(10, 10);
        public final Float32 activation = new Float32(_activation);
        public static final Bool8S _auto_mode = new Bool8S(14, 14);
        public final Bool8 auto_mode = new Bool8(_auto_mode);
        public static final Bool8S _enabled = new Bool8S(15, 15);
        public final Bool8 enabled = new Bool8(_enabled);
    }

    public static class tDistanceData extends Struct {
        public final static int sizeof32 = 8;
        public final static int sizeof64 = 8;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tDistanceData() {}
        public tDistanceData(FixedBuffer dbb) {
            super(dbb);
        }
        public tDistanceData(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final PointerS _buffer = new PointerS(0, 0);
        public final Pointer buffer = new Pointer(_buffer);
        public static final PointerS _info = new PointerS(0, 0);
        public final Pointer info = new Pointer(_info);
    }

    public static class tDistanceDataFormatInfo extends Struct {
        public final static int sizeof32 = 20;
        public final static int sizeof64 = 20;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tDistanceDataFormatInfo() {}
        public tDistanceDataFormatInfo(FixedBuffer dbb) {
            super(dbb);
        }
        public tDistanceDataFormatInfo(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final PointerS _name = new PointerS(0, 0);
        public final Pointer name = new Pointer(_name);
        public static final Unsigned32S _number_of_values = new Unsigned32S(4, 4);
        public final Unsigned32 number_of_values = new Unsigned32(_number_of_values);
        public static final Unsigned32S _number_of_bytes_per_value = new Unsigned32S(8, 8);
        public final Unsigned32 number_of_bytes_per_value = new Unsigned32(_number_of_bytes_per_value);
        public static final Bool8S _is_planar = new Bool8S(12, 12);
        public final Bool8 is_planar = new Bool8(_is_planar);
        public static final Signed32S _value_type = new Signed32S(16, 16);
        public final Signed32 value_type = new Signed32(_value_type);
    }

    public static class tTime extends Struct {
        public final static int sizeof32 = 8;
        public final static int sizeof64 = 8;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tTime() {}
        public tTime(FixedBuffer dbb) {
            super(dbb);
        }
        public tTime(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final Signed32S _tv_sec = new Signed32S(0, 0);
        public final Signed32 tv_sec = new Signed32(_tv_sec);
        public static final Signed32S _tv_usec = new Signed32S(4, 4);
        public final Signed32 tv_usec = new Signed32(_tv_usec);
    }

    public static class tDistanceDataInfo extends Struct {
        public final static int sizeof32 = 172;
        public final static int sizeof64 = 172;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tDistanceDataInfo() {}
        public tDistanceDataInfo(FixedBuffer dbb) {
            super(dbb);
        }
        public tDistanceDataInfo(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final Signed32S _distance_data_format = new Signed32S(0, 0);
        public final Signed32 distance_data_format = new Signed32(_distance_data_format);
        public static final Unsigned32S _capacity = new Unsigned32S(4, 4);
        public final Unsigned32 capacity = new Unsigned32(_capacity);
        public static final Unsigned32S _dimension = new Unsigned32S(8, 8);
        public final Unsigned32 dimension = new Unsigned32(_dimension);
        public static final Signed32S _unit = new Signed32S(12, 12);
        public final Signed32 unit = new Signed32(_unit);
        public static final InnerStruct _sensor_pose = new InnerStruct(16, 16);
        public final tPose3D sensor_pose = new tPose3D(this, 16, 16);
        public static final InnerStruct _sensor_orientation_delta = new InnerStruct(64, 64);
        public static final InnerStruct _sensor_position_delta = new InnerStruct(88, 88);
        public static final InnerStruct _robot_pose = new InnerStruct(112, 112);
        public final tPose3D robot_pose = new tPose3D(this, 112, 112);
        public static final InnerStruct _timestamp = new InnerStruct(160, 160);
        public final tTime timestamp = new tTime(this, 160, 160);
        public static final Bool8S _buffer_owned = new Bool8S(168, 168);
        public final Bool8 buffer_owned = new Bool8(_buffer_owned);
    }

    public static class tPose3D extends Struct {
        public final static int sizeof32 = 48;
        public final static int sizeof64 = 48;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tPose3D() {}
        public tPose3D(FixedBuffer dbb) {
            super(dbb);
        }
        public tPose3D(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final Float64S _z = new Float64S(24, 24);
        public final Float64 z = new Float64(_z);
        public static final Float64S _roll = new Float64S(32, 32);
        public final Float64 roll = new Float64(_roll);
        public static final Float64S _pitch = new Float64S(40, 40);
        public final Float64 pitch = new Float64(_pitch);
        public static final Float64S _x = new Float64S(0, 0);
        public final Float64 x = new Float64(_x);
        public static final Float64S _y = new Float64S(8, 8);
        public final Float64 y = new Float64(_y);
        public static final Float64S _yaw = new Float64S(16, 16);
        public final Float64 yaw = new Float64(_yaw);
    }

    /** tValueType */
    public static final int
    eVT_POLAR = 0,
                eVT_CARTESIAN = 1,
                                eVT_DISTANCE_ONLY = 2,
                                                    eVT_DIMENSION = 3;

    /** cDistanceDataFormatInfo */
    public static final Object[][] cDistanceDataFormatInfo = new Object[][] {
        new Object[]{"eDF_POLAR_2D_FLOAT", 2, 4, false, eVT_POLAR},
        new Object[]{"eDF_POLAR_3D_FLOAT", 3, 4, false, eVT_POLAR},
        new Object[]{"eDF_CARTESIAN_2D_FLOAT", 2, 4, false, eVT_CARTESIAN},
        new Object[]{"eDF_CARTESIAN_3D_FLOAT", 3, 4, false, eVT_CARTESIAN},
        new Object[]{"eDF_POLAR_2D_DOUBLE", 2, 8, false, eVT_POLAR},
        new Object[]{"eDF_POLAR_3D_DOUBLE", 3, 8, false, eVT_POLAR},
        new Object[]{"eDF_CARTESIAN_2D_DOUBLE", 2, 8, false, eVT_CARTESIAN},
        new Object[]{"eDF_CARTESIAN_3D_DOUBLE", 3, 8, false, eVT_CARTESIAN},
        new Object[]{"eDF_POLAR_2D_FLOAT_PLANAR", 2, 4, true, eVT_POLAR},
        new Object[]{"eDF_POLAR_3D_FLOAT_PLANAR", 3, 4, true, eVT_POLAR},
        new Object[]{"eDF_CARTESIAN_2D_FLOAT_PLANAR", 2, 4, true, eVT_CARTESIAN},
        new Object[]{"eDF_CARTESIAN_3D_FLOAT_PLANAR", 3, 4, true, eVT_CARTESIAN},
        new Object[]{"eDF_DISTANCE_ONLY_FLOAT_PLANAR", 1, 4, true, eVT_DISTANCE_ONLY},
        new Object[]{"eDF_POLAR_2D_DOUBLE_PLANAR", 2, 8, true, eVT_POLAR},
        new Object[]{"eDF_POLAR_3D_DOUBLE_PLANAR", 3, 8, true, eVT_POLAR},
        new Object[]{"eDF_CARTESIAN_2D_DOUBLE_PLANAR", 2, 8, true, eVT_CARTESIAN},
        new Object[]{"eDF_CARTESIAN_3D_DOUBLE_PLANAR", 3, 8, true, eVT_CARTESIAN},
        new Object[]{"eDF_DISTANCE_ONLY_DOUBLE_PLANAR", 1, 8, true, eVT_DISTANCE_ONLY},
        new Object[]{"eDF_DISTANCE_ONLY_UNSIGNED16_PLANAR", 1, 2, true, eVT_DISTANCE_ONLY},
        new Object[]{"eDF_INVALID", 0, 0, false, eVT_DIMENSION}
    };

    /** tDistanceDataFormat */
    public static final int
    eDF_POLAR_2D_FLOAT = 0,
                         eDF_POLAR_3D_FLOAT = 1,
                                              eDF_CARTESIAN_2D_FLOAT = 2,
                                                                       eDF_CARTESIAN_3D_FLOAT = 3,
                                                                                                eDF_POLAR_2D_DOUBLE = 4,
                                                                                                                      eDF_POLAR_3D_DOUBLE = 5,
                                                                                                                                            eDF_CARTESIAN_2D_DOUBLE = 6,
                                                                                                                                                                      eDF_CARTESIAN_3D_DOUBLE = 7,
                                                                                                                                                                                                eDF_POLAR_2D_FLOAT_PLANAR = 8,
                                                                                                                                                                                                                            eDF_POLAR_3D_FLOAT_PLANAR = 9,
                                                                                                                                                                                                                                                        eDF_CARTESIAN_2D_FLOAT_PLANAR = 10,
                                                                                                                                                                                                                                                                                        eDF_CARTESIAN_3D_FLOAT_PLANAR = 11,
                                                                                                                                                                                                                                                                                                                        eDF_DISTANCE_ONLY_FLOAT_PLANAR = 12,
                                                                                                                                                                                                                                                                                                                                                         eDF_POLAR_2D_DOUBLE_PLANAR = 13,
                                                                                                                                                                                                                                                                                                                                                                                      eDF_POLAR_3D_DOUBLE_PLANAR = 14,
                                                                                                                                                                                                                                                                                                                                                                                                                   eDF_CARTESIAN_2D_DOUBLE_PLANAR = 15,
                                                                                                                                                                                                                                                                                                                                                                                                                                                    eDF_CARTESIAN_3D_DOUBLE_PLANAR = 16,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     eDF_DISTANCE_ONLY_DOUBLE_PLANAR = 17,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       eDF_DISTANCE_ONLY_UNSIGNED16_PLANAR = 18,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             eDF_INVALID = 19,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           eDF_DIMENSION = 20;

    /** Enum containing eBBT_UNKNOWN */
    public static final int
    eBBT_UNKNOWN = 0,
                   eBBT_USER_DEFINED_IO = 1,
                                          eBBT_CHAR_U = 2,
                                                        eBBT_CHAR_S = 3,
                                                                      eBBT_SHORT_INT_U = 4,
                                                                                         eBBT_SHORT_INT_S = 5,
                                                                                                            eBBT_LONG_INT_U = 6,
                                                                                                                              eBBT_LONG_INT_S = 7,
                                                                                                                                                eBBT_FLOAT = 8,
                                                                                                                                                             eBBT_DOUBLE = 9,
                                                                                                                                                                           eBBT_GEOMETRY_ENTRIES = 10,
                                                                                                                                                                                                   eBBT_LOG_STREAM = 11,
                                                                                                                                                                                                                     eBBT_IMAGE = 12,
                                                                                                                                                                                                                                  eBBT_SIMVIS3D_ELEMENT_DESCRIPTOR = 13,
                                                                                                                                                                                                                                                                     eBBT_MATRIX_FLOAT_2X2 = 14,
                                                                                                                                                                                                                                                                                             eBBT_MATRIX_FLOAT_3X3 = 15,
                                                                                                                                                                                                                                                                                                                     eBBT_MATRIX_FLOAT_4X4 = 16,
                                                                                                                                                                                                                                                                                                                                             eBBT_MATRIX_DOUBLE_2X2 = 17,
                                                                                                                                                                                                                                                                                                                                                                      eBBT_MATRIX_DOUBLE_3X3 = 18,
                                                                                                                                                                                                                                                                                                                                                                                               eBBT_MATRIX_DOUBLE_4X4 = 19,
                                                                                                                                                                                                                                                                                                                                                                                                                        eBBT_BEHAVIOUR_INFO = 20,
                                                                                                                                                                                                                                                                                                                                                                                                                                              eBBT_SERIALIZED_LOG = 21,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                    eBBT_SCANNER_DATA = 22,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        eBBT_DISTANCE_DATA = 23,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             eBBT_EXTRACTED_EDGE = 24,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   eBBT_EXTRACTED_CLUSTER = 25,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            eBBT_SCANNER_DATA_EXTENDED = 26,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         eBBT_FACE_LIST = 27,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          eBBT_METHOD_MARK = 28,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             eBBT_CAMERA_FEATURE_SET = 29,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       eBBT_TVEC3_PAIR = 30,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         eBBT_COMPLEX_DOUBLE = 31,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               eBBT_BUILDING_GRID_MAP = 32,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        eBBT_TRANSMISSION_GRID_MAP = 33,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     eBBT_GRID_MAP_PATH = 34,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          eBBT_SECTOR_MAP_BB = 35,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               eBBT_SECTOR_MAP_DESCRIPTION_BB = 36,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                eBBT_GRID_MAP = 37,
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                eBBT_DIMENSION = 38;

    /** tDistanceUnit */
    public static final int
    eDISTANCE_UNIT_MM = 0,
                        eDISTANCE_UNIT_CM = 1,
                                            eDISTANCE_UNIT_DM = 2,
                                                                eDISTANCE_UNIT_M = 3,
                                                                                   eDISTANCE_UNIT_DIMENSION = 4;

    public static class tGeometryEntryHeader extends Struct {
        public final static int sizeof32 = 36;
        public final static int sizeof64 = 36;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tGeometryEntryHeader() {}
        public tGeometryEntryHeader(FixedBuffer dbb) {
            super(dbb);
        }
        public tGeometryEntryHeader(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final Signed32S _type = new Signed32S(0, 0);
        public final Signed32 type = new Signed32(_type);
        public static final InnerStruct _color = new InnerStruct(4, 4);
        public final tRGB32 color = new tRGB32(this, 4, 4);
        public static final InnerStruct _pose = new InnerStruct(8, 8);
        public final tPose2D pose = new tPose2D(this, 8, 8);
        public static final Unsigned16S _dimension = new Unsigned16S(32, 32);
        public final Unsigned16 dimension = new Unsigned16(_dimension);
        public static final Bool8S _filled = new Bool8S(34, 34);
        public final Bool8 filled = new Bool8(_filled);
    }

    public static class tRGB24 extends Struct {
        public final static int sizeof32 = 3;
        public final static int sizeof64 = 3;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tRGB24() {}
        public tRGB24(FixedBuffer dbb) {
            super(dbb);
        }
        public tRGB24(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final Unsigned8S _r = new Unsigned8S(0, 0);
        public final Unsigned8 r = new Unsigned8(_r);
        public static final Unsigned8S _g = new Unsigned8S(1, 1);
        public final Unsigned8 g = new Unsigned8(_g);
        public static final Unsigned8S _b = new Unsigned8S(2, 2);
        public final Unsigned8 b = new Unsigned8(_b);
    }

    public static class tRGB32 extends Struct {
        public final static int sizeof32 = 4;
        public final static int sizeof64 = 4;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tRGB32() {}
        public tRGB32(FixedBuffer dbb) {
            super(dbb);
        }
        public tRGB32(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final Unsigned8S _r = new Unsigned8S(0, 0);
        public final Unsigned8 r = new Unsigned8(_r);
        public static final Unsigned8S _g = new Unsigned8S(1, 1);
        public final Unsigned8 g = new Unsigned8(_g);
        public static final Unsigned8S _b = new Unsigned8S(2, 2);
        public final Unsigned8 b = new Unsigned8(_b);
        public static final Unsigned8S _a = new Unsigned8S(3, 3);
        public final Unsigned8 a = new Unsigned8(_a);
    }

    public static class tCircle extends Struct {
        public final static int sizeof32 = 44;
        public final static int sizeof64 = 44;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tCircle() {}
        public tCircle(FixedBuffer dbb) {
            super(dbb);
        }
        public tCircle(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final Float64S _radius = new Float64S(20, 20);
        public final Float64 radius = new Float64(_radius);
        public static final Float64S _rad_start = new Float64S(28, 28);
        public final Float64 rad_start = new Float64(_rad_start);
        public static final Float64S _rad_length = new Float64S(36, 36);
        public final Float64 rad_length = new Float64(_rad_length);
        public static final InnerStruct _point = new InnerStruct(4, 4);
        public final tVec2T_double point = new tVec2T_double(this, 4, 4);
    }

    public static class tRectangle extends Struct {
        public final static int sizeof32 = 52;
        public final static int sizeof64 = 52;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tRectangle() {}
        public tRectangle(FixedBuffer dbb) {
            super(dbb);
        }
        public tRectangle(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final InnerStruct _dir1 = new InnerStruct(20, 20);
        public final tVec2T_double dir1 = new tVec2T_double(this, 20, 20);
        public static final InnerStruct _dir2 = new InnerStruct(36, 36);
        public final tVec2T_double dir2 = new tVec2T_double(this, 36, 36);
        public static final InnerStruct _point = new InnerStruct(4, 4);
        public final tVec2T_double point = new tVec2T_double(this, 4, 4);
    }

    /** tGeometryType */
    public static final int eGT_POINT = 0, eGT_POINT_3D = 1, eGT_LINE = 2, eGT_LINE_SEGMENT = 3, eGT_CIRCLE = 4, eGT_RECTANGLE = 5, eGT_TEXT = 6, eGT_TRIANGLE = 7, eGT_ARROW = 8,
                                        eGT_CUBE = 9, eGT_CYLINDER = 10, eGT_CIRCLE_3D = 11, eGT_RECTANGLE_3D = 12, eGT_DIMENSION = 13;

    public static class tPose2D extends Struct {
        public final static int sizeof32 = 24;
        public final static int sizeof64 = 24;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tPose2D() {}
        public tPose2D(FixedBuffer dbb) {
            super(dbb);
        }
        public tPose2D(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final Float64S _x = new Float64S(0, 0);
        public final Float64 x = new Float64(_x);
        public static final Float64S _y = new Float64S(8, 8);
        public final Float64 y = new Float64(_y);
        public static final Float64S _yaw = new Float64S(16, 16);
        public final Float64 yaw = new Float64(_yaw);
    }

    public static class tText extends Struct {
        public final static int sizeof32 = 168;
        public final static int sizeof64 = 168;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tText() {}
        public tText(FixedBuffer dbb) {
            super(dbb);
        }
        public tText(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final InnerStruct _position = new InnerStruct(20, 20);
        public final tVec2T_double position = new tVec2T_double(this, 20, 20);
        public static final Signed8S _text_0 = new Signed8S(36, 36);
        public final Signed8 text_0 = new Signed8(_text_0);
        public static final Float32S _size = new Float32S(164, 164);
        public final Float32 size = new Float32(_size);
        public static final InnerStruct _point = new InnerStruct(4, 4);
        public final tVec2T_double point = new tVec2T_double(this, 4, 4);
    }

    public static class tLineSegment extends Struct {
        public final static int sizeof32 = 52;
        public final static int sizeof64 = 52;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tLineSegment() {}
        public tLineSegment(FixedBuffer dbb) {
            super(dbb);
        }
        public tLineSegment(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final InnerStruct _start = new InnerStruct(20, 20);
        public final tVec2T_double start = new tVec2T_double(this, 20, 20);
        public static final InnerStruct _end = new InnerStruct(36, 36);
        public final tVec2T_double end = new tVec2T_double(this, 36, 36);
        public static final InnerStruct _point = new InnerStruct(4, 4);
        public final tVec2T_double point = new tVec2T_double(this, 4, 4);
    }

    public static class tLine extends Struct {
        public final static int sizeof32 = 36;
        public final static int sizeof64 = 36;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tLine() {}
        public tLine(FixedBuffer dbb) {
            super(dbb);
        }
        public tLine(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final InnerStruct _dir = new InnerStruct(20, 20);
        public final tVec2T_double dir = new tVec2T_double(this, 20, 20);
        public static final InnerStruct _point = new InnerStruct(4, 4);
        public final tVec2T_double point = new tVec2T_double(this, 4, 4);
    }

    public static class tVec2 extends Struct {
        public final static int sizeof32 = 16;
        public final static int sizeof64 = 16;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tVec2() {}
        public tVec2(FixedBuffer dbb) {
            super(dbb);
        }
        public tVec2(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final Float64S _x = new Float64S(0, 0);
        public final Float64 x = new Float64(_x);
        public static final Float64S _y = new Float64S(8, 8);
        public final Float64 y = new Float64(_y);
    }

    public static class tVec2T_double extends Struct {
        public final static int sizeof32 = 16;
        public final static int sizeof64 = 16;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tVec2T_double() {}
        public tVec2T_double(FixedBuffer dbb) {
            super(dbb);
        }
        public tVec2T_double(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final Float64S _x = new Float64S(0, 0);
        public final Float64 x = new Float64(_x);
        public static final Float64S _y = new Float64S(8, 8);
        public final Float64 y = new Float64(_y);
    }

    public static class tVec3 extends Struct {
        public final static int sizeof32 = 24;
        public final static int sizeof64 = 24;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tVec3() {}
        public tVec3(FixedBuffer dbb) {
            super(dbb);
        }
        public tVec3(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final Float64S _x = new Float64S(0, 0);
        public final Float64 x = new Float64(_x);
        public static final Float64S _y = new Float64S(8, 8);
        public final Float64 y = new Float64(_y);
        public static final Float64S _z = new Float64S(16, 16);
        public final Float64 z = new Float64(_z);
    }

    public static class tImageInfo extends Struct {
        public final static int sizeof32 = 48;
        public final static int sizeof64 = 48;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tImageInfo() {}
        public tImageInfo(FixedBuffer dbb) {
            super(dbb);
        }
        public tImageInfo(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final Unsigned32S _width = new Unsigned32S(0, 0);
        public final Unsigned32 width = new Unsigned32(_width);
        public static final Unsigned32S _height = new Unsigned32S(4, 4);
        public final Unsigned32 height = new Unsigned32(_height);
        public static final Unsigned32S _width_step = new Unsigned32S(8, 8);
        public final Unsigned32 width_step = new Unsigned32(_width_step);
        public static final Signed32S _format = new Signed32S(12, 12);
        public final Signed32 format = new Signed32(_format);
        public static final Unsigned32S _image_size = new Unsigned32S(16, 16);
        public final Unsigned32 image_size = new Unsigned32(_image_size);
        public static final Unsigned32S _extra_data_size = new Unsigned32S(20, 20);
        public final Unsigned32 extra_data_size = new Unsigned32(_extra_data_size);
        public static final Unsigned32S _extra_data_offset = new Unsigned32S(24, 24);
        public final Unsigned32 extra_data_offset = new Unsigned32(_extra_data_offset);
        public static final InnerStruct _region_of_interest = new InnerStruct(28, 28);
        public static final Bool8S _region_of_interest_valid = new Bool8S(44, 44);
        public final Bool8 region_of_interest_valid = new Bool8(_region_of_interest_valid);
    }

    public static class tImage extends Struct {
        public final static int sizeof32 = 16;
        public final static int sizeof64 = 16;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tImage() {}
        public tImage(FixedBuffer dbb) {
            super(dbb);
        }
        public tImage(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final Bool8S __buffer_owned = new Bool8S(4, 4);
        public final Bool8 _buffer_owned = new Bool8(__buffer_owned);
        public static final PointerS __buffer = new PointerS(0, 0);
        public final Pointer _buffer = new Pointer(__buffer);
        public static final PointerS __image_info = new PointerS(0, 0);
        public final Pointer _image_info = new Pointer(__image_info);
        public static final PointerS __image_ptr = new PointerS(12, 12);
        public final Pointer _image_ptr = new Pointer(__image_ptr);
    }

    /** tImageFormat */
    public static final int eIMAGE_FORMAT_MONO8 = 0, eIMAGE_FORMAT_MONO16 = 1, eIMAGE_FORMAT_MONO32_FLOAT = 2, eIMAGE_FORMAT_RGB565 = 3, eIMAGE_FORMAT_RGB24 = 4, eIMAGE_FORMAT_BGR24 = 5, eIMAGE_FORMAT_RGB32 = 6, eIMAGE_FORMAT_BGR32 = 7, eIMAGE_FORMAT_YUV420P = 8, eIMAGE_FORMAT_YUV411 = 9,
            eIMAGE_FORMAT_YUV422 = 10, eIMAGE_FORMAT_UYVY422 = 11, eIMAGE_FORMAT_YUV444 = 12, eIMAGE_FORMAT_BAYER_RGGB = 13, eIMAGE_FORMAT_BAYER_GBRG = 14, eIMAGE_FORMAT_BAYER_GRBG = 15, eIMAGE_FORMAT_BAYER_BGGR = 16, eIMAGE_FORMAT_HSV = 17, eIMAGE_FORMAT_HLS = 18, eIMAGE_FORMAT_HI240 = 19, eIMAGE_FORMAT_DIMENSION = 20;

    /** cImageInfoSizeWithPadding */
    public static final int cImageInfoSizeWithPadding = 48;

    public static class tBlackboardInfo extends Struct {
        public final static int sizeof32 = 24;
        public final static int sizeof64 = 24;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tBlackboardInfo() {}
        public tBlackboardInfo(FixedBuffer dbb) {
            super(dbb);
        }
        public tBlackboardInfo(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final PointerS _description = new PointerS(0, 0);
        public final Pointer description = new Pointer(_description);
        public static final Unsigned16S _content_type = new Unsigned16S(4, 4);
        public final Unsigned16 content_type = new Unsigned16(_content_type);
        public static final Unsigned32S _element_size = new Unsigned32S(8, 8);
        public final Unsigned32 element_size = new Unsigned32(_element_size);
        public static final Unsigned32S _number_of_elements = new Unsigned32S(12, 12);
        public final Unsigned32 number_of_elements = new Unsigned32(_number_of_elements);
        public static final Unsigned32S _capacity = new Unsigned32S(16, 16);
        public final Unsigned32 capacity = new Unsigned32(_capacity);
        public static final Unsigned32S _resize_increment = new Unsigned32S(20, 20);
        public final Unsigned32 resize_increment = new Unsigned32(_resize_increment);
    }

    public static class tTriangle extends Struct {
        public final static int sizeof32 = 68;
        public final static int sizeof64 = 68;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tTriangle() {}
        public tTriangle(FixedBuffer dbb) {
            super(dbb);
        }
        public tTriangle(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final InnerStruct _point_1 = new InnerStruct(20, 20);
        public final tVec2T_double point_1 = new tVec2T_double(this, 20, 20);
        public static final InnerStruct _point_2 = new InnerStruct(36, 36);
        public final tVec2T_double point_2 = new tVec2T_double(this, 36, 36);
        public static final InnerStruct _point_3 = new InnerStruct(52, 52);
        public final tVec2T_double point_3 = new tVec2T_double(this, 52, 52);
        public static final InnerStruct _point = new InnerStruct(4, 4);
        public final tVec2T_double point = new tVec2T_double(this, 4, 4);
    }

    public static class tVec2Geom extends Struct {
        public final static int sizeof32 = 36;
        public final static int sizeof64 = 36;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tVec2Geom() {}
        public tVec2Geom(FixedBuffer dbb) {
            super(dbb);
        }
        public tVec2Geom(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final InnerStruct _point = new InnerStruct(4, 4);
        public final tVec2T_double point = new tVec2T_double(this, 4, 4);
        public static final Float64S _x = new Float64S(2, 2);
        public final Float64 x = new Float64(_x);
        public static final Float64S _y = new Float64S(10, 10);
        public final Float64 y = new Float64(_y);
    }

    public static class tVec3Geom extends Struct {
        public final static int sizeof32 = 44;
        public final static int sizeof64 = 44;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tVec3Geom() {}
        public tVec3Geom(FixedBuffer dbb) {
            super(dbb);
        }
        public tVec3Geom(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final InnerStruct _point = new InnerStruct(4, 4);
        public final tVec2T_double point = new tVec2T_double(this, 4, 4);
        public static final Float64S _x = new Float64S(2, 2);
        public final Float64 x = new Float64(_x);
        public static final Float64S _y = new Float64S(10, 10);
        public final Float64 y = new Float64(_y);
        public static final Float64S _z = new Float64S(18, 18);
        public final Float64 z = new Float64(_z);
    }

    public static class tArrow extends Struct {
        public final static int sizeof32 = 76;
        public final static int sizeof64 = 76;
        public final static int sizeof   = JNIInfo.is64BitPlatform() ? sizeof64 : sizeof32;
        public int getSize32() {
            return sizeof32;
        }
        public int getSize64() {
            return sizeof64;
        }
        public int getSize()   {
            return sizeof;
        }

        public tArrow() {}
        public tArrow(FixedBuffer dbb) {
            super(dbb);
        }
        public tArrow(StructBase parentStruct, int offset32, int offset64) {
            super(parentStruct, offset32, offset64);
        }

        public static final InnerStruct _start = new InnerStruct(20, 20);
        public final tVec2T_double start = new tVec2T_double(this, 20, 20);
        public static final InnerStruct _end = new InnerStruct(36, 36);
        public final tVec2T_double end = new tVec2T_double(this, 36, 36);
        public static final Bool8S _head_at_start = new Bool8S(52, 52);
        public final Bool8 head_at_start = new Bool8(_head_at_start);
        public static final Bool8S _head_at_end = new Bool8S(53, 53);
        public final Bool8 head_at_end = new Bool8(_head_at_end);
        public static final Bool8S _filled = new Bool8S(54, 54);
        public final Bool8 filled = new Bool8(_filled);
        public static final Float64S _head_opening_angle = new Float64S(56, 56);
        public final Float64 head_opening_angle = new Float64(_head_opening_angle);
        public static final Float64S _head_length = new Float64S(64, 64);
        public final Float64 head_length = new Float64(_head_length);
        public static final Bool8S _head_length_relative = new Bool8S(72, 72);
        public final Bool8 head_length_relative = new Bool8(_head_length_relative);
        public static final InnerStruct _point = new InnerStruct(4, 4);
        public final tVec2T_double point = new tVec2T_double(this, 4, 4);
    }
}
