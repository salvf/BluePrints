package demo.Escritor_Lector;


import blueprints.ui.BPComponent;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

/**
 *
 * @author terro
 */
public class Biblioteca extends javax.swing.JPanel{

    /**
     * Creates new form Biblioteca
     */
    public static DefaultListModel<String> model = new DefaultListModel<>() ;
    BPComponent comp;
    public static ArrayList<String> arrayLibros= new ArrayList<>();
    
    public Biblioteca() {
        initComponents();
        setBackground(new Color(0.0f,0.0f,0.0f,0.0f));
        libros.setModel(model);
    }
    
    public static synchronized void addLibro(String libro){
            model.addElement(libro);
    }
    
    public static synchronized String leerLibro(){
        String s="";
        if(model.size()>0){
            s= model.remove(0);
        }    
        return s;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        libros = new javax.swing.JList<>();

        jLabel1.setForeground(new java.awt.Color(161, 200, 201));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Biblioteca");

        libros.setBackground(new java.awt.Color(0, 102, 102));
        libros.setForeground(new java.awt.Color(51, 255, 255));
        libros.setModel(new javax.swing.AbstractListModel<String>() {
            ArrayList<String> strings = arrayLibros;
            public int getSize() { return strings.size(); }
            public String getElementAt(int i) { return strings.get(i); }
        });
        libros.setSelectionBackground(new java.awt.Color(102, 102, 102));
        jScrollPane1.setViewportView(libros);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

   
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel jLabel1;
    public javax.swing.JScrollPane jScrollPane1;
    public static javax.swing.JList<String> libros;
    // End of variables declaration//GEN-END:variables

}
