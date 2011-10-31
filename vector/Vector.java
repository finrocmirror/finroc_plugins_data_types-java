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
package org.finroc.plugins.data_types.vector;

import org.rrlib.finroc_core_utils.serialization.Copyable;
import org.rrlib.finroc_core_utils.serialization.InputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.OutputStreamBuffer;
import org.rrlib.finroc_core_utils.serialization.RRLibSerializableImpl;
import org.rrlib.finroc_core_utils.serialization.StringInputStream;
import org.rrlib.finroc_core_utils.serialization.StringOutputStream;

/**
 * @author max
 *
 * Generic base class for Java equivalents to tVec2i, tVec3i, tVec6i, tVec2d, tVec3d, tVec6d
 */
public class Vector extends RRLibSerializableImpl implements Copyable<Vector> {

    /** Backend that stores values */
    protected int[] ibuffer;
    protected double[] dbuffer;

    /** Type of backend used */
    protected Type type;
    protected int components;

    public enum Type { INT, DOUBLE }

    public Vector(int components, Type type) {
        ensureBackendAvailability(components, type);
        this.type = type;
        this.components = components;
    }

    private void ensureBackendAvailability(int components, Type type2) {
        if (type2 == Type.INT && (ibuffer == null || ibuffer.length < components)) {
            ibuffer = new int[components];
        } else if (type2 == Type.DOUBLE && (dbuffer == null || dbuffer.length < components)) {
            dbuffer = new double[components];
        }
    }

    @Override
    public void serialize(OutputStreamBuffer os) {
        if (type == Type.INT) {
            for (int i = 0; i < components; i++) {
                os.writeInt(ibuffer[i]);
            }
        } else if (type == Type.DOUBLE) {
            for (int i = 0; i < components; i++) {
                os.writeDouble(dbuffer[i]);
            }
        }
    }

    @Override
    public void deserialize(InputStreamBuffer is) {
        if (type == Type.INT) {
            for (int i = 0; i < components; i++) {
                ibuffer[i] = is.readInt();
            }
        } else if (type == Type.DOUBLE) {
            for (int i = 0; i < components; i++) {
                dbuffer[i] = is.readDouble();
            }
        }
    }

    @Override
    public void serialize(StringOutputStream os) {
        if (type == Type.INT) {
            os.append('(').append(ibuffer[0]);
            for (int i = 1; i < components; i++) {
                os.append(", ").append(ibuffer[i]);
            }
            os.append(')');
        } else if (type == Type.DOUBLE) {
            os.append('(').append(dbuffer[0]);
            for (int i = 1; i < components; i++) {
                os.append(", ").append(dbuffer[i]);
            }
            os.append(')');
        }
    }

    @Override
    public void deserialize(StringInputStream is) throws Exception {
        String s = is.readAll();
        s = s.trim();
        if (s.startsWith("(") && s.endsWith(")")) {
            s = s.substring(1, s.length() - 1);
            String[] nums = s.split(",");
            if (type == Type.INT) {
                if (nums.length == components) {
                    try {
                        for (int i = 0; i < components; i++) {
                            ibuffer[i] = Integer.parseInt(nums[i].trim());
                        }
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (type == Type.DOUBLE) {
                if (nums.length == components) {
                    try {
                        for (int i = 0; i < components; i++) {
                            dbuffer[i] = Double.parseDouble(nums[i].trim());
                        }
                        return;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        throw new Exception("Cannot parse " + s);
    }

    public int componentCount() {
        return components;
    }

    public int getInt(int index) {
        if (type == Type.INT) {
            return ibuffer[index];
        } else {
            return (int)dbuffer[index];
        }
    }
    public int getDouble(int index) {
        if (type == Type.INT) {
            return ibuffer[index];
        } else {
            return (int)dbuffer[index];
        }
    }
    public void set(int index, int value) {
        if (type == Type.INT) {
            ibuffer[index] = value;
        } else if (type == Type.DOUBLE) {
            dbuffer[index] = value;
        }
    }
    public void set(int index, double value) {
        if (type == Type.INT) {
            ibuffer[index] = (int)value;
        } else if (type == Type.DOUBLE) {
            dbuffer[index] = value;
        }
    }

    @Override
    public void copyFrom(Vector o) {
        ensureBackendAvailability(o.components, o.type);
        type = o.type;
        components = o.components;
        for (int i = 0; i < o.components; i++) {
            if (o.type == Type.INT) {
                ibuffer[i] = o.ibuffer[i];
            } else if (o.type == Type.DOUBLE) {
                dbuffer[i] = o.dbuffer[i];
            }
        }
    }
}
