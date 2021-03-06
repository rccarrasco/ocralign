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
package eu.digitisation.images;

import eu.digitisation.math.Counter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import javax.media.jai.JAI;

/**
 * Extends BufferedImage with some useful operations
 *
 * @author R.C.C.
 */
public class Bimage extends BufferedImage implements Iterable<Point> {

    static int defaultImageType = BufferedImage.TYPE_INT_RGB;

    /**
     * Basic constructor
     *
     * @param width
     * @param height
     * @param imageType
     */
    public Bimage(int width, int height, int imageType) {
        super(width, height, imageType);
    }

    /**
     * Basic constructor
     *
     * @param width
     * @param height
     */
    public Bimage(int width, int height) {
        super(width, height, defaultImageType);
    }

    /**
     * Create a BufferedImage from another BufferedImage. Type set to default in
     * case of TYPE_CUSTOM (not handled by BufferedImage) .
     *
     * @param image the source image
     */
    public Bimage(BufferedImage image) {
        super(image.getWidth(null), image.getHeight(null),
                image.getType() == BufferedImage.TYPE_CUSTOM
                ? defaultImageType
                : image.getType());
        Graphics2D g = createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }

    /**
     * Create a BufferedImage of the given type from another BufferedImage.
     *
     * @param image the source image
     * @param type the type of BufferedImage
     */
    public Bimage(BufferedImage image, int type) {
        super(image.getWidth(null), image.getHeight(null), type);
        Graphics2D g = createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
    }

    /**
     * Create image from file.
     *
     * @param file the file storing the image
     * @throws IOException
     * @throws NullPointerException if the file format is unsupported
     */
    public Bimage(File file) throws IOException {
        //            this(javax.imageio.ImageIO.read(file));
        this(JAI.create("FileLoad",
                file.getCanonicalPath()).getAsBufferedImage());
    }

    /**
     * Create a scaled image
     *
     * @param img the source image
     * @param scale the scale factor
     */
    public Bimage(BufferedImage img, double scale) {
        super((int) (scale * img.getWidth()),
                (int) (scale * img.getHeight()),
                img.getType());
        int hints = java.awt.Image.SCALE_SMOOTH; //scaling algorithm
        Image scaled = img.getScaledInstance(this.getWidth(),
                this.getHeight(),
                hints);
        Graphics2D g = createGraphics();
        g.drawImage(scaled, 0, 0, null);
        g.dispose();
    }

    /**
     * Create a new image from two layers (with the type of first)
     *
     * @param first the first source image
     * @param second the second source image
     */
    public Bimage(BufferedImage first, BufferedImage second) {
        super(Math.max(first.getWidth(), second.getWidth()),
                Math.max(first.getHeight(), second.getHeight()),
                first.getType());
        BufferedImage combined = new BufferedImage(this.getWidth(),
                this.getHeight(),
                this.getType());
        Graphics2D g = combined.createGraphics();
        g.drawImage(first, 0, 0, null);
        g.drawImage(second, 0, 0, null);
        g.dispose();
    }

    /**
     * Clear the image to white
     */
    public void clear() {
        Graphics2D g = createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.dispose();
    }

    /**
     * Add a polygonal frontier to the image
     *
     * @param p a polygon
     * @param color the color of the polygon
     * @param stroke the line width in pixels
     */
    public void add(Polygon p, Color color, float stroke) {
        Graphics2D g = createGraphics();
        g.setColor(color);
        g.setStroke(new BasicStroke(stroke));
        g.drawPolygon(p);
        g.dispose();
    }

    /**
     * Add a dashed polygonal frontier to the image
     *
     * @param p a polygon
     * @param color the color of the polygon
     * @param stroke the line width in pixels
     * @param pattern the dash pattern, for example, {4f,2f} draws dashes with
     * length 4-units and separated 2 units
     */
    public void add(Polygon p, Color color, float stroke, float[] pattern) {
        Graphics2D g = createGraphics();
        BasicStroke bs = new BasicStroke(stroke, BasicStroke.CAP_BUTT,
                BasicStroke.JOIN_MITER, 1, pattern, 0.0f);
        g.setColor(color);
        g.setStroke(bs);
        g.drawPolygon(p);
        g.dispose();
    }

    /**
     * Add polygonal frontiers to the image
     *
     * @param polygons list of polygonal regions
     * @param color he color of the polygons
     * @param stroke the line width in pixels
     */
    public void add(List<Polygon> polygons, Color color, float stroke) {
        for (Polygon p : polygons) {
            add(p, color, stroke);
        }
    }

    /**
     * Add polygonal frontiers to the image
     *
     * @param polygons an array of polygonal regions
     * @param color he color of the polygons
     * @param stroke the line width in pixels
     * @param pattern the dash pattern, for example, {4f,2f} draws dashes
     * 4-pixels long separated by 2 pixels
     */
    public void add(Polygon[] polygons, Color color, float stroke, float[] pattern) {
        for (Polygon p : polygons) {
            add(p, color, stroke, pattern);
        }
    }

    /**
     * Add polygonal frontiers to the image
     *
     * @param polygons an array of polygonal regions
     * @param color he color of the polygons
     * @param stroke the line width in pixels
     * @param pattern the dash pattern, for example, {4f,2f} draws dashes
     * 4-pixels long separated by 2 pixels
     */
    public void add(List<Polygon> polygons, Color color, float stroke, float[] pattern) {
        for (Polygon p : polygons) {
            add(p, color, stroke, pattern);
        }
    }

    /**
     * Write the image to a file
     *
     * @param file the output file
     * @throws java.io.IOException
     */
    public void write(File file)
            throws IOException {
        Format format = Format.valueOf(file);
        JAI.create("filestore", this,
                file.getCanonicalPath(), format.toString());
        //javax.imageio.ImageIO.write(this, format, file);
    }

    /**
     * Finds the background (statistical mode of the rgb value for pixels in the
     * image)
     *
     * @return the mode of the color for pixels in this image
     */
    public Color background() {
        Counter<Integer> colors = new Counter<>();

        for (int x = 0; x < getWidth(); ++x) {
            for (int y = 0; y < getHeight(); ++y) {
                int rgb = getRGB(x, y);
                colors.inc(rgb);
            }
        }

        Integer mu = colors.maxValue();
        for (Integer n : colors.keySet()) {
            if (colors.get(n).equals(mu)) {
                return new Color(n);
            }
        }
        return null;
    }

    /**
     * The relative luminance (weighted average, see
     * http://en.wikipedia.org/wiki/Luminance_(relative)) of a pixel
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the luminance of pixel at (x,y), as defined in colorimetry
     */
    public int luminance(int x, int y) {
        Color c = new Color(getRGB(x, y));
        return (int) (0.2126 * c.getRed()
                + 0.7152 * c.getGreen()
                + 0.0722 * c.getBlue());
    }

    /**
     *
     * @param x the x coordinate
     * @param y the y coordinate
     *
     * @return the color of pixel (x, y)
     */
    public Color color(int x, int y) {
        return new Color(getRGB(x, y));
    }

    /**
     *
     * @return An iterator over the points in the image
     */
    @Override
    public Iterator<Point> iterator() {
        Iterator<Point> it = new Iterator<Point>() {
            int x = 0;
            int y = 0;

            @Override
            public boolean hasNext() {
                return x + 1 < getWidth() || y + 1 < getHeight();
            }

            @Override
            public Point next() {
                if (++x == getWidth()) {  // end of row reached
                    x = 0;
                    ++y;
                }
                return new Point(x, y);
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }

        };

        return it;
    }

}
