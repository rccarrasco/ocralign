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
import eu.digitisation.image.Display;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author RCC
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: image_file angle");
        } else {
            File imfile = new File(args[0]);
            double alpha = Math.PI * Double.parseDouble(args[1]) / 180;
            Bimage bim = new Bimage(imfile);
            int[] p = Enhancement.projection(null, alpha);
            for (int n = 0; n < p.length; ++n) {
                System.out.println(n + " " + p[n]);
            }
            /*
             File gtfile = new File(args[1]);
             File ofile = new File(imfile.getAbsolutePath().replaceAll("^(.*)\\.(.*)$", "$1_marked.$2"));
             Bimage plot = Layout.plot(imfile, gtfile, ComponentType.BLOCK, Color.RED, 2f);
             plot.write(ofile);
             System.out.println("Output dumped to " + ofile.getAbsolutePath());
             */

            //alpha = 180 * Enhancement.skew(bim) / Math.PI;
            //double alpha2 = 180 * Enhancement.skew2(bim) / Math.PI;
            //System.err.println("Image rotation = " + alpha + " degrees");
            //System.err.println("Image rotation = " + alpha2 + " degrees");
            //p.slice();
            //Bimage rotated = Transform.rotate(bim, 5 * Math.PI / 180);
            //rotated.write(ofile);
            //System.err.println("Output image in " + ofile);
            //Display.draw(rotated, rotated.getWidth(), rotated.getHeight());
        }
    }
}
