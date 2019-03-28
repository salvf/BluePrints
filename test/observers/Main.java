/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package observers;

public class Main {
    public static void main(String args[]){
        Observado observado = new Observado();
        Observador observador = new Observador();
        
        observado.addObserver(observador);
        
        observado.cambiarMensaje("Cambio 1");
        observado.cambiarMensaje("Cambio 2");
        observado.cambiarMensaje("Cambio 3");
    }
}
