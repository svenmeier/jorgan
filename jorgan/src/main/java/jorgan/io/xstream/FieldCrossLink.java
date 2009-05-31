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
package jorgan.io.xstream;

import java.lang.reflect.Field;

import com.thoughtworks.xstream.converters.ConversionException;

/**
 * A cross-link of a {@link Field}.
 */
public class FieldCrossLink implements CrossLink {

	private Class parentClass;

	private Field field;

	public FieldCrossLink(Class<?> owner, String field) {
		this.parentClass = owner;

		try {
			this.field = owner.getDeclaredField(field);
			this.field.setAccessible(true);
		} catch (Exception ex) {
			throw new Error(ex);
		}
	}

	public boolean isCrossLinked(Object object) {
		return field.getType().isInstance(object);
	}

	public boolean isCrossLink(Object parent, Object child) {
		return parentClass.isInstance(parent) && isCrossLinked(child);
	}

	public void crossLink(Object parent, Object child) {
		try {
			field.set(parent, child);
		} catch (IllegalAccessException ex) {
			throw new ConversionException(ex);
		}
	}
}
