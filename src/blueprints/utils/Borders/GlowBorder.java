/*
 * Copyright 2019 Salvador Vera Franco.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package blueprints.utils.Borders;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import static blueprints.utils.Borders.BlurUtils.generateBlur;
import static blueprints.utils.Borders.BlurUtils.generateGlow;

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
        BufferedImage img = generateBlur(width-size, height-size, size, color, alpha);
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
