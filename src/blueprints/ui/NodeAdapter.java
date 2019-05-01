/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blueprints.ui;

/**
 *
 * @author terro
 */
public abstract class NodeAdapter implements InputNodeListener, OutputNodeListener, ConnectionListener{
    
    public void OnRecive(BPNode sendernode, Object newValue, Object oldValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void OnChange(BPNode node, Object newValue, Object oldValue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    public void OnSend(BPNode sender, BPNode receiver, Object Value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onConnect(BPNode outnode, BPNode inputnode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onDisconnect(BPNode outnode, BPNode inputnode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
