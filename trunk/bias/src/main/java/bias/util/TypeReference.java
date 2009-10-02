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
package bias.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Utility to get a reference to a {@link Type}.
 * 
 * @param T
 *            the type to get reference to
 */
public abstract class TypeReference<T> {

	private final Type type;

	private volatile Constructor<?> constructor;

	protected TypeReference() {
		Type superclass = getClass().getGenericSuperclass();
		if (superclass instanceof Class) {
			throw new RuntimeException("Missing type parameter.");
		}
		this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
	}

	/**
	 * Instantiates a new instance of {@code T} using the default, no-arg
	 * constructor.
	 */
	@SuppressWarnings("unchecked")
	public T newInstance() throws NoSuchMethodException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException {
		if (constructor == null) {
			Class<?> rawType = type instanceof Class<?> ? (Class<?>) type
					: (Class<?>) ((ParameterizedType) type).getRawType();
			constructor = rawType.getConstructor();
		}
		return (T) constructor.newInstance();
	}

	/**
	 * Gets the referenced type.
	 */
	public Type getType() {
		return this.type;
	}
}
