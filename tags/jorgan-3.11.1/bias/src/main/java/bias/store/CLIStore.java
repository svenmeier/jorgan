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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bias.util.cli.CLIException;
import bias.util.cli.Input;
import bias.util.cli.Option;
import bias.util.cli.Switch;
import bias.util.converter.CompositeConverter;
import bias.util.converter.Converter;

/**
 * A store using arguments of a command line interface.
 */
public class CLIStore extends AbstractStore {

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

	private List<Option> options = new ArrayList<Option>();

	private Map<String, Object> values = new HashMap<String, Object>();

	/**
	 * Add a switch.
	 * 
	 * @param key
	 *            key to use for switch value
	 * @param name
	 *            name of switch
	 * @return option switch
	 */
	public Option addSwitch(final String key, final char name) {
		Switch option = new Switch(name) {
			@Override
			protected void onSwitch() {
				values.put(key, Boolean.TRUE);
			}
		};

		options.add(option);

		return option;
	}

	/**
	 * Add an input.
	 * 
	 * @param key
	 *            key to use for input value
	 * @param name
	 *            name of input
	 * @param required
	 *            is input required
	 * @param type
	 *            type of input
	 * @return input option
	 */
	public Option addInput(final String key, final char name,
			final boolean required, final Type type) {
		Input option = new Input(name, required) {
			@Override
			protected void onInput(String input) throws CLIException {
				try {
					values.put(key, getConverter().fromString(input, type));
				} catch (Exception e) {
					throw new CLIException("unable to convert");
				}
			}
		};

		options.add(option);

		return option;
	}

	@Override
	protected Set<String> getKeysImpl(String path) {
		Set<String> keys = new HashSet<String>();

		for (String key : this.values.keySet()) {
			if (getPath(key).equals(path)) {
				keys.add(key);
			}
		}

		return keys;
	}

	@Override
	protected Object getValueImpl(String key, Type type) {
		return values.get(key);
	}

	@Override
	public void setValueImpl(String key, Type type, Object value) {
	}

	/**
	 * Get all added actions.
	 * 
	 * @return actions
	 */
	public Collection<Option> getOptions() {
		return Collections.unmodifiableCollection(options);
	}
}