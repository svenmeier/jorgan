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
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.lang.reflect.Method;
import java.util.List;

/**
 * A customer of beans.
 * 
 * @see jorgan.swing.beans.PropertiesPanel#setBeanCustomizer(BeanCustomizer)
 */
public interface BeanCustomizer {

	/**
	 * Get a beanInfo for the given class of beans.
	 * 
	 * @param beanClass
	 *            class of beans to get beanInfo for
	 * @return the bean info for the given class
	 * @throws IntrospectionException
	 */
	public BeanInfo getBeanInfo(Class<?> beanClass) throws IntrospectionException;

	/**
	 * Get an editor for a property.
	 * 
	 * @param descriptor
	 *            descriptor of property to get editor for
	 * @return the property editor for the given descriptor
	 * @throws IntrospectionException
	 */
	public PropertyEditor getPropertyEditor(PropertyDescriptor descriptor)
			throws IntrospectionException;

	public void beforeWrite(List<Object> beans, Method method);

	public void afterWrite(List<Object> beans, Method method);
}