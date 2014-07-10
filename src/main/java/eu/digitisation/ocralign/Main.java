package eu.digitisation.ocralign;

import eu.digitisation.image.Bimage;
import eu.digitisation.layout.ComponentType;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

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
/**
 *
 * @author RCC
 */
public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: image_file groundtruth_file");
        } else {
            File imfile = new File(args[0]);
            File gtfile = new File(args[1]);
            File ofile = new File(imfile.getAbsolutePath().replaceAll("^(.*)\\.(.*)$", "$1_marked.$2"));
            Bimage plot = Layout.plot(imfile, gtfile, ComponentType.BLOCK, Color.RED, 2f);
            plot.write(ofile);
            System.out.println("Output dumped to " + ofile.getAbsolutePath());
        }
    }
}
