/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package observers;

import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author Jonathan
 */
public class Observador implements Observer{

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Nueva Actualizacion: "+o+" -> "+arg);
    }
    
}
