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

	private String keyPrefix;

	private ResourceBundle resources;

	private I18N(Class clazz) {
		keyPrefix = "";

		String resourcePath = clazz.getName();
		while (true) {
			int index = resourcePath.lastIndexOf('.');
			if (index == -1) {
				resources = new EmptyResourceBundle();
				break;
			}

			keyPrefix = resourcePath.substring(index + 1) + '.' + keyPrefix;
			resourcePath = resourcePath.substring(0, index);

			try {
				resources = ResourceBundle.getBundle(resourcePath + ".i18n");
				break;
			} catch (MissingResourceException tryAgain) {
			}
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
			return resources.getString(key);
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
		public Enumeration getKeys() {
			return Collections.enumeration(Collections.EMPTY_LIST);
		}

		protected Object handleGetObject(String key) {
			return null;
		}
	}
}