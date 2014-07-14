
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

/**
 *
 * @author RCC
 */
public class Binarization {

    public static void histogram(Bimage bim) {
        Counter<Integer> counter = new Counter();
        Point box = new Point(bim.getWidth(), bim.getHeight());
        for (Point p : bim) {
            //System.out.println(p + " " + box);
            Color c = bim.color(p.x, p.y);
            int average = (c.getRed() + c.getGreen() + c.getBlue())/3;
            counter.inc(c.getRed());
        }
        Plot plot = new Plot("Luminiscence", counter);
        plot.show(600, 400, 60);
    }

}
