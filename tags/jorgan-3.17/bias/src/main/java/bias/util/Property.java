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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.StringTokenizer;

import bias.ConfigurationException;

/**
 * A property.
 */
public class Property {

	private Class<?> owningClass;

	private String name;

	private Type type;

	private Get[] getters;

	private Get getter;

	private Set setter;

	public Property(Class<?> owningClass, String name) {
		this.owningClass = owningClass;
		this.name = name;

		init();
	}

	public Class<?> getOwningClass() {
		return owningClass;
	}

	/**
	 * Get the name.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Write this property to the given object.
	 * 
	 * @param object
	 * @param value
	 * @throws Exception
	 */
	public final void write(Object object, Object value) {

		if (setter == null) {
			throw new ConfigurationException("property '" + this
					+ "' is read-only");
		}

		try {
			for (int g = 0; g < getters.length - 1; g++) {
				object = getters[g].get(object);
			}

			setter.set(object, value);
		} catch (Exception ex) {
			throw new ConfigurationException(ex);
		}
	}

	/**
	 * Read this property from the given object.
	 * 
	 * @param object
	 * @throws Exception
	 */
	public final Object read(Object object) {

		if (getter == null) {
			throw new ConfigurationException("property '" + this
					+ "' is write-only");
		}

		try {
			Object value = object;
			for (Get getter : getters) {
				value = getter.get(value);
			}

			value = this.getter.get(value);

			return value;
		} catch (Exception ex) {
			throw new ConfigurationException(ex);
		}
	}

	/**
	 * Get the type.
	 * 
	 * @return type
	 */
	public Type getType() {

		return type;
	}

	protected void init() {

		Class<?> owningClass = this.owningClass;
		String name = this.name;

		StringTokenizer tokens = new StringTokenizer(this.name, ".");
		getters = new Get[tokens.countTokens() - 1];
		for (int g = 0; g < getters.length; g++) {
			String token = tokens.nextToken();

			name = Character.toUpperCase(token.charAt(0)) + token.substring(1);

			getters[g] = createGet(owningClass, name);
			if (getter == null) {
				throw new ConfigurationException(this.name);
			}
			owningClass = getters[g].getType();
		}
		name = tokens.nextToken();

		getter = createGet(owningClass, name);
		if (getter != null) {
			this.type = getter.getGenericType();
		}

		setter = createSet(owningClass, name, this.type);
		if (setter != null) {
			this.type = setter.getGenericType();
		}

		if (getter == null && setter == null) {
			throw new ConfigurationException("unkown property '" + this.name
					+ "' of class " + owningClass.getName());
		}
	}

	private Set createSet(Class<?> owningClass, String name, Type type) {
		try {
			return new MethodSet(owningClass, name, type);
		} catch (Exception noMethod) {
		}

		try {
			return new FieldSet(owningClass, name);
		} catch (Exception noField) {
		}

		return null;
	}

	private Get createGet(Class<?> owningClass, String name) {
		try {
			return new MethodGet(owningClass, name);
		} catch (Exception noMethod) {
		}

		try {
			return new FieldGet(owningClass, name);
		} catch (Exception noField) {
		}

		return null;
	}

	public String getTypeName() {

		if (type instanceof Class<?>) {
			return ((Class<?>) type).getName();
		} else if (type instanceof ParameterizedType) {
			return ((ParameterizedType) type).toString();
		} else {
			throw new ConfigurationException("unexpected type '" + getType()
					+ "'");
		}
	}

	@Override
	public String toString() {
		return owningClass.getName() + "." + name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Property)) {
			return false;
		}
		Property property = (Property) obj;

		return owningClass == property.owningClass
				&& name.equals(property.name);
	}

	private static interface Get {
		public Object get(Object owner) throws Exception;

		public Class<?> getType();

		public Type getGenericType();
	}

	private static class MethodGet implements Get {

		private Method method;

		public MethodGet(Class<?> owningClass, String name) throws Exception {
			try {
				String methodName = "get"
						+ Character.toUpperCase(name.charAt(0))
						+ name.substring(1);

				method = owningClass.getMethod(methodName, new Class[0]);
			} catch (NoSuchMethodException noGet) {
				try {
					String methodName = "is"
							+ Character.toUpperCase(name.charAt(0))
							+ name.substring(1);

					method = owningClass.getMethod(methodName, new Class[0]);
				} catch (NoSuchMethodException noBoolean) {
					throw noGet;
				}

				Class<?> type = method.getReturnType();
				if (type != Boolean.class && type != Boolean.TYPE) {
					throw noGet;
				}
			}
		}

		public Type getGenericType() {
			return method.getGenericReturnType();
		}

		public Class<?> getType() {
			return method.getReturnType();
		}

		public Object get(Object owner) throws Exception {
			return method.invoke(owner, new Object[0]);
		}
	}

	private static class FieldGet implements Get {
		private Field field;

		public FieldGet(Class<?> owningClass, String name) throws Exception {
			while (owningClass != Object.class) {
				Field[] fields = owningClass.getDeclaredFields();
				for (Field field : fields) {
					if (name.equals(field.getName())) {
						this.field = field;
						if (!Modifier.isPublic(field.getModifiers())) {
							field.setAccessible(true);
						}
						return;
					}
				}
				owningClass = owningClass.getSuperclass();
			}
			throw new NoSuchFieldException(owningClass.getName() + "#" + name);
		}

		public Object get(Object owner) throws Exception {
			return field.get(owner);
		}

		public Type getGenericType() {
			return field.getGenericType();
		}

		public Class<?> getType() {
			return field.getType();
		}
	}

	private static interface Set {
		public void set(Object owner, Object value) throws Exception;

		public Type getGenericType();
	}

	private static class MethodSet implements Set {

		private Method method;

		public MethodSet(Class<?> owningClass, String name, Type type)
				throws Exception {
			String methodName = "set" + Character.toUpperCase(name.charAt(0))
					+ name.substring(1);

			Method[] methods = owningClass.getMethods();
			for (Method method : methods) {
				if (methodName.equals(method.getName())
						&& method.getParameterTypes().length == 1) {

					if (type == null || equalType(method.getGenericParameterTypes()[0], type)) {
						this.method = method;
						return;
					}
				}
			}
			throw new NoSuchMethodException();
		}

		public Type getGenericType() {
			return method.getGenericParameterTypes()[0];
		}

		public void set(Object owner, Object value) throws Exception {
			method.invoke(owner, new Object[] { value });
		}

		private boolean equalType(Type type1, Type type2) {
			if (type1 instanceof ParameterizedType
					&& type2 instanceof ParameterizedType) {
				ParameterizedType p1 = (ParameterizedType) type1;
				ParameterizedType p2 = (ParameterizedType) type2;

				return p1.getRawType().equals(p2.getRawType())
						&& Arrays.equals(p1.getActualTypeArguments(), p2
								.getActualTypeArguments());
			} else {
				return type1.equals(type2);
			}
		}
	}

	private static class FieldSet implements Set {
		private Field field;

		public FieldSet(Class<?> owningClass, String name) throws Exception {
			while (owningClass != Object.class) {
				Field[] fields = owningClass.getDeclaredFields();
				for (Field field : fields) {
					if (name.equals(field.getName())) {
						this.field = field;
						if (!Modifier.isPublic(field.getModifiers())) {
							field.setAccessible(true);
						}
						return;
					}
				}
				owningClass = owningClass.getSuperclass();
			}
			throw new NoSuchFieldException(owningClass.getName() + "#" + name);
		}

		public Type getGenericType() {
			return field.getGenericType();
		}

		public void set(Object owner, Object value) throws Exception {
			field.set(owner, value);
		}
	}
}