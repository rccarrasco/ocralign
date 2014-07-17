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

import eu.digitisation.images.Bimage;
import eu.digitisation.images.Display;
import eu.digitisation.math.Arrays;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 *
 * Perform operations to improve image quality
 *
 * @author R.C.C.
 * @version 20131110
 */
public class ImageEnhancement {

    /**
     * For debugging: print vertical projection of gray levels
     *
     * @param bim the input image
     */
    public static void stats(Bimage bim) {
        for (int y = 0; y < bim.getHeight(); ++y) {
            int sum = 0;
            for (int x = 0; x < bim.getWidth(); ++x) {
                sum += bim.luminance(x, y);
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
    private static int[] addedRGB(Bimage bim, int y) {
        int[] s = new int[3];
        for (int x = 0; x < bim.getWidth(); ++x) {
            int rgb = bim.getRGB(x, y);
            s[0] += (rgb & 0xff0000) >> 16;
            s[1] += (rgb & 0x00ff00) >> 8;
            s[2] += (rgb & 0x0000ff);
        }
        return s;
    }

    /**
     *
     * @param bim the input image
     * @param alpha the line angle (alpha > 0 if growing, alpha < 0 if
     * declining). Absolute value must be pi/2 at most.
     *
     * @return the horizontal projection of darkness when points (x,y) are
     * rotated to (x',y') Since y-values are indeed negative (x=0, y=0
     * represents the upper-left corner in the picture), the transformation is
     * not the standard but the following (as if alpha was negative): x' = x *
     * cos(alpha) + y * sin(alpha) y' = -x * sin(alpha) + y * cos(alpha)
     */
    public static int[] projection(Bimage bim, double alpha) {
        // highest point is either (0,0) or upper-right corner (width, 0)
        int ymin = (alpha < 0)
                ? 0
                : (int) Math.round(-Math.sin(alpha) * bim.getWidth());
        // lowest point eihter (0, height) or (width, height)
        int ymax = (alpha < 0)
                ? (int) Math.round(-Math.sin(alpha) * bim.getWidth() + Math.cos(alpha) * bim.getHeight())
                : (int) Math.round(Math.cos(alpha) * bim.getHeight());

        //System.out.println(ymin + " " + ymax);
        int[] values = new int[ymax - ymin + 1];
        for (int y = 0; y < bim.getHeight(); ++y) {
            for (int x = 0; x < bim.getWidth(); ++x) {
                int pos = (int) Math.round(-Math.sin(alpha) * x + Math.cos(alpha) * y);
                values[pos - ymin] += (255 - bim.luminance(x, y));
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
    private static double sharpness(Bimage bim, double alpha) {
        return Arrays.std(projection(bim, alpha));
    }

    /**
     * Find maximum within [left, right] with precision epsilon
     */
    private static double findSkew(Bimage bim, double left,
            double right, double epsilon) {
        while (right > left && right - left > epsilon) {
            double leftThird = (2 * left + right) / 3;
            double rightThird = (left + 2 * right) / 3;

            if (sharpness(bim, leftThird) < sharpness(bim, rightThird)) {
                left = leftThird;
            } else {
                right = rightThird;
            }
        }
        return (left + right) / 2;
    }

    /**
     *
     * @param bim the input image
     * @return the skew angle of this page
     */
    public static double skew(Bimage bim) {
        return findSkew(bim, -0.1, 0.1, 0.01);
    }

    public static double skew2(Bimage bim) {
        double mu = 0;
        double skew = 0;

        for (double zeta = -5; zeta < 5; zeta += 0.1) {
            double alpha = Math.PI * zeta / 180;
            int[] pros = projection(bim, alpha);
            //new Histogram("zeta=" + String.format("%.1f", zeta), pros).show(400,400,40);
            // System.out.println(pros.length);
            double s = Arrays.std(pros);
            System.out.println(String.format("%.2f", zeta)
                    //+ " " + Math.round(bim.getWidth() * Math.tan(alpha))
                    + " " + String.format("%.2f", s));
            if (s > mu) {
                mu = s;
                skew = alpha;
            }
        }
        return skew;
    }

    private static Histogram luminanceHistogram(Bimage bim) {
        Histogram hist = new Histogram();

        for (Point p : bim) {
            int lumin = bim.luminance(p.x, p.y);
            hist.inc(lumin);
        }
        return hist;
    }

    /**
     *
     * @param bim the input image
     * @param threshold the value (between 0 and 255) splitting black and white
     * pixels
     * @return a binarised (black and white) version of the image
     */
    public static Bimage binary(Bimage bim, int threshold) {
        int width = bim.getWidth();
        int height = bim.getHeight();
        int whiteRGB = Color.WHITE.getRGB();
        Bimage binary = new Bimage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                if (bim.luminance(x, y) > threshold) {
                    binary.setRGB(x, y, whiteRGB);
                } else {
                    binary.setRGB(x, y, 0);
                }
            }
        }
        return binary;
    }

    /**
     * @param bim the input image
     * @return a binarised (black and white) version of the image
     */
    public static Bimage binarise(Bimage bim) {
        Histogram hist = luminanceHistogram(bim);
        double high = hist.average();
        double low = hist.mode(0, (int) (int) (hist.average() - 1.5 * hist.std()));
        int threshold = (int) ((low + high) / 2);

        return binary(bim, threshold);
    }

    public static void showHistogram(Bimage bim, File ofile) throws IOException {
        Histogram hist = luminanceHistogram(bim);
        int high = (int) (hist.average() - 1.5 * hist.std());
        int second = hist.mode(0, high);

        System.out.println("mu=" + hist.average());
        System.out.println("std=" + hist.std());
        System.out.println("decile=" + hist.percentile(0.1));
        System.out.println("left max=" + second);

        int size = 1 + Collections.max(hist.keySet());
        double[] X = new double[size];
        double[] Y = new double[size];

        for (int n = 0;
                n < size;
                ++n) {
            X[n] = n;
            if (hist.value(n) > 0) {
                Y[n] = hist.value(n);//Math.log(counter.value(n));
            } else {
                Y[n] = 0;
            }
//            System.err.println(n + " " + Y[n]);
        }

        Plot plot = new Plot("Luminiscence", X, Y);

        plot.show(600, 400, 60);
        plot.save(ofile, 600, 400, 60);
    }

    public static void main(String[] args) throws Exception {
        String ifname = args[0];
        String ofname = args[1];
        File ifile = new File(ifname);
        File ofile = new File(ofname);
        Bimage bim = new Bimage(ifile);

        double alpha = ImageEnhancement.skew(bim);
        System.out.println("Image rotation=" + alpha);
        Bimage output = Transform.rotate(bim, alpha);
        //output.slice();
        output.write(ofile);
        System.err.println("Output image in " + ofname);
        Display.draw(output, 600, 900);
    }
}
