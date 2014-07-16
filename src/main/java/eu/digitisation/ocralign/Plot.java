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

import eu.digitisation.images.Display;
import eu.digitisation.images.Format;
import eu.digitisation.math.Arrays;
import eu.digitisation.math.Counter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.media.jai.JAI;

/**
 * Creates toy plots
 *
 * @author R.C.C.
 */
public class Plot {

    private static final long serialVersionUID = 1L;

    String title;
    double[] X;
    double[] Y;

    /**
     * Create plot.
     *
     * @param title the title for this plot
     * @param X an array of X-values
     * @param Y an array of Y-values
     */
    public Plot(String title, double[] X, double[] Y) {
        this.title = title;
        this.X = X;
        this.Y = Y;
    }

    public <T extends Comparable<T>> Plot(String title, Counter<T> counter) {
        int size = counter.size();
        int n = 0;
        this.title = title;
        X = new double[size];
        Y = new double[size];

        for (T key : counter.keySet()) {
            X[n] = n;
            Y[n] = counter.value(key);
            ++n;
        }
    }

    /**
     * Set the plot title
     *
     * @param title the title for this plot
     */
    public void title(String title) {
        this.title = title;
    }

    /**
     * Integer exponentiation (for axis)
     *
     * @param base base
     * @param exp exponent
     * @return the exp-th power of base
     */
    private int pow(int base, int exp) {
        int result = 1;
        while (exp != 0) {
            if ((exp & 1) == 1) {
                result *= base;
            }
            exp >>= 1;
            base *= base;
        }
        return result;
    }

    private BufferedImage toBufferedImage(int width, int height, int margin) {
        BufferedImage img
                = new BufferedImage(width + 2 * margin,
                        height + 2 * margin,
                        BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        double xhigh = Arrays.max(X);
        double xlow = Arrays.min(X);
        double xrange = xhigh - xlow;
        double yhigh = Arrays.max(Y);
        double ylow = Arrays.min(Y);
        double yrange = yhigh - ylow;

        // white background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width + 2 * margin, height + 2 * margin);
        
        // draw lines
        g.setColor(Color.BLUE);
        int xprev = (int) Math.round((width * (X[0] - xlow)) / xrange);
        int yprev = (int) Math.round((height * (Y[0] - ylow)) / yrange);
        g.fillRect(margin + xprev - 1, height + margin - yprev, 3, 3);
        for (int n = 1; n < X.length; ++n) {
            int xpos = (int) Math.round((width * (X[n] - xlow)) / xrange);
            int ypos = (int) Math.round((height * (Y[n] - ylow)) / yrange);
            g.setColor(Color.BLUE);
            //  g.fillRect(margin + xpos - 1, height + margin - ypos, 3, ypos);
            //  g.fillRect(margin + xpos - 1, height + margin - ypos, 3, ypos);
            g.setColor(Color.RED);
            g.drawLine(margin + xprev, height + margin - yprev,
                    margin + xpos, height + margin - ypos);

            xprev = xpos;
            yprev = ypos;
        }

        // draw title
        g.setColor(Color.DARK_GRAY);
        if (title != null) {
            g.drawString(title, margin, margin / 2);
        }

        // draw X and Y axes
        g.setColor(Color.BLUE);
        g.drawRect(margin, margin, width, height);

        // draw Y-ticks
        int e = (int) Math.ceil(Math.log(yrange) / Math.log(10)) - 1;
        int ystep = (e > 0) ? pow(10, e) : 1;
        for (int y = (int) Math.round(ylow - ylow % ystep); y <= yhigh; y += ystep) {
            int ypos = (int) Math.round((height * (y - ylow)) / yrange);
            g.drawString(String.valueOf(y) + "-", 0, height + margin - ypos);
        }

        // draw X-ticks
        e = (int) Math.ceil(Math.log(xrange) / Math.log(10)) - 1;
        int xstep = (e > 0) ? pow(10, e) : 1;
        for (int x = (int) Math.round(xlow - xlow % xstep); x <= xhigh; x += xstep) {
            int xpos = (int) Math.round((width * (x - xlow)) / xrange);
            g.drawString(String.valueOf(x), margin + xpos - 6 * e, height + margin + 12);
            g.drawLine(margin + xpos, height + margin,
                    margin + xpos, height + margin - 5);
        }

        g.dispose();
        return img;
    }

    /**
     * Display histogram on screen
     *
     * @param width display width (in pixels)
     * @param height display height (in pixels)
     * @param margin display margins (in pixels)
     */
    public void show(int width, int height, int margin) {
        Display.draw(toBufferedImage(width, height, margin));
    }

    public void save(File file, int width, int height, int margin) 
            throws IOException {
        BufferedImage img = toBufferedImage(width, height, margin);
        Format format = Format.valueOf(file);
        JAI.create("filestore", img,
                file.getCanonicalPath(), format.toString());
    }
}
