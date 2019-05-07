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
 * @author Salvador Vera Franco
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
                }
                
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (origin != null && !view.inAny) {
                        JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, view);
                        if (viewPort != null) {
                            Rectangle View = viewPort.getViewRect();
                            View.x += (origin.x - e.getX());
                            View.y += (origin.y - e.getY());
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
