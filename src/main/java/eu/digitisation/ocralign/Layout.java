/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.digitisation.ocralign;

import eu.digitisation.images.Bimage;
import eu.digitisation.input.FileType;
import eu.digitisation.layout.ALTOPage;
import eu.digitisation.layout.ComponentType;
import eu.digitisation.layout.FR10Page;
import eu.digitisation.layout.HOCRPage;
import eu.digitisation.layout.PAGEPage;
import eu.digitisation.layout.Page;
import java.awt.Color;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author rafa
 */
public class Layout {

    /**
     *
     * @param ifile image file (e.g., TIF)
     * @param gtfile ground truth file (PAGE XML format)
     * @param type component to be depicted (words, lines, paragraphs) 
     * @param color the color of the border for each component region
     * @param stroke the width of the border
     * @return the image with the depicted regions 
     * @throws java.io.IOException
     */
    public static Bimage plot(File ifile, File gtfile, ComponentType type,
            Color color, float stroke) throws IOException {
        Bimage page = null;
        Page gt = null;

        if (ifile.exists()) {
            try {
                page = Transform.toRGB(new Bimage(ifile));
            } catch (NullPointerException ex) {
                throw new IOException("Unsupported format");
            }
        } else {
            throw new java.io.IOException(ifile.getCanonicalPath() + " not found");
        }
        if (gtfile.exists()) {
            FileType ftype = FileType.valueOf(gtfile);
            switch (ftype) {
                case PAGE:
                    gt = new PAGEPage(gtfile);
                    break;
                case HOCR:
                    gt = new HOCRPage(gtfile);
                    break;
                case FR10:
                    gt = new FR10Page(gtfile);
                    break;
                case ALTO:
                    gt = new ALTOPage(gtfile);
                    break;
                default:
                    throw new java.lang.UnsupportedOperationException("Still not implemented");
            }
        } else {
            throw new java.io.IOException(gtfile.getCanonicalPath() + " not found");
        }
        List<Polygon> poly = gt.getFrontiers(type);
        page.add(poly, color, stroke);
        return page;
    }
}
