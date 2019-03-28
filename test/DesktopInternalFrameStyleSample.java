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

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;

public class DesktopInternalFrameStyleSample {

  public static void main(final String[] args) {
    JFrame frame = new JFrame();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    JDesktopPane desktop = new JDesktopPane();
    JInternalFrame internalFrames[] = { new JInternalFrame("Can Do All", true, true, true, true),
        new JInternalFrame("Not Resizable", false, true, true, true),
        new JInternalFrame("Not Closable", true, false, true, true),
        new JInternalFrame("Not Maximizable", true, true, false, true),
        new JInternalFrame("Not Iconifiable", true, true, true, false) };

    int pos = 0;
    desktop.setDragMode(JDesktopPane.LIVE_DRAG_MODE);
    for (JInternalFrame internalFrame : internalFrames) {
      desktop.add(internalFrame);
      
      internalFrame.setBounds(pos * 25, pos * 25, 200, 100);
      pos++;

      JLabel label = new JLabel(internalFrame.getTitle(), JLabel.CENTER);
      internalFrame.add(label, BorderLayout.CENTER);

      internalFrame.setVisible(true);
    }

    frame.add(desktop, BorderLayout.CENTER);
    frame.setSize(500, 300);
    frame.setVisible(true);
  }
}