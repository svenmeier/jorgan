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
package bias;

import java.lang.reflect.Type;
import java.util.Set;

import bias.store.StoreListener;

/**
 * A store of values.
 */
public interface Store {

	public boolean hasKey(String key);

	/**
	 * Get the keys with the given path.
	 * 
	 * @param path
	 *            path to get keys for
	 * @return keys
	 */
	public Set<String> getKeys(String path);

	/**
	 * Get the value for the given key.
	 * 
	 * @param key
	 *            key to get value for
	 * @param type
	 *            type of value
	 * @return value
	 */
	public Object getValue(String key, Type type);

	/**
	 * Set the value for the given key.
	 * 
	 * @param key
	 *            key to set value for
	 * @param type
	 *            type of value
	 * @param value
	 *            value to set
	 */
	public void setValue(String key, Type type, Object value);

	public void onError(String key, Exception ex);

	/**
	 * Add a listener.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addListener(StoreListener listener);

	/**
	 * Remove a listener.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeListener(StoreListener listener);

	public boolean isReadOnly();
}
