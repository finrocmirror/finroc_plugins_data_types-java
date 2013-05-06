//
// You received this file as part of Finroc
// A Framework for intelligent robot control
//
// Copyright (C) Finroc GbR (finroc.org)
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License
// as published by the Free Software Foundation; either version 2
// of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
//
//----------------------------------------------------------------------
package org.finroc.plugins.data_types.util;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

/**
 * A shape for bezier splines
 */
public class BezierSpline extends Path2D.Double {

    /** UID */
    private static final long serialVersionUID = 2136129794501102627L;

    /** Parameter T for spline curves */
    private final double T;

    /** Precalculated helper variables for spline curves */
    private final double X;
    private final double Y;
    private final double Z;

    public BezierSpline(Point2D.Double[] points) {
        this(points, 0);
    }

    public BezierSpline(java.awt.geom.Point2D.Double[] points, float tension) {
        super(Path2D.WIND_NON_ZERO, points.length);
        T = tension;
        X = (1 - T) / 4;
        Y = (1 + T) / 2;
        Z = (1 - T) / 2;

        // duplicate first point and last point
        Point2D.Double[] splinePoints = new Point2D.Double[points.length + 2];
        for (int i = 0; i < points.length; i++) {
            splinePoints[i + 1] = points[i];
        }
        splinePoints[0] = splinePoints[1];
        splinePoints[splinePoints.length - 1] = splinePoints[splinePoints.length - 2];
        moveTo(splinePoints[0].x, splinePoints[0].y);
        for (int i = 0; i < points.length - 1; i++) {
            Point2D.Double p1 = splinePoints[i + 1];
            Point2D.Double p2 = splinePoints[i + 2];
            Point2D.Double p3 = splinePoints[i + 3];
            double b1x = Y * p1.x + Z * p2.x;
            double b1y = Y * p1.y + Z * p2.y;
            double b2x = Z * p1.x + Y * p2.x;
            double b2y = Z * p1.y + Y * p2.y;
            double b3x = X * (p1.x + p3.x) + Y * p2.x;
            double b3y = X * (p1.y + p3.y) + Y * p2.y;
            curveTo(b1x, b1y, b2x, b2y, b3x, b3y);
        }
        Point2D.Double last = splinePoints[splinePoints.length - 1];
        lineTo(last.x, last.y);
    }
}
