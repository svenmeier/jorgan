package jorgan.gui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import jorgan.disposition.Element;

/**
 * Element <code>i18n</code> utilities.
 */
public class Elements {

	private static Logger logger = Logger.getLogger(Elements.class.getName());

	/**
	 * The resource bundle.
	 */
	protected static ResourceBundle resources = ResourceBundle
			.getBundle("jorgan.gui.resources");

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
	public static String getDisplayName(Class elementClass) {
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
	public static String getDisplayName(Class elementClass, String property) {
		return getString(classWithoutPackage(elementClass) + "." + property);
	}

	private static String getString(String classAndProperty) {
		try {
			return resources.getString("disposition." + classAndProperty);
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
