package demo.Escritor_Lector;



import blueprints.ui.BPComponent;
import blueprints.ui.BPDesktop;
import blueprints.ui.BPViewport;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Semaphore;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author terro
 */
public class Main {
    
    public static BPViewport bluePrintView;
    public static HashMap<String,Thread> threadlector=new HashMap<>();
    public static HashMap<String,Thread> threadescritor=new HashMap<>();
    static Semaphore semaforo = new Semaphore(10);
    static Semaphore semaforo1 = new Semaphore(20);
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException  ex) {  }
            BibliotecaBP biblioteca = new BibliotecaBP("Biblioteca");
            biblioteca.setBounds(405 ,191, 182, 228);
            bluePrintView = new BPViewport();
            PopUp pop=new PopUp();
            bluePrintView.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseClicked(MouseEvent e) {
                    if(SwingUtilities.isRightMouseButton(e)){
                        pop.setX(e.getX());
                        pop.setY(e.getY());
                        pop.show(bluePrintView, e.getX(), e.getY());
                    }
                }
            });
            bluePrintView.addComponent(biblioteca);

            BarTool bar=new BarTool();
            bluePrintView.addObserver(bar);
                    
            biblioteca.addInputNode("ANY");
            biblioteca.addOutputNode("ANY");
            
            biblioteca.setOutputValue("hola");
            
            //System.out.println(""+biblioteca.getOutputValue());
            
            JFrame frame = new JFrame("ESCRITOR LECTOR BluePrint Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new BPDesktop(bluePrintView),BorderLayout.CENTER);
            frame.add(bar,BorderLayout.NORTH);
            frame.setSize(1000, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
               
    }
    
    //      POPUP CLASS
    public static class PopUp extends JPopupMenu{
        JMenuItem lector;
        JMenuItem escritor;
        private int x=0;
        private int y=0;
        private int esc=1;
        private int lec =1;
        public PopUp(){
            lector = new JMenuItem("Agregar Lector");
            escritor = new JMenuItem("Agregar Escritor");
            
            lector.addActionListener((ActionEvent e) -> {
                LectorBP L1=new LectorBP("Lector "+lec);
                L1.setBounds(x, y, 150, 40);
                L1.addInputNode("ANY");
                int posix=threadlector.size();
                threadlector.put(L1.getName(),new Thread(L1,L1.getName()));                
                bluePrintView.add(L1);
                L1=null;
                lec++;
            });
            
            escritor.addActionListener((ActionEvent e) -> {
                EscritorBP E1=new EscritorBP("Escritor "+esc);
                E1.setBounds(x, y, 150, 40);
                E1.addOutputNode("ANY");
                int posix=threadescritor.size();
                threadescritor.put(E1.getName(),new Thread(E1,E1.getName()));
                bluePrintView.add(E1);
                E1=null;
                esc++;
            });
            
            add(escritor);
            add(lector);
    }
        public void setX(int x){
            this.x=x;
        }
        public void setY(int y){
            this.y=y;
        }
    }
    
    //      ESCRITOR CLASS
    public static class EscritorBP extends BPComponent implements Runnable{
        private final JLabel lb;
        private final JLabel es;
        public EscritorBP(String name) {
            super(name);
            lb= new JLabel(name);
            es= new JLabel("");
            lb.setVerticalAlignment(JLabel.CENTER);
            lb.setForeground(new Color(161 , 201 , 200));
            es.setVerticalAlignment(JLabel.CENTER);
            es.setForeground(new Color(255,255,51));

            add(lb);
            add(es,BorderLayout.CENTER);
        }
        
        public void setLibro(String text){
            es.setText(text);
        } 

        @Override
        public void run() {
                while(isConnected()){
                    try {
                        semaforo.acquire();
                        setState(BPComponent.STATE_RUNNING);
                        Random r = new Random();
                        char c = (char)(r.nextInt(26) + 'A');
                        char ch = (char)(r.nextInt(26) + 'A');
                        r=null;
                        String l= "Libro "+c+ch;
                        es.setText("Esta escribiendo "+l);
                        Thread.sleep((BarTool.velocidad.getValue()/2)*200);
                        es.setText("");
                        Biblioteca.addLibro(l);
                        setState(BPComponent.STATE_WAITING);
                        semaforo.release();
                        Thread.sleep((BarTool.velocidad.getValue()/2)*100);
                    } catch (InterruptedException ex) {}
                }
                setState(BPComponent.STATE_WAITING);
            }
    }
    
    
    //      LECTOR CLASS
    public static class LectorBP extends BPComponent implements Runnable{
        private final JLabel lb;
        private final JLabel es;
        public LectorBP(String name) {
            super(name);
            lb= new JLabel(name);
            es= new JLabel("");
            lb.setVerticalAlignment(JLabel.CENTER);
            lb.setForeground(new Color(161 , 201 , 200));
            es.setVerticalAlignment(JLabel.CENTER);
            es.setForeground(new Color(255,255,51));

            add(lb);
            add(es,BorderLayout.CENTER);
        }
                
        public void setLibro(String text){
            es.setText(text);
        }

        @Override
        public void run() {
                while(isConnected()){
                    try {
                        if(Biblioteca.model.size()>0){
                            semaforo1.acquire();
                            Thread.sleep((BarTool.velocidad.getValue()/2)*50);
                            String s=Biblioteca.leerLibro();
                            if(!s.equals("")){
                                setState(BPComponent.STATE_RUNNING);
                                es.setText("Esta leyendo "+s);
                                Thread.sleep((BarTool.velocidad.getValue()/2)*350);
                                es.setText("");
                                semaforo1.release();
                                setState(BPComponent.STATE_WAITING);
                                Thread.sleep(3500);
                            }
                            else Thread.sleep(1000);
                        }
                        else Thread.sleep(2000);
                    } catch (InterruptedException ex) {}
                }
                setState(BPComponent.STATE_WAITING);
            }
    }
    
    
    //      BIBLIOTECA CLASS
    public static class BibliotecaBP extends BPComponent{
        private final Biblioteca b;
        public BibliotecaBP(String name) {
           super(name);
           b=new Biblioteca();
           setBounds(b.getBounds());
           add(b);
        } 
    }
}
