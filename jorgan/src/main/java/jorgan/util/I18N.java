package jorgan.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**
 * Internationalization utility class.
 */
public class I18N {

	private static Logger logger = Logger.getLogger(I18N.class.getName());

	private String name;

	private String keyPrefix;

	private ResourceBundle bundle;

	private I18N(Class clazz) {
		name = clazz.getName();

		int index = name.lastIndexOf('.');
		keyPrefix = name.substring(index + 1) + ".";
		try {
			bundle = ResourceBundle.getBundle(name.substring(0, index)
					+ ".i18n");
		} catch (MissingResourceException ex) {
			bundle = new EmptyResourceBundle();
		}
	}

	/**
	 * Get an internationalized string for the given key.
	 * 
	 * @param key
	 *            key to get string for
	 * @return internationalized string
	 */
	public String getString(String key) {
		key = keyPrefix + key;

		try {
			return bundle.getString(key);
		} catch (MissingResourceException ex) {
			logger.info("missing resource '" + key + "'");

			return key;
		}
	}

	/**
	 * Factory method.
	 * 
	 * @param clazz
	 *            clazz to get internationalization for
	 * @return internationalization
	 */
	public static I18N get(Class clazz) {
		return new I18N(clazz);
	}

	private static class EmptyResourceBundle extends ResourceBundle {
		public Enumeration<String> getKeys() {
			return Collections.enumeration(Collections.<String> emptyList());
		}

		protected Object handleGetObject(String key) {
			return key;
		}
	}
}