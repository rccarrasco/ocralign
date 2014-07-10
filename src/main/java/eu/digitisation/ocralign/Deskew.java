/*
 * Copyright (C) 2013 Universidad de Alicante
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

import eu.digitisation.image.Bimage;
import eu.digitisation.image.Display;
import eu.digitisation.input.FileType;
import eu.digitisation.layout.Page;
import eu.digitisation.log.Messages;
import eu.digitisation.math.Arrays;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.media.jai.JAI;

/**
 * A printed text
 *
 * @author R.C.C.
 * @version 20131110
 */
public class Deskew extends Bimage {

    /**
     *
     * @param file
     * @throws java.io.IOException
     * @throws NullPointerException if the file format is unsupported
     */
    public Deskew(File file) throws IOException {
        super(JAI.create("FileLoad",
                file.getCanonicalPath()).getAsBufferedImage(),
                BufferedImage.TYPE_INT_RGB);

    }

    public Deskew(Bimage bim) {
        super(bim);

    }

    /**
     * The luminance (weighted average, see
     * http://en.wikipedia.org/wiki/Luminance_(colorimetry)) of a pixel
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the luminance of pixel at (x,y), as defined in colorimetry
     */
    private int luminance(int x, int y) {
        Color c = new Color(getRGB(x, y));
        return (int) (0.2126 * c.getRed()
                + 0.7152 * c.getGreen()
                + 0.0722 * c.getBlue());
    }

    /**
     * For debugging: print vertical projection of gray levels
     */
    public void stats() {
        for (int y = 0; y < getHeight(); ++y) {
            int sum = 0;
            for (int x = 0; x < getWidth(); ++x) {
                sum += luminance(x, y);
            }
            System.out.println(y + " " + sum);
        }
    }

    /**
     * Sum RGB values for all x
     *
     * @param y the value of y-coordinate
     * @return
     */
    private int[] sumRGB(int y) {
        int[] s = new int[3];
        for (int x = 0; x < getWidth(); ++x) {
            int rgb = getRGB(x, y);
            s[0] += (rgb & 0xff0000) >> 16;
            s[1] += (rgb & 0x00ff00) >> 8;
            s[2] += (rgb & 0x0000ff);
        }
        return s;
    }

    
    /**
     *
     * @param alpha the line angle (alpha>0 if growing, alpha<0 if declining)
     *
     * @return the projection of darkness for every line y' = y + x * tan(alpha)
     */
    private int[] projection(double alpha) {
        double slope = Math.tan(alpha);
        int shift = (int) Math.round(slope * getWidth());
        int ymin = Math.min(0, shift);
        int ymax = Math.max(getHeight(), getHeight() + shift);
        //System.out.println(ymin+" "+ymax);
        int[] values = new int[ymax - ymin];
        for (int y = 0; y < getHeight(); ++y) {
            for (int x = 0; x < getWidth(); ++x) {
                int pos = (int) Math.round(y + slope * x);
                values[pos - ymin] += (255 - luminance(x, y));
            }
        }
        return values;
    }

    /**
     * How sharp is the image horizontal projection when rotated with angle
     * alpha
     *
     * @param alpha the rotation angle
     * @return a measure of the sharpness (clean separation of lines)
     */
    private double sharpness(double alpha) {
        return Arrays.std(projection(alpha));
    }

    /**
     * Find maximum within [left, right] with precision epsilon
     */
    private double findSkew(double left, double right, double epsilon) {
        while (right > left && right - left > epsilon) {
            double leftThird = (2 * left + right) / 3;
            double rightThird = (left + 2 * right) / 3;

            if (sharpness(leftThird) < sharpness(rightThird)) {
                left = leftThird;
            } else {
                right = rightThird;
            }
        }
        return (left + right) / 2;
    }

    /**
     *
     * @return the skew angle of this page
     */
    public double skew() {
        return findSkew(-0.1, 0.1, 0.001);
    }

    public double skew2() {
        double mu = 0;
        double skew = 0;

        for (double zeta = -3; zeta < 3; zeta += 0.1) {
            double alpha = Math.PI * zeta / 180;
            int[] pros = projection(alpha);
            //new Histogram("zeta=" + String.format("%.1f", zeta), pros).show(400,400,40);
            // System.out.println(pros.length);
            double s = Arrays.std(pros);
            System.out.println(String.format("%.2f", zeta)
                    + " " + Math.round(getWidth() * Math.tan(alpha))
                    + " " + String.format("%.1f", s));
            if (s > mu) {
                mu = s;
                skew = alpha;
            }
        }
        return skew;
    }

    public static void main(String[] args) throws Exception {
        String ifname = args[0];
        String ofname = args[1];
        File ifile = new File(ifname);
        File ofile = new File(ofname);
        Deskew input = new Deskew(ifile);
        //Bimage input = new Bimage(ifile);

        double alpha = input.skew();
        System.out.println("Image rotation=" + alpha);
        Deskew output = new Deskew(input.rotate(-alpha));
        //output.slice();
        output.write(ofile);
        System.err.println("Output image in " + ofname);
        Display.draw(output, 600, 900);
    }
}
