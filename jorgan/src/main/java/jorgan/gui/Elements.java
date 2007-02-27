package jorgan.gui;

import java.util.MissingResourceException;
import java.util.logging.Logger;

import jorgan.disposition.Element;
import jorgan.util.I18N;

/**
 * Element <code>i18n</code> utilities.
 */
public class Elements {

	private static Logger logger = Logger.getLogger(Elements.class.getName());

	private static I18N i18n = I18N.get(Elements.class);

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
		return getString(classWithoutPackage(elementClass));
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
	public static String getDisplayName(Class<? extends Element> elementClass, String property) {
		return getString(classWithoutPackage(elementClass) + "." + property);
	}

	private static String getString(String classAndProperty) {
		try {
			return i18n.getString(classAndProperty);
		} catch (MissingResourceException ex) {
			logger.info("missing resource '" + classAndProperty + "'");

			return classAndProperty;
		}
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
