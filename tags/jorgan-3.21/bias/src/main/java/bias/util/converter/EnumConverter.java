package bias.util.converter;

import java.lang.reflect.Method;
import java.lang.reflect.Type;


public class EnumConverter implements Converter {

	public String toString(Object object, Type type) throws Exception {
		Enum<?> e = (Enum<?>) object;

		return e.name();
	}

	public Object fromString(String string, Type type) throws Exception {
		Class<?> clazz = (Class<?>) type;

		Method method = clazz
				.getMethod("valueOf", new Class<?>[] { String.class });
		return method.invoke(null, string);
	}
}
