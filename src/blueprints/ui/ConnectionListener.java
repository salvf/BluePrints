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
public interface ConnectionListener {
    
    public void onConnect(BPNode outnode,BPNode inputnode);
    
    public void onDisconnect(BPNode outnode,BPNode inputnode);
}
