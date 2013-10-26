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

import org.finroc.core.plugin.Plugin;
import org.finroc.core.plugin.Plugins;
import org.rrlib.finroc_core_utils.log.LogDomain;
import org.finroc.plugins.data_types.mca.MCA;
import org.finroc.plugins.data_types.vector.Vector2i;

/**
 * @author Max Reichardt
 *
 */
public class DataTypePlugin implements Plugin {

    @Override
    public void init(/*PluginManager mgr*/) {
        Plugins.loadAllDataTypesInPackage(BehaviorStatus.class);
        Plugins.loadAllDataTypesInPackage(MCA.class);
        Plugins.loadAllDataTypesInPackage(Vector2i.class);
    }

    /** Log domain for this class */
    public static final LogDomain logDomain = Plugins.logDomain.getSubDomain("data_types");
}
