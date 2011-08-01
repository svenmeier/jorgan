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
import java.util.HashMap;
import java.util.Map;

import bias.util.Tokenizer;

/**
 * Converter for {@link Map}s.
 */
public class MapConverter implements Converter {

	private Converter converter;

	public MapConverter(Converter converter) {
		this.converter = converter;
	}

	public String toString(Object object, Type type) throws Exception {
		Map<?, ?> map = (Map<?, ?>) object;

		Type keyType = getKeyType(type);
		Type valueType = getValueType(type);

		String[] tokens = new String[map.size() * 2];
		int index = 0;
		for (Object key : map.keySet()) {
			Object value = map.get(key);

			tokens[index] = converter.toString(key, keyType);
			tokens[index + 1] = converter.toString(value, valueType);

			index += 2;
		}

		return new Tokenizer(tokens).toString();
	}

	public Object fromString(String string, Type type) throws Exception {

		String[] tokens = new Tokenizer(string).getTokens();

		Map<Object, Object> map = new HashMap<Object, Object>();
		Type keyType = getKeyType(type);
		Type valueType = getValueType(type);
		for (int t = 0; t < tokens.length; t += 2) {
			Object key = null;
			Object value = null;

			key = converter.fromString(tokens[t], keyType);
			value = converter.fromString(tokens[t + 1], valueType);

			map.put(key, value);
		}

		return map;
	}

	private Type getKeyType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			return parameterizedType.getActualTypeArguments()[0];
		}
		throw new IllegalArgumentException("cannot convert raw maps");
	}

	private Type getValueType(Type type) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			return parameterizedType.getActualTypeArguments()[1];
		}
		throw new IllegalArgumentException("cannot convert raw maps");
	}
}
