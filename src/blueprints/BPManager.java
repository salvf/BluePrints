/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blueprints;

import blueprints.ui.BPComponent;
import blueprints.ui.BPViewport;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 *
 * @author terro
 */
public abstract class BPManager {
 
    public static class Desktop extends Observable{
        private final List<BPComponent[]> connections;
        private  BPViewport bluePrintDesktop;
        
        public Desktop(ArrayList<BPComponent[]> initConnections) {
            this.connections=initConnections;
        }
        
        
        public Desktop(ArrayList<BPComponent[]> initConnections, BPViewport desktop) {
            this.connections=initConnections;
            bluePrintDesktop = desktop;
        }
        
        public  BPViewport getBPDesktop(){
            return bluePrintDesktop;
        }
        
        public List<BPComponent[]> getConnections(){
            return connections;
        }
        
        public boolean add(BPComponent[] e){
            boolean returnb =connections.add(e);
            e[0].setConnection(true);
            e[1].setConnection(true);
            setChanged();
            notifyObservers(connections);
            return returnb;
        }
        
        public BPComponent[] remove(int index){
            BPComponent[] returnb = connections.remove(index);
            returnb[0].setConnection(false);
            returnb[1].setConnection(false);
            setChanged();
            notifyObservers(connections);
            return returnb;
        }
        
        public boolean remove(Object o){
            boolean returnb = connections.remove(o);
            BPComponent[] comp = (BPComponent[])o;
            comp[0].setConnection(false);
            comp[1].setConnection(false);
            setChanged();
            notifyObservers(connections);
            return returnb;
        }
        
    }
    //public static class Component{}
    
}
