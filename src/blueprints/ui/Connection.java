/*
 * Copyright 2019 Salvador Vera Franco.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package blueprints.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

/**
 *
 * @author Salvador Vera Franco
 */
public class Connection {
    BPNode start, end;
    private CubicCurve2D curve;
    boolean isHighlighted = false;

    public Connection(BPNode start, BPNode end) {
        this.start = start;
        this.end = end;
        updateCurve();
    }

    public void updateCurve() {
        // Get direction vectors for control points
        Point2D startDir = start.getDirectionVector();
        Point2D endDir = end.getDirectionVector();

        // Calculate control points for the cubic Bezier curve
        double ctrlX1 = start.getX() + startDir.getX();
        double ctrlX2 = end.getX() + endDir.getX();
        
        // Create the cubic Bezier curve
        curve = new CubicCurve2D.Double(
                start.getX(), start.getY(),
                ctrlX1, start.getY(),
                ctrlX2, end.getY(),
                end.getX(), end.getY());
    }

    public void updateCurve2() {
        Rectangle parentBounds = end.getGraphNode().getBounds();
        Rectangle childBounds = start.getGraphNode().getBounds();

        double xOut = parentBounds.getMaxX();
        double xIn = childBounds.getX();
        double yOut = parentBounds.getCenterY();
        double yIn = childBounds.getCenterY();
        double xCenter = (xOut + xIn) / 2;

        double ctrlX1, ctrlX2;
        if (xOut <= xIn) {
            ctrlX1 = ctrlX2 = xCenter;
        } else {
            ctrlX1 = xOut + (xOut - xCenter);
            ctrlX2 = xIn + (xIn - xCenter);
        }

      /*  curve = new CubicCurve2D.Double(
                xOut, yOut,
                ctrlX1, yOut,
                ctrlX2, yIn,
                xIn, yIn);
*/
        curve = new CubicCurve2D.Double(
                start.getX(), start.getY(),
                ctrlX1, yIn,
                ctrlX2, yOut,
                end.getX(), end.getY());
                
    }

    public void draw(Graphics2D g) {
        updateCurve();

        // Draw with different style if highlighted
        if (isHighlighted) {
            g.setColor(Color.RED);
            g.setStroke(new BasicStroke(6.0f));
        } else {
            g.setColor(new Color(41, 200, 114));
            g.setStroke(new BasicStroke(5.0f));
        }

        g.draw(curve);
    }

    // Determine if the mouse is near the curve
    public boolean isNearPoint(int mx, int my, double threshold) {
        // Flatten the curve into line segments
        PathIterator pi = curve.getPathIterator(null, 0.5);
        double[] coords = new double[6];
        double[] lastPoint = new double[2];

        pi.currentSegment(coords);
        lastPoint[0] = coords[0];
        lastPoint[1] = coords[1];
        pi.next();

        while (!pi.isDone()) {
            int type = pi.currentSegment(coords);

            if (type == PathIterator.SEG_LINETO) {
                // Check distance from point to line segment
                double dist = distanceToLineSegment(
                        lastPoint[0], lastPoint[1],
                        coords[0], coords[1],
                        mx, my);

                if (dist < threshold) {
                    return true;
                }

                lastPoint[0] = coords[0];
                lastPoint[1] = coords[1];
            }

            pi.next();
        }

        return false;
    }

    // Calculate distance from point to line segment
    private double distanceToLineSegment(
            double x1, double y1, // Line start
            double x2, double y2, // Line end
            double px, double py // Point
    ) {
        double lineLengthSquared = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);

        if (lineLengthSquared == 0) {
            return Math.sqrt((px - x1) * (px - x1) + (py - y1) * (py - y1));
        }

        // Projection of point onto line
        double t = ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / lineLengthSquared;
        t = Math.max(0, Math.min(1, t)); // Clamp t to line segment

        double projX = x1 + t * (x2 - x1);
        double projY = y1 + t * (y2 - y1);

        return Math.sqrt((px - projX) * (px - projX) + (py - projY) * (py - projY));
    }
}
