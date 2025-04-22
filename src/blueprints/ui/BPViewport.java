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

import blueprints.BPManager;
import blueprints.utils.Resizer;
import java.awt.geom.Point2D;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author Salvador Vera Franco
 */
public class BPViewport extends JLayeredPane {

    BPManager.Viewport view_manager;
    public boolean inAny = false;

    private double zoomFactor = 1.0; // Nivel de zoom inicial

    /** PRUEBAS */
    private ArrayList<BPComponent> components = new ArrayList<>();
    private ArrayList<Connection> connections = new ArrayList<>();
    private BPComponent selectedComponent = null;
    private Point lastMousePos;
    // private double zoomFactor = 1.0;
    private double panX = 0, panY = 0;
    private BPNode tempConnectionStart = null;
    private Connection highlightedConnection = null;

    /** PRUEBAS */
    public BPViewport() {
        view_manager = new BPManager.Viewport(new ArrayList<>(), this);
        setOpaque(true);
        setDoubleBuffered(true);
        /*
         * MouseAdapter ma = new MouseAdapter() {
         * private BPComponent dragComponent;
         * private Point clickPoint;
         * private Point offset;
         * private boolean inComponent = false;
         * 
         * private boolean inNode = false;
         * private int nodetype = -1;
         * private BPComponent component;
         * 
         * public void mousePressed(MouseEvent e) {
         * if (inComponent(e)) {
         * component = (BPComponent) getComponentAt(e.getPoint());
         * dragComponent = component;
         * dragComponent.setState(BPComponent.STATE_SELECTED);
         * setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
         * if (component.isMovable()) {
         * clickPoint = e.getPoint();
         * int deltaX = clickPoint.x - dragComponent.getX();
         * int deltaY = clickPoint.y - dragComponent.getY();
         * offset = new Point(deltaX, deltaY);
         * inComponent = true;
         * }
         * } else {
         * inComponent = false;
         * if (inInputNode(e)) {
         * inNode = true;
         * // drawPreLine(inNode);
         * nodetype = 2;
         * setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
         * parentnode = getInputNode(e);
         * } else if (inOutputNode(e)) {
         * inNode = true;
         * // drawPreLine(inNode);
         * nodetype = 1;
         * setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
         * childnode = getOutputNode(e);
         * } else {
         * inNode = false;
         * // drawPreLine(inNode);
         * }
         * }
         * }
         * 
         * public void mouseMoved(MouseEvent e) {
         * if (inInputNode(e) || inOutputNode(e)) {
         * inAny = true;
         * setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
         * } else if (inComponent(e)) {
         * inAny = true;
         * setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
         * BPComponent c = (BPComponent) getComponentAt(e.getPoint());
         * Resizer.cursor(e, c);
         * repaint();
         * } else {
         * inAny = false;
         * setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
         * }
         * }
         * 
         * public void mouseReleased(MouseEvent e) {
         * if (dragComponent != null)
         * dragComponent.setState(dragComponent.getLastState());
         * inNode = false;
         * if (parentnode != null && childnode != null) {
         * connect(parentnode, childnode);
         * repaint();
         * }
         * parentnode = null;
         * childnode = null;
         * nodetype = -1;
         * if (inAny)
         * setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
         * else
         * setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
         * }
         * 
         * public void mouseDragged(MouseEvent e) {
         * if (inComponent) {
         * int mouseX = e.getX();
         * int mouseY = e.getY();
         * int xDelta = mouseX - offset.x;
         * int yDelta = mouseY - offset.y;
         * dragComponent.setLocation(xDelta, yDelta);
         * repaint();
         * }
         * switch (nodetype) {
         * case 1:
         * if (inInputNode(e)) {
         * parentnode = getInputNode(e);
         * } else
         * parentnode = null;
         * break;
         * case 2:
         * if (inOutputNode(e)) {
         * childnode = getOutputNode(e);
         * } else
         * childnode = null;
         * break;
         * default:
         * inNode = false;
         * break;
         * }
         * }
         * };
         * addMouseListener(ma);
         * addMouseMotionListener(ma);
         */
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePos = e.getPoint();
                /*
                 * // Convert screen coordinates to world coordinates from scale value
                 * Point2D transformed = screenToWorld(e.getPoint());
                 * int mx = (int) transformed.getX();
                 * int my = (int) transformed.getY();
                 * 
                 */

                // Check if a connection was clicked
                if (highlightedConnection != null && SwingUtilities.isLeftMouseButton(e)) {
                    connections.remove(highlightedConnection);
                    highlightedConnection = null;
                    repaint();
                    return;
                }

                BPNode clickedPoint = null;
                
                List<BPComponent> bpComponents = Arrays.stream(getComponents())
                        .filter(component -> component instanceof BPComponent)
                        .map(component -> (BPComponent) component)
                        .collect(Collectors.toList());
                        
                for (BPComponent bpc : bpComponents) {
                    // Check if the mouse is over a Blueprint component
                    if (bpc.getBounds().contains(lastMousePos) && bpc.isMovable()) {
                        selectedComponent = bpc;
                       // inAny = true;
                        return;
                    }

                    // clickedPoint = component.getConnectionPointAt(mx, my);
                    clickedPoint = bpc.getNodeAt((int) lastMousePos.getX(), (int) lastMousePos.getY());
                    if (clickedPoint != null) {

                        if (SwingUtilities.isRightMouseButton(e)) {
                           // inAny = true;
                            if (tempConnectionStart == null) {
                                tempConnectionStart = clickedPoint;
                            } else {
                                // Validate connection compatibility
                                if (tempConnectionStart.canConnectTo(clickedPoint)) {
                                    // Create a connection between two points
                                    connections.add(new Connection(tempConnectionStart, clickedPoint));
                                } else {
                                    /*
                                     * System.out.println("Conexión inválida: " +
                                     * tempConnectionStart.connectionType + "-" + tempConnectionStart.dataType +
                                     * " no puede conectarse con " +
                                     * clickedPoint.connectionType + "-" + clickedPoint.dataType);
                                     */
                                }
                                tempConnectionStart = null;
                            }
                            repaint();
                            return;
                        }
                        break;
                    }
                }
                //dispatchDesktopEvent(e);
                // selectedComponent = null;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selectedComponent = null;
                inAny = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point current = e.getPoint();
                try {
                    double dx = (current.x - lastMousePos.x); // / zoomFactor;
                    double dy = (current.y - lastMousePos.y); // / zoomFactor;
                    if (selectedComponent != null) {
                        inAny = true;
                        int selectedNodeX = selectedComponent.getX();
                        int selectedNodeY = selectedComponent.getY();
                        int xDelta = selectedNodeX + (int) dx;
                        int yDelta = selectedNodeY + (int) dy;
    
                        selectedComponent.setLocation(xDelta, yDelta);
                        repaint();
    
                        /*
                         * Update connection points
                         * for (BPNode cp : selectedComponent.getNodes().values()) {
                         * cp.updatePosition();
                         * }
                         */
                    } else {
                      //  panX += (current.x - lastMousePos.x);
                      //  panY += (current.y - lastMousePos.y);
                    }
    
                    lastMousePos = current;
                    repaint();
                } catch (NullPointerException exception) {
                    // TODO: handle exception
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                /*
                 * / Point2D transformed = screenToWorld(e.getPoint());
                 * int mx = (int) transformed.getX();
                 * int my = (int) transformed.getY();
                 */
                // Check if mouse is over a connection
                Connection prevHighlighted = highlightedConnection;
                highlightedConnection = null;

                for (Connection conn : connections) {
                    if (conn.isNearPoint((int) e.getPoint().getX(), (int) e.getPoint().getY(), 5.0)) {
                        highlightedConnection = conn;
                        conn.isHighlighted = true;
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                        break;
                    } else {
                        conn.isHighlighted = false;
                    }
                }

                // If no connection is highlighted, reset cursor and status
                if (highlightedConnection == null) {
                    setCursor(Cursor.getDefaultCursor());
                }

                // Update temporary connection end point for preview
                if (tempConnectionStart != null) {
                    lastMousePos = e.getPoint(); // Update the last mouse position for the curve preview
                }

                // Repaint only if highlighting changed or preview is active
                if (prevHighlighted != highlightedConnection || tempConnectionStart != null) {
                    repaint();
                }
            }
        });

        /*
         * addMouseWheelListener(e -> {
         * double delta = 0.1; // Zoom increment
         * double oldZoomFactor = zoomFactor;
         * 
         * if (e.getPreciseWheelRotation() < 0) {
         * zoomFactor = Math.min(zoomFactor + delta, 5.0); // Maximum zoom level
         * } else {
         * zoomFactor = Math.max(zoomFactor - delta, 0.1); // Minimum zoom level
         * }
         * 
         * // Update components only if zoom factor has changed
         * if (zoomFactor != oldZoomFactor) {
         * updateComponentSizes();
         * }
         * });
         */
        setBackground(new Color(37, 50, 58));
    }

    private void dispatchDesktopEvent(MouseEvent e) {
        Component parent = SwingUtilities.getAncestorOfClass(BPDesktop.class, BPViewport.this);
        if (parent instanceof BPDesktop) {
            MouseEvent desktopEvent = SwingUtilities.convertMouseEvent(BPViewport.this, e, parent);
            parent.dispatchEvent(desktopEvent);
        }
    }

    public List<BPNode[]> getConnections() {
        return view_manager.getConnections();
    }

    private boolean inComponent(MouseEvent e) {
        Component c = getComponentAt(e.getPoint());
        return (c != BPViewport.this && c != null);
    }

    
    public void connect(BPNode Nparent, BPNode Nchild) {
        connections.add(new Connection(Nparent, Nchild));
    }

    public void disconnect(BPNode Nparent, BPNode Nchild) {
        connections.removeIf((conn) -> {
            return conn.start == Nparent && conn.end == Nchild;
        });
    }

    public BPManager.Viewport getViewManager() {
        return view_manager;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Aplicar zoom al Graphics2D
        // g2d.scale(zoomFactor, zoomFactor);

        // Draw Components, Nodes and conections
        connections.forEach((conn) -> {
            conn.draw(g2d);
        });

        Arrays.stream(getComponents()).forEach((comp) -> {
            BPComponent bpc = (BPComponent) comp;
            bpc.draw(g2d); // Llamar al método draw de BPComponent
        });

        drawTemporaryConnection(g2d);
        g2d.dispose();
    }

    private void drawTemporaryConnection(Graphics2D g2) {
        // Draw temporary connection curve if creating a connection
        if (tempConnectionStart != null && lastMousePos != null) {
            // Point2D p = screenToWorld(lastMousePos);
            Point2D p = lastMousePos.getLocation();
            int mx = (int) p.getX();
            int my = (int) p.getY();

            // Create temporary end point for drawing
            Point2D startDir = tempConnectionStart.getDirectionVector();
            double ctrlX1 = tempConnectionStart.getX() + startDir.getX();

            // Since we don't have an end connection point yet, just use mouse position and
            // invert the start direction
            double ctrlX2 = mx - startDir.getX();

            CubicCurve2D curve = new CubicCurve2D.Double(
                    tempConnectionStart.getX(), tempConnectionStart.getY(),
                    ctrlX1, tempConnectionStart.getY(),
                    ctrlX2, my,
                    mx, my);

            // Draw with the color of the data type
            g2.setColor(Color.LIGHT_GRAY);
            g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[] { 5 }, 0));
            g2.draw(curve);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(7680, 4320);
    }

    /**
     * Adjusts the size and position of components based on the zoom level.
     */
    private void updateComponentSizes() {
        boolean componentsUpdated = false;

        for (Component comp : getComponents()) {
            Rectangle bounds = comp.getBounds();

            // Calculate new bounds based on zoom factor
            int newX = (int) (bounds.x * zoomFactor);
            int newY = (int) (bounds.y * zoomFactor);
            int newWidth = (int) (bounds.width * zoomFactor);
            int newHeight = (int) (bounds.height * zoomFactor);

            // Update component bounds only if they have changed
            if (bounds.x != newX || bounds.y != newY || bounds.width != newWidth || bounds.height != newHeight) {
                comp.setBounds(newX, newY, newWidth, newHeight);
                componentsUpdated = true;
            }
        }

        // Revalidate and repaint only if components were updated
        if (componentsUpdated) {
            revalidate();
            repaint();
        }
    }
}
