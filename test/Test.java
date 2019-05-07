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

import blueprints.ui.BPComponent;
import blueprints.ui.BPDesktop;
import blueprints.ui.BPNode;
import blueprints.ui.BPViewport;
import blueprints.ui.NodeAdapter;
import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 *
 * @author Salvador Vera Franco
 */
public class Test {
    
    public static void main(String... args){
        NodeAdapter na = new NodeAdapter() {
            @Override
            public void onConnect(BPNode outnode, BPNode inputnode) {
                System.out.println("There's a new connection");
            }

            @Override
            public void OnRecive(BPNode sendernode, Object Value) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        };
        BPComponent com1 = new BPComponent();
        BPComponent com2 = new BPComponent();
        BPComponent com3 = new BPComponent();
        com1.addNode("n1", new BPNode(String.class, true));
        com1.addNode("n2", new BPNode(String.class, false));
        com1.setBounds(50, 350, 150, 40);
        com2.addNode("n1", new BPNode(String.class, true));
        com2.addNode("n0", new BPNode(String.class, true));
        com2.addNode("n2", new BPNode(String.class, false));
        com2.addNode("n3", new BPNode(String.class, false));
        com2.addNode("n4", new BPNode(String.class, false));
        com2.setBounds(400, 150, 150, 80);
        com3.addNode("n0", new BPNode(String.class, true));
        com3.setBounds(750, 50, 150, 40);
        com2.getInputNodes().entrySet().forEach((t) -> {
            t.getValue().addConnetionListener(na);
            t.getValue().addInputNodeListener(na);
        });
        
        BPViewport bpView= new BPViewport();
        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        bpView.add(com1);
        bpView.add(com2);
        bpView.add(com3);
        frame.add(new BPDesktop(bpView),BorderLayout.CENTER);
        frame.setSize(1000, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
}
