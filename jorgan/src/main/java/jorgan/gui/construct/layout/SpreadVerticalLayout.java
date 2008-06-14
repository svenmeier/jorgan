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
package jorgan.gui.construct.layout;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jorgan.disposition.Displayable;
import jorgan.gui.console.View;

public class SpreadVerticalLayout extends ViewLayout {

    private int y;

    private int height;

    private int count;

    @Override
	protected void init(View<? extends Displayable> pressed, List<View<? extends Displayable>> views) {

        Collections.sort(views, new Comparator<View<? extends Displayable>>() {
            public int compare(View<? extends Displayable> view1, View<? extends Displayable> view2) {
                
                return view1.getY() - view2.getY();
            }
        });
        count = views.size();

        View<? extends Displayable> top = views.get(0);
        View<? extends Displayable> bottom = views.get(views.size() - 1);

        y = top.getY() + top.getHeight() / 2;
        height = bottom.getY() + bottom.getHeight() / 2 - y;
    }

    @Override
	protected void visit(View<? extends Displayable> view, int index) {
        changePosition(view, view.getX(), y + (height * index / (count - 1))
                - view.getHeight() / 2);
    }
}