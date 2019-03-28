/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package blueprints.utils.Borders;

import static blueprints.utils.Borders.GlowUtils.generateGlow;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;

/**
 *
 * @author terro
 */
public class GlowBorder extends AbstractBorder implements Border{

    private final int size;
    private final float alpha;
    private final Color color;
    
    public GlowBorder(int size,float alpha) {
        this.size = size;
        this.alpha = alpha;
        this.color=Color.BLACK;
    }
    
    public GlowBorder(Color color,int size,float alpha) {
        this.size = size;
        this.alpha = alpha;
        this.color=color;
    }
    
    

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        super.paintBorder(c, g, x, y, width, height);
        Graphics2D g2d = (Graphics2D) g.create();
        BufferedImage img = generateGlow(width-size, height-size, size, color, alpha);
        int dx = ((width - img.getWidth()) / 2);
        int dy = ((height - img.getHeight()) / 2);
        g2d.drawImage(img, dx, dy, null);
        g2d.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int dsize=size/2;
        return new Insets(dsize,dsize,dsize,dsize);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }
    
}
