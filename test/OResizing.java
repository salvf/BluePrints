import java.awt.*;
import java.awt.event.*;
import java.awt.geom.GeneralPath;
import javax.swing.*;
 
public class OResizing extends JPanel {
    Rectangle rect ;

    public OResizing(int x,int y,int w, int h) {
        this.setLocation(x, y);
        this.setSize(w,h);
        this.setBackground(Color.red);
        rect= new Rectangle(0,0,this.getBounds().width,this.getBounds().height);
    }
    
    
 
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(Color.blue);
        //g2.setStroke(new BasicStroke(15.0f));
        //g2.fill(r ect);
        rect=new Rectangle(0, 0, this.getWidth()-1, this.getHeight()-1);
        g2.draw(rect);
        rect=new Rectangle(0, 0, this.getWidth(), this.getHeight());
    }
 
    public static void main(String[] args) {
        OResizing test = new OResizing(5,5,200,200);
        OResizer resizer = new OResizer(test);
        JFrame f = new JFrame();
        f.setLayout(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(test);
        test.addMouseListener(resizer);
        test.addMouseMotionListener(resizer);
        f.setSize(Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height/2);
        //f.setLocation(100,100);
        f.setVisible(true);
    }
}
 
/**
 * MouseAdapter okay for j2se 1.6+
 * Use MouseInputAdapter for j2se 1.5-
 */
class OResizer extends MouseAdapter {
    OResizing component;
    boolean dragging = false;
    // Give user some leeway for selections.
    final int PROX_DIST = 5;
 
    public OResizer(OResizing r) {
        component = r;
    }
 
    public void mouseDragged(MouseEvent e) {
        if(component.getCursor() != Cursor.getDefaultCursor()) {
            Point p = e.getPoint();
            Rectangle r = component.rect;
            Rectangle comp = component.getBounds();
            int x=comp.x,y=comp.y,w=comp.width,h=comp.height;
            int type = component.getCursor().getType();
            int dx = p.x - r.x;
            int dy = p.y - r.y;
            switch(type) {
                case Cursor.N_RESIZE_CURSOR:
                    int height = r.height - dy;
                    System.out.println("p:"+p.y+" r:"+r.y+" dy:"+dy+" h:"+r.height+"->"+height);
                    r.setRect(r.x, r.y+dy, r.width, height);
                    y=component.getBounds().y+dy;
                    h=height;
                    //System.out.println("N y="+y+" h="+h);
                    break;
                case Cursor.NW_RESIZE_CURSOR:
                    int width = r.width - dx;
                    height = r.height - dy;
                    r.setRect(r.x+dx, r.y+dy, width, height);
                    y=component.getBounds().y+r.y;
                    x=component.getBounds().x+r.x;
                    h=height;
                    w=width;
                    System.out.println("NW y="+y+" h="+h+"x="+x+" w="+w);
                    break;
                case Cursor.W_RESIZE_CURSOR:
                    width = r.width - dx;
                    r.setRect(r.x+dx, r.y, width, r.height);
                    x=component.getBounds().x+r.x;
                    w=width;
                    System.out.println("W x="+x+" w="+w);
                    break;
                case Cursor.SW_RESIZE_CURSOR:
                    width = r.width - dx;
                    height = dy;
                    r.setRect(r.x+dx, r.y, width, height);
                    x=component.getBounds().x+r.x;
                    h=height;
                    w=width;
                    System.out.println("SW h="+h+"x="+x+" w="+w);
                    break;
                case Cursor.S_RESIZE_CURSOR:
                    height = dy;
                    r.setRect(r.x, r.y, r.width, height);
                    h=height;
                    System.out.println("S h="+h);
                    break;
                case Cursor.SE_RESIZE_CURSOR:
                    width = dx;
                    height = dy;
                    r.setRect(r.x, r.y, width, height);
                    h=height;
                    w=width;
                    System.out.println("SE h="+h+" w="+w);
                    break;
                case Cursor.E_RESIZE_CURSOR:
                    width = dx;
                    r.setRect(r.x, r.y, width, r.height);
                    w=width;
                    System.out.println("E w="+w);
                    break;
                case Cursor.NE_RESIZE_CURSOR:
                    width = dx;
                    height = r.height - dy;
                    r.setRect(r.x, r.y+dy, width, height);
                    y=component.getBounds().y+r.y;
                    h=height;
                    w=width;
                    System.out.println("NE y="+y+" h="+h+" w="+w);
                    break;
                default:
                    System.out.println("unexpected type: " + type);
            }
            
            component.setBounds(x,y,w,h);
            //component.repaint();
        }
    }
 
    public void mouseMoved(MouseEvent e) {
        Point p = e.getPoint();
        if(!isOverRect(p)) {
            if(component.getCursor() != Cursor.getDefaultCursor()) {
                component.setCursor(Cursor.getDefaultCursor());
            }
            return;
        }
        // Locate cursor relative to center of rect.
        int outcode = getOutcode(p);
        Rectangle r = component.rect;
        switch(outcode) {
            case Rectangle.OUT_TOP:
                if(Math.abs(p.y - r.y) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.N_RESIZE_CURSOR));
                }
                break;
            case Rectangle.OUT_TOP + Rectangle.OUT_LEFT:
                if(Math.abs(p.y - r.y) < PROX_DIST &&
                   Math.abs(p.x - r.x) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.NW_RESIZE_CURSOR));
                }
                break;
            case Rectangle.OUT_LEFT:
                if(Math.abs(p.x - r.x) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.W_RESIZE_CURSOR));
                }
                break;
            case Rectangle.OUT_LEFT + Rectangle.OUT_BOTTOM:
                if(Math.abs(p.x - r.x) < PROX_DIST &&
                   Math.abs(p.y - (r.y+r.height)) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.SW_RESIZE_CURSOR));
                }
                break;
            case Rectangle.OUT_BOTTOM:
                if(Math.abs(p.y - (r.y+r.height)) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.S_RESIZE_CURSOR));
                }
                break;
            case Rectangle.OUT_BOTTOM + Rectangle.OUT_RIGHT:
                if(Math.abs(p.x - (r.x+r.width)) < PROX_DIST &&
                   Math.abs(p.y - (r.y+r.height)) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.SE_RESIZE_CURSOR));
                }
                break;
            case Rectangle.OUT_RIGHT:
                if(Math.abs(p.x - (r.x+r.width)) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.E_RESIZE_CURSOR));
                }
                break;
            case Rectangle.OUT_RIGHT + Rectangle.OUT_TOP:
                if(Math.abs(p.x - (r.x+r.width)) < PROX_DIST &&
                   Math.abs(p.y - r.y) < PROX_DIST) {
                    component.setCursor(Cursor.getPredefinedCursor(
                                        Cursor.NE_RESIZE_CURSOR));
                }
                break;
            default:    // center
                component.setCursor(Cursor.getDefaultCursor());
        }
    }
 
    /**
     * Make a smaller Rectangle and use it to locate the
     * cursor relative to the Rectangle center.
     */
    private int getOutcode(Point p) {
        Rectangle r = (Rectangle)component.rect.clone();
        r.grow(-PROX_DIST, -PROX_DIST);
        return r.outcode(p.x, p.y);        
    }
 
    /**
     * Make a larger Rectangle and check to see if the
     * cursor is over it.
     */
    private boolean isOverRect(Point p) {
        Rectangle r = (Rectangle)component.rect.clone();
        r.grow(PROX_DIST, PROX_DIST);
        return r.contains(p);
    }
}
