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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;

/**
 * Default implementation of a beanCustomizer that uses the default beans schema
 * for lookup of {@link java.beans.BeanInfo}s and
 * {@link java.beans.PropertyEditor}s.
 * 
 * @see java.beans.Introspector
 * @see java.beans.PropertyEditorManager
 */
public class DefaultBeanCustomizer implements BeanCustomizer {

	public BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException {
		return new WriteableBeanInfo(new SortingBeanInfo(Introspector.getBeanInfo(beanClass)));
	}

	public PropertyEditor getPropertyEditor(PropertyDescriptor descriptor)
			throws IntrospectionException {
		if (descriptor.getPropertyEditorClass() == null) {
			return findPropertyEditor(descriptor.getPropertyType());
		} else {
			try {
				return (PropertyEditor) descriptor.getPropertyEditorClass()
						.newInstance();
			} catch (Exception ex) {
				throw new IntrospectionException(ex.getMessage());
			}
		}
	}

	/**
	 * Hook method for subclasses that want to implement a custom find of a
	 * {@link PropertyEditor} if none is defined by a {@link PropertyDescriptor}.
	 * 
	 * @see java.beans.PropertyDescriptor#getPropertyEditorClass()
	 * 
	 * @param propertyType
	 *            type of property to find editor for
	 */
	protected PropertyEditor findPropertyEditor(Class<?> propertyType)
			throws IntrospectionException {
		return PropertyEditorManager.findEditor(propertyType);
	}
}