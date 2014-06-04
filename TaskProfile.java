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


import org.finroc.core.datatype.Duration;
import org.rrlib.serialization.BinaryInputStream;
import org.rrlib.serialization.BinaryOutputStream;
import org.rrlib.serialization.BinarySerializable;
import org.rrlib.serialization.PortDataListImpl;
import org.rrlib.serialization.rtti.Copyable;
import org.rrlib.serialization.rtti.DataType;

/**
 * @author Max Reichardt
 *
 * Equivalent to finroc::scheduling::tTaskProfile
 */
public class TaskProfile implements BinarySerializable, Copyable<TaskProfile> {

    public static class List extends PortDataListImpl<TaskProfile> {

        public List() {
            super(TaskProfile.TYPE);
        }

        @Override
        public void deserialize(BinaryInputStream is) throws Exception {
            super.deserialize(is);

            for (int i = 0; i < size(); i++) {
                get(i).schedulePosition = i;
            }
        }


    }

    public final static DataType<TaskProfile> TYPE = new DataType<TaskProfile>(TaskProfile.class, "TaskProfile", false);
    public final static DataType<List> LIST_TYPE = new DataType<List>(List.class, "List<TaskProfile>", false);

    static {
        TYPE.getInfo().listType = LIST_TYPE;
        LIST_TYPE.getInfo().elementType = TYPE;
    }

    /** Enum to specify which kind of task a task profile is associated to */
    public enum TaskClassification { SENSE, CONTROL, OTHER };

    /** Last execution duration */
    public Duration lastExecutionDuration = new Duration();

    /** Maximum execution duration (excluding first/initial execution) */
    public Duration maxExecutionDuration = new Duration();

    /** Average execution duration */
    public Duration averageExecutionDuration = new Duration();

    /** Total execution duration */
    public Duration totalExecutionDuration = new Duration();

    /** Handle of framework element associated with task */
    public int handle = 0;

    /** Specifies which kind of task a task profile is associated to (used e.g. as hint for finstruct) */
    public TaskClassification taskClassification = TaskClassification.OTHER;

    /** Task's Position in schedule */
    public int schedulePosition = -1;

    @Override
    public void serialize(BinaryOutputStream stream) {
        lastExecutionDuration.serialize(stream);
        maxExecutionDuration.serialize(stream);
        averageExecutionDuration.serialize(stream);
        totalExecutionDuration.serialize(stream);
        stream.writeInt(handle);
        stream.writeEnum(taskClassification);
    }

    @Override
    public void deserialize(BinaryInputStream stream) throws Exception {
        lastExecutionDuration.deserialize(stream);
        maxExecutionDuration.deserialize(stream);
        averageExecutionDuration.deserialize(stream);
        totalExecutionDuration.deserialize(stream);
        handle = stream.readInt();
        taskClassification = stream.readEnum(TaskClassification.class);
    }

    @Override
    public void copyFrom(TaskProfile source) {
        lastExecutionDuration = source.lastExecutionDuration;
        maxExecutionDuration = source.maxExecutionDuration;
        averageExecutionDuration = source.averageExecutionDuration;
        totalExecutionDuration = source.totalExecutionDuration;
        handle = source.handle;
        taskClassification = source.taskClassification;
        schedulePosition = source.schedulePosition;
    }
}

