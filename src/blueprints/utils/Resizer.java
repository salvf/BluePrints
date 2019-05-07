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
package blueprints.utils;

import blueprints.ui.BPComponent;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Salvador Vera Franco
 */
public class Resizer extends MouseAdapter {
    BPComponent component;
    private static final int PROX_DIST = 5;
 
    public Resizer(BPComponent r) {
        component = r;
    }
 
    @Override
    public void mouseDragged(MouseEvent e) {
        if(component.getCursor() != Cursor.getDefaultCursor()) {
            Point p = e.getPoint();
            Rectangle r = component.rect;
            Rectangle comp = component.getBounds();
            int x=comp.x,y=comp.y,w=comp.width,h=comp.height;
            int type = component.getCursor().getType();
            int dx = p.x - r.x;
            int dy = p.y - r.y;
            switch(type) {
                case Cursor.N_RESIZE_CURSOR:
                    int height = r.height - dy;
                    r.setRect(r.x, r.y+dy, r.width, height);
                    y=component.getBounds().y+dy;
                    h=height;
                    break;
                case Cursor.NW_RESIZE_CURSOR:
                    int width = r.width - dx;
                    height = r.height - dy;
                    r.setRect(r.x+dx, r.y+dy, width, height);
                    y=component.getBounds().y+r.y;
                    x=component.getBounds().x+r.x;
                    h=height;
                    w=width;
                    break;
                case Cursor.W_RESIZE_CURSOR:
                    width = r.width - dx;
                    r.setRect(r.x+dx, r.y, width, r.height);
                    x=component.getBounds().x+r.x;
                    w=width;
                    break;
                case Cursor.SW_RESIZE_CURSOR:
                    width = r.width - dx;
                    height = dy;
                    r.setRect(r.x+dx, r.y, width, height);
                    x=component.getBounds().x+r.x;
                    h=height;
                    w=width;
                    break;
                case Cursor.S_RESIZE_CURSOR:
                    height = dy;
                    r.setRect(r.x, r.y, r.width, height);
                    h=height;
                    break;
                case Cursor.SE_RESIZE_CURSOR:
                    width = dx;
                    height = dy;
                    r.setRect(r.x, r.y, width, height);
                    h=height;
                    w=width;
                    break;
                case Cursor.E_RESIZE_CURSOR:
                    width = dx;
                    r.setRect(r.x, r.y, width, r.height);
                    w=width;
                    break;
                case Cursor.NE_RESIZE_CURSOR:
                    width = dx;
                    height = r.height - dy;
                    r.setRect(r.x, r.y+dy, width, height);
                    y=component.getBounds().y+r.y;
                    h=height;
                    w=width;
                    break;
                default:
                    System.out.println("unexpected type: " + type);
            }
            
            component.setBounds(x,y,w,h);
            component.getParent().repaint();
            component.getParent().getGraphics().dispose();
        }
    }
 
    @Override
    public void mouseMoved(MouseEvent e) {
        cursor(e, component, true);
    }
    
    public static void cursor(MouseEvent e,BPComponent component){
        cursor(e, component, false);
    }
    
    private static void cursor(MouseEvent e,BPComponent component,boolean innerListener){
        Point p = e.getPoint();
        Rectangle r = (innerListener)?component.rect:component.getBounds();
        if(!isOverRect(p,r)) {
            if(component.getCursor() != Cursor.getDefaultCursor()) {
                component.setCursor(Cursor.getDefaultCursor());
                component.initResizable(false);
            }
            return;
        }
        int outcode = getOutcode(p,r);
        switch(outcode) {
            case Rectangle.OUT_TOP:
                if(Math.abs(p.y - r.y) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.N_RESIZE_CURSOR));
                    component.initResizable(true);
                }
                break;
            case Rectangle.OUT_TOP + Rectangle.OUT_LEFT:
                if(Math.abs(p.y - r.y) < PROX_DIST &&
                   Math.abs(p.x - r.x) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.NW_RESIZE_CURSOR));
                    component.initResizable(true);
                }
                break;
            case Rectangle.OUT_LEFT:
                if(Math.abs(p.x - r.x) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.W_RESIZE_CURSOR));
                    component.initResizable(true);
                }
                break;
            case Rectangle.OUT_LEFT + Rectangle.OUT_BOTTOM:
                if(Math.abs(p.x - r.x) < PROX_DIST &&
                   Math.abs(p.y - (r.y+r.height)) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.SW_RESIZE_CURSOR));
                    component.initResizable(true);
                }
                break;
            case Rectangle.OUT_BOTTOM:
                if(Math.abs(p.y - (r.y+r.height)) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.S_RESIZE_CURSOR));
                    component.initResizable(true);
                }
                break;
            case Rectangle.OUT_BOTTOM + Rectangle.OUT_RIGHT:
                if(Math.abs(p.x - (r.x+r.width)) < PROX_DIST &&
                   Math.abs(p.y - (r.y+r.height)) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.SE_RESIZE_CURSOR));
                    component.initResizable(true);
                }
                break;
            case Rectangle.OUT_RIGHT:
                if(Math.abs(p.x - (r.x+r.width)) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.E_RESIZE_CURSOR));
                    component.initResizable(true);
                }
                break;
            case Rectangle.OUT_RIGHT + Rectangle.OUT_TOP:
                if(Math.abs(p.x - (r.x+r.width)) < PROX_DIST &&
                   Math.abs(p.y - r.y) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.NE_RESIZE_CURSOR));
                    component.initResizable(true);
                }
                break;
            default:   
                component.setCursor(Cursor.getDefaultCursor());
                component.initResizable(false);
                break;
        }
        
    }
 
    
    /**
     * Make a smaller Rectangle and use it to locate the
     * cursor relative to the Rectangle center.
     */
    private static int getOutcode(Point p,Rectangle rect) {
        Rectangle r = (Rectangle)rect.clone();
        r.grow(-PROX_DIST, -PROX_DIST);
        return r.outcode(p.x, p.y);        
    }
 
    /**
     * Make a larger Rectangle and check to see if the
     * cursor is over it.
     */
    private static boolean isOverRect(Point p,Rectangle rect) {
        Rectangle r = (Rectangle)rect.clone();
        r.grow(PROX_DIST, PROX_DIST);
        return r.contains(p);
    }
}

