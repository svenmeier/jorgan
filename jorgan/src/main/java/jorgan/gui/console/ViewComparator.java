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
package jorgan.gui.console;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The comparator used to sort the views according to their location.
 */
public class ViewComparator implements Comparator {

    private boolean horizontal;

    private boolean vertical;

    /**
     * Create a new comparator.
     * 
     * @param horizontal
     *            should views be compared horizontally
     * @param vertical
     *            should views be compared vertically
     */
    public ViewComparator(boolean horizontal, boolean vertical) {
        if (!horizontal && !vertical) {
            throw new IllegalArgumentException(
                    "need compare horizonal or vertical");
        }

        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public int compare(Object o1, Object o2) {
        View view1 = (View) o1;
        View view2 = (View) o2;

        int v0 = 0;
        int v1 = 0;
        if (horizontal) {
            v0 += view1.getX();
            v1 += view2.getX();
        }
        if (vertical) {
            v0 += view1.getY();
            v1 += view2.getY();
        }

        if (v0 < v1) {
            return 1;
        } else {
            return -1;
        }
    }

    public void sort(List list) {
        Collections.sort(list, this);
    }
}