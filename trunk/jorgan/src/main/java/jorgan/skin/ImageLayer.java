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

    public void setView(View view) {
        super.setView(view);
        
        if (image == null) {
            image = ImageCache.getImage(resolve(file), view.getConsolePanel());
        }
    }

    protected void draw(Graphics2D g, int x, int y, int width, int height) {

        if (width == calcWidth() && height == calcHeight()
                || border.equals(ZERO_BORDER)) {
            drawImage(g, image, x, y, x + width, y + height, 0, 0, width, height);
        } else {
            // TOP-LEFT
            drawImage(g, image, x, y, x + border.left, y + border.top, 0, 0,
                    border.left, border.top);
            // BOTTOM-LEFT
            drawImage(g, image, x, y + height - border.bottom, x + border.left,
                    y + height, 0, calcHeight() - border.bottom, border.left,
                    calcHeight());
            // TOP-RIGHT
            drawImage(g, image, x + width - border.right, y, x + width, y
                    + border.top, calcWidth() - border.right, 0, calcWidth(),
                    border.top);
            // BOTTOM-RIGHT
            drawImage(g, image, x + width - border.right, y + height
                    - border.bottom, x + width, y + height, calcWidth()
                    - border.right, calcHeight() - border.bottom, calcWidth(),
                    calcHeight());
            // TOP
            drawImage(g, image, x + border.left, y, x + width - border.right, y
                    + border.top, border.left, 0, calcWidth() - border.right,
                    border.top);
            // BOTTOM
            drawImage(g, image, x + border.left, y + height - border.bottom, x
                    + width - border.right, y + height, border.left,
                    calcHeight() - border.bottom, calcWidth() - border.right,
                    calcHeight());
            // LEFT
            drawImage(g, image, x, y + border.top, x + border.left, y + height
                    - border.bottom, 0, border.top, border.left, calcHeight()
                    - border.bottom);
            // RIGHT
            drawImage(g, image, x + width - border.right, y + border.top, x
                    + width, y + height - border.bottom, calcWidth()
                    - border.right, border.top, calcWidth(), calcHeight()
                    - border.bottom);
            // CENTER
            drawImage(g, image, x + border.left, y + border.top, x + width
                    - border.right, y + height - border.bottom, border.left,
                    border.top, calcWidth() - border.right, calcHeight()
                            - border.bottom);
        }
    }

    protected void drawImage(Graphics2D g, Image image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2) {
        g.drawImage(image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
    }
    
    public Object clone() {
        ImageLayer clone = (ImageLayer) super.clone();

        clone.border = (Insets) border.clone();

        return clone;
    }
}