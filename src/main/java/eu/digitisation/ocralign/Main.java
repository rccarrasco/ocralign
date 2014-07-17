/*
 * Copyright (C) 2014 rafa
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
import java.io.File;
import java.io.IOException;

/**
 *
 * @author RCC
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: image_file angle");
        } else {
            // Input
            File imgfile = new File(args[0]);
            Bimage bim = new Bimage(imgfile);

            // outputs
            File hfile = new File(imgfile.getAbsolutePath().replaceAll("^(.*)\\.(.*)$", "$1_hplot.$2"));
            File lfile = new File(imgfile.getAbsolutePath().replaceAll("^(.*)\\.(.*)$", "$1_lplot.$2"));
            File outfile = new File(imgfile.getAbsolutePath().replaceAll("^(.*)\\.(.*)$", "$1_out.$2"));
            System.err.println("Output image in " + outfile);

            // Deskew
            double alpha = 180 * ImageEnhancement.skew(bim) / Math.PI;
            System.err.println("Image rotation = " + alpha + " degrees");

    
            // binarization
            //ImageEnhancement.showHistogram(bim, lfile);
            Bimage binary = ImageEnhancement.binarise(bim);
            binary.write(outfile);
            
            // Lines
              double[] Y = LineSplit.smoothProjection(bim); 
              double[] X = new double[Y.length];
             
              for (int n = 0; n < Y.length; ++n) { 
                  X[n] = n;
              //System.out.println(n + " " + Y[n]); 
              }
             
            Plot hplot = new Plot("H-projection", X, Y);
            //hplot.show(800, 400, 20);
            hplot.save(hfile, 800, 400, 20);
        }
    }
}
