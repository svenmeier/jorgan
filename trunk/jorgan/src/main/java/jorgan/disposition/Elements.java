package jorgan.disposition;

import java.util.HashMap;
import java.util.Map;

import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * Element <code>i18n</code> utilities.
 */
public class Elements {

	private static Configuration config = Configuration.getRoot().get(
			Elements.class);

	private static Map<String, String> messages = new HashMap<String, String>();

	private Elements() {
	}

	/**
	 * Get the display name of the given element.
	 * 
	 * @param element
	 *            element to get display name for
	 * @return the display name
	 */
	public static String getDisplayName(Element element) {

		String name = element.getName();
		if ("".equals(name)) {
			name = getDisplayName(element.getClass());
		}

		return name;
	}

	/**
	 * Get the display name of the given element class.
	 * 
	 * @param elementClass
	 *            class of elements to get display name for
	 * @return the display name
	 */
	public static String getDisplayName(Class<? extends Element> elementClass) {
		return getMessage(classWithoutPackage(elementClass));
	}

	/**
	 * Get the display name of the given property of element class.
	 * 
	 * @param elementClass
	 *            class of elements to get display name for
	 * @param property
	 *            properyt
	 * @return the display name
	 */
	public static String getDisplayName(Class<? extends Element> elementClass,
			String property) {
		return getMessage(classWithoutPackage(elementClass) + "." + property);
	}

	private static String getMessage(String key) {
		String message = messages.get(key);
		if (message == null) {
			message = config.get(key).read(new MessageBuilder()).build();
			messages.put(key, message);
		}
		return message;
	}

	private static String classWithoutPackage(Class clazz) {
		String name = clazz.getName();

		int index = name.lastIndexOf('.');
		if (index != -1) {
			name = name.substring(index + 1);
		}

		name = Character.toLowerCase(name.charAt(0)) + name.substring(1);

		return name;
	}
}
