
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author terro
 */
public class Porpeties {
    static ObjectProperty inte = new SimpleObjectProperty();
    //ObservableValue<Integer> obsInt=inte;
    public static void main(String[] args){
        inte.addListener((ObservableValue observable, Object oldValue, Object newValue) -> {
            System.out.println(newValue.toString());
        });
        for(int a=0;a<10;a++)
            inte.set(a);
    } 
}
