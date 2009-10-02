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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import bias.ConfigurationException;

/**
 * A store using {@link ResourceBundle}s.
 */
public class ResourceBundlesStore extends ConvertingStore {

	private String suffix;

	/**
	 * Create a store.
	 */
	public ResourceBundlesStore(String suffix) {
		this.suffix = suffix;
	}

	@Override
	protected List<String> getKeysImpl(String path) {
		List<String> keys = new ArrayList<String>();

		String subpath = "";
		while (true) {
			ResourceBundle bundle = getBundle(subpath);
			if (bundle != null) {
				for (String key : Collections.list(bundle.getKeys())) {
					if (getPath(key).equals(path)) {
						keys.add(subpath + key);
					}
				}
			}

			int index = path.indexOf('/');
			if (index == -1) {
				break;
			}
			subpath += path.substring(0, index + 1);
			path = path.substring(index + 1);
		}

		return keys;
	}

	@Override
	protected String getString(String key) {
		String path = "";
		String relativeKey = key;

		while (true) {
			String string = getBundleValue(path, relativeKey);
			if (string != null) {
				return string;
			}

			int index = relativeKey.indexOf('/');
			if (index == -1) {
				break;
			}
			path += relativeKey.substring(0, index + 1);
			relativeKey = relativeKey.substring(index + 1);
		}

		throw new ConfigurationException("unkown key '" + key + "'");
	}

	private ResourceBundle getBundle(String path) {
		try {
			return ResourceBundle.getBundle(path + suffix);
		} catch (MissingResourceException ex) {
		}
		return null;
	}

	private String getBundleValue(String path, String key) {
		try {
			ResourceBundle bundle = getBundle(path);
			if (bundle != null) {
				return bundle.getString(key);
			}
		} catch (MissingResourceException ex) {
		}
		return null;
	}

	@Override
	protected void putString(String key, String string) {
	}
	
	@Override
	public boolean isReadOnly() {
		return true;
	}
}