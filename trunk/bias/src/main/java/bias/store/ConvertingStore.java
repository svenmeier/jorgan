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

import bias.ConfigurationException;
import bias.util.converter.CompositeConverter;
import bias.util.converter.Converter;

/**
 * Abstract store that uses a converter.
 * 
 * @see #createConverter()
 */
public abstract class ConvertingStore extends AbstractStore {

	private Converter converter;

	/**
	 * Hook method to create a converter.<br>
	 * This default implementation creates a {@link CompositeConverter}.
	 * 
	 * @return converter
	 */
	protected Converter createConverter() {
		return new CompositeConverter();
	}

	public Converter getConverter() {
		if (converter == null) {
			converter = createConverter();
		}
		return converter;
	}

	@Override
	protected Object getValueImpl(String key, Type type)
			 {
		String string = getString(key);
		if (string == null) {
			throw new ConfigurationException("unknown key '" + key + "'");
		}

		Object value;
		try {
			value = getConverter().fromString(string, type);
		} catch (Exception ex) {
			throw new ConfigurationException(ex);
		}
		return value;
	}

	@Override
	protected void setValueImpl(String key, Type type, Object value)
			 {
		try {
			String string = getConverter().toString(value, type);
			putString(key, string);
		} catch (Exception ex) {
			throw new ConfigurationException(ex);
		}
	}

	protected abstract String getString(String key);

	protected abstract void putString(String key, String string);
}