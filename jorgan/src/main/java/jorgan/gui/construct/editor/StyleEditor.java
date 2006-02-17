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
package jorgan.gui.construct.editor;

import java.beans.*;
import java.util.Iterator;

import jorgan.disposition.*;
import jorgan.skin.SkinManager;

/**
 * Property editor for a skin property.
 */
public class StyleEditor extends PropertyEditorSupport implements
        ElementAwareEditor {

    private Console console;

    public void setElement(Element element) {
        if (element instanceof Console) {
            console = (Console) element;
        } else {
            Iterator iterator = element.getReferrer(Console.class).iterator();
            if (iterator.hasNext()) {
                console = (Console) iterator.next();
            } else {
                console = null;
            }
        }
    }

    public String[] getTags() {

        String[] tags = new String[0];

        if (console != null) {
            String skinName = console.getSkin();
            if (skinName != null) {
                tags = SkinManager.instance().getStyleNames(skinName);
            }
        }

        return tags;
    }

    public String getAsText() {

        String style = (String) getValue();

        if (style == null) {
            return "";
        } else {
            return style;
        }
    }

    public void setAsText(String text) {

        if (text == null || "".equals(text)) {
            setValue(null);
        } else {
            setValue(text);
        }
    }
}
