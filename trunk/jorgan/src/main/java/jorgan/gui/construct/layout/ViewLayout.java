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

import java.util.List;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import jorgan.gui.console.View;

/**
 * The Layout for views.
 */
public abstract class ViewLayout {

    private static final ResourceBundle resources = ResourceBundle
            .getBundle("jorgan.gui.resources");

    private String name;

    private Icon icon;

    public ViewLayout(String key) {
        if (key != null) {
            this.name = resources.getString("view." + key);
            this.icon = new ImageIcon(View.class.getResource("/jorgan/gui/img/"
                    + key + ".gif"));
        }
    }

    public String getName() {
        return name;
    }

    public Icon getIcon() {
        return icon;
    }

    public void layout(View pressed, List views) {
        init(pressed, views);

        for (int s = 0; s < views.size(); s++) {
            View view = (View) views.get(s);
            visit(view, s);
        }
    }

    protected void changePosition(View view, int x, int y) {
        view.getConsolePanel().getConsole()
                .setLocation(view.getElement(), x, y);
    }

    protected void init(View pressed, List views) {
    }

    protected void visit(View view, int index) {
    }
}