/**
 * Bias - POJO Configuration.
 * Copyright (C) 2007 Sven Meier
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bias.store;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * A store using {@link java.util.Properties}.
 */
public class PropertiesStore extends ConvertingStore {

	private Properties properties;

	/**
	 * Create a store on fresh {@link Properties}.
	 */
	public PropertiesStore() {
		this(new Properties());
	}

	/**
	 * Create a store using the given properties.
	 * 
	 * @param properties
	 *            properties to use
	 */
	public PropertiesStore(Properties properties) {
		this.properties = properties;
	}

	public PropertiesStore(Class<?> clazz, String name) {
		this.properties = new Properties();

		InputStream input = clazz.getResourceAsStream(name);
		if (input == null) {
			throw new IllegalArgumentException("properties '" + name
					+ "' not found");
		}

		try {
			properties.load(input);
		} catch (IOException ex) {
			throw new Error(ex);
		} finally {
			try {
				input.close();
			} catch (IOException ignore) {
			}
		}
	}

	public Properties getProperties() {
		return properties;
	}

	@Override
	protected Set<String> getKeysImpl(String path) {
		Set<String> keys = new HashSet<String>();

		for (Object object : this.properties.keySet()) {
			String key = (String) object;

			if (getPath(key).equals(path)) {
				keys.add(key);
			}
		}

		return keys;
	}

	@Override
	protected String getString(String key) {
		return properties.getProperty(key);
	}

	@Override
	protected void putString(String key, String string) {
		properties.setProperty(key, string);
	}
}
