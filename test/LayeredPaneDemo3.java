/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author terro
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

public class LayeredPaneDemo3 extends JFrame
{
  public LayeredPaneDemo3() 
  {
    super("Custom MDI: Part IV");
    setSize(570,400);
    getContentPane().setBackground(new Color(244,232,152));

    setLayeredPane(new MDIPane());
   
    ImageIcon ii = new ImageIcon("earth.jpg");
    InnerFrame[] frames = new InnerFrame[5];
    for(int i=0; i<5; i++) 
    {
      frames[i] = new InnerFrame("InnerFrame " + i);
      frames[i].setBounds(50+i*20, 50+i*20, 200, 200);
      frames[i].getContentPane().add(
        new JScrollPane(new JLabel(ii)));
      getLayeredPane().add(frames[i]);
    }

    WindowListener l = new WindowAdapter() {
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    };

    Dimension dim = getToolkit().getScreenSize();
    setLocation(dim.width/2-getWidth()/2, 
      dim.height/2-getHeight()/2);

    ImageIcon image = new ImageIcon("spiral.gif");
    setIconImage(image.getImage());
    addWindowListener(l);
    setVisible(true);
  }

  public static void main(String[] args) 
  {
    new LayeredPaneDemo3();
  }
}

class InnerFrame extends JPanel implements RootPaneContainer{
  private static String IMAGE_DIR = "mdi" + java.io.File.separator;
  private static ImageIcon ICONIZE_BUTTON_ICON = new ImageIcon(IMAGE_DIR+"iconize.gif");
  private static ImageIcon RESTORE_BUTTON_ICON = new ImageIcon(IMAGE_DIR+"restore.gif");
  private static ImageIcon CLOSE_BUTTON_ICON = new ImageIcon(IMAGE_DIR+"close.gif");
  private static ImageIcon MAXIMIZE_BUTTON_ICON = new ImageIcon(IMAGE_DIR+"maximize.gif");
  private static ImageIcon MINIMIZE_BUTTON_ICON = new ImageIcon(IMAGE_DIR+"minimize.gif");
  private static ImageIcon PRESS_CLOSE_BUTTON_ICON = new ImageIcon(IMAGE_DIR+"pressclose.gif");
  private static ImageIcon PRESS_RESTORE_BUTTON_ICON = new ImageIcon(IMAGE_DIR+"pressrestore.gif");
  private static ImageIcon PRESS_ICONIZE_BUTTON_ICON = new ImageIcon(IMAGE_DIR+"pressiconize.gif");
  private static ImageIcon PRESS_MAXIMIZE_BUTTON_ICON = new ImageIcon(IMAGE_DIR+"pressmaximize.gif");
  private static ImageIcon PRESS_MINIMIZE_BUTTON_ICON = new ImageIcon(IMAGE_DIR+"pressminimize.gif");
  private static ImageIcon DEFAULT_FRAME_ICON = new ImageIcon(IMAGE_DIR+"default.gif");
  private static int BORDER_THICKNESS = 4;
  private static int WIDTH = 200;
  private static int HEIGHT = 200;
  private static int TITLE_BAR_HEIGHT = 25;
  private static int FRAME_ICON_PADDING = 2;
  private static int ICONIZED_WIDTH = 150;
  private static Color DEFAULT_TITLE_BAR_BG_COLOR = new Color(108,190,116);
  private static Color DEFAULT_BORDER_COLOR = new Color(8,90,16);
  private static Color DEFAULT_SELECTED_TITLE_BAR_BG_COLOR = new Color(91,182,249);
  private static Color DEFAULT_SELECTED_BORDER_COLOR = new Color(0,82,149);

  private Color m_titleBarBackground = DEFAULT_TITLE_BAR_BG_COLOR;
  private Color m_titleBarForeground = Color.black;
  private Color m_BorderColor = DEFAULT_BORDER_COLOR;
  private Color m_selectedTitleBarBackground = DEFAULT_SELECTED_TITLE_BAR_BG_COLOR;
  private Color m_selectedBorderColor = DEFAULT_SELECTED_BORDER_COLOR;

  private int m_titleBarHeight = TITLE_BAR_HEIGHT;
  private int m_width = WIDTH;
  private int m_height = HEIGHT;
  private int m_iconizedWidth = ICONIZED_WIDTH;
  private int m_x;
  private int m_y;

  private String m_title;
  private JLabel m_titleLabel;
  private JLabel m_iconLabel;

  private boolean m_iconified;
  private boolean m_maximized;
  private boolean m_selected;

  private boolean m_iconizeable;
  private boolean m_resizeable;
  private boolean m_closeable;
  private boolean m_maximizeable;
 
  // only false when maximized
  private boolean m_draggable = true;

  private JRootPane m_rootPane;

  // used to wrap m_titlePanel and m_rootPane
  private JPanel m_frameContentPanel;

  private JPanel m_titlePanel; 
  private JPanel m_contentPanel;
  private JPanel m_buttonPanel; 
  private JPanel m_buttonWrapperPanel;

  private InnerFrameButton m_iconize;
  private InnerFrameButton m_close;
  private InnerFrameButton m_maximize;

  private ImageIcon m_frameIcon = DEFAULT_FRAME_ICON;

  private NorthResizeEdge m_northResizer;
  private SouthResizeEdge m_southResizer;
  private EastResizeEdge m_eastResizer;
  private WestResizeEdge m_westResizer;

  public InnerFrame() {
    this("");
  }

  public InnerFrame(String title) {
    this(title, null);
  }

  public InnerFrame(String title, ImageIcon frameIcon) {
    this(title, frameIcon, true, true, true, true);
  }

  public InnerFrame(String title, ImageIcon frameIcon,
   boolean resizeable, boolean iconizeable, 
   boolean maximizeable, boolean closeable) {
    super.setLayout(new BorderLayout());
    attachNorthResizeEdge();
    attachSouthResizeEdge();
    attachEastResizeEdge();
    attachWestResizeEdge();
    populateInnerFrame();

    setTitle(title);
    setResizeable(resizeable);
    setIconizeable(iconizeable);
    setCloseable(closeable);
    setMaximizeable(maximizeable);
    if (frameIcon != null)
      setFrameIcon(frameIcon);
  }

  protected void populateInnerFrame() {
    m_rootPane = new JRootPane();
    m_frameContentPanel = new JPanel();
    m_frameContentPanel.setLayout(new BorderLayout());
    createTitleBar();
    m_contentPanel = new JPanel(new BorderLayout());
    setContentPane(m_contentPanel);
    m_frameContentPanel.add(m_titlePanel, BorderLayout.NORTH);
    m_frameContentPanel.add(m_rootPane, BorderLayout.CENTER);
    setupCapturePanel();
    super.add(m_frameContentPanel, BorderLayout.CENTER);
  }

  protected void setupCapturePanel() {
    CapturePanel mouseTrap = new CapturePanel();
    m_rootPane.getLayeredPane().add(mouseTrap, 
      new Integer(Integer.MIN_VALUE));
    mouseTrap.setBounds(0,0,10000,10000);
    setGlassPane(new GlassCapturePanel());
    getGlassPane().setVisible(true);
  } 

  // don't allow this in root pane containers 
  public Component add(Component c) {
    return null;
  }

  // don't allow this in root pane containers 
  public void setLayout(LayoutManager mgr) {
  }

  public JMenuBar getJMenuBar() {
    return m_rootPane.getJMenuBar();
  }

  public JRootPane getRootPane() {
    return m_rootPane;
  }

  public Container getContentPane() {
    return m_rootPane.getContentPane();
  }

  public Component getGlassPane() {
    return m_rootPane.getGlassPane();
  }

  public JLayeredPane getLayeredPane() {
    return m_rootPane.getLayeredPane();
  }

  public void setJMenuBar(JMenuBar menu) {
    m_rootPane.setJMenuBar(menu);
  }

  public void setContentPane(Container content) {
    m_rootPane.setContentPane(content);
  }

  public void setGlassPane(Component glass) {
    m_rootPane.setGlassPane(glass);
  }

  public void setLayeredPane(JLayeredPane layered) {
    m_rootPane.setLayeredPane(layered);
  }

  public void toFront() {
    if (getParent() instanceof JLayeredPane)
      ((JLayeredPane) getParent()).moveToFront(this);
    if (!isSelected())
      setSelected(true);
  }

  public void close() {
    if (getParent() instanceof JLayeredPane) {
      JLayeredPane jlp = (JLayeredPane) getParent();
      jlp.remove(InnerFrame.this);
      jlp.repaint();
    }
  }

  public boolean isIconizeable() {
    return m_iconizeable;
  }

  public void setIconizeable(boolean b) {
    m_iconizeable = b;
    m_iconize.setVisible(b);
    m_titlePanel.revalidate();
  }

  public boolean isCloseable() {
    return m_closeable;
  }

  public void setCloseable(boolean b) {
    m_closeable = b;
    m_close.setVisible(b);
    m_titlePanel.revalidate();
  }

  public boolean isMaximizeable() {
    return m_maximizeable;
  }

  public void setMaximizeable(boolean b) {
    m_maximizeable = b;
    m_maximize.setVisible(b);
    m_titlePanel.revalidate();
  }

  public boolean isIconified() {
    return m_iconified;
  }

  public void setIconified(boolean b) {
    m_iconified = b;
    if (b) {
      if (isMaximized())
        setMaximized(false);
      toFront();
      m_width = getWidth();     // remember width
      m_height = getHeight();   // remember height
      setBounds(getX(), getY(), ICONIZED_WIDTH, 
        m_titleBarHeight + 2*BORDER_THICKNESS);
      m_iconize.setIcon(RESTORE_BUTTON_ICON);
      m_iconize.setPressedIcon(PRESS_RESTORE_BUTTON_ICON);
      setResizeable(false);
    }
    else {
      toFront();
      setBounds(getX(), getY(), m_width, m_height);
      m_iconize.setIcon(ICONIZE_BUTTON_ICON);
      m_iconize.setPressedIcon(PRESS_ICONIZE_BUTTON_ICON);
      setResizeable(true);
    }
    revalidate();
  }

  public boolean isMaximized() {
    return m_maximized;
  }

  public void setMaximized(boolean b) {
    m_maximized = b;
    if (b)
    {
      if (isIconified())
        setIconified(false);
      toFront();
      m_width = getWidth();     // remember width
      m_height = getHeight();   // remember height
      m_x = getX();             // remember x
      m_y = getY();             // remember y
      setBounds(0, 0, getParent().getWidth(), getParent().getHeight());
      m_maximize.setIcon(MINIMIZE_BUTTON_ICON);
      m_maximize.setPressedIcon(PRESS_MINIMIZE_BUTTON_ICON);
      setResizeable(false);
      setDraggable(false);
    }
    else {
      toFront();
      setBounds(m_x, m_y, m_width, m_height);
      m_maximize.setIcon(MAXIMIZE_BUTTON_ICON);
      m_maximize.setPressedIcon(PRESS_MAXIMIZE_BUTTON_ICON);
      setResizeable(true);
      setDraggable(true);
    }
    revalidate();
  }

  public boolean isSelected() {
    return m_selected;
  }

  public void setSelected(boolean b) {
    if (b) 
    {
      if (m_selected != true && getParent() instanceof JLayeredPane) 
      {
        JLayeredPane jlp = (JLayeredPane) getParent();
        int layer = jlp.getLayer(this);
        Component[] components = jlp.getComponentsInLayer(layer);
        for (int i=0; i<components.length; i++) {
          if (components[i] instanceof InnerFrame) {
            InnerFrame tempFrame = (InnerFrame) components[i];
            if (!tempFrame.equals(this))
              tempFrame.setSelected(false);
          }
        }
        m_selected = true;
        updateBorderColors();
        updateTitleBarColors();
        getGlassPane().setVisible(false);
        repaint();
      }
    }
    else 
    {  
      m_selected = false;
      updateBorderColors();
      updateTitleBarColors();
      getGlassPane().setVisible(true);
      repaint();
    }
  }

  ////////////////////////////////////////////
  //////////////// Title Bar /////////////////
  ////////////////////////////////////////////

  public void setTitleBarBackground(Color c) {
    m_titleBarBackground = c;
    updateTitleBarColors();
  }

  public Color getTitleBarBackground() {
    return m_titleBarBackground;
  }

  public void setTitleBarForeground(Color c) {
    m_titleBarForeground = c;
    m_titleLabel.setForeground(c);
    m_titlePanel.repaint();
  }

  public Color getTitleBarForeground() {
    return m_titleBarForeground;
  }

  public void setSelectedTitleBarBackground(Color c) {
    m_titleBarBackground = c;
    updateTitleBarColors();
  }

  public Color getSelectedTitleBarBackground() {
    return m_selectedTitleBarBackground;
  }

  protected void updateTitleBarColors() {
    if (isSelected())
      m_titlePanel.setBackground(m_selectedTitleBarBackground);
    else
      m_titlePanel.setBackground(m_titleBarBackground);
  }

  public void setFrameIcon(ImageIcon fi) {
    m_frameIcon = fi;

    if (fi != null) {
      if (m_frameIcon.getIconHeight() > TITLE_BAR_HEIGHT)
        setTitleBarHeight(m_frameIcon.getIconHeight() + 2*FRAME_ICON_PADDING);
      m_iconLabel.setIcon(m_frameIcon);
    }
    else setTitleBarHeight(TITLE_BAR_HEIGHT);
    revalidate();
  }
 
  public ImageIcon getFrameIcon() {
    return m_frameIcon;
  }

  public void setTitle(String s) {
    m_title = s;
    m_titleLabel.setText(s);
    m_titlePanel.repaint();
  }

  public String getTitle() {
    return m_title;
  }

  public void setTitleBarHeight(int h) {
    m_titleBarHeight = h;
  }

  public int getTitleBarHeight() {
    return m_titleBarHeight;
  }

  public boolean isDraggable() {
    return m_draggable;
  }
  
  private void setDraggable(boolean b) {
    m_draggable = b;
  }
 
  // create the title bar: m_titlePanel
  protected void createTitleBar() {
    m_titlePanel = new JPanel() {
      public Dimension getPreferredSize() {
        return new Dimension(InnerFrame.this.getWidth(), 
          m_titleBarHeight);
      }
    };
    m_titlePanel.setLayout(new BorderLayout());
    m_titlePanel.setOpaque(true);
    m_titlePanel.setBackground(m_titleBarBackground);

    m_titleLabel = new JLabel();
    m_titleLabel.setForeground(m_titleBarForeground);

    m_close = new InnerFrameButton(CLOSE_BUTTON_ICON);
    m_close.setPressedIcon(PRESS_CLOSE_BUTTON_ICON);
    m_close.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InnerFrame.this.close();
      }
    });

    m_maximize = new InnerFrameButton(MAXIMIZE_BUTTON_ICON);
    m_maximize.setPressedIcon(PRESS_MAXIMIZE_BUTTON_ICON);
    m_maximize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InnerFrame.this.setMaximized(!InnerFrame.this.isMaximized());
      }
    });

    m_iconize = new InnerFrameButton(ICONIZE_BUTTON_ICON);
    m_iconize.setPressedIcon(PRESS_ICONIZE_BUTTON_ICON);
    m_iconize.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        InnerFrame.this.setIconified(!InnerFrame.this.isIconified());
      }
    });

    m_buttonWrapperPanel = new JPanel();
    m_buttonWrapperPanel.setOpaque(false);
    m_buttonPanel = new JPanel(new GridLayout(1,3));
    m_buttonPanel.setOpaque(false);
    m_buttonPanel.add(m_iconize);
    m_buttonPanel.add(m_maximize);
    m_buttonPanel.add(m_close);
    m_buttonPanel.setAlignmentX(0.5f);
    m_buttonPanel.setAlignmentY(0.5f);
    m_buttonWrapperPanel.add(m_buttonPanel);

    m_iconLabel = new JLabel();
    m_iconLabel.setBorder(new EmptyBorder(
      FRAME_ICON_PADDING, FRAME_ICON_PADDING,
      FRAME_ICON_PADDING, FRAME_ICON_PADDING));
    if (m_frameIcon != null)
      m_iconLabel.setIcon(m_frameIcon);

    m_titlePanel.add(m_titleLabel, BorderLayout.CENTER);
    m_titlePanel.add(m_buttonWrapperPanel, BorderLayout.EAST);
    m_titlePanel.add(m_iconLabel, BorderLayout.WEST);

    InnerFrameTitleBarMouseAdapter iftbma = 
      new InnerFrameTitleBarMouseAdapter(this);
    m_titlePanel.addMouseListener(iftbma);
    m_titlePanel.addMouseMotionListener(iftbma);
  }

  // title bar mouse adapter for frame dragging
  class InnerFrameTitleBarMouseAdapter 
  extends MouseInputAdapter
  {
    InnerFrame m_if;
    int m_XDifference, m_YDifference;
    boolean m_dragging;

    public InnerFrameTitleBarMouseAdapter(InnerFrame inf) {
      m_if = inf;
    }

    // don't allow dragging outside of parent
    public void mouseDragged(MouseEvent e) {
      int ex = e.getX();
      int ey = e.getY();
      int x = m_if.getX();
      int y = m_if.getY();
      int w = m_if.getParent().getWidth();
      int h = m_if.getParent().getHeight();
      if (m_dragging & m_if.isDraggable()) {
        if ((ey + y > 0 && ey + y < h) && (ex + x > 0 && ex + x < w))
          m_if.setLocation(ex-m_XDifference + x, ey-m_YDifference + y);
        else if (!(ey + y > 0 && ey + y < h) && (ex + x > 0 && ex + x < w)) {
          if (!(ey + y > 0) && ey + y < h)
            m_if.setLocation(ex-m_XDifference + x, 0-m_YDifference);
          else if (ey + y > 0 && !(ey + y < h))
            m_if.setLocation(ex-m_XDifference + x, h-m_YDifference);
        }
        else if ((ey + y > 0 && ey + y < h) && !(ex + x > 0 && ex + x < w)) {
          if (!(ex + x > 0) && ex + x < w)
            m_if.setLocation(0-m_XDifference, ey-m_YDifference + y);
          else if (ex + x > 0 && !(ex + x < w)) 
            m_if.setLocation(w-m_XDifference, ey-m_YDifference + y);
        }
        else if (!(ey + y > 0) && ey + y < h && !(ex + x > 0) && ex + x < w) 
          m_if.setLocation(0-m_XDifference, 0-m_YDifference);
        else if (!(ey + y > 0) && ey + y < h && ex + x > 0 && !(ex + x < w)) 
          m_if.setLocation(w-m_XDifference, 0-m_YDifference);
        else if (ey + y > 0 && !(ey + y < h) && !(ex + x > 0) && ex + x < w) 
          m_if.setLocation(0-m_XDifference, h-m_YDifference);
        else if (ey + y > 0 && !(ey + y < h) && ex + x > 0 && !(ex + x < w)) 
          m_if.setLocation(w-m_XDifference, h-m_YDifference);
      }
    } 

    public void mousePressed(MouseEvent e) {  
      m_if.toFront();
      m_XDifference = e.getX();
      m_YDifference = e.getY();
      m_dragging = true;
    }

    public void mouseReleased(MouseEvent e) {
      m_dragging = false;
    }
  }
  
  // custom button class for title bar
  class InnerFrameButton extends JButton 
  {
    Dimension m_dim;

    public InnerFrameButton(ImageIcon ii) {
      super(ii);
      m_dim = new Dimension(ii.getIconWidth(), ii.getIconHeight());
      setOpaque(false);
      setContentAreaFilled(false);
      setBorder(null);
    }

    public Dimension getPreferredSize() {
      return m_dim;
    }

    public Dimension getMinimumSize() {
      return m_dim;
    }

    public Dimension getMaximumSize() {
      return m_dim;
    }
  }

  ///////////////////////////////////////////////
  /////////// Mouse Event Capturing /////////////
  ///////////////////////////////////////////////

  class CapturePanel extends JPanel 
  {
    public CapturePanel() {
      MouseInputAdapter mia = new MouseInputAdapter() {};
      addMouseListener(mia);
      addMouseMotionListener(mia);
    }
  }

  ///////////////////////////////////////////////
  ///////////// GlassPane Selector //////////////
  ///////////////////////////////////////////////

  class GlassCapturePanel extends JPanel
  {
    public GlassCapturePanel() {
      MouseInputAdapter mia = new MouseInputAdapter() {
        public void mousePressed(MouseEvent e) {
          InnerFrame.this.toFront();
        }
      };
      addMouseListener(mia);
      addMouseMotionListener(mia);
      setOpaque(false);
    }
  }

  ///////////////////////////////////////////////
  //////////////// Resizability /////////////////
  ///////////////////////////////////////////////

  public void setBorderColor(Color c) {
    m_BorderColor = c;
    updateBorderColors();
  }
   
  public Color getBorderColor() {
    return m_BorderColor;
  } 

  public void setSelectedBorderColor(Color c) {
    m_selectedBorderColor = c;
    updateBorderColors();
  }

  public Color getSelectedBorderColor() {
    return m_selectedBorderColor;
  } 

  protected void updateBorderColors() {
    if (isSelected()) {
      m_northResizer.setBackground(m_selectedBorderColor);
      m_southResizer.setBackground(m_selectedBorderColor);
      m_eastResizer.setBackground(m_selectedBorderColor);
      m_westResizer.setBackground(m_selectedBorderColor);
    } else {
      m_northResizer.setBackground(m_BorderColor);
      m_southResizer.setBackground(m_BorderColor);
      m_eastResizer.setBackground(m_BorderColor);
      m_westResizer.setBackground(m_BorderColor);
    }
  }
   
  public boolean isResizeable() {
    return m_resizeable;
  }

  public void setResizeable(boolean b) {
    if (!b && m_resizeable == true) {
      m_northResizer.removeMouseListener(m_northResizer);
      m_northResizer.removeMouseMotionListener(m_northResizer);
      m_southResizer.removeMouseListener(m_southResizer);
      m_southResizer.removeMouseMotionListener(m_southResizer);
      m_eastResizer.removeMouseListener(m_eastResizer);
      m_eastResizer.removeMouseMotionListener(m_eastResizer);
      m_westResizer.removeMouseListener(m_westResizer);
      m_westResizer.removeMouseMotionListener(m_westResizer);
    }
    else if (b && m_resizeable == false) {
      m_northResizer.addMouseListener(m_northResizer);
      m_northResizer.addMouseMotionListener(m_northResizer);
      m_southResizer.addMouseListener(m_southResizer);
      m_southResizer.addMouseMotionListener(m_southResizer);
      m_eastResizer.addMouseListener(m_eastResizer);
      m_eastResizer.addMouseMotionListener(m_eastResizer);
      m_westResizer.addMouseListener(m_westResizer);
      m_westResizer.addMouseMotionListener(m_westResizer);
    }
    m_resizeable = b;
  }

  protected void attachNorthResizeEdge() {
    m_northResizer = new NorthResizeEdge(this);
    super.add(m_northResizer, BorderLayout.NORTH);
  }

  protected void attachSouthResizeEdge() {
    m_southResizer = new SouthResizeEdge(this);
    super.add(m_southResizer, BorderLayout.SOUTH);
  }

  protected void attachEastResizeEdge() {
    m_eastResizer = new EastResizeEdge(this);
    super.add(m_eastResizer, BorderLayout.EAST);
  }

  protected void attachWestResizeEdge() {
    m_westResizer = new WestResizeEdge(this);
    super.add(m_westResizer, BorderLayout.WEST);
  }

  class EastResizeEdge extends JPanel implements MouseListener, MouseMotionListener {
    private int WIDTH = BORDER_THICKNESS;
    private int MIN_WIDTH = ICONIZED_WIDTH;
    private boolean m_dragging;
    private JComponent m_resizeComponent;
  
    protected EastResizeEdge(JComponent c) {
      m_resizeComponent = c;
      setOpaque(true);
      setBackground(m_BorderColor);
    }

    public Dimension getPreferredSize() {
      return new Dimension(WIDTH, m_resizeComponent.getHeight());
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {
      m_dragging = false;
    }

    public void mouseDragged(MouseEvent e) {
      if (m_resizeComponent.getWidth() + e.getX() >= MIN_WIDTH)
        m_resizeComponent.setBounds(m_resizeComponent.getX(), 
          m_resizeComponent.getY(), 
          m_resizeComponent.getWidth() + e.getX(),
          m_resizeComponent.getHeight());
      else
        m_resizeComponent.setBounds(m_resizeComponent.getX(), 
          m_resizeComponent.getY(), 
          MIN_WIDTH, m_resizeComponent.getHeight());
      m_resizeComponent.validate();
    }

    public void mouseEntered(MouseEvent e) {
      if (!m_dragging)
        setCursor(Cursor.getPredefinedCursor(
          Cursor.E_RESIZE_CURSOR));
    }
    
    public void mouseExited(MouseEvent e) {
      if (!m_dragging)
        setCursor(Cursor.getPredefinedCursor(
          Cursor.DEFAULT_CURSOR));
    }
  
    public void mousePressed(MouseEvent e) {
      toFront();
      m_dragging = true;
    }  
  }

  class WestResizeEdge extends JPanel implements MouseListener, MouseMotionListener {
    private int WIDTH = BORDER_THICKNESS;
    private int MIN_WIDTH = ICONIZED_WIDTH;
    private int m_dragX, m_rightX;
    private boolean m_dragging;
    private JComponent m_resizeComponent;
  
    protected WestResizeEdge(JComponent c) {
      m_resizeComponent = c;
      setOpaque(true);
      setBackground(m_BorderColor);
    }

    public Dimension getPreferredSize() {
      return new Dimension(WIDTH, m_resizeComponent.getHeight());
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
  
    public void mouseReleased(MouseEvent e) {
      m_dragging = false;
    }

    public void mouseDragged(MouseEvent e) {
      if (m_resizeComponent.getWidth()-
       (e.getX()-m_dragX) >= MIN_WIDTH)
        m_resizeComponent.setBounds(
          m_resizeComponent.getX() + (e.getX()-m_dragX), 
          m_resizeComponent.getY(), 
          m_resizeComponent.getWidth()-(e.getX()-m_dragX),
          m_resizeComponent.getHeight());
      else
        if (m_resizeComponent.getX() + MIN_WIDTH < m_rightX)
          m_resizeComponent.setBounds(m_rightX-MIN_WIDTH, 
            m_resizeComponent.getY(), 
            MIN_WIDTH, m_resizeComponent.getHeight());
        else
          m_resizeComponent.setBounds(m_resizeComponent.getX(), 
            m_resizeComponent.getY(), 
            MIN_WIDTH, m_resizeComponent.getHeight());
      m_resizeComponent.validate();
    }
  
    public void mouseEntered(MouseEvent e) {
      if (!m_dragging)
        setCursor(Cursor.getPredefinedCursor(
          Cursor.W_RESIZE_CURSOR));
    }
    
    public void mouseExited(MouseEvent e) {
      if (!m_dragging)
        setCursor(Cursor.getPredefinedCursor(
          Cursor.DEFAULT_CURSOR));
    }
    
    public void mousePressed(MouseEvent e) {
      toFront();
      m_rightX = m_resizeComponent.getX() + 
        m_resizeComponent.getWidth();
      m_dragging = true;
      m_dragX = e.getX();
    }  
  }
  
  class NorthResizeEdge extends JPanel implements MouseListener, MouseMotionListener {
    private static final int NORTH = 0;
    private static final int NORTHEAST = 1;
    private static final int NORTHWEST = 2;
    private int CORNER = 10;
    private int HEIGHT = BORDER_THICKNESS;
    private int MIN_WIDTH = ICONIZED_WIDTH;
    private int MIN_HEIGHT = TITLE_BAR_HEIGHT+(2*HEIGHT);
    private int m_width, m_dragX, m_dragY, m_rightX, m_lowerY;
    private boolean m_dragging;
    private JComponent m_resizeComponent;
    private int m_mode;
    
    protected NorthResizeEdge(JComponent c) {
      m_resizeComponent = c;
      setOpaque(true);
      setBackground(m_BorderColor);
    }

    public Dimension getPreferredSize() {
      return new Dimension(m_resizeComponent.getWidth(), HEIGHT);
    }

    public void mouseClicked(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) {
      if (!m_dragging) {
        if (e.getX() < CORNER) {
          setCursor(Cursor.getPredefinedCursor(
            Cursor.NW_RESIZE_CURSOR));
        }
        else if(e.getX() > getWidth()-CORNER) {
          setCursor(Cursor.getPredefinedCursor(
            Cursor.NE_RESIZE_CURSOR));
        }
        else {
          setCursor(Cursor.getPredefinedCursor(
            Cursor.N_RESIZE_CURSOR));
        }
      }
    }

    public void mouseReleased(MouseEvent e) {
      m_dragging = false;
    }

    public void mouseDragged(MouseEvent e) {
      int h = m_resizeComponent.getHeight();
      int w = m_resizeComponent.getWidth();
      int x = m_resizeComponent.getX();
      int y = m_resizeComponent.getY();
      int ex = e.getX();
      int ey = e.getY();
      switch (m_mode) {
        case NORTH:
          if (h-(ey-m_dragY) >= MIN_HEIGHT)
            m_resizeComponent.setBounds(x, y + (ey-m_dragY), 
              w, h-(ey-m_dragY));
          else
              m_resizeComponent.setBounds(x, 
                m_lowerY-MIN_HEIGHT, w, MIN_HEIGHT);
          break;
        case NORTHEAST:
          if (h-(ey-m_dragY) >= MIN_HEIGHT
          && w + (ex-(getWidth()-CORNER)) >= MIN_WIDTH)
            m_resizeComponent.setBounds(x, 
              y + (ey-m_dragY), w + (ex-(getWidth()-CORNER)),
                h-(ey-m_dragY));
          else if (h-(ey-m_dragY) >= MIN_HEIGHT
          && !(w + (ex-(getWidth()-CORNER)) >= MIN_WIDTH))
            m_resizeComponent.setBounds(x, 
              y + (ey-m_dragY), MIN_WIDTH, h-(ey-m_dragY));
          else if (!(h-(ey-m_dragY) >= MIN_HEIGHT)
          && w + (ex-(getWidth()-CORNER)) >= MIN_WIDTH)
            m_resizeComponent.setBounds(x, 
              m_lowerY-MIN_HEIGHT, w + (ex-(getWidth()-CORNER)), 
                MIN_HEIGHT);
          else
            m_resizeComponent.setBounds(x, 
              m_lowerY-MIN_HEIGHT, MIN_WIDTH, MIN_HEIGHT);
          break;
        case NORTHWEST:
          if (h-(ey-m_dragY) >= MIN_HEIGHT
          && w-(ex-m_dragX) >= MIN_WIDTH)
            m_resizeComponent.setBounds(x + (ex-m_dragX), 
              y + (ey-m_dragY), w-(ex-m_dragX),
                h-(ey-m_dragY));
          else if (h-(ey-m_dragY) >= MIN_HEIGHT
          && !(w-(ex-m_dragX) >= MIN_WIDTH)) {
            if (x + MIN_WIDTH < m_rightX) 
              m_resizeComponent.setBounds(m_rightX-MIN_WIDTH, 
                y + (ey-m_dragY), MIN_WIDTH, h-(ey-m_dragY));
            else
              m_resizeComponent.setBounds(x, 
                y + (ey-m_dragY), w, h-(ey-m_dragY));
          } 
          else if (!(h-(ey-m_dragY) >= MIN_HEIGHT)
          && w-(ex-m_dragX) >= MIN_WIDTH) 
            m_resizeComponent.setBounds(x + (ex-m_dragX), 
              m_lowerY-MIN_HEIGHT, w-(ex-m_dragX), MIN_HEIGHT);
          else
            m_resizeComponent.setBounds(m_rightX-MIN_WIDTH, 
              m_lowerY-MIN_HEIGHT, MIN_WIDTH, MIN_HEIGHT);
          break;
      }
      m_rightX = x + w;
      m_resizeComponent.validate();
    }
  
    public void mouseEntered(MouseEvent e) {
      mouseMoved(e);
    }
    
    public void mouseExited(MouseEvent e) {
      if (!m_dragging)
        setCursor(Cursor.getPredefinedCursor(
          Cursor.DEFAULT_CURSOR));
    }
    
    public void mousePressed(MouseEvent e) {
      toFront();
      m_dragging = true;
      m_dragX = e.getX();
      m_dragY = e.getY();
      m_lowerY = m_resizeComponent.getY()
        + m_resizeComponent.getHeight();
      if (e.getX() < CORNER) {
        m_mode = NORTHWEST;
      }
      else if(e.getX() > getWidth()-CORNER) {
        m_mode = NORTHEAST;
      }
      else {
        m_mode = NORTH;    
      }
    }  
  }
  
  class SouthResizeEdge extends JPanel
  implements MouseListener, MouseMotionListener {
    private static final int SOUTH = 0;
    private static final int SOUTHEAST = 1;
    private static final int SOUTHWEST = 2;
    private int CORNER = 10;
    private int HEIGHT = BORDER_THICKNESS;
    private int MIN_WIDTH = ICONIZED_WIDTH;
    private int MIN_HEIGHT = TITLE_BAR_HEIGHT+(2*HEIGHT);
    private int m_width, m_dragX, m_dragY, m_rightX;
    private boolean m_dragging;
    private JComponent m_resizeComponent;
    private int m_mode;
    
    protected SouthResizeEdge(JComponent c) {
      m_resizeComponent = c;
      setOpaque(true);
      setBackground(m_BorderColor);
    }

    public Dimension getPreferredSize() {
      return new Dimension(m_resizeComponent.getWidth(), HEIGHT);
    }
  
    public void mouseClicked(MouseEvent e) {}
  
    public void mouseMoved(MouseEvent e) {
      if (!m_dragging) {
        if (e.getX() < CORNER) {
          setCursor(Cursor.getPredefinedCursor(
            Cursor.SW_RESIZE_CURSOR));
        }
        else if(e.getX() > getWidth()-CORNER) {
          setCursor(Cursor.getPredefinedCursor(
            Cursor.SE_RESIZE_CURSOR));
        }
        else {
          setCursor(Cursor.getPredefinedCursor(
            Cursor.S_RESIZE_CURSOR));
        }
      }
    }
  
    public void mouseReleased(MouseEvent e) {
      m_dragging = false;
    }
  
    public void mouseDragged(MouseEvent e) {
      int h = m_resizeComponent.getHeight();
      int w = m_resizeComponent.getWidth();
      int x = m_resizeComponent.getX();
      int y = m_resizeComponent.getY();
      int ex = e.getX();
      int ey = e.getY();
      switch (m_mode) {
        case SOUTH:
          if (h+(ey-m_dragY) >= MIN_HEIGHT)
           m_resizeComponent.setBounds(x, y, w, h+(ey-m_dragY));
          else
            m_resizeComponent.setBounds(x, y, w, MIN_HEIGHT);
          break;
        case SOUTHEAST:
          if (h+(ey-m_dragY) >= MIN_HEIGHT
            && w + (ex-(getWidth()-CORNER)) >= MIN_WIDTH)
            m_resizeComponent.setBounds(x, y, 
              w + (ex-(getWidth()-CORNER)), h+(ey-m_dragY));
          else if (h+(ey-m_dragY) >= MIN_HEIGHT
            && !(w + (ex-(getWidth()-CORNER)) >= MIN_WIDTH))
            m_resizeComponent.setBounds(x, y, 
              MIN_WIDTH, h+(ey-m_dragY));
          else if (!(h+(ey-m_dragY) >= MIN_HEIGHT)
            && w + (ex-(getWidth()-CORNER)) >= MIN_WIDTH)
            m_resizeComponent.setBounds(x, y, 
              w + (ex-(getWidth()-CORNER)), MIN_HEIGHT);
          else
            m_resizeComponent.setBounds(x, 
              y, MIN_WIDTH, MIN_HEIGHT);
          break;
        case SOUTHWEST:
          if (h+(ey-m_dragY) >= MIN_HEIGHT 
            && w-(ex-m_dragX) >= MIN_WIDTH)
            m_resizeComponent.setBounds(x + (ex-m_dragX), y, 
              w-(ex-m_dragX), h+(ey-m_dragY));
          else if (h+(ey-m_dragY) >= MIN_HEIGHT
            && !(w-(ex-m_dragX) >= MIN_WIDTH)) {
            if (x + MIN_WIDTH < m_rightX)
              m_resizeComponent.setBounds(m_rightX-MIN_WIDTH, y, 
                MIN_WIDTH, h+(ey-m_dragY));
            else
              m_resizeComponent.setBounds(x, y, w, 
                h+(ey-m_dragY));
          }
          else if (!(h+(ey-m_dragY) >= MIN_HEIGHT)
            && w-(ex-m_dragX) >= MIN_WIDTH)
            m_resizeComponent.setBounds(x + (ex-m_dragX), y, 
              w-(ex-m_dragX), MIN_HEIGHT);
          else
            m_resizeComponent.setBounds(m_rightX-MIN_WIDTH, 
              y, MIN_WIDTH, MIN_HEIGHT);
          break;
      }
      m_rightX = x + w;
      m_resizeComponent.validate();
    }
  
    public void mouseEntered(MouseEvent e) {
      mouseMoved(e);
    }
    
    public void mouseExited(MouseEvent e) {
      if (!m_dragging)
        setCursor(Cursor.getPredefinedCursor(
          Cursor.DEFAULT_CURSOR));
    }
    
    public void mousePressed(MouseEvent e) {
      toFront();
      m_dragging = true;
      m_dragX = e.getX();
      m_dragY = e.getY();
      if (e.getX() < CORNER) {
        m_mode = SOUTHWEST;
      }
      else if(e.getX() > getWidth()-CORNER) {
        m_mode = SOUTHEAST;
      }
      else {
        m_mode = SOUTH;    
      }
    }  
  }
}

class MDIPane extends JLayeredPane implements ComponentListener
{
  public MDIPane() {
    addComponentListener(this);
    setOpaque(true);

    // default background color
    setBackground(new Color(244,232,152));
  }

  public void componentHidden(ComponentEvent e) {}
  public void componentMoved(ComponentEvent e) {}
  public void componentShown(ComponentEvent e) {}
  public void componentResized(ComponentEvent e) { lineup(); }
  public void lineup() {
    int frameHeight, frameWidth, currentX, currentY, lheight, lwidth;
    lwidth = getWidth();
    lheight = getHeight();
    currentX = 0;
    currentY = lheight;
    Component[] components = getComponents();
    for (int i=components.length-1; i>-1; i--) {
      if (components[i] instanceof InnerFrame) {
        InnerFrame tempFrame = (InnerFrame) components[i];
        frameHeight = tempFrame.getHeight();
        frameWidth = tempFrame.getWidth();
        if (tempFrame.isMaximized()) {
          tempFrame.setBounds(0,0,getWidth(),getHeight());
          tempFrame.validate(); 
          tempFrame.repaint(); 
        }
        else if (tempFrame.isIconified()) {
          if (currentX+frameWidth > lwidth) {
            currentX = 0;
            currentY -= frameHeight;
          }
          tempFrame.setLocation(currentX, currentY-frameHeight);
          currentX += frameWidth;
        }
      }
    }
  }
}