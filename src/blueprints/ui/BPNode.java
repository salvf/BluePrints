/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blueprints.ui;

import java.util.ArrayList;

/**
 *
 * @author terro
 */
public class BPNode<T> implements Node<T>{

        private T value; 
        private final Class<T> type;
        private final boolean isInput; 
        private final ArrayList<OutputNodeListener> outlisteners= new ArrayList<>();
        private final ArrayList<InputNodeListener> inlisteners= new ArrayList<>();
        
        public BPNode(Class<T> type,boolean isInput){
            this.isInput=isInput;
            this.type=type;
        }
        
        @Override
        public void setValue(T value){
            if(equalsType(value)){
                T oldvalue = this.value;
                this.value = value;
                if(!isInput)
                    outlisteners.forEach((l)->{
                        l.OnChange(this, oldvalue, this.value);
                    });
                else 
                    inlisteners.forEach((l)->{
                        l.OnRecive(this, this.value);
                    });
            }else throw new NodeTypeException("The value of this node is of different type than the value entered");
        }
        
        @Override
        public T getValue(){
            return this.value;
        } 
        
        public String getType(){
            return this.type.getSimpleName();
        }
                
        public boolean addInputNodeListener(NodeAdapter listener){
            return inlisteners.add(listener);
        }
        
        public boolean addOutputNodeListener(OutputNodeListener listener){
            return outlisteners.add(listener);
        }
        
        public boolean isInput(){
            return this.isInput;
        }
        
        private boolean equalsType(T value){
            return value.getClass().getSimpleName().toLowerCase().contains(
                    getType().toLowerCase());
        }


    }