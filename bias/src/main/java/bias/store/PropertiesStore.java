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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import bias.ConfigurationException;

/**
 * A store using {@link java.util.Properties}.
 */
public class PropertiesStore extends ConvertingStore {

	private Properties properties;
	
	private File file;

	/**
	 * Create a store on fresh {@link Properties}.
	 */
	public PropertiesStore() {
		this(new Properties());
	}

	public PropertiesStore(File file) {
		this();
		
		this.file = file.getAbsoluteFile();
		
		if (this.file.isDirectory()) {
			throw new ConfigurationException("file must not be a directory");
		}
		
		if (this.file.exists()) {
			try {
				FileInputStream input = new FileInputStream(file);
				try {
					properties.load(input);
				} finally {
					input.close();
				}
			} catch (IOException ex) {
				throw new ConfigurationException(ex);
			}
		}
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

		try {
			InputStream input = clazz.getResourceAsStream(name);
			if (input == null) {
				throw new ConfigurationException("properties '" + name
						+ "' not found");
			}
			try {
				properties.load(input);
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			throw new ConfigurationException(ex);
		}
	}

	public void flush() {
		if (this.file != null) {
			File directory = this.file.getParentFile();
			if (!directory.exists()) {
				if (!directory.mkdirs()) {
					throw new ConfigurationException("cannot create directory '" + directory +"'");
				}
			}
			
			try {
				FileOutputStream output  = new FileOutputStream(file);
				try {
					properties.store(output, "bias");
				} finally {
					output.close();
				}
			} catch (IOException ex) {
				throw new ConfigurationException(ex);
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
