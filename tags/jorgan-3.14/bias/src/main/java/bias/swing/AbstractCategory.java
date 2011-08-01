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
package bias.swing;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import bias.Configuration;
import bias.Store;
import bias.store.DefaultingStore;
import bias.util.Property;

/**
 * Abstract category.
 */
public abstract class AbstractCategory implements Category {

	private Configuration configuration;

	private JComponent component;

	private String name;

	private Map<String, Model<?>> models = new HashMap<String, Model<?>>();

	protected AbstractCategory(Configuration configuration) {
		this.configuration = configuration;
	}

	public final JComponent getComponent() {
		if (component == null) {
			component = createComponent();

			read();
		}
		return component;
	}

	protected abstract JComponent createComponent();

	public final void apply() {
		write();

		for (Model<?> model : models.values()) {
			model.apply();
		}
	}

	protected void write() {
	}

	public final void restore() {
		for (Model<?> model : models.values()) {
			model.restore();
		}

		read();
	}

	protected void read() {

	}

	protected <T> Model<T> getModel(Property property) {
		return getModel(property.getOwningClass().getName().replace('.', '/'),
				property);
	}

	@SuppressWarnings("unchecked")
	protected <T> Model<T> getModel(String path, Property property) {
		String key = path + "/" + property.getName();

		Model<T> model = (Model<T>)models.get(key);
		if (model == null) {
			Store store = configuration.getStore(key);
			
			model = new Model<T>(key, property.getType(), store);

			this.models.put(key, model);
		}

		return model;
	}

	public Class<? extends Category> getParentCategory() {
		return null;
	}

	public static class Model<T> {

		private String key;

		private Type type;

		private T value;
		
		private Store store;

		@SuppressWarnings("unchecked")
		private Model(String key, Type type, Store store) {
			this.key = key;
			this.type = type;
			this.store = store;
			
			this.value = (T)store.getValue(key, type);
		}

		private void apply() {
			store.setValue(key, type, value);
		}

		@SuppressWarnings("unchecked")
		private void restore() {
			if (store instanceof DefaultingStore) {
				value = (T)((DefaultingStore) store).getDefault(key, type);
			}
		}

		public String getKey() {
			return key;
		}

		public Type getType() {
			return type;
		}

		public Store getStore() {
			return store;
		}
		
		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}