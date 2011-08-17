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
package org.finroc.plugins.data_types;

import org.rrlib.finroc_core_utils.log.LogLevel;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;

/**
 * @author max
 *
 * Java equivalent for tCameraFeature in rrlib_coviroa
 */
public class CameraFeature extends RRLibSerializableImpl {

    enum CameraFeatureMode {
        eCFM_OFF,
        eCFM_MANUAL,
        eCFM_AUTOMATIC,
        eCFM_ABSOLUTE,
        eCFM_ONE_PUSH_AUTOMATIC,
        eCFM_DIMENSION
    }

    enum CameraFeatureID {
        eCF_FEATURE_BRIGHTNESS,
        eCF_FEATURE_EXPOSURE,
        eCF_FEATURE_SHARPNESS,
        eCF_FEATURE_WHITE_BALANCE,
        eCF_FEATURE_HUE,
        eCF_FEATURE_SATURATION,
        eCF_FEATURE_GAMMA,
        eCF_FEATURE_SHUTTER,
        eCF_FEATURE_GAIN,
        eCF_FEATURE_IRIS,
        eCF_FEATURE_FOCUS,
        eCF_FEATURE_TEMPERATURE,
        eCF_FEATURE_TRIGGER,
        eCF_FEATURE_TRIGGER_DELAY,
        eCF_FEATURE_WHITE_SHADING,
        eCF_FEATURE_FRAME_RATE,
        eCF_FEATURE_ZOOM,
        eCF_FEATURE_PAN,
        eCF_FEATURE_TILT,
        eCF_FEATURE_OPTICAL_FILTER,
        eCF_FEATURE_CAPTURE_SIZE,
        eCF_FEATURE_CAPTURE_QUALITY,
        eCF_FEATURE_CONTRAST,
        eCF_DIMENSION
    }

    /**
     * Java equivalent for tCameraFeatureCapability in rrlib_coviroa
     */
    public class Capability extends RRLibSerializableImpl {

        /** Indicates whether this feature capability is available or not. */
        public boolean available;

        /** Indicates whether this feature capability is currently active or not. */
        public boolean active;

        @Override
        public void serialize(OutputStreamBuffer os) {
            os.writeBoolean(available);
            os.writeBoolean(active);
        }
        @Override
        public void deserialize(InputStreamBuffer is) {
            available = is.readBoolean();
            active = is.readBoolean();
        }
    }

    /**
     * Java equivalent for CameraFeatureCapabilityRange in rrlib_coviroa
     */
    public class CapabilityRange extends Capability {

        public float min, max;

        @Override
        public void serialize(OutputStreamBuffer os) {
            super.serialize(os);
            os.writeFloat(min);
            os.writeFloat(max);
        }
        @Override
        public void deserialize(InputStreamBuffer is) {
            super.deserialize(is);
            min = is.readFloat();
            max = is.readFloat();
        }
    }

    /**
     * Java equivalent for CameraFeatureSet in rrlib_coviroa
     */
    public class Set extends RRLibSerializableImpl {

        /** Name and vendor of camera */
        public String name, vendor;

        /** Feature in set */
        public CameraFeature[] features = new CameraFeature[CameraFeatureID.eCF_DIMENSION.ordinal()];

        public Set() {
            for (int i = 0; i < features.length; i++) {
                features[i] = new CameraFeature();
                features[i].featureId = CameraFeatureID.values()[i];
            }
        }

        @Override
        public void serialize(OutputStreamBuffer os) {
            os.writeString(name);
            os.writeString(vendor);
            for (int i = 0; i < features.length; i++) {
                features[i].serialize(os);
            }
        }

        @Override
        public void deserialize(InputStreamBuffer is) {
            name = is.readString();
            vendor = is.readString();
            for (int i = 0; i < features.length; i++) {
                features[i].deserialize(is);
                if (features[i].featureId.ordinal() != i) {
                    DataTypePlugin.logDomain.log(LogLevel.LL_WARNING, "CameraFeature", "Invalid feature id. Stream seems corrupted :-/.");
                }
            }
        }
    }

    /** Capabilities */
    public Capability automatic;
    public Capability one_push;
    public Capability readout;
    public Capability on_off;
    public CapabilityRange manual;
    public CapabilityRange absolute;

    /** Stores the unique id of this camera feature. */
    private CameraFeatureID featureId;

    /** Indicates whether this feature is available in the camera at hand or not. */
    private boolean available;

    /** Stores the mode of the camera feature. */
    private CameraFeatureMode mode;

    /** Absolute value of this camera feature if applicable. */
    private float absoluteValue;

//  /**
//   * Indicates how often the settings of this feature were altered.
//   *
//   * This flag is needed in order to check for changes in
//   * comparison to another tCameraFeature copy.
//   */
//  private int modified;
//
//  /**
//   * Stores the camera feature set comprising this
//   * tCameraFeature.
//   *
//   * Note that this variable will be invalid when copying
//   * this object or the camera feature set containing it!
//   * Call the BuildIndex() routine of the copied tCameraFeatureSet
//   * in order to fix this. In the case that you copied
//   * it standalone updating a value will have an influence on the
//   * tCameraFeatureSet you copied it from. If you left the
//   * address space (as due to mca2 blackboard transfer),
//   * this may even segfault. So if you need a copy
//   * copy the whole feature set and call BuildIndex()
//   * before using this feature set again!
//   */
//  tCameraFeatureSet* parent;

    /** Maximum number of values a single camera feature may have */
    static final int MAX_NUMBER_OF_VALUES = 3;

    /**
     * Stores the actual value(s) the camera feature currently has.
     *
     * number_of_values indicates how many of these values are used.
     *
     * Implementation note:
     * We store an array with always the maximum size in this class,
     * because this is simpler and less memory-intensive than allocating
     * it dynamically.
     * (There are other possibilities (union, subclass etc.), but this is
     *  more complex and memory consumption shouldn't be critical here)
     */
    int[] values = new int[MAX_NUMBER_OF_VALUES];

    /**
     * @return Returns the number of values defining
     * the properties of this feature.
     */
    public int GetNumberOfValues() {
        switch (featureId) {
        case eCF_FEATURE_WHITE_BALANCE:
            return 2;
        case eCF_FEATURE_WHITE_SHADING:
            return 3;
        default:
            return 1;
        }
    }

    /**
     * Get current value of camera feature
     *
     * @param index Index of value (in case this is a feature with multiple values)
     * @return Current value
     */
    int GetValue(int index) {
        assert(index >= 0 && index < GetNumberOfValues());
        return values[index];
    }

    @Override
    public void serialize(OutputStreamBuffer os) {
        os.writeByte((byte)featureId.ordinal());
        os.writeBoolean(available);
        os.writeByte((byte)mode.ordinal());
        os.writeFloat(absoluteValue);
        for (int i = 0; i < GetNumberOfValues(); i++) {
            os.writeInt(GetValue(i));
        }
        automatic.serialize(os);
        one_push.serialize(os);
        readout.serialize(os);
        on_off.serialize(os);
        manual.serialize(os);
        absolute.serialize(os);
    }

    @Override
    public void deserialize(InputStreamBuffer is) {
        featureId = CameraFeatureID.values()[is.readByte()];
        available = is.readBoolean();
        mode = CameraFeatureMode.values()[is.readByte()];
        absoluteValue = is.readFloat();
        for (int i = 0; i < GetNumberOfValues(); i++) {
            values[i] = is.readInt();
        }
        automatic.deserialize(is);
        one_push.deserialize(is);
        readout.deserialize(is);
        on_off.deserialize(is);
        manual.deserialize(is);
        absolute.deserialize(is);
    }
}
