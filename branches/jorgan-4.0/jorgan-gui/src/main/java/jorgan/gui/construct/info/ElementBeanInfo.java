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
package jorgan.gui.construct.info;

import java.beans.PropertyDescriptor;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.gui.construct.editor.StringEditor;
import jorgan.swing.beans.PropertiesBeanInfo;

/**
 * BeanInfo for {@link jorgan.disposition.Element}.
 */
public class ElementBeanInfo extends PropertiesBeanInfo {

	protected void registerProperties() {
		// add name first so it's positioned at the default index
		// @see #getDefaultPropertyIndex()
		add("name", Element.class, StringEditor.class);
	}

	protected void add(String name, Class<? extends Element> clazz,
			Class<?> editor) {
		PropertyDescriptor descriptor = add(name, clazz, Elements
				.getDisplayName(clazz, name), editor);

		descriptor.setPreferred(true);
	}
}
