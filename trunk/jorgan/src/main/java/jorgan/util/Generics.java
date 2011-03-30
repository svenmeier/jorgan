package jorgan.util;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class Generics {

	/**
	 * Create a list.
	 * 
	 * @param <E>
	 * @param os
	 * @param clazz
	 * @return list
	 */
	@SuppressWarnings("unchecked")
	public static <E> List<E> asList(Object[] os, Class<E> clazz) {
		E[] es = (E[]) Array.newInstance(clazz, os.length);

		System.arraycopy(os, 0, es, 0, os.length);

		return Arrays.asList(es);
	}

	public static Type getTypeParameter(Type type) {
		while (type != null) {
			if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				Type[] arguments = parameterizedType.getActualTypeArguments();
				if (arguments.length == 1) {
					return arguments[0];
				}
			}
			type = ((Class<?>) type).getGenericSuperclass();
		}
		throw new IllegalArgumentException("no type parameter for " + type);
	}
}
