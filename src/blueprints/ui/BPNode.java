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
public class BPNode implements Node{
        private final SimpleObjectProperty value = new SimpleObjectProperty();
        private String type = ""; 
        public BPNode(String TYPE){
            this.type=TYPE;
        }
        
        @Override
        public void setValue(Object value){
            this.value.set(value);
        }
        
        @Override
        public SimpleObjectProperty getValue(){
            return this.value;
        } 
        
        public String getType(){
            return this.type;
        }


    }