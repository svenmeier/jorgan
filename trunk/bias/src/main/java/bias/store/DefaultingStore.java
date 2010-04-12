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
import java.util.Set;

import bias.Store;

/**
 * A store with defaults.
 */
public class DefaultingStore extends AbstractStore {

	private Store store;

	private Store defaultsStore;

	public DefaultingStore(Store store, Store defaultsStore) {
		this.store = store;
		this.defaultsStore = defaultsStore;
	}

	@Override
	public void onError(String key, Exception ex) {
		defaultsStore.onError(key, ex);
	}

	@Override
	protected Set<String> getKeysImpl(String path) {
		return defaultsStore.getKeys(path);
	}

	public Object getDefault(String key, Type type) {
		return defaultsStore.getValue(key, type);
	}

	@Override
	protected Object getValueImpl(String key, Type type) {
		if (store.getKeys(getPath(key)).contains(key)) {
			try {
				return store.getValue(key, type);
			} catch (Exception ex) {
				onError(key, ex);
			}
		}

		return defaultsStore.getValue(key, type);
	}

	@Override
	protected void setValueImpl(String key, Type type, Object value) {
		store.setValue(key, type, value);
	}
}