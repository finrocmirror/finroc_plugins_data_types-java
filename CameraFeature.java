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

import org.rrlib.finroc_core_utils.log.LogLevel;
import org.rrlib.finroc_core_utils.rtti.DataType;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;
import org.rrlib.finroc_core_utils.serialization.Serialization;
import org.rrlib.finroc_core_utils.xml.XMLNode;

/**
 * @author Max Reichardt
 *
 * Java equivalent for tCameraFeature in rrlib_coviroa
 */
public class CameraFeature extends RRLibSerializableImpl {

    public final static DataType<CameraFeature> TYPE = new DataType<CameraFeature>(CameraFeature.class);
    public final static DataType<CameraFeature.Set> SET_TYPE = new DataType<CameraFeature.Set>(CameraFeature.Set.class, "CameraFeatureSet");

    public enum Mode {
        OFF,
        MANUAL,
        AUTOMATIC,
        ABSOLUTE,
        ONE_PUSH_AUTOMATIC,
        DIMENSION
    }

    public enum ID {
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
    public static class Set extends RRLibSerializableImpl {

        /** Name and vendor of camera */
        public String name, vendor;

        /** Feature in set */
        public CameraFeature[] features = new CameraFeature[ID.eCF_DIMENSION.ordinal()];

        public Set() {
            for (int i = 0; i < features.length; i++) {
                features[i] = new CameraFeature();
                features[i].featureId = ID.values()[i];
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
                    DataTypePlugin.logDomain.log(LogLevel.WARNING, "CameraFeature", "Invalid feature id. Stream seems corrupted :-/.");
                }
            }
        }

        @Override
        public void serialize(XMLNode node) throws Exception {
            // important part: values
            for (int f = 0; f < ID.eCF_DIMENSION.ordinal(); f++) {
                CameraFeature cf = features[f];
                if (cf.available) {
                    XMLNode cfnode = node.addChildNode("feature");
                    cfnode.setAttribute("id", "" + cf.getFeatureName());
                    cfnode.setAttribute("mode", Serialization.serialize(cf.getMode()));
                    if (cf.on_off.available && cf.on_off.active == false) {
                        cfnode.setAttribute("on", false);
                    } else {
                        String s = "";
                        switch (cf.getMode()) {
                        case ABSOLUTE:
                            cfnode.setContent("" + cf.getAbsoluteValue());
                            break;
                        case MANUAL:
                            s += cf.getValue(0);
                            for (int i = 1; i < cf.getNumberOfValues(); i++) {
                                s += ", " + cf.getValue(i);
                            }
                            cfnode.setContent(s);
                            break;
                        case AUTOMATIC:
                        case OFF:
                        case ONE_PUSH_AUTOMATIC:
                            break;
                        default:
                            DataTypePlugin.logDomain.log(LogLevel.ERROR, "CameraFeature", "Not handled");
                            break;
                        }
                    }
                }
            }

            // optional camera info
            XMLNode inode = node.addChildNode("info");
            inode.setAttribute("hint", "not required in config file");
            inode.setAttribute("vendor", vendor);
            inode.setAttribute("camera", name);

            // feature capabilities
            for (int f = 0; f < ID.eCF_DIMENSION.ordinal(); f++) {
                CameraFeature cf = features[f];
                if (cf.available) {
                    XMLNode cfnode = inode.addChildNode("feature");
                    cfnode.setAttribute("id", "" + cf.getFeatureName());

                    String available = "";
                    available += cf.absolute.available ? "1" : "0";
                    available += cf.automatic.available ? "1" : "0";
                    available += cf.manual.available ? "1" : "0";
                    available += cf.on_off.available ? "1" : "0";
                    available += cf.one_push.available ? "1" : "0";
                    available += cf.readout.available ? "1" : "0";
                    cfnode.setAttribute("available", available);
                    if (cf.absolute.available) {
                        cfnode.setAttribute("abs_min", cf.absolute.min);
                        cfnode.setAttribute("abs_max", cf.absolute.max);
                    }
                    if (cf.manual.available) {
                        cfnode.setAttribute("min", cf.manual.min);
                        cfnode.setAttribute("max", cf.manual.max);
                    }
                }
            }
        }

        @Override
        public void deserialize(XMLNode node) throws Exception {
            // clear
            for (int i = 0; i < features.length; i++) {
                features[i] = new CameraFeature();
                features[i].featureId = ID.values()[i];
            }

            try {
                for (XMLNode.ConstChildIterator it = node.getChildrenBegin(); it.get() != node.getChildrenEnd(); it.next()) {
                    if (it.get().getName().equals("feature")) {
                        CameraFeature cf = features[Serialization.deserialize(it.get().getStringAttribute("id"), ID.class).ordinal()];
                        cf.available = true;
                        cf.mode = Serialization.deserialize(it.get().getStringAttribute("mode"), Mode.class);
                        if (it.get().hasAttribute("on") && (!it.get().getBoolAttribute("on"))) {
                            cf.on_off.available = true;
                            cf.on_off.active = false;
                        } else {
                            cf.on_off.active = true;
                            switch (cf.getMode()) {
                            case ABSOLUTE:
                                cf.absoluteValue = Float.parseFloat(it.get().getTextContent());
                                break;
                            case AUTOMATIC:
                            case OFF:
                            case ONE_PUSH_AUTOMATIC:
                                break;
                            default:
                                DataTypePlugin.logDomain.log(LogLevel.ERROR, "CameraFeature", "Not handled");
                                break;
                            case MANUAL:
                                String[] split = it.get().getTextContent().split(",");
                                for (int i = 0; i < cf.getNumberOfValues(); i++) {
                                    cf.values[i] = Integer.parseInt(split[i]);
                                }
                                break;
                            }
                        }
                    } else if (it.get().getName().equals("info")) {
                        vendor = it.get().getStringAttribute("vendor");
                        name = it.get().getStringAttribute("camera");

                        // feature capabilities
                        for (XMLNode.ConstChildIterator it2 = it.get().getChildrenBegin(); it2.get() != it.get().getChildrenEnd(); it2.next()) {
                            if (it2.get().getName().equals("feature")) {
                                CameraFeature cf = features[Serialization.deserialize(it.get().getStringAttribute("id"), ID.class).ordinal()];
                                cf.available = true;

                                String s = it2.get().getStringAttribute("available");
                                cf.absolute.available = s.charAt(0) == '1';
                                cf.automatic.available = s.charAt(1) == '1';
                                cf.manual.available = s.charAt(2) == '1';
                                cf.on_off.available = s.charAt(3) == '1';
                                cf.one_push.available = s.charAt(4) == '1';
                                cf.readout.available = s.charAt(5) == '1';

                                if (cf.absolute.available) {
                                    cf.absolute.min = it2.get().getFloatAttribute("abs_min");
                                    cf.absolute.min = it2.get().getFloatAttribute("abs_max");
                                }
                                if (cf.manual.available) {
                                    cf.manual.min = it2.get().getFloatAttribute("min");
                                    cf.manual.min = it2.get().getFloatAttribute("max");
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                DataTypePlugin.logDomain.log(LogLevel.ERROR, "CameraFeature", "Error deserializing camera feature set: " + e);
            }
        }
    }

    /** Capabilities */
    public Capability automatic = new Capability();
    public Capability one_push = new Capability();
    public Capability readout = new Capability();
    public Capability on_off = new Capability();
    public CapabilityRange manual = new CapabilityRange();
    public CapabilityRange absolute = new CapabilityRange();

    /** Stores the unique id of this camera feature. */
    private ID featureId;

    /** Indicates whether this feature is available in the camera at hand or not. */
    private boolean available;

    /** Stores the mode of the camera feature. */
    private Mode mode;

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
    public int getNumberOfValues() {
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
    public int getValue(int index) {
        assert(index >= 0 && index < getNumberOfValues());
        return values[index];
    }

    @Override
    public void serialize(OutputStreamBuffer os) {
        os.writeByte((byte)featureId.ordinal());
        os.writeBoolean(available);
        os.writeByte((byte)mode.ordinal());
        os.writeFloat(absoluteValue);
        for (int i = 0; i < getNumberOfValues(); i++) {
            os.writeInt(getValue(i));
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
        featureId = ID.values()[is.readByte()];
        available = is.readBoolean();
        mode = Mode.values()[is.readByte()];
        absoluteValue = is.readFloat();
        for (int i = 0; i < getNumberOfValues(); i++) {
            values[i] = is.readInt();
        }
        automatic.deserialize(is);
        one_push.deserialize(is);
        readout.deserialize(is);
        on_off.deserialize(is);
        manual.deserialize(is);
        absolute.deserialize(is);
    }

    /*!
     * Is any mode of camera feature available?
     */
    public boolean isAvailable() {
        return absolute.active || automatic.active || manual.active || on_off.active || one_push.active || readout.active;
    }

    /**
     * @return unique id of this camera feature
     */
    public ID getFeatureId() {
        return featureId;
    }

    /**
     * (Do not use when feature is part of feature set)
     *
     * @param featureId New feature id
     */
    public void setFeatureId(ID featureId) {
        this.featureId = featureId;
    }

    /**
     * @return Name of this camera feature
     */
    public String getFeatureName() {
        String s = featureId.toString().substring(12).toLowerCase().replace('_', ' ');
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * @return mode of the camera feature
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * @param mode New mode of the camera feature
     */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /**
     * @param index Index of value
     * @param newValue New Value
     */
    public void setValue(int index, int newValue) {
        values[index] = newValue;
    }

    /**
     * @return Absolute value of this camera feature if applicable.
     */
    public float getAbsoluteValue() {
        return absoluteValue;
    }

    /**
     * @param absoluteValue Absolute value of this camera feature if applicable.
     */
    public void setAbsoluteValue(float absoluteValue) {
        this.absoluteValue = absoluteValue;
    }
}
