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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import jorgan.gui.console.ContinuousView;

/**
 * Style.
 */
public class SliderLayer extends CompositeLayer implements Cloneable {

    public static final int DIRECTION_LEFT_RIGHT = 1;

    public static final int DIRECTION_RIGHT_LEFT = 2;

    public static final int DIRECTION_TOP_BOTTOM = 3;

    public static final int DIRECTION_BOTTOM_TOP = 4;

    private int direction = DIRECTION_LEFT_RIGHT;

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getDirection() {
        return direction;
    }

    protected void drawChildren(Graphics2D g, Dimension dimension) {
        if (getChildCount() > 0) {
            int position = 0;

            if (view instanceof ContinuousView) {
                position = ((ContinuousView) view).getPosition();
            }

            int index = Math
                    .round(((float) (getChildCount() - 1) * position) / 127);

            Layer layer = getChild(index);

            layer.draw(g, dimension);
        }
    }

    protected boolean isPressable() {
        return true;
    }

    public void mousePressed(int x, int y, Dimension size) {
        mouseDragged(x, y, size);
    }

    public void mouseDragged(int x, int y, Dimension size) {
        Rectangle rectangle = getUnpaddedBounds(size);

        int position = 0;

        switch (direction) {
        case DIRECTION_LEFT_RIGHT:
            position = (x - rectangle.x) * 127 / rectangle.width;
            break;
        case DIRECTION_RIGHT_LEFT:
            position = (rectangle.width - (x - rectangle.x)) * 127
                    / rectangle.width;
            break;
        case DIRECTION_TOP_BOTTOM:
            position = (y - rectangle.y) * 127 / rectangle.height;
            ;
            break;
        case DIRECTION_BOTTOM_TOP:
            position = (rectangle.height - (y - rectangle.y)) * 127
                    / rectangle.height;
            break;
        }

        if (view instanceof ContinuousView) {
            ((ContinuousView) view).setPosition(position);
        }
    }

    public Object clone() {
        return (SliderLayer) super.clone();
    }
}