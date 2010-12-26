/**
 * Bias - POJO Configuration.
 * Copyright (C) 2007 Sven Meier
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bias.util.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public class ConstantConverter implements Converter {

	public Converter converter;

	public ConstantConverter(Converter converter) {
		this.converter = converter;
	}
	
	public Object fromString(String string, Type type) throws Exception {
		int hash = string.indexOf('#');
		if (hash == -1 || string.length() <= 1) {
			return converter.fromString(string, type);
		} else {
			Field field = Class.forName(string.substring(0, hash))
					.getDeclaredField(string.substring(hash + 1));

			int modifiers = field.getModifiers();
			if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers)
					&& Modifier.isFinal(modifiers)) {
				return field.get(null);
			}

			throw new IllegalArgumentException(string);
		}
	}

	public String toString(Object object, Type type) throws Exception {
		return converter.toString(object, type);
	}
}