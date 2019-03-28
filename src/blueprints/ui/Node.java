/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blueprints.ui;


import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author terro
 */
public interface Node{
    
    public void setValue(Object value);
        
    public SimpleObjectProperty getValue();
    
}
