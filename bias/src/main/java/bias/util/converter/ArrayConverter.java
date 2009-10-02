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

import java.lang.reflect.Array;
import java.lang.reflect.Type;

import bias.util.Tokenizer;

public class ArrayConverter implements Converter {

	private Converter converter;

	public ArrayConverter(Converter converter) {
		this.converter = converter;
	}

	public String toString(Object array, Type type) throws Exception {
		int length = Array.getLength(array);
		Class componentType = ((Class) type).getComponentType();

		String[] tokens = new String[length];
		for (int i = 0; i < length; i++) {
			Object component = Array.get(array, i);
			tokens[i] = converter.toString(component, componentType);
		}

		return new Tokenizer(tokens).toString();
	}

	public Object fromString(String string, Type type) throws Exception {

		String[] tokens = new Tokenizer(string).getTokens();

		Class componentType = ((Class) type).getComponentType();

		Object array = Array.newInstance(componentType, tokens.length);
		for (int i = 0; i < tokens.length; i++) {
			String token = tokens[i];
			Array.set(array, i, converter.fromString(token, componentType));
		}

		return array;
	}
}
