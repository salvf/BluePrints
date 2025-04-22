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

import blueprints.utils.Resizer;
import blueprints.utils.Borders.BlurUtils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 *
 * @author Salvador Vera Franco
 */
public class BPComponent extends JPanel {
    /*
     * private final HashMap<String, BPNode> input = new HashMap<>();
     * private final HashMap<String, BPNode> output = new HashMap<>();
     */ 
    private final HashMap<String, BPNode> nodes = new HashMap<>();
    private Timer timer;
    private final Color bckgrd;
    private float dashPhase = 0f;
    public static final int STATE_WAITING = 1000;
    public static final int STATE_SELECTED = 1010;
    public static final int STATE_UNSELECTED = 0101;
    public static final int STATE_ERROR = 1001;
    public static final int STATE_RUNNING = 1111;
    private int state = STATE_WAITING;
    private int laststate = state;
    private final float dash[] = { 5.0f, 5.0f };
    private Resizer resizer;
    private boolean resize = true;
    private boolean movable = true;
    private boolean shadow = true;
    private int size = 28;
    private float alpha = 1f;
    private Color shadowcolor = Color.BLACK;
    private final int PROX_DIST = 5;

    public BPComponent() {
        this.bckgrd = new Color(0.27f, 0.37f, 0.39f, 0.7f);
        setBackground(bckgrd);
        ThreadStroke();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(getCursor() == Cursor.getDefaultCursor()){
                    setState(BPComponent.STATE_SELECTED);
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    dispatchViewportEvent(e);
                }
                
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setState(BPComponent.STATE_UNSELECTED);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                dispatchViewportEvent(e);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (getCursor() != Cursor.getDefaultCursor() || getCursor().getType() != Cursor.MOVE_CURSOR) {
                    Point p = e.getPoint();
                    Rectangle r = rect;
                    Rectangle comp = getBounds();
                    int x = comp.x, y = comp.y, w = comp.width, h = comp.height;
                    int type = getCursor().getType();
                    int dx = p.x - r.x;
                    int dy = p.y - r.y;
                    getParentViewport().inAny = true;
                    switch (type) {
                        case Cursor.N_RESIZE_CURSOR:
                            int height = r.height - dy;
                            r.setRect(r.x, r.y + dy, r.width, height);
                            y = getBounds().y + dy;
                            h = height;
                            break;
                        case Cursor.NW_RESIZE_CURSOR:
                            int width = r.width - dx;
                            height = r.height - dy;
                            r.setRect(r.x + dx, r.y + dy, width, height);
                            y = getBounds().y + r.y;
                            x = getBounds().x + r.x;
                            h = height;
                            w = width;
                            break;
                        case Cursor.W_RESIZE_CURSOR:
                            width = r.width - dx;
                            r.setRect(r.x + dx, r.y, width, r.height);
                            x = getBounds().x + r.x;
                            w = width;
                            break;
                        case Cursor.SW_RESIZE_CURSOR:
                            width = r.width - dx;
                            height = dy;
                            r.setRect(r.x + dx, r.y, width, height);
                            x = getBounds().x + r.x;
                            h = height;
                            w = width;
                            break;
                        case Cursor.S_RESIZE_CURSOR:
                            height = dy;
                            r.setRect(r.x, r.y, r.width, height);
                            h = height;
                            break;
                        case Cursor.SE_RESIZE_CURSOR:
                            width = dx;
                            height = dy;
                            r.setRect(r.x, r.y, width, height);
                            h = height;
                            w = width;
                            break;
                        case Cursor.E_RESIZE_CURSOR:
                            width = dx;
                            r.setRect(r.x, r.y, width, r.height);
                            w = width;
                            break;
                        case Cursor.NE_RESIZE_CURSOR:
                            width = dx;
                            height = r.height - dy;
                            r.setRect(r.x, r.y + dy, width, height);
                            y = getBounds().y + r.y;
                            h = height;
                            w = width;
                            break;
                        default:
                            getParentViewport().inAny = false;

                    }
        
                    setBounds(x, y, w, h);
                    getParent().repaint();
                    getParent().getGraphics().dispose();
        
                    dispatchViewportEvent(e);
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                cursor(e, BPComponent.this, true);
                dispatchViewportEvent(e);
            }     
        });
    }

    private BPViewport getParentViewport() {
        Component parent = SwingUtilities.getAncestorOfClass(BPViewport.class, this);
        if (parent instanceof BPViewport) {
            return (BPViewport) parent;
        }
        return null;
    }

    private void dispatchViewportEvent(MouseEvent e) {
        Component parent = SwingUtilities.getAncestorOfClass(BPViewport.class, BPComponent.this);
        if (parent instanceof BPViewport) {
            MouseEvent viewportEvent = SwingUtilities.convertMouseEvent(BPComponent.this, e, parent);
            parent.dispatchEvent(viewportEvent);
        }
    }

    public void setShadow(boolean shadow, Color color, int size, float alpha) {
        this.shadow = shadow;
        this.shadowcolor = color;
        this.size = size;
        this.alpha = alpha;
    }

    public void setShadow(boolean shadow, int size) {
        this.shadow = shadow;
        this.size = size;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public void setShadowValues(Color color, int size, float alpha) {
        this.size = size;
        this.shadowcolor = color;
        this.alpha = alpha;
    }

    public boolean isShadow() {
        return this.shadow;
    }

    public Color getShadowColor() {
        return this.shadowcolor;
    }

    public int getShadowSize() {
        return this.size;
    }

    public float getShadowAlpha() {
        return this.alpha;
    }

    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    public boolean isMovable() {
        return this.movable;
    }

    public void setResaizable(boolean resizable) {
        this.resize = resizable;
    }

    public boolean isResaizable() {
        return this.resize;
    }

    /*
     * @param resize true init the Resizer class, false destroy the Resizer
     */
    public void initResizable(boolean resize) {
        if (this.resize && resize && getMouseMotionListeners().length == 0) {
            resizer = new Resizer(this);
            this.addMouseMotionListener(resizer);
        } else if (!resize) {
            this.removeMouseMotionListener(resizer);
            resizer = null;
        }
    }

    public Color getBackgroundColor() {
        return bckgrd;
    }

    public void addNode(String id, BPNode node) {
        node.setParent(this);
        nodes.put(id, node);
    }

    public HashMap<String, BPNode> getInputNodes() {
        return nodes.values().stream()
                .filter(BPNode::isInput)
                .collect(HashMap::new, (m, v) -> m.put(v.getType(), v), HashMap::putAll);
    }

    public HashMap<String, BPNode> getOutputNodes() {
        return nodes.values().stream()
                .filter(n -> !n.isInput())
                .collect(HashMap::new, (m, v) -> m.put(v.getType(), v), HashMap::putAll);
    }

    public HashMap<String, BPNode> getNodes() {
        return nodes;
    }

    public BPViewport getViewport() {
        return (BPViewport) getParent();
    }

    private void ThreadStroke() {
        SwingUtilities.invokeLater(() -> {
            ActionListener listener = (ActionEvent e) -> {
                this.dashPhase += 9.0f;
                repaint();
            };
            timer = new Timer(30, listener);
            timer.start();
        });
    }

    private void state(Graphics2D g) {
        switch (state) {
            case STATE_RUNNING:
                g.setColor(new Color(126, 250, 113));
                g.setStroke(new BasicStroke(
                        3.5f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_MITER,
                        2.5f,
                        dash,
                        dashPhase));
                if (!timer.isRunning())
                    timer.start();
                break;
            case STATE_WAITING:
            case STATE_UNSELECTED:
                g.setStroke(new BasicStroke(4.5f));
                g.setColor(new Color(161, 201, 200));
                stopTimer();
                break;
            case STATE_SELECTED:
                g.setStroke(new BasicStroke(4.5f));
                g.setColor(new Color(32, 195, 201));
                stopTimer();
                break;
            case STATE_ERROR:
                g.setStroke(new BasicStroke(4.5f));
                g.setColor(new Color(235, 82, 82));
                break;
            default:
                break;
        }

    }

    private void stopTimer() {
        if (timer != null)
            if (timer.isRunning())
                timer.stop();
    }

    public void setState(int state) {
        setLastState(this.state);
        this.state = state;
        repaint();
    }

    private void setLastState(int state) {
        if (state == STATE_SELECTED)
            this.laststate = STATE_UNSELECTED;
        else
            this.laststate = state;
    }

    public int getLastState() {
        return this.laststate;
    }

    public BPNode getNodeAt(int mx, int my) {
        for (BPNode cp : nodes.values()) {
            if (cp.contains(mx, my)) {
                return cp;
            }
        }
        return null;
    }

    public Rectangle rect;
    private boolean repaint = false;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rect = new Rectangle(0, 0, getBounds().width - 1, getBounds().height - 1);
        state(g2d);
        g2d.draw(rect);
        g2d.dispose();
        // stop infinite repaint
        if (repaint)
            getParent().repaint();
        repaint = !repaint;
        rect = new Rectangle(0, 0, getBounds().width, getBounds().height);
    }

    public void draw(Graphics2D g2d) {

        drawBlurShadow(g2d);

        g2d.setColor(new Color(161, 201, 200));
        Rectangle bounds = getBounds();
        double xw = bounds.getX() + bounds.getWidth() + 20;
        double xin = bounds.getX() - 20;
        double y = bounds.getY();

        drawNodeGroup(g2d, this.getOutputNodes(), xw, y, bounds.getHeight());
        drawNodeGroup(g2d, this.getInputNodes(), xin, y, bounds.getHeight());
    }

    private void drawNodeGroup(Graphics2D g2d, Map<?, BPNode> nodes, double x, double y, double height) {
        if (nodes == null || nodes.isEmpty())
            return;

        int count = 1;
        int size = nodes.size() + 1;
        double margin = height / size;

        for (BPNode node : nodes.values()) {
            double nodeY = y + (margin * count++);
            node.setPosition(x, nodeY);
            node.draw(g2d);
        }
    }

    private void drawBlurShadow(Graphics2D g2d) {
        if (shadow) {
            int width = this.getBounds().width;
            int height = this.getBounds().height;
            Color color = this.getShadowColor();
            int size = this.getShadowSize();
            float alpha = this.getShadowAlpha();
            int dx = this.getBounds().x - size - (size / 4);
            int dy = this.getBounds().y - size - (size / 4);
            BufferedImage img = BlurUtils.generateBlur(width, height, size, color, alpha);
            g2d.drawImage(img, dx, dy, this);
        }
    }

    private void cursor(MouseEvent e, BPComponent component, boolean innerListener) {
        Point p = e.getPoint();
        Rectangle r = (innerListener) ? component.rect : component.getBounds();
        if (!isOverRect(p, r)) {
            if (component.getCursor() != Cursor.getDefaultCursor()) {
                component.setCursor(Cursor.getDefaultCursor());
                //component.initResizable(false);
            }
            return;
        }
        int outcode = getOutcode(p, r);
        switch (outcode) {
            case Rectangle.OUT_TOP:
                if (Math.abs(p.y - r.y) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                            Cursor.N_RESIZE_CURSOR));
                    //component.initResizable(true);
                }
                break;
            case Rectangle.OUT_TOP + Rectangle.OUT_LEFT:
                if (Math.abs(p.y - r.y) < PROX_DIST &&
                        Math.abs(p.x - r.x) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                            Cursor.NW_RESIZE_CURSOR));
                    //component.initResizable(true);
                }
                break;
            case Rectangle.OUT_LEFT:
                if (Math.abs(p.x - r.x) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                            Cursor.W_RESIZE_CURSOR));
                    //component.initResizable(true);
                }
                break;
            case Rectangle.OUT_LEFT + Rectangle.OUT_BOTTOM:
                if (Math.abs(p.x - r.x) < PROX_DIST &&
                        Math.abs(p.y - (r.y + r.height)) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                            Cursor.SW_RESIZE_CURSOR));
                    //component.initResizable(true);
                }
                break;
            case Rectangle.OUT_BOTTOM:
                if (Math.abs(p.y - (r.y + r.height)) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                            Cursor.S_RESIZE_CURSOR));
                    //component.initResizable(true);
                }
                break;
            case Rectangle.OUT_BOTTOM + Rectangle.OUT_RIGHT:
                if (Math.abs(p.x - (r.x + r.width)) < PROX_DIST &&
                        Math.abs(p.y - (r.y + r.height)) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                            Cursor.SE_RESIZE_CURSOR));
                    //component.initResizable(true);
                }
                break;
            case Rectangle.OUT_RIGHT:
                if (Math.abs(p.x - (r.x + r.width)) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                            Cursor.E_RESIZE_CURSOR));
                    //component.initResizable(true);
                }
                break;
            case Rectangle.OUT_RIGHT + Rectangle.OUT_TOP:
                if (Math.abs(p.x - (r.x + r.width)) < PROX_DIST &&
                        Math.abs(p.y - r.y) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                            Cursor.NE_RESIZE_CURSOR));
                    //component.initResizable(true);
                }
                break;
            default:
                component.setCursor(Cursor.getDefaultCursor());
                //component.initResizable(false);
                break;
        }

    }

    /**
     * Make a smaller Rectangle and use it to locate the
     * cursor relative to the Rectangle center.
     */
    private int getOutcode(Point p, Rectangle rect) {
        Rectangle r = (Rectangle) rect.clone();
        r.grow(-PROX_DIST, -PROX_DIST);
        return r.outcode(p.x, p.y);
    }

    /**
     * Make a larger Rectangle and check to see if the
     * cursor is over it.
     */
    private boolean isOverRect(Point p, Rectangle rect) {
        Rectangle r = (Rectangle) rect.clone();
        r.grow(PROX_DIST, PROX_DIST);
        return r.contains(p);
    }

}
