/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blueprints.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 *
 * @author terro
 */
public class BPDesktop extends JPanel{
        private BPViewport view;
        private final JScrollPane scrl;

        public BPDesktop(BPViewport viewport) {
            this.view= viewport;
            setLayout(new BorderLayout());
            view.setAutoscrolls(true);
            scrl=new JScrollPane(view,JScrollPane.VERTICAL_SCROLLBAR_NEVER,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrl.setOpaque(false);
            scrl.setBounds(0, 0, getSize().height, getSize().width);
            
            add(scrl);
            MouseAdapter ma = new MouseAdapter() {
                
                private Point origin;
                

                @Override
                public void mousePressed(MouseEvent e) {
                    origin = new Point(e.getPoint());
                    System.out.println("For Desktop Press");
                }
                
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (origin != null && !view.inAny) {
                        JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, view);
                        if (viewPort != null) {
                            Rectangle View = viewPort.getViewRect();
                            View.x += (origin.x - e.getX());
                            View.y += (origin.y - e.getY());
                            System.out.println(View.toString());
                            view.scrollRectToVisible(View);
                        }
                    }
                }
                
            };
            view.addMouseListener(ma);
            view.addMouseMotionListener(ma);
            /*            //Center Viewport
            Rectangle bounds = scrl.getViewport().getViewRect();
            Dimension size = scrl.getViewport().getViewSize();
            int x = (size.width - bounds.width) / 2;
            int y = (size.height - bounds.height) / 2;
            scrl.getViewport().setViewPosition(new Point(x, y));*/

        }
        
    @Override
        public Dimension getPreferredSize() {
            return new Dimension(600, 400);
        }    
        
}
