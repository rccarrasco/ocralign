/*
 * Copyright (C) 2014 Universidad de Alicante
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package eu.digitisation.ocralign;

import eu.digitisation.images.Bimage;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

/**
 * BImage basic transformations
 * @author R.C.C
 */
public class Transform {

    /**
     * Create a scaled image
     *
     * @param bim the input image
     * @param scale the scale factor
     * @return a scaled image
     */
    public static Bimage scale(Bimage bim, double scale) {
        int w = (int) (scale * bim.getWidth());
        int h = (int) (scale * bim.getHeight());
        Bimage scaled = new Bimage(w, h, bim.getType());
        //int hints = java.awt.Image.SCALE_SMOOTH; //scaling algorithm
        //Image img = getScaledInstance(w, h, hints);
        Graphics2D g = scaled.createGraphics();
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        g.drawImage(bim, at, null);
        g.dispose();
        return scaled;
    }

    /**
     * Create a rotated image
     *
     * @param bim the input image
     * @param alpha the rotation angle (anticlockwise)
     * @return the rotated image
     */
    public static Bimage rotate(Bimage bim, double alpha) {
        double cos = Math.cos(alpha);
        double sin = Math.sin(alpha);
        int w = (int) Math.floor(bim.getWidth() * cos + bim.getHeight() * sin);
        int h = (int) Math.floor(bim.getHeight() * cos + bim.getWidth() * sin);
        Bimage rotated = new Bimage(w, h, bim.getType());
        Graphics2D g = (Graphics2D) rotated.getGraphics();

        g.setBackground(bim.background());
        g.clearRect(0, 0, w, h);
        if (alpha < 0) {
            g.translate(bim.getHeight() * sin, 0);
        } else {
            g.translate(0, bim.getWidth() * sin);
        }
        g.rotate(-alpha);
        g.drawImage(bim, 0, 0, null);
        g.dispose();

        return rotated;
    }

    /**
     * Transform image to gray-scale
     *
     * @param bim the input image
     * @return this image as gray-scale image
     */
    public static Bimage toGrayScale(BufferedImage bim) {
        ColorSpace space = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp operation = new ColorConvertOp(space, null);
        return new Bimage(operation.filter(bim, null));
    }

    /**
     * Transform image to RGB
     *
     * @param bim the input image
     *
     * @return this image as RGB image
     */
    public static Bimage toRGB(BufferedImage bim) {
        Bimage output = new Bimage(bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = output.createGraphics();
        g.drawImage(bim, 0, 0, null);
        g.dispose();
        return output;
    }

}
