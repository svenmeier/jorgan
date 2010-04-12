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

import java.util.HashSet;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import bias.ConfigurationException;

/**
 * Store using {@link java.util.prefs.Preferences}.
 */
public class PreferencesStore extends ConvertingStore {

	private static final String DEFAULT = new String();

	private Preferences preferences;

	public PreferencesStore(Preferences preferences) {
		this.preferences = preferences;
	}

	@Override
	protected Set<String> getKeysImpl(String path) {
		Set<String> keys = new HashSet<String>();

		Preferences node = preferences.node(path);
		try {
			for (String key : node.keys()) {
				keys.add(path + "/" + key);
			}
		} catch (BackingStoreException ex) {
			throw new ConfigurationException(ex);
		}

		return keys;
	}

	@Override
	protected String getString(String key) {
		int index = key.lastIndexOf('/');

		String string = preferences.node(key.substring(0, index)).get(
				key.substring(index + 1), DEFAULT);

		if (string == DEFAULT) {
			return null;
		} else {
			return string;
		}
	}

	@Override
	protected void putString(String key, String string) {
		int index = key.lastIndexOf('/');

		preferences.node(key.substring(0, index)).put(key.substring(index + 1),
				string);
	}

	public void flush() {
		try {
			preferences.flush();
		} catch (BackingStoreException ex) {
			throw new ConfigurationException(ex);
		}
	}

	/**
	 * Factory method for user-specific preferences.
	 * 
	 * @return preferences
	 */
	public static PreferencesStore user() {
		return new PreferencesStore(Preferences.userRoot());

	}

	/**
	 * Factory method for system-specific preferences.
	 * 
	 * @return preferences
	 */
	public static PreferencesStore system() {
		return new PreferencesStore(Preferences.systemRoot());
	}
}