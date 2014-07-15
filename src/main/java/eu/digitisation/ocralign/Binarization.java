
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
import eu.digitisation.math.Counter;
import java.awt.Color;
import java.awt.Point;
import java.util.Collections;

/**
 *
 * @author RCC
 */
public class Binarization {

    public static void histogram(Bimage bim) {
        int total = 0;
        int dark = 0;
        Counter<Integer> counter = new Counter<>();
        Point box = new Point(bim.getWidth(), bim.getHeight());
        for (Point p : bim) {
            //System.out.println(p + " " + box);
            Color c = bim.color(p.x, p.y);
            int average = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
            counter.inc(average);
            ++total;
            if (average < 150) {
                ++dark;
            }
        }
        System.out.println("dark=" + (100.0 * dark) / total);

        int size = 1 + Collections.max(counter.keySet());
        double[] X = new double[size];
        double[] Y = new double[size];

        for (int n = 0; n < size; ++n) {
            X[n] = n;
            if (counter.value(n) > 0) {
                //System.err.println(n + " " + Math.log(counter.value(n)));
                Y[n] = counter.value(n);//Math.log(counter.value(n));
            } else {
                Y[n] = 0;
            }
        }

        Plot plot = new Plot("Luminiscence", X, Y);
        plot.show(600, 400, 60);
    }

}
