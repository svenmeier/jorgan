package jorgan.disposition;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * Element <code>i18n</code> utilities.
 */
public class Elements {

	private static Configuration config = Configuration.getRoot();

	private static Map<String, String> messages = new HashMap<String, String>();

	private static HashMap<Class<?>, Icon> icons = new HashMap<Class<?>, Icon>();

	/**
	 * Get the description name of the given element.
	 * 
	 * @param element
	 *            element to get description name for
	 * @return the description name
	 */
	public static String getDescriptionName(Element element) {
		String descriptionName = element.getTexts().get("name");
		if (descriptionName != null && !descriptionName.trim().isEmpty()) {
			return descriptionName;
		}

		return getDisplayName(element);
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
		if (name.trim().isEmpty()) {
			name = getDisplayName(element.getClass());
		}

		return name;
	}

	/**
	 * Get the display name of the given class.
	 * 
	 * @param clazz
	 *            class to get display name for
	 * @return the display name
	 */
	public static String getDisplayName(Class<?> clazz) {
		return getMessage(clazz, "this");
	}

	/**
	 * Get the display name of the given property of a class.
	 * 
	 * @param clazz
	 *            class to get display name for
	 * @param property
	 *            property
	 * @return the display name
	 */
	public static String getDisplayName(Class<?> clazz, String property) {
		return getMessage(clazz, property);
	}

	private static String getMessage(Class<?> clazz, String key) {
		String completeKey = clazz.getName() + "#" + key;
		String message = messages.get(completeKey);
		if (message == null) {
			message = config.get(clazz).get(key).read(new MessageBuilder())
					.build();
			messages.put(completeKey, message);
		}
		return message;
	}

	/**
	 * Get an icon representation for the given class.
	 * 
	 * @param clazz
	 * @return
	 */
	public static Icon getIcon(Class<?> clazz) {

		Icon icon = icons.get(clazz);
		if (icon == null) {
			icon = createIcon(clazz);
			icons.put(clazz, icon);
		}

		return icon;
	}

	private static Icon createIcon(Class<?> clazz) {
		while (clazz != null) {
			URL url = clazz
					.getResource("img/" + clazz.getSimpleName() + ".gif");
			if (url != null) {
				return new ImageIcon(url);
			}
			clazz = clazz.getSuperclass();
		}

		throw new Error("no icon for " + clazz);
	}
}