package blueprints.ui;

import blueprints.utils.Resizer;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 *
 * @author terro
 */
public class BPComponent extends JPanel{
        private final HashMap<String,BPNode> input = new HashMap<>();
        private final HashMap<String,BPNode> output = new HashMap<>();
        private final String name;
        private Timer timer;
        private final Color bckgrd;
        private float dashPhase = 0f;
        public static final int STATE_WAITING = 1000;
        public static final int STATE_SELECTED = 1010;
        public static final int STATE_UNSELECTED = 0101;
        public static final int STATE_ERROR = 1001;
        public static final int STATE_RUNNING = 1111;
        private int state = STATE_WAITING;
        private int laststate = state;
        private final float dash[] = {5.0f,5.0f};
        private boolean connected=false;
        private Resizer resizer;
        private boolean resize=true;
        private boolean movable=true;
        private boolean shadow = true;
        private int size = 28;
        private float alpha = 1f;
        private Color shadowcolor = Color.BLACK;
        public BPComponent(String name) {
            this.name=name;
            this.bckgrd= new Color(0.27f, 0.37f, 0.39f,0.7f);
            setBackground(bckgrd);
            ThreadStroke();
        }
        
        
        public void setShadow(boolean shadow,Color color, int size, float alpha){
            this.shadow = shadow;
            this.shadowcolor = color;
            this.size = size;
            this.alpha = alpha;
        }
        
        public void setShadow(boolean shadow,int size){
            this.shadow = shadow;
            this.size = size;
        }
        
        public void setShadow(boolean shadow){
            this.shadow = shadow;
        }
        
        public void setShadowValues(Color color, int size, float alpha){
            this.size = size;
            this.shadowcolor = color;
            this.alpha = alpha;
        }
        
        public boolean isShadow(){
            return this.shadow;
        }
        
        public Color getShadowColor(){
            return this.shadowcolor;
        }
        
        public int getShadowSize(){
            return this.size;
        }
        
        public float getShadowAlpha(){
            return this.alpha;
        }
        
        public void setMovable(boolean movable){
            this.movable=movable;
        }
        
        public boolean isMovable(){
            return this.movable;
        }
        
        public void setResaizable(boolean resizable){
            this.resize=resizable;
        }
        
        public boolean isResaizable(){
            return this.resize;
        }
        
        /*
         * @param resize true init the Resizer class, false destroy the Resizer
         */
        public void initResizable(boolean resize){
            if(this.resize&&resize&&getMouseMotionListeners().length==0){
                resizer=new Resizer(this);
                this.addMouseMotionListener(resizer);
            }else if(!resize){
                this.removeMouseMotionListener(resizer);
                resizer=null;
            }
        }
        
        public Color getBackgroundColor(){
            return bckgrd;
        }
        
        @Override
        public String getName(){
            return name;
        }
        
        public boolean isConnected(){
            return connected;
        }
        
        public void setConnection(boolean connected){
            this.connected=connected;
        } 

        public void addNode(String id,BPNode node){
            if(node.isInput())
                input.put(id,node);
            else 
                output.put(id,node);
        }
        
        public Object getOutputValue(String nodeid){
            return output.get(nodeid).getValue();
        }
        
        public void setOutputValue(String nodeid,Object value){
            output.get(nodeid).setValue(value);
        }
        
        public Object getInputValue(String nodeid){
            return input.get(nodeid).getValue();
        }
        
        public void setInputValue(String nodeid,Object value){
            input.get(nodeid).setValue(value);
        }

        public HashMap<String,BPNode> getInputNodes() {
           return input;
        }

        public HashMap<String,BPNode> getOutputNodes() {
            return output;
        }
        
        public BPViewport getDesktop(){
            return (BPViewport)getParent();
        }
        
        private void ThreadStroke(){
            SwingUtilities.invokeLater(() -> {
                ActionListener listener = (ActionEvent e) -> {
                    this.dashPhase += 9.0f;
                    repaint();
                };
                timer = new Timer(30, listener);
                timer.start();
            });
        }
        
        private void state(Graphics2D g){
            switch (state) {
                case STATE_RUNNING:
                    g.setColor(new Color(126 , 250 , 113));
                    g.setStroke(new BasicStroke(
                            3.5f,
                            BasicStroke.CAP_ROUND,
                            BasicStroke.JOIN_MITER,
                            2.5f,
                            dash,
                            dashPhase
                    )); if(!timer.isRunning())
                        timer.start();
                    break;
                case STATE_WAITING:
                case STATE_UNSELECTED:
                    g.setStroke(new BasicStroke(4.5f));
                    g.setColor(new Color(161 , 201 , 200));
                    stopTimer();
                    break;
                case STATE_SELECTED:
                    g.setStroke(new BasicStroke(4.5f));
                    g.setColor(new Color(32 , 195 , 201));
                    stopTimer();
                    break;
                case STATE_ERROR:
                    g.setStroke(new BasicStroke(4.5f));
                    g.setColor(new Color(235 , 82 , 82));
                    break;
                default:
                    break;
            }
            
        }
        
        private void stopTimer(){
            if(timer!=null)
                if(timer.isRunning())
                     timer.stop();
        }
        
        public void setState(int state){
            setLastState(this.state);
            this.state=state;
            repaint();
        }
        
        private void setLastState(int state){
            if(state==STATE_SELECTED)
                this.laststate=STATE_UNSELECTED;
            else 
                this.laststate=state;
        }
        
        public int getLastState(){
            return this.laststate;
        }
        
        public Rectangle rect;
        
        @Override
        public void paint(Graphics g){
            super.paint(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
             rect= new Rectangle(0, 0, getBounds().width-1, getBounds().height-1);
            state(g2d);
            g2d.draw(rect);
            g2d.dispose();
            getParent().repaint();
            rect= new Rectangle(0, 0, getBounds().width, getBounds().height);
        }

    }
