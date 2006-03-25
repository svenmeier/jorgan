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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.net.URL;

import jorgan.gui.console.View;

/**
 * A layer.
 */
public abstract class Layer implements Resolver, Cloneable {

    public static final int CENTER = 0;

    public static final int TOP = 1;

    public static final int TOP_RIGHT = 2;

    public static final int RIGHT = 3;

    public static final int BOTTOM_RIGHT = 4;
    
    public static final int BOTTOM = 5;

    public static final int BOTTOM_LEFT = 6;

    public static final int LEFT = 7;
    
    public static final int TOP_LEFT = 8;

    public static final int NONE = 0;

    public static final int HORIZONAL = 1;

    public static final int VERTICAL = 2;

    public static final int BOTH = 3;

    private int anchor = CENTER;

    private int fill = NONE;

    private int width;

    private int height;

    private Insets padding = new Insets(0, 0, 0, 0);

    private transient Resolver resolver;

    protected transient View view;

    public void setResolver(Resolver resolver) {
        this.resolver = resolver;
    }

    public URL resolve(String name) {
        return this.resolver.resolve(name);
    }

    public void init(View view, Component component) {
        this.view = view;
    }

    public void draw(Graphics2D g, Dimension dimension) {

        Rectangle rectangle = getUnpaddedBounds(dimension);

        draw(g, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    protected Rectangle getUnpaddedBounds(Dimension size) {
        Rectangle rectangle = new Rectangle(0, 0, getUnpaddedWidth(),
                getUnpadddedHeight());

        if (fill == BOTH || fill == HORIZONAL) {
            rectangle.width = size.width - padding.left - padding.right;
        }
        if (fill == BOTH || fill == VERTICAL) {
            rectangle.height = size.height - padding.top - padding.bottom;
        }

        if (anchor == TOP_LEFT || anchor == LEFT || anchor == BOTTOM_LEFT) {
            rectangle.x = padding.left;
        } else if (anchor == TOP_RIGHT || anchor == RIGHT || anchor == BOTTOM_RIGHT) {
            rectangle.x = size.width - padding.right - rectangle.width;
        } else {
            rectangle.x = padding.left
                    + (size.width - padding.left - padding.right) / 2
                    - rectangle.width / 2;
        }

        if (anchor == TOP_LEFT || anchor == TOP || anchor == TOP_RIGHT) {
            rectangle.y = padding.top;
        } else if (anchor == BOTTOM_LEFT || anchor == BOTTOM || anchor == BOTTOM_RIGHT) {
            rectangle.y = size.height - padding.bottom - rectangle.height;
        } else {
            rectangle.y = padding.top
                    + (size.height - padding.top - padding.bottom) / 2
                    - rectangle.height / 2;
        }

        return rectangle;
    }

    protected void draw(Graphics2D g, int x, int y, int width, int height) {
    }

    public Object clone() {
        try {
            Layer clone = (Layer) super.clone();

            clone.setPadding(new Insets(padding.top, padding.left,
                    padding.bottom, padding.right));

            return clone;
        } catch (CloneNotSupportedException ex) {
            throw new Error();
        }
    }

    public void setFill(int fill) {
        this.fill = fill;
    }

    public int getFill() {
        return fill;

    }

    public void setPadding(Insets padding) {
        this.padding = padding;
    }

    public Insets getPadding() {
        return padding;
    }

    public void setAnchor(int anchor) {
        this.anchor = anchor;
    }

    public int getAnchor() {
        return anchor;
    }

    public Dimension getSize() {
        return new Dimension(getUnpaddedWidth() + padding.left + padding.right,
                getUnpadddedHeight() + padding.top + padding.bottom);

    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    protected int getUnpaddedWidth() {
        if (this.width == 0) {
            return calcWidth();
        } else {
            return this.width;
        }
    }

    /**
     * Calculate the width in case is is not explicitely set, i.e. it is
     * <code>0</code>.
     * 
     * @return the calculated width
     */
    protected int calcWidth() {
        return 0;
    }

    protected int getUnpadddedHeight() {
        if (this.width == 0) {
            return calcHeight();
        } else {
            return this.height;
        }
    }

    /**
     * Calculate the height in case is is not explicitely set, i.e. it is
     * <code>0</code>.
     * 
     * @return the calculated height
     */
    protected int calcHeight() {
        return 0;
    }

    public boolean isPressable(int x, int y, Dimension dimension) {
        if (isPressable()) {
            Rectangle rectangle = getUnpaddedBounds(dimension);

            return rectangle.contains(x, y);
        }

        return false;
    }

    protected boolean isPressable() {
        return false;
    }

    public void mousePressed(int x, int y, Dimension size) {
    }

    public void mouseDragged(int x, int y, Dimension size) {
    }

    public void mouseReleased(int x, int y, Dimension size) {
    }

    protected void released() {
    }
}