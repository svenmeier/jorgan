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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bias.ConfigurationException;
import bias.Store;

/**
 * Abstract store base class.
 */
public abstract class AbstractStore implements Store {

	private Map<String, Set<String>> keys = new HashMap<String, Set<String>>();

	private List<StoreListener> listeners = new ArrayList<StoreListener>();

	public void onError(String key, Exception ex) {
		System.err.println("illegal key '" + key + "' : " + ex.getMessage());
	}

	public void addListener(StoreListener listener) {
		listeners.add(listener);
	}

	public void removeListener(StoreListener listener) {
		if (!listeners.remove(listener)) {
			throw new IllegalArgumentException("unknown listener");
		}
	}

	public boolean isReadOnly() {
		return false;
	}
	
	private void notifyListeners(String key) {
		for (StoreListener listener : listeners) {
			listener.valueChanged(this, key);
		}
	}

	public final Set<String> getKeys(String path) {
		Set<String> keys = this.keys.get(path);
		if (keys == null) {
			keys = getKeysImpl(path);
			this.keys.put(path, keys);
		}
		return keys;
	}

	public boolean hasKey(String key) {
		String path = AbstractStore.getPath(key);
		
		if ("".equals(path)) {
			return false;
		} else {
			return getKeys(path).contains(key);
		}
	}
	
	protected abstract Set<String> getKeysImpl(String path);

	public final void setValue(String key, Type type, Object value) {
		setValueImpl(key, type, value);

		Set<String> keys = getKeys(getPath(key));
		keys.add(key);

		notifyListeners(key);
	}

	protected abstract void setValueImpl(String key, Type type, Object value);

	public final Object getValue(String key, Type type) {
		if (!getKeys(getPath(key)).contains(key)) {
			throw new ConfigurationException("unkown key '" + key + "'");
		}
		return getValueImpl(key, type);
	}

	protected abstract Object getValueImpl(String key, Type type);
	
	public static String getPath(String key) {
		int index = key.lastIndexOf('/');
		if (index == -1) {
			return "";
		} else {
			return key.substring(0, index);
		}
	}

	/**
	 * Default implementation does nothing.
	 */
	public void flush() {
	}
}