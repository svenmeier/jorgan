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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bias.store.AbstractStore;
import bias.store.StoreListener;
import bias.util.Property;
import bias.util.WeakSet;

/**
 * <em>Bias</em> moves configuration where it belongs - into your POJOs.
 */
public class Configuration {

	/**
	 * Mapping from path to configuration - shared between all configurations in
	 * the same hierarchy.
	 */
	private Map<String, Configuration> configurations;

	/**
	 * The parental configuration.
	 */
	private Configuration parent;

	/**
	 * The path of this configuration.
	 */
	private String path;

	/**
	 * All added stores.
	 */
	private List<Store> stores;

	/**
	 * All objects given as an argument to {@link #read(Object)} and
	 * {@link #write(Object)}.
	 */
	private WeakSet<Object> objects;

	/**
	 * The currently writing store for each thread.
	 */
	private ThreadLocal<Store> writingStore = new ThreadLocal<Store>();

	public Configuration() {
		this.path = "";

		this.configurations = new HashMap<String, Configuration>();
		configurations.put(this.path, this);
	}

	private Configuration(String path, Configuration parent) {
		this.path = path;
		this.parent = parent;

		this.configurations = parent.configurations;
		configurations.put(this.path, this);
	}

	/**
	 * Add a store to this configuration.
	 * 
	 * @param store
	 *            store to add
	 */
	public void addStore(final Store store) {
		if (this.stores == null) {
			this.stores = new ArrayList<Store>();
		}
		this.stores.add(store);

		store.addListener(new StoreListener() {
			public void valueChanged(Store store, String key) {
				if (store != writingStore.get()) {
					String path = AbstractStore.getPath(key);

					Configuration config = configurations.get(path);
					if (config != null) {
						Configuration parent = config;
						while (parent != null) {
							if (parent == Configuration.this) {
								config.readValue(store, key);
								return;
							}
							parent = parent.getParent();
						}
					}
				}
			}
		});
	}

	private void readValue(Store store, String key) {
		if (objects != null) {
			for (Object object : objects) {
				try {
					Property property = getProperty(object.getClass(), key);
					property.write(object, store.getValue(key, property
							.getType()));
				} catch (Exception ex) {
					store.onError(key, ex);
				}
			}
		}
	}

	/**
	 * Get all stores.
	 * 
	 * @return stores
	 */
	public List<Store> getStores() {
		if (stores == null) {
			return Collections.<Store> emptyList();
		} else {
			return Collections.<Store> unmodifiableList(stores);
		}
	}

	public Store getStore(String key) {
		for (Store store : stores) {
			if (store.hasKey(key)) {
				return store;
			}
		}
		
		int index = key.indexOf('/');
		if (index == -1) {
			return null;
		} else {
			return get(key.substring(index + 1)).getStore(key);
		}
	}
	
	/**
	 * Read the configuration of the given object.
	 * 
	 * @param <T>
	 *            type of object
	 * @param t
	 *            object to read configuration for
	 * @return read object
	 */
	public <T> T read(T t) {
		getObjects().add(t);

		Configuration config = this;
		while (config != null) {
			config.readValues(t, path);

			config = config.getParent();
		}

		return t;
	}

	private void readValues(Object object, String path) {
		if (stores != null) {
			for (Store store : stores) {
				for (String key : store.getKeys(path)) {
					try {
						Property property = getProperty(object.getClass(), key);
						property.write(object, store.getValue(key, property
								.getType()));
					} catch (Exception ex) {
						store.onError(key, ex);
					}
				}
			}
		}
	}

	/**
	 * Write the configuration of the given object.
	 * 
	 * @param <T>
	 *            type of object
	 * @param t
	 *            object to write configuration for
	 * @return written object
	 */
	public <T> T write(T t) {
		getObjects().add(t);

		Configuration config = this;
		while (config != null) {
			config.writeValues(t, path);

			config = config.getParent();
		}

		return t;
	}

	private void writeValues(Object object, String path) {
		if (stores != null) {
			for (Store store : stores) {
				if (!store.isReadOnly()) {
					try {
						writingStore.set(store);
						for (String key : store.getKeys(path)) {
							try {
								Property property = getProperty(object.getClass(),
										key);
								store.setValue(key, property.getType(), property
										.read(object));
							} catch (Exception ex) {
								store.onError(key, ex);
							}
						}
					} finally {
						writingStore.set(null);
					}
				}
			}
		}
	}

	/**
	 * Get the path.
	 * 
	 * @return path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Get the parent configuration.
	 * 
	 * @return parent configuration
	 */
	public Configuration getParent() {
		return parent;
	}

	public Set<Object> getObjects() {
		if (objects == null) {
			objects = new WeakSet<Object>();
		}
		return objects;
	}

	/**
	 * Get the configuration with the given path.
	 * 
	 * @param path
	 *            path, e.g. "foo/bar"
	 * @return configuration
	 */
	public Configuration get(final String path) {
		String fullPath = "".equals(this.path) ? path : this.path + "/" + path;

		Configuration configuration = configurations.get(fullPath);
		if (configuration == null) {
			int index = path.indexOf('/');
			if (index == -1) {
				configuration = new Configuration(fullPath, this);
			} else {
				Configuration parent = get(path.substring(0, index));
				configuration = parent.get(path.substring(index + 1));
			}
		}

		return configuration;
	}

	public Configuration get(final Class<?> path) {
		return get(path.getName().replace('.', '/'));
	}

	private Map<String, Property> properties = new HashMap<String, Property>();

	private Property getProperty(Class<?> clazz, String key) {
		String name = key.substring(key.lastIndexOf('/') + 1);

		String lookup = clazz.getName() + "." + name;
		Property property = properties.get(lookup);
		if (property == null) {
			property = new Property(clazz, name);
			properties.put(lookup, property);
		}
		return property;
	}

	public void flush() {
		for (String path : configurations.keySet()) {
			if (path.startsWith(this.path)) {
				Configuration configuration = configurations.get(path);
				
				if (configuration.stores != null) {
					for (Store store : configuration.stores) {
						store.flush();
					}
				}
			}
		}
	}
	
	private static final Configuration root = new Configuration();

	/**
	 * Get the root configuration.
	 * 
	 * @return
	 */
	public static Configuration getRoot() {
		return root;
	}
}