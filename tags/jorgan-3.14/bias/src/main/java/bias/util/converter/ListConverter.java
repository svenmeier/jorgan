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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import bias.util.Tokenizer;

/**
 * Converter for {@link List}s.
 */
public class ListConverter implements Converter {

	private Converter converter;

	public ListConverter(Converter converter) {
		this.converter = converter;
	}

	public String toString(Object object, Type type) throws Exception {
		List<?> list = (List<?>) object;

		Type elementType = getElementType(type);

		String[] tokens = new String[list.size()];
		for (int t = 0; t < list.size(); t++) {
			Object element = list.get(t);
			tokens[t] = converter.toString(element, elementType);
		}

		return new Tokenizer(tokens).toString();
	}

	public Object fromString(String string, Type type) throws Exception {

		String[] tokens = new Tokenizer(string).getTokens();

		Type elementType = getElementType(type);

		List<Object> list = new ArrayList<Object>(tokens.length);
		for (int i = 0; i < tokens.length; i++) {
			list.add(converter.fromString(tokens[i], elementType));
		}

		return list;
	}

	private Type getElementType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			return parameterizedType.getActualTypeArguments()[0];
		}
		throw new IllegalArgumentException("cannot convert raw lists");
	}
}
