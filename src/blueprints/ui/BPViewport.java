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
import blueprints.utils.Borders.BlurUtils;
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
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JLayeredPane;

/**
 *
 * @author Salvador Vera Franco
 */
public class BPViewport extends JLayeredPane {

        BPManager.Viewport view_manager;
        private final ArrayList<BPNode> innodes= new ArrayList<>();
        private final ArrayList<BPNode>  outnodes= new ArrayList<>();
        public boolean inAny=false;
       // private boolean drawLine=false;
       // private Point line1,line2;
        
        private BPNode parentnode = null;
        private BPNode childnode = null;
        
        private double margin;

        public BPViewport() {
            view_manager= new BPManager.Viewport(new ArrayList<>(),this); 
            setOpaque(true);
            setDoubleBuffered(true);
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
                            parentnode = getInputNode(e);
                        }
                        else if(inOutputNode(e))
                        {
                            inNode = true;
                         //   drawPreLine(inNode);
                            nodetype = 1;
                            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                            childnode = getOutputNode(e);
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
                        repaint();
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
                    if(parentnode!=null && childnode!=null){
                        connect(parentnode,childnode);
                        repaint();
                    }
                    parentnode = null;
                    childnode = null;
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
                                parentnode=getInputNode(e);
                            }
                            else parentnode = null;
                            break;
                        case 2:
                            if(inOutputNode(e)){
                                childnode=getOutputNode(e);
                            }
                            else childnode = null;
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
        
        public List<BPNode[]> getConnections(){
            return view_manager.getConnections();
        }
        
        private BPNode getInputNode(MouseEvent e){
            return innodes.stream().parallel().filter((t) -> 
                t.getGraphNode().contains(e.getPoint()) 
            ).findFirst().get();
        }
        
        private BPNode getOutputNode(MouseEvent e){
            return outnodes.stream().parallel().filter((t) -> 
                t.getGraphNode().contains(e.getPoint()) 
            ).findFirst().get();
        }
        
        private boolean inInputNode(MouseEvent e){
            return innodes.stream().parallel().anyMatch((t) -> {
                return t.getGraphNode().contains(e.getPoint());
            });
        }
        
        private boolean inOutputNode(MouseEvent e){
            return outnodes.stream().parallel().anyMatch((t) -> {
                return t.getGraphNode().contains(e.getPoint());
            });
        }
        
        private boolean inComponent(MouseEvent e){
           Component c = getComponentAt(e.getPoint());
            return (c != BPViewport.this && c != null);
        }
        
        public void connect(BPNode Nparent, BPNode Nchild) {
            if(Nparent!=Nchild){
                view_manager.add(new BPNode[]{Nparent, Nchild});
            }
        }
        
        public BPManager.Viewport getViewManager(){
           return view_manager;
        }
        
        private double count = 1;

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            innodes.clear();
            outnodes.clear();
            
            //Draw Nodes
            Arrays.stream(getComponents()).forEach((comp) -> {
                Rectangle bound = comp.getBounds();
                BPComponent bpc =(BPComponent)comp;
                if(bpc.isShadow())
                    blurshadow(g2d,bpc);
                g2d.setColor(new Color(161 , 201 , 200));
                double xw = bound.getX()+bound.getWidth()+20;
                double xin = bound.getX() - 20;
                int size =0;
                if(bpc.getOutputNodes().size()>0){
                    size =(bpc.getOutputNodes().size()+1);
                    margin=(bound.getHeight()/size);
                    bpc.getOutputNodes().entrySet().forEach((t) -> {
                        g2d.fill(t.getValue().setGraphNode(centeredNode(
                           xw, bound.getY()+(margin*count), 10)));
                        outnodes.add(t.getValue());
                        count++;
                    });
                    count=1;
                }    
                if (bpc.getInputNodes().size() > 0){
                    size =(bpc.getInputNodes().size()+1);
                    margin=(bound.getHeight()/size);
                    bpc.getInputNodes().entrySet().forEach((t) -> {
                        g2d.fill(t.getValue().setGraphNode(centeredNode(
                        xin, bound.getY()+(margin*count), 10)));
                        innodes.add(t.getValue());
                        count++;
                    });
                    count=1;
                }  
            });
            
            //Draw Connections  
            view_manager.getConnections().stream().parallel().map((connection) -> {
                Rectangle innerparent = connection[1].getGraphNode().getBounds();
                Rectangle innerchild = connection[0].getGraphNode().getBounds();
                GeneralPath path = new GeneralPath();
                double Xcenter=(innerparent.getCenterX()+innerchild.getCenterX())/2;
                double Xout = innerparent.getX()+innerparent.getWidth();
                double Xin = innerchild.getX();
                double xs[]={Xout,Xcenter,Xcenter,Xin};
                double ys[]={innerparent.getCenterY(),innerchild.getCenterY()};
                path.moveTo(xs[0], ys[0]);

                if( xs[0] <= xs[3] || xs[3] > xs[0]){
                    xs[1]=Xcenter;
                    xs[2]=xs[1];
                }
                else{
                    xs[1]=(xs[0]-Xcenter)+xs[0];
                    xs[2]=(xs[3]-Xcenter)+xs[3];
                }
                path.curveTo(xs[1], ys[0], xs[2], ys[1], xs[3], ys[1]);
                return path;
            }).forEachOrdered((path) -> {
                g2d.setColor(new Color(41,200,114));
                g2d.setStroke(new BasicStroke(5.0f));
                g2d.draw(path);
            });
            //if(drawLine){            } 
            g2d.dispose();
        }
        
        
        
        public void blurshadow(Graphics2D g2d,BPComponent comp){
            int width = comp.getBounds().width;
            int height = comp.getBounds().height;
            Color color = comp.getShadowColor();
            int size = comp.getShadowSize();
            float alpha = comp.getShadowAlpha();
            int dx= comp.getBounds().x-size-(size/4); 
            int dy= comp.getBounds().y-size-(size/4);
            BufferedImage img = BlurUtils.generateBlur(width, height, size, color, alpha);
            g2d.drawImage(img, dx, dy, comp);
        }

        public Ellipse2D.Double centeredNode(double x, double y, int r) {
            x = x - (r / 2);
            y = y - (r / 2);
            return new Ellipse2D.Double(x, y, r, r);
        }
        
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(7680, 4320);
        }
       /* private void drawPreLine(boolean drawLine){
            this.drawLine=drawLine;
            
        }*/

    }
