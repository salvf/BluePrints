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

import blueprints.utils.Filters.GaussianFilter;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;

public class BlurUtils {

    public static BufferedImage generateBlur(int width, int height, int size, Color glow, float alpha) {
        BufferedImage source = createCompatibleImage(width, height);
        Graphics2D g2d = source.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        return generateGlow(source, size, glow, alpha);
    }

    public static BufferedImage generateGlow(BufferedImage imgSource, int size, Color color, float alpha) {

        int imgWidth = (int)Math.round(imgSource.getWidth() + (size * 2.5));
        int imgHeight = (int)Math.round(imgSource.getHeight() + (size * 2.5));

        BufferedImage imgMask = createCompatibleImage(imgWidth, imgHeight);
        Graphics2D g2 = imgMask.createGraphics();

        int x = Math.round((imgWidth - imgSource.getWidth()) / 2f);
        int y = Math.round((imgHeight - imgSource.getHeight()) / 2f);
        g2.drawImage(imgSource, x, y, null);
        g2.dispose();

        BufferedImage imgGlow = BlurUtils.generateBlur(imgMask, size, color, alpha);

        imgGlow = applyMask(imgGlow, imgMask, AlphaComposite.DST_OUT);

        return imgGlow;

    }
    public static BufferedImage generateBlur(BufferedImage imgSource, int size, Color color, float alpha) {

        GaussianFilter filter = new GaussianFilter(size);

        int imgWidth = imgSource.getWidth();
        int imgHeight = imgSource.getHeight();

        BufferedImage imgBlur = createCompatibleImage(imgWidth, imgHeight);
        Graphics2D g2 = imgBlur.createGraphics();

        g2.drawImage(imgSource, 0, 0, null);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, alpha));
        g2.setColor(color);

        g2.fillRect(0, 0, imgSource.getWidth(), imgSource.getHeight());
        g2.dispose();

        imgBlur = filter.filter(imgBlur, null);

        return imgBlur;

    }

    public static BufferedImage createCompatibleImage(int width, int height) {
        return createCompatibleImage(width, height, Transparency.TRANSLUCENT);
    }

    public static BufferedImage createCompatibleImage(int width, int height, int transparency) {
        BufferedImage image = getGraphicsConfiguration().createCompatibleImage(width, height, transparency);
        image.coerceData(true);
        return image;
    }
    public static GraphicsConfiguration getGraphicsConfiguration() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    }

    public static BufferedImage applyMask(BufferedImage sourceImage, BufferedImage maskImage, int method) {
        BufferedImage maskedImage = null;
        if (sourceImage != null) {
            int width = maskImage.getWidth(null);
            int height = maskImage.getHeight(null);

            maskedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D mg = maskedImage.createGraphics();

            int x = (width - sourceImage.getWidth(null)) / 2;
            int y = (height - sourceImage.getHeight(null)) / 2;

            mg.drawImage(sourceImage, x, y, null);
            mg.setComposite(AlphaComposite.getInstance(method));

            mg.drawImage(maskImage, 0, 0, null);

            mg.dispose();
        }
        return maskedImage;
    }

}
