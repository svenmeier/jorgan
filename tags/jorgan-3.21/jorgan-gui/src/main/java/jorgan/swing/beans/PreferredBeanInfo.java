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

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.EventSetDescriptor;
import java.beans.MethodDescriptor;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link java.beans.BeanInfo} wrapper that removes non-important property
 * descriptors.
 */
public class PreferredBeanInfo implements BeanInfo {

	private BeanInfo info;

	private PropertyDescriptor[] descriptors;

	/**
	 * Wrap the given bean info.
	 * 
	 * @param info
	 *            bean info to wrap
	 */
	public PreferredBeanInfo(BeanInfo info) {
		this.info = info;
	}

	public BeanInfo[] getAdditionalBeanInfo() {
		return info.getAdditionalBeanInfo();
	}

	public BeanDescriptor getBeanDescriptor() {
		return info.getBeanDescriptor();
	}

	public int getDefaultEventIndex() {
		return info.getDefaultEventIndex();
	}

	public int getDefaultPropertyIndex() {
		int index = info.getDefaultPropertyIndex();
		if (index == -1) {
			return -1;
		} else {
			return 0;
		}
	}

	public EventSetDescriptor[] getEventSetDescriptors() {
		return info.getEventSetDescriptors();
	}

	public Image getIcon(int iconKind) {
		return info.getIcon(iconKind);
	}

	public MethodDescriptor[] getMethodDescriptors() {
		return info.getMethodDescriptors();
	}

	public PropertyDescriptor[] getPropertyDescriptors() {
		if (descriptors == null) {
			PropertyDescriptor[] descriptors = info.getPropertyDescriptors();

			List<PropertyDescriptor> filtered = new ArrayList<PropertyDescriptor>(
					descriptors.length);
			for (PropertyDescriptor descriptor : descriptors) {
				if (descriptor.isPreferred()) {
					filtered.add(descriptor);
				}
			}

			this.descriptors = filtered.toArray(new PropertyDescriptor[filtered
					.size()]);
		}

		return descriptors;
	}
}