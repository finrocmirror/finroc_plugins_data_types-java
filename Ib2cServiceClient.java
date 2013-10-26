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

import org.finroc.core.FrameworkElement;
import org.finroc.core.port.PortCreationInfo;
import org.finroc.core.port.rpc.ClientPort;
import org.finroc.core.port.rpc.Method;
import org.finroc.core.port.rpc.RPCInterfaceType;

/**
 * @author Max Reichardt
 *
 * Client port for finroc_plugins_ib2c service
 */
public class Ib2cServiceClient extends ClientPort {

    class Service {

        /**
         * Sets stimulation of behavior module with specified handle
         *
         * \param moduleHandle Runtime Handle of behavior module to modify
         * \param mode New stimulation mode
         */
        void setStimulationMode(int moduleHandle, BehaviorStatus.StimulationMode mode) {
            // Just for signature
            throw new RuntimeException("Not implemented in Java");
        }
    }

    /** Port name of admin interface */
    public static final String PORT_NAME = "ib2c";

    /** Qualified port name */
    public static final String QUALIFIED_PORT_NAME = "Runtime/Services/ib2c";

    /** Method in ib2c service */
    public static Method SET_STIMULATION_MODE = new Method(Ib2cServiceClient.class, "setStimulationMode");

    /** Data Type of method calls to this port */
    public static final RPCInterfaceType DATA_TYPE = new RPCInterfaceType("ib2c Interface", SET_STIMULATION_MODE);


    public Ib2cServiceClient(String name, FrameworkElement parent) {
        super(new PortCreationInfo(name, parent, DATA_TYPE));
    }

    /**
     * Sets stimulation of behavior module with specified handle
     *
     * \param moduleHandle Runtime Handle of behavior module to modify
     * \param mode New stimulation mode
     */
    public void setStimulationMode(int moduleHandle, BehaviorStatus.StimulationMode mode) {
        this.call(SET_STIMULATION_MODE, moduleHandle, mode);
    }
}
