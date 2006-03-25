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

import jorgan.gui.console.View;

/**
 * Style.
 */
public class Skin {

    /**
     * Constant for a focus towards the bottom left of the console. 
     */
    public static final int BOTTOM_LEFT = 0;

    /**
     * Constant for a focus towards the bottom right of the console. 
     */
    public static final int BOTTOM_RIGHT = 1;
    
    /**
     * Constant for a focus towards the top left of the console. 
     */
    public static final int TOP_LEFT = 2;
    
    /**
     * Constant for a focus towards the top rightof the console. 
     */
    public static final int TOP_RIGHT = 3;
    
    private String name = "";
    
    private int focus = BOTTOM_RIGHT;

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

    public int getFocus() {
        return focus;
    }
    
    public void setFocus(int focus) {
        this.focus = focus;
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

    /**
     * Compare the given two views according to the focus of this skin.
     * 
     * @param view1 first view
     * @param view2 second view
     * @return  comparison result
     * 
     * @see #BOTTOM_LEFT
     * @see #BOTTOM_RIGHT
     * @see #TOP_LEFT
     * @see #TOP_RIGHT
     */
    public int compare(View view1, View view2) {
        switch (focus) {
            case BOTTOM_LEFT:
                return (view1.getX() - view2.getX()) + (view2.getY() - view1.getY());   
            case BOTTOM_RIGHT:
                return (view2.getX() - view1.getX()) + (view2.getY() - view1.getY());   
            case TOP_LEFT:
                return (view1.getX() - view2.getX()) + (view1.getY() - view2.getY());   
            case TOP_RIGHT:
                return (view2.getX() - view1.getX()) + (view1.getY() - view2.getY());   
        }

        throw new Error();
    }

}