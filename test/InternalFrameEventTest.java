/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author terro
 */
import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;

public class InternalFrameEventTest {
  public static void main(String[] a) {

    JFrame frame = new JFrame();
    Container contentPane = frame.getContentPane();

    JLayeredPane desktop = new JDesktopPane();
    desktop.setOpaque(false);
    contentPane.add(desktop, BorderLayout.CENTER);
    desktop.add(createLayer("One"), JLayeredPane.POPUP_LAYER);
    desktop.add(createLayer("Two"), JLayeredPane.DEFAULT_LAYER);
    desktop.add(createLayer("Three"), JLayeredPane.PALETTE_LAYER);

    frame.setSize(400, 200);
    frame.setVisible(true);

  }

  public static JInternalFrame createLayer(String label) {
    return new SelfInternalFrame(label);
  }

}

class SelfInternalFrame extends JInternalFrame {
  public SelfInternalFrame(String s) {
    getContentPane().add(new JLabel(s), BorderLayout.CENTER);
    addInternalFrameListener(new InternalFrameListener() {
      public void internalFrameActivated(InternalFrameEvent e) {
        System.out.println("Activated" + e.getSource());
      }

      public void internalFrameClosed(InternalFrameEvent e) {
        System.out.println("Closed");
      }

      public void internalFrameClosing(InternalFrameEvent e) {
        System.out.println("Closing");
      }

      public void internalFrameDeactivated(InternalFrameEvent e) {
        System.out.println("Deactivated");
      }

      public void internalFrameDeiconified(InternalFrameEvent e) {
        System.out.println("Deiconified");
      }

      public void internalFrameIconified(InternalFrameEvent e) {
        System.out.println("Iconified");
      }

      public void internalFrameOpened(InternalFrameEvent e) {
        System.out.println("Opened");
      }
    });
    setBounds(50, 50, 100, 100);
    setResizable(true);
    setClosable(true);
    setMaximizable(true);
    setIconifiable(true);
    setTitle(s);
  }
}

           
         