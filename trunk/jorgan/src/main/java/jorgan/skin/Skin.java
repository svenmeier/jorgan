/*
 * jOrgan - Java Virtual Pipe Organ
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

import java.net.URL;
import java.util.ArrayList;

/**
 * Style.
 */
public class Skin {

    private String name = "";

    private ArrayList styles = new ArrayList();

    private SkinSource source;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        this.name = name;
    }

    public int getStyleCount() {
        return styles.size();
    }

    public String[] getStyleNames() {
        String[] names = new String[1 + styles.size()];

        for (int s = 0; s < styles.size(); s++) {
            names[s + 1] = getStyle(s).getName();
        }

        return names;
    }

    public Style getStyle(int index) {
        return (Style) styles.get(index);
    }

    public void addStyle(Style style) {
        styles.add(style);
        style.setResolver(new Resolver() {
            public URL resolve(String name) {
                return source.getURL(name);
            }

            public void setResolver(Resolver parent) {
            }
        });
    }

    public void setSource(SkinSource source) {
        this.source = source;
    }

    public SkinSource getSource() {
        return source;
    }

    public Skin getSkin() {
        return this;
    }

    public Style createStyle(String styleName) {

        for (int s = 0; s < styles.size(); s++) {
            Style style = (Style) styles.get(s);
            if (style.getName().equals(styleName)) {
                return (Style) style.clone();
            }
        }
        return null;
    }
}