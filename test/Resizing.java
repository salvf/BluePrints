import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
 
public class Resizing extends JPanel {
    //Rectangle rect = new Rectangle(100,100,150,150);

    public Resizing() {
        this.setBackground(Color.red);
        this.setSize(60, 70);
        this.setLocation(10, 15);
    }
    
    public static void main(String[] args) {
        Resizing test = new Resizing();
        new Resizer(test);
        
        JFrame f = new JFrame();
        f.setLayout(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(400,400);
        f.setLocation(100,100);
        f.add(test);
        f.setVisible(true);
    }
}
 
/**
 * MouseAdapter okay for j2se 1.6+
 * Use MouseInputAdapter for j2se 1.5-
 */
class Resizer extends MouseAdapter {
    Resizing component;
    boolean dragging = false;
    // Give user some leeway for selections.
    final int PROX_DIST = 3;
 
    public Resizer(Resizing r) {
        component = r;
        component.addMouseListener(this);
        component.addMouseMotionListener(this);
    }
 
    public void mouseDragged(MouseEvent e) {
         if(component.getCursor() != Cursor.getDefaultCursor()) {
            Point p = e.getPoint();
            //Rectangle r = component.rect;
            Rectangle r = component.getBounds();
            int x=r.x,y=r.y,w=r.width,h=r.height;
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
                // If cursor is not over rect reset it to the default.
                component.setCursor(Cursor.getDefaultCursor());
            }
            return;
        }
        // Locate cursor relative to center of rect.
        int outcode = getOutcode(p);
        Rectangle r = component.getBounds();
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
        Rectangle r = (Rectangle)component.getBounds().clone();
        r.grow(-PROX_DIST, -PROX_DIST);
        return r.outcode(p.x, p.y);        
    }
 
    /**
     * Make a larger Rectangle and check to see if the
     * cursor is over it.
     */
    private boolean isOverRect(Point p) {
        Rectangle r = (Rectangle)component.getBounds().clone();
        r.grow(PROX_DIST, PROX_DIST);
        return r.contains(p);
    }
}