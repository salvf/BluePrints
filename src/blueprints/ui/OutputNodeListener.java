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
public interface OutputNodeListener {
    public void OnChange(BPNode node, Object newValue, Object oldValue);
    
    public void OnSend(BPNode sender, BPNode receiver, Object Value);
    
}