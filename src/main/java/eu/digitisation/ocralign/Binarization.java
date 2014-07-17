
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
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

/**
 *
 * @author RCC
 */
public class Binarization {

    /**
     *
     * @param hist a histogram of integers
     * @return average value of the integers
     */
    protected static double average(Counter<Integer> hist) {
        int tot = 0;
        int sum = 0;

        for (Integer n : hist.keySet()) {
            int val = hist.value(n);
            sum += n * val;
            tot += val;
        }

        return sum / (double) tot;
    }

    /**
     *
     * @param hist a histogram of integers
     * @return the standard deviation value of the integers
     */
    protected static double std(Counter<Integer> hist) {
        int tot = 0;
        double mu = average(hist);
        double sum = 0;

        for (Integer n : hist.keySet()) {
            int val = hist.value(n);
            sum += (n - mu) * (n - mu) * val;
            tot += val;
        }

        return Math.sqrt(sum / tot);
    }

    /**
     *
     * @param hist a histogram of integers
     * @param low low end of range (inclusive)
     * @param high high end of range (exclusive)
     *
     * @return the mode (most frequent key) in this range
     */
    protected static Integer mode(Counter<Integer> hist, int low, int high) {
        Integer mode = null;

        for (int n = low; n < high; ++n) {
            int val = hist.value(n);
            if (mode == null || val > hist.value(mode)) {
                mode = n;
            }
        }
        return mode;
    }

    protected static int percentile(Counter<Integer> hist, double threshold) {
        int total = 0;

        for (Integer val : hist.values()) {
            total += val;
        }

        // keys are sorted (fortuantely)
        int partial = 0;
        for (Integer n : hist.keySet()) {
            partial += hist.value(n);
            if (partial > total * threshold) {
                return n;
            }
        }

        return hist.lastKey();
    }

    private static Counter<Integer> luminanceHistogram(Bimage bim) {
        Counter<Integer> hist = new Counter<>();

        for (Point p : bim) {
            int lumin = bim.luminance(p.x, p.y);
            hist.inc(lumin);
        }
        return hist;
    }

    public static void showHistogram(Bimage bim, File ofile) throws IOException {
        Counter<Integer> hist = luminanceHistogram(bim);
        int high = (int) (average(hist) - 1.5 * std(hist));
        int second = mode(hist, 0, high);

        System.out.println("mu=" + average(hist));
        System.out.println("std=" + std(hist));
        System.out.println("decile=" + percentile(hist, 0.1));
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

}
