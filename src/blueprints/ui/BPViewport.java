/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blueprints.ui;

import blueprints.BPManager;
import blueprints.utils.Resizer;
import blueprints.utils.Borders.GlowUtils;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;

/**
 *
 * @author terro
 */
public class BPViewport extends JLayeredPane {

        BPManager.Desktop dek_manager;
        private final List<Shape> nodesin = new ArrayList<>();
        private final List<Shape> nodesout = new ArrayList<>();
        public boolean inAny=false;
       // private boolean drawLine=false;
       // private Point line1,line2;
        private BPComponent parent = null;
        private BPComponent child = null;

        public BPViewport() {
            dek_manager= new BPManager.Desktop(new ArrayList<>(),this); 
            setOpaque(true);
            MouseAdapter ma = new MouseAdapter() {
                private BPComponent dragComponent;
                private Point clickPoint;
                private Point offset;
                private boolean inComponent=false;
                
                private boolean inNode=false;
                private int nodetype = -1;
                private BPComponent component;
                
                public void mousePressed(MouseEvent e) {
                    if (inComponent(e)) {
                        component = (BPComponent)getComponentAt(e.getPoint());
                        dragComponent = component;
                        dragComponent.setState(BPComponent.STATE_SELECTED);
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                        if(component.isMovable()){
                            clickPoint = e.getPoint();
                            int deltaX = clickPoint.x - dragComponent.getX();
                            int deltaY = clickPoint.y - dragComponent.getY();
                            offset = new Point(deltaX, deltaY);
                            inComponent=true;
                        }
                    }
                    else {
                        inComponent=false;
                        if(inInputNode(e))
                        {
                            inNode = true;
                           // drawPreLine(inNode);
                            nodetype = 2;
                            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                            parent = getBluePrintComponent(e, true);
                        }
                        else if(inOutputNode(e))
                        {
                            inNode = true;
                         //   drawPreLine(inNode);
                            nodetype = 1;
                            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                            child = getBluePrintComponent(e, false);
                        }
                        else 
                        {
                            inNode = false; 
                           // drawPreLine(inNode);
                        }
                    }
                }
                
                public void mouseMoved(MouseEvent e) {
                    if(inInputNode(e) || inOutputNode(e) )
                    {
                        inAny=true;
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }
                    else if( inComponent(e)){
                        inAny=true;
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        BPComponent c= (BPComponent)getComponentAt(e.getPoint());
                        Resizer.cursor(e, c);
                    }
                    else {
                        inAny=false;
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }
                }
                
                public void mouseReleased(MouseEvent e) {
                    
                    if(dragComponent!=null)
                        dragComponent.setState(dragComponent.getLastState());
                    inNode = false;
                    if(parent!=null && child!=null){
                        connect(parent, child);
                        repaint();
                    }
                    parent = null;
                    child = null;
                    nodetype=-1;
                    if(inAny)
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    else
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }

                public void mouseDragged(MouseEvent e) {
                    if(inComponent){
                        int mouseX = e.getX();
                        int mouseY = e.getY();
                        int xDelta = mouseX - offset.x;
                        int yDelta = mouseY - offset.y;
                        dragComponent.setLocation(xDelta, yDelta);
                        repaint();
                    }
                    switch (nodetype) {
                        case 1:
                            if(inInputNode(e)){
                                parent=getBluePrintComponent(e, true);
                            }
                            else parent = null;
                            break;
                        case 2:
                            if(inOutputNode(e)){
                                child=getBluePrintComponent(e, false);
                            }
                            else child = null;
                            break;
                        default:
                            inNode = false;
                            break;
                    }
                }
            };

            addMouseListener(ma);
            addMouseMotionListener(ma);
            setBackground(new Color(37, 50, 58));
        }
        
        public List<BPComponent[]> getConnections(){
            return dek_manager.getConnections();
        }
        
        private boolean inInputNode(MouseEvent e){
            return nodesin.stream().anyMatch((node)->(node.contains(e.getPoint())));
        }
        
        private boolean inOutputNode(MouseEvent e){
            return nodesout.stream().anyMatch((node)->(node.contains(e.getPoint())));
        }
        
        private BPComponent getBluePrintComponent(MouseEvent e, boolean isInput){
            Component comp =null;
            if(isInput)
                comp= getComponentAt(e.getX()-50, e.getY());
            else 
                comp= getComponentAt(e.getX()+50, e.getY());
            
            if(comp!=null)
                return (BPComponent) comp;
            else 
                return null;
            
        }
        
        private boolean inComponent(MouseEvent e){
           Component c = getComponentAt(e.getPoint());
            return (c != BPViewport.this && c != null);
        }
        
        public void connect(BPComponent parent, BPComponent child) {
            if(parent!=child){
                dek_manager.add(new BPComponent[]{parent, child});
            }
        }
        
        public void addObserver(Observer o){
           dek_manager.addObserver(o);
        }
        
        public BPManager.Desktop getManager(){
           return dek_manager;
        }
        
        public void addnConnect(BPComponent parent, BPComponent child) {
            if (parent.getParent() != this) 
                add(parent);
            
            if (child.getParent() != this) 
                add(child);
            
            if(parent!=child)
                dek_manager.add(new BPComponent[]{parent, child});
        }

        public void addComponent(JComponent component){
            add(component);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            nodesin.clear();
            nodesout.clear();
            for(Component comp:getComponents()){
                Rectangle bound = comp.getBounds();
                BPComponent bpc =(BPComponent)comp;
                if(bpc.isShadow())
                    glowshadow(g2d,bpc);
                g2d.setColor(new Color(161 , 201 , 200));
                double Xout = bound.getX()+bound.getWidth()+20;
                double Xin = bound.getX() - 20;
                    if(bpc.getOutputNodes().size()>0)
                        drawCenteredCircle(g2d, (int)Xout, (int)bound.getCenterY(), 10, true);
                    if (bpc.getInputNodes().size() > 0)
                      drawCenteredCircle(g2d, (int) Xin, (int) bound.getCenterY(), 10, false);
            }
            
            dek_manager.getConnections().stream().map((connection) -> {
                Rectangle innerparent = connection[0].getBounds();
                Rectangle innerchild = connection[1].getBounds();
                GeneralPath path = new GeneralPath();
                double Xcenter=(innerparent.getCenterX()+innerchild.getCenterX())/2;
                double Xout = innerparent.getX()+innerparent.getWidth()+20;
                double Xin = innerchild.getX() - 20;
                path.moveTo(Xout, innerparent.getCenterY());
                // xs = { 75, (int)(75+375)/2, (int)(75+375)/2, 375 };
                // ys = { 400,       400,            80,         80 };
                // path.curveTo(xs[1], ys[1], xs[2], ys[2], xs[3], ys[3]);
                if( Xout+50 <= Xin || Xin > Xout +50)
                    path.curveTo(Xcenter,innerparent.getCenterY() ,Xcenter, innerchild.getCenterY(), Xin, innerchild.getCenterY());
                else
                    path.curveTo(Xout+50, innerparent.getCenterY(), Xin-50, innerchild.getCenterY(), Xin, innerchild.getCenterY());
                
                LinearGradientPaint lpg = new LinearGradientPaint(
                new Point((int)Xout, (int)innerparent.getCenterY()),
                new Point((int)Xin, (int)innerchild.getCenterY() ),
                new float[]{0.0f, 1.0f},
                new Color[]{Color.GREEN, Color.red});
                g2d.setPaint(lpg);
                
                return path;
            }).forEachOrdered((path) -> {
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.draw(path);
            });
            
            //if(drawLine){            }
            
           g2d.dispose();
        }
        
        
        
        public void glowshadow(Graphics2D g2d,BPComponent comp){
            int width = comp.getBounds().width;
            int height = comp.getBounds().height;
            Color color = comp.getShadowColor();
            int size = comp.getShadowSize();
            float alpha = comp.getShadowAlpha();
            int dx= comp.getBounds().x-size-(size/4); 
            int dy= comp.getBounds().y-size-(size/4);
            BufferedImage img = GlowUtils.generateGlow(width, height, size, color, alpha);
            g2d.drawImage(img, dx, dy, comp);
        }

        public void drawCenteredCircle(Graphics2D g, int x, int y, int r, boolean isNodeInput) {
            x = x - (r / 2);
            y = y - (r / 2);
            Shape circle = new Ellipse2D.Double(x, y, r, r);
            if(isNodeInput)
                nodesin.add(circle);
            else 
                nodesout.add(circle);
            g.fill(circle);
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(7680, 4320);
        }
       /* private void drawPreLine(boolean drawLine){
            this.drawLine=drawLine;
            
        }*/

    }
