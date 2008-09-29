package jorgan.gui.img;

import java.net.URL;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ElementIcons {

	private static HashMap<Class<?>, Icon> icons = new HashMap<Class<?>, Icon>();

	private ElementIcons() {
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
			URL url = ElementIcons.class.getResource("elements/"
					+ clazz.getSimpleName() + ".gif");
			if (url != null) {
				return new ImageIcon(url);
			}
			clazz = clazz.getSuperclass();
		}

		throw new Error("no icon for " + clazz);
	}
}
