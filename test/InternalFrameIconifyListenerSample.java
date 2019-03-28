/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author terro
 */
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.Painter;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.plaf.metal.MetalBorders;


class InternalFrameIconifyListener extends InternalFrameAdapter {
    Point currentPoint=null;
  public void internalFrameIconified(InternalFrameEvent internalFrameEvent) {
    JInternalFrame source = (JInternalFrame) internalFrameEvent.getSource();
    currentPoint=internalFrameEvent.getInternalFrame().getLocation();
    internalFrameEvent.getInternalFrame().setLocation(currentPoint);
    internalFrameEvent.getInternalFrame().updateUI();
    System.out.println("Iconified: " + source.getTitle()+" Point"+currentPoint.toString());
  }

  public void internalFrameDeiconified(InternalFrameEvent internalFrameEvent) {
    JInternalFrame source = (JInternalFrame) internalFrameEvent.getSource();
    internalFrameEvent.getInternalFrame().setLocation(currentPoint);
    System.out.println("Deiconified: " + source.getTitle()+" Point"+currentPoint.toString());
  }
}

public class InternalFrameIconifyListenerSample {
    public static String[] str1LF = {
    "javax.swing.plaf.metal.MetalLookAndFeel",
    "javax.swing.plaf.nimbus.NimbusLookAndFeel",
    "com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
    "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel",
    "com.sun.java.swing.plaf.motif.MotifLookAndFeel",
    "com.sun.java.swing.plaf.gtk.GTKLookAndFeel"
};
static JFrame frame;
    public InternalFrameIconifyListenerSample() {
         frame= new JFrame();
        /*try {
            UIManager.setLookAndFeel(str1LF[1]);
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        
    }
    

  public static void main(final String[] args) throws UnsupportedLookAndFeelException {
      
    new InternalFrameIconifyListenerSample();
     
        JFrame.setDefaultLookAndFeelDecorated(true);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    JDesktopPane desktop = new JDesktopPane();
    desktop.setBackground(Color.gray);
    JInternalFrame internalFrame = new JInternalFrame("Can Do All", true, false, false, false);
    
    JInternalFrame internalFrame2 = new JInternalFrame("Can Do All", true, true, true, true);
    //internalFrame.putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
    InternalFrameListener internalFrameListener = new InternalFrameIconifyListener();
    
    UIManager.put("InternalFrame[Enabled+WindowFocused].backgroundPainter", (Painter<Object>) (Graphics2D g, Object c, int w, int h) -> {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setStroke(new BasicStroke(2f));
            g.setColor(Color.GRAY);
            g.fillRoundRect(0, 0, w-1, h-1, 8, 8);
            g.setColor(Color.WHITE);
            g.drawRoundRect(0, 0, w-1, h-1, 8, 8);
    });
		
    UIManager.put("InternalFrame[Enabled].backgroundPainter", (Painter<Object>) (Graphics2D g, Object c, int w, int h) -> {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setStroke(new BasicStroke(2f));
            g.setColor(Color.RED);
            g.fillRoundRect(0, 0, w-1, h-1, 8, 8);
            g.setColor(Color.WHITE);
            g.drawRoundRect(0, 0, w-1, h-1, 8, 8);
    });
    
//     Add listener for iconification events
    internalFrame.addInternalFrameListener(internalFrameListener);
    
    desktop.add(internalFrame);
    desktop.add(internalFrame2);
//    internalFrame.setFrameIcon(null);
//    internalFrame2.setFrameIcon(null);
    internalFrame.setBounds(25, 25, 200, 100);
    internalFrame2.setBounds(250, 25, 200, 100);

    internalFrame.add(new JLabel(internalFrame.getTitle(), JLabel.CENTER), BorderLayout.CENTER);
    internalFrame.setVisible(true);

    internalFrame2.add(new JLabel(internalFrame2.getTitle(), JLabel.CENTER), BorderLayout.CENTER);
    internalFrame2.setVisible(true);
    frame.add(desktop, BorderLayout.CENTER);
    frame.setSize(500, 300);
    frame.setVisible(true);

  }
}