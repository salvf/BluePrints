/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package observers;

import java.util.Observable;

/**
 *
 * @author Jonathan
 */
public class Observado extends Observable{
    String mensaje;
    
    public Observado(){
        mensaje = "Objeto Observado Iniciado";
    }
    
    public void cambiarMensaje(String m){
        mensaje = m;
        //Marcamos el objeto observable como objeto que ha cambiado
        setChanged();
        //Notificamos a los observadores y le enviamos el nuevo valor
        notifyObservers(mensaje);
        //notifyObservers(); Este metodo solo notifica que hubo cambios en el objeto
    }
}

