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

import java.util.ArrayList;

import org.finroc.jc.annotation.JavaOnly;

import org.finroc.plugin.blackboard.BlackboardPlugin;
import org.finroc.plugin.datatype.BehaviourInfo;
import org.finroc.serialization.DataType;
import org.finroc.serialization.DataTypeBase;
import org.finroc.serialization.FixedBuffer;

/**
 * @author max
 *
 */
@JavaOnly
public class BehaviourInfoBlackboard extends MCABlackboardBuffer implements BehaviourInfo {

    public final static DataType<BehaviourInfoBlackboard> TYPE = new DataType<BehaviourInfoBlackboard>(BehaviourInfoBlackboard.class, "List<Behaviour Info>");
    public final static DataTypeBase BB_TYPE = BlackboardPlugin.registerBlackboardType(TYPE, "Blackboard<Behaviour Info>");

    /** Struct wrapper instances for accessing behaviour info - they stay constant once added */
    public final ArrayList<Entry> entries = new ArrayList<Entry>();

    @Override
    public BehaviourInfo.Entry getEntry(int i) {
        if (i >= getElements()) {
            throw new IndexOutOfBoundsException("" + i);
        }
        while (i >= entries.size()) {
            entries.add(new Entry(getBuffer(), getElementOffset(entries.size())));
        }
        return entries.get(i);
    }

    @Override
    public int size() {
        return getElements();
    }

    @Override
    public void setSize(int size) {
        super.resize(size, size, -1, true);
    }

    @JavaOnly
    private static class Entry extends MCA.tBehaviourInfo implements BehaviourInfo.Entry {

        public Entry(FixedBuffer dbb, int offset) {
            super(dbb);
            this.setRelAddress(offset);
        }

        @Override
        public float getActivation() {
            return activation.get();
        }

        @Override
        public short getBehId() {
            return beh_id.get();
        }

        @Override
        public float getActivity() {
            return activity.get();
        }

        @Override
        public float getTargetRating() {
            return target_rating.get();
        }

        @Override
        public boolean getAutoMode() {
            return auto_mode.get();
        }

        @Override
        public boolean isEnabled() {
            return enabled.get();
        }

        @Override
        public void setActivity(float a) {
            this.activity.set(a);
        }

        @Override
        public void setAutoMode(boolean auto_mode) {
            this.auto_mode.set(auto_mode);
        }

        @Override
        public void setBehId(short beh_id) {
            this.beh_id.set(beh_id);
        }

        @Override
        public void setEnabled(boolean enabled) {
            this.enabled.set(enabled);
        }

        @Override
        public void setActivation(float jota) {
            this.activation.set(jota);
        }

        @Override
        public void setTargetRating(float r) {
            this.target_rating.set(r);
        }
    }
}

