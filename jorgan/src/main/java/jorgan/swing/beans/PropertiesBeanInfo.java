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
package jorgan.swing.beans;

import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;
import java.util.ArrayList;
import java.util.List;

/**
 * BeanInfo specialized for properties.
 */
public class PropertiesBeanInfo extends SimpleBeanInfo {

	private List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();

	@Override
	public final PropertyDescriptor[] getPropertyDescriptors() {

		descriptors.clear();

		registerProperties();

		return descriptors.toArray(new PropertyDescriptor[descriptors.size()]);
	}

	@Override
	public int getDefaultPropertyIndex() {
		return 0;
	}

	protected void registerProperties() {
	}

	protected void add(String name, Class<?> clazz, String displayName,
			Class<?> editor) {
		try {
			PropertyDescriptor descriptor = new PropertyDescriptor(name, clazz);

			descriptor.setDisplayName(displayName);

			if (editor != null) {
				descriptor.setPropertyEditorClass(editor);
			}

			descriptors.add(descriptor);
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}
}
