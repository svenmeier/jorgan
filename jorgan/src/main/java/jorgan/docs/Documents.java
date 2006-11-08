/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan.docs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import jorgan.disposition.Element;
import jorgan.util.Bootstrap;

/**
 * The documents of jOrgan.
 */
public class Documents {

	/**
	 * The name of the system property to specify the path to docs from. <br>
	 * If this system property is not set, docs will be loaded in a "docs"
	 * folder relative to the installation directory.
	 */
	private static final String DOCS_PATH_PROPERTY = "jorgan.docs.path";

	private static Documents instance = new Documents();

	private Properties displayNames;

	private Map instructions = new HashMap();

	/**
	 * Get the display name of the given element.
	 * 
	 * @param element
	 *            element to get display name for
	 * @return the display name
	 */
	public String getDisplayName(Element element) {

		String name = element.getName();
		if ("".equals(name)) {
			name = getDisplayName(element.getClass());
		}

		return name;
	}

	/**
	 * Get the display name for the given element class.
	 * 
	 * @param clazz
	 *            class of elements to get display name for
	 * @return the display name
	 */
	public String getDisplayName(Class clazz) {
		String key = classWithoutPackage(clazz);

		return getDisplayNames().getProperty(key, key);
	}

	/**
	 * Get the display name for a property of the given element class.
	 * 
	 * @param clazz
	 *            class of element
	 * @param property
	 *            the property to get display name for
	 * @return the display name
	 */
	public String getDisplayName(Class clazz, String property) {
		while (clazz != Object.class) {
			String key = classWithoutPackage(clazz) + "." + property;

			String displayName = getDisplayNames().getProperty(key);
			if (displayName != null) {
				return displayName;
			}

			clazz = clazz.getSuperclass();
		}

		return property;
	}

	/**
	 * Get the instructions for the given element class.
	 * 
	 * @param clazz
	 *            class of element
	 * @return instructions
	 */
	public URL getInstructions(Class clazz) {
		String key = classWithoutPackage(clazz) + ".html";

		URL url = (URL) instructions.get(key);
		if (url == null) {
			url = locate("instructions", key);
			instructions.put(key, url);
		}
		return url;
	}

	/**
	 * Get the instructions for a property of the given element class.
	 * 
	 * @param clazz
	 *            class of element
	 * @param property
	 *            property to get display name for
	 * @return instructions
	 */
	public URL getInstructions(Class clazz, String property) {
		String key = classWithoutPackage(clazz) + "." + property + ".html";

		URL url = (URL) instructions.get(key);
		if (url == null) {
			while (clazz != Object.class) {
				url = locate("instructions", classWithoutPackage(clazz) + "."
						+ property + ".html");
				if (url != null) {
					try {
						InputStream stream = url.openStream();
						stream.close();
						break;
					} catch (IOException ex) {
					}
				}
				clazz = clazz.getSuperclass();
			}
			instructions.put(key, url);
		}
		return url;
	}

	/**
	 * Get help.
	 * 
	 * @return help
	 */
	public URL getHelp() {
		URL url = locate("help", "jhelpset.hs");
		return url;
	}

	protected Properties getDisplayNames() {
		if (displayNames == null) {
			displayNames = new Properties();

			URL url = locate("instructions", "displayNames.properties");
			if (url != null) {
				try {
					InputStream in = url.openStream();
					displayNames.load(in);
					in.close();
				} catch (IOException ex) {
				}
			}
		}
		return displayNames;
	}

	private URL locate(String bundleName, String fileName) {

		File docsDir = new File(System.getProperty(DOCS_PATH_PROPERTY,
				Bootstrap.getDirectory() + "/docs"));
		if (docsDir.exists() && docsDir.isDirectory()) {
			try {
				String locale = Locale.getDefault().toString();
				while (true) {
					File zip = new File(docsDir, bundleName
							+ ("".equals(locale) ? "" : "_" + locale) + ".zip");
					if (zip.exists() && zip.isFile()) {
						return new URL("jar:" + zip.toURL() + "!/" + fileName);
					}

					File file = new File(new File(docsDir, bundleName + "/"
							+ locale.replace('_', '/')), fileName);
					if (file.exists()) {
						return file.toURL();
					}

					if (locale.length() == 0) {
						break;
					} else {
						int index = locale.lastIndexOf('_');
						if (index == -1) {
							locale = "";
						} else {
							locale = locale.substring(0, index);
						}
					}
				}
			} catch (Exception e) {
			}
		}
		return null;
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

	/**
	 * Get the singleton instance.
	 * 
	 * @return instance
	 */
	public static Documents getInstance() {
		return instance;
	}
}