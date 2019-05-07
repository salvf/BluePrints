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

package blueprints;

import blueprints.ui.BPNode;
import blueprints.ui.BPViewport;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Salvador Vera Franco
 */
public abstract class BPManager {
 
    public static class Viewport{
        private final List<BPNode[]> connections;
        private  BPViewport bpView;
        
        public Viewport(ArrayList<BPNode[]> initConnections) {
            this.connections=initConnections;
        }
        
        
        public Viewport(ArrayList<BPNode[]> initConnections, BPViewport view) {
            this.connections=initConnections;
            bpView = view;
        }
        
        public  BPViewport getBPViewport(){
            return bpView;
        }
        
        public List<BPNode[]> getConnections(){
            return connections;
        }
        
        public boolean add(BPNode[] e){
            boolean returnb =connections.add(e);
            e[0].setConnection(true,e[1]);
            e[1].setConnection(true,e[0]);
            return returnb;
        }
        
        public BPNode[] remove(int index){
            BPNode[] returnb = connections.remove(index);
            returnb[0].setConnection(false,returnb[1]);
            returnb[1].setConnection(false,returnb[0]);
            return returnb;
        }
        
        public boolean remove(Object o){
            boolean returnb = connections.remove((BPNode[])o);
            BPNode[] comp = (BPNode[])o;
            comp[0].setConnection(false,comp[1]);
            comp[1].setConnection(false,comp[0]);
            return returnb;
        }
        
    }
    
}
