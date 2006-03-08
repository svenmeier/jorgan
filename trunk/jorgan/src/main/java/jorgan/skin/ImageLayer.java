/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.skin;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;

import jorgan.gui.console.View;

/**
 * An image layer.
 */
public class ImageLayer extends Layer {

    private Insets ZERO_BORDER = new Insets(0, 0, 0, 0);

    private String file = "";

    private Insets border = new Insets(0, 0, 0, 0);

    private transient Image image;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        if (file == null) {
            throw new IllegalArgumentException("file of image cannot be null");
        }
        this.file = file;
    }

    public Insets getBorder() {
        return border;
    }

    public void setBorder(Insets border) {
        this.border = border;
    }

    /**
     * Calculate the height based on the image's height.
     * 
     * @return the image's height
     */
    protected int calcHeight() {
        return image.getHeight(null);
    }

    /**
     * Calculate the width based on the image's width.
     * 
     * @return the image's width
     */
    protected int calcWidth() {
        return image.getWidth(null);
    }

    public void init(View view, Component component) {
        if (image == null) {
            image = ImageCache.getImage(resolve(file));
        }
    }

    protected void draw(Graphics2D g, int x, int y, int width, int height) {
        if (width == calcWidth() && height == calcHeight()
                || border.equals(ZERO_BORDER)) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            // TOP-LEFT
            g.drawImage(image, x, y, x + border.left, y + border.top, 0, 0,
                    border.left, border.top, null);
            // BOTTOM-LEFT
            g.drawImage(image, x, y + height - border.bottom, x + border.left,
                    y + height, 0, calcHeight() - border.bottom,
                    border.left, calcHeight(), null);
            // TOP-RIGHT
            g.drawImage(image, x + width - border.right, y, x + width, y
                    + border.top, calcWidth() - border.right, 0,
                    calcWidth(), border.top, null);
            // BOTTOM-RIGHT
            g.drawImage(image, x + width - border.right, y + height
                    - border.bottom, x + width, y + height, calcWidth()
                    - border.right, calcHeight() - border.bottom,
                    calcWidth(), calcHeight(), null);
            // TOP
            g.drawImage(image, x + border.left, y, x + width - border.right, y
                    + border.top, border.left, 0,
                    calcWidth() - border.right, border.top, null);
            // BOTTOM
            g.drawImage(image, x + border.left, y + height - border.bottom, x
                    + width - border.right, y + height, border.left,
                    calcHeight() - border.bottom, calcWidth()
                            - border.right, calcHeight(), null);
            // LEFT
            g.drawImage(image, x, y + border.top, x + border.left, y + height
                    - border.bottom, 0, border.top, border.left,
                    calcHeight() - border.bottom, null);
            // RIGHT
            g.drawImage(image, x + width - border.right, y + border.top, x
                    + width, y + height - border.bottom, calcWidth()
                    - border.right, border.top, calcWidth(), calcHeight()
                    - border.bottom, null);
            // CENTER
            g.drawImage(image, x + border.left, y + border.top, x + width
                    - border.right, y + height - border.bottom, border.left,
                    border.top, calcWidth() - border.right, calcHeight()
                            - border.bottom, null);
        }
    }

    public Object clone() {
        ImageLayer clone = (ImageLayer) super.clone();

        clone.border = (Insets) border.clone();

        return clone;
    }
}