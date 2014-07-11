/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.digitisation.ocralign;

import eu.digitisation.images.Bimage;
import eu.digitisation.input.FileType;
import eu.digitisation.layout.Page;
import eu.digitisation.log.Messages;
import eu.digitisation.math.Arrays;
import java.awt.Color;
import java.awt.Polygon;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author rafa
 */
public class LineSplit {

    static double threshold; // the threshold applied for line segmentation

    static {
        readProperties();
    }

    private static void readProperties() {
        Properties prop = new Properties();
        try {
            InputStream in = FileType.class.getResourceAsStream("/default.properties");
            prop.load(in);
            String s = prop.getProperty("line.threshold");
            if (s != null && s.length() > 0) {
                threshold = Double.valueOf(s);
            } else { // defualt value
                threshold = -0.4;
            }
            System.err.println("threshold=" + threshold);
        } catch (IOException ex) {
            Messages.info(Page.class.getName() + ": " + ex);
        }

    }

    /**
     * Horizontal projection of darkness
     *
     * @return the added darkness for every row (x-value) in the image.
     */
    private static int[] yprojection(Bimage bim) {
        int[] values = new int[bim.getHeight()];

        for (int y = 0; y < bim.getHeight(); ++y) {
            int sum = 0;
            for (int x = 0; x < bim.getWidth(); ++x) {
                sum += 255 - bim.luminance(x, y);
            }
            values[y] = sum;
        }
        return values;
    }

    /**
     * Split image into component lines
     */
    public static void slice(Bimage bim) {
        int[] values = yprojection(bim);
        ArrayList<Integer> limits = new ArrayList<>();
        double B = Arrays.average(values);
        //double A = Math.max(Stat.max(values) - B, B - Stat.min(values));
        double sigma = Arrays.std(values);
        int upper = 0;
        boolean inner = false;
       //double[] Y = new double[values.length];
       //double[] Z = new double[values.length]; // normalized values

        for (int y = 0; y < bim.getHeight(); ++y) {
            double nval = (values[y] - B) / sigma; // normalized value
            //System.out.println(y + " " + nval);
            //Y[y] = y;
            //Z[y] = nval;
            if (inner) {
                if (nval < threshold) {
                    limits.add(y);
                    inner = false;
                }
            } else {
                if (nval > threshold) {
                    limits.add(y);
                    inner = true;
                }
            }
        }
        addBoxes(bim, limits);
        //new Plot(Y, Z).show(400, 400, 40);
    }

    /**
     * Add boxes to image
     *
     * @param limits
     */
    private static void addBoxes(Bimage bim, List<Integer> limits) {
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.CYAN};
        for (int n = 0; n + 1 < limits.size(); ++n) {
            int s = n / 2;
            int y = limits.get(n);
            int h = limits.get(++n) - y; // rectangle height
            int x = 10 * (n % 2); // avoid full overlapping 
            int w = bim.getWidth() - 10 - 2 * x; // rectangle width
            Polygon poly = new Polygon();
            poly.addPoint(x, y);
            poly.addPoint(x + w, y);
            poly.addPoint(x + w, y + h);
            poly.addPoint(x, y + h);

            bim.add(poly, colors[s % 4], 1);
        }
    }
}
