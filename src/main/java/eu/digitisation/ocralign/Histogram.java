/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.digitisation.ocralign;

import eu.digitisation.math.Counter;

/**
 *
 * @author rafa
 */
public class Histogram extends Counter<Integer> {
    /**
     *
     * @return average value of the integers
     */
    protected double average() {
        int tot = 0;
        int sum = 0;

        for (Integer n : keySet()) {
            int val = value(n);
            sum += n * val;
            tot += val;
        }

        return sum / (double) tot;
    }

    /**
     *
     * @return the standard deviation value of the integers
     */
    protected double std() {
        int tot = 0;
        double mu = average();
        double sum = 0;

        for (Integer n : keySet()) {
            int val = value(n);
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
    protected Integer mode(int low, int high) {
        Integer mode = null;

        for (int n = low; n < high; ++n) {
            int val = value(n);
            if (mode == null || val > value(mode)) {
                mode = n;
            }
        }
        return mode;
    }

    /**
     *
     * @param threshold 
     * @return the first key with cumulative mass above the threshold
     */
    protected int percentile(double threshold) {
        int total = 0;

        for (Integer val : values()) {
            total += val;
        }

        // keys are sorted (fortuantely)
        int partial = 0;
        for (Integer n : keySet()) {
            partial += value(n);
            if (partial > total * threshold) {
                return n;
            }
        }

        return lastKey();
    }
}
