
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class DraggViewPort {

    public static void main(String[] args) {
        new DraggViewPort();
    }

    public DraggViewPort() {
        EventQueue.invokeLater(() -> {
            JFrame frame = new JFrame("Testing");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new TestPane());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public class TestPane extends JPanel {

        private JLabel map;
        private JScrollPane scrl;

        public TestPane() {
            setLayout(new BorderLayout());
            try {
                map = new JLabel(new ImageIcon(ImageIO.read(new File("C:\\Users\\terro\\Desktop\\qw.jpg"))));
                map.setAutoscrolls(true);
                scrl=new JScrollPane(map);
                scrl.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrl.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                add(scrl);
                MouseAdapter ma = new MouseAdapter() {

                    private Point origin;
int a=0;
                    @Override
                    public void mousePressed(MouseEvent e) {
                        origin = new Point(e.getPoint());
                        System.out.println(a++);
                    }

                    @Override
                    public void mouseDragged(MouseEvent e) {
                        if (origin != null) {
                            JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, map);
                            if (viewPort != null) {
                                Rectangle view = viewPort.getViewRect();
                                view.x += (origin.x - e.getX());
                                view.y += (origin.y - e.getY());

                                map.scrollRectToVisible(view);
                            }
                        }
                    }
                };

                map.addMouseListener(ma);
                map.addMouseMotionListener(ma);
            } catch (IOException ex) {            }
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(600, 400);
        }

    }

}