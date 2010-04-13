package jorgan.util;

import java.lang.reflect.Array;
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
}
