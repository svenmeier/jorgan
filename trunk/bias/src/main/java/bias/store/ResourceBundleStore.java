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
import java.util.ResourceBundle;

/**
 * A store using {@link ResourceBundle}.
 */
public class ResourceBundleStore extends ConvertingStore {

	private ResourceBundle bundle;

	/**
	 * Create a store.
	 */
	public ResourceBundleStore(String baseName) {
		this(ResourceBundle.getBundle(baseName));
	}

	/**
	 * Create a store using the given resource bundle.
	 * 
	 * @param bundle
	 *            bundle to use
	 */
	public ResourceBundleStore(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	/**
	 * Get the wrapped bundle
	 * 
	 * @return the bundle
	 */
	public ResourceBundle getResourceBundle() {
		return bundle;
	}

	@Override
	protected List<String> getKeysImpl(String path) {
		List<String> keys = new ArrayList<String>();

		for (Object object : Collections.list(bundle.getKeys())) {
			String key = (String) object;

			if (getPath(key).equals(path)) {
				keys.add(key);
			}
		}

		return keys;
	}

	@Override
	protected String getString(String key) {
		return bundle.getString(key);
	}

	@Override
	protected void putString(String key, String string) {
	}
	
	@Override
	public boolean isReadOnly() {
		return true;
	}
}
