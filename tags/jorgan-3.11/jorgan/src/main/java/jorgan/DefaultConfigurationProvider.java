/*
 * jOrgan - Java Virtual Organ
 * Copyright (C) 2003 Sven Meier
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package jorgan;

import java.util.ArrayList;
import java.util.List;

import jorgan.spi.ConfigurationProvider;
import bias.Store;
import bias.store.DefaultingStore;
import bias.store.PreferencesStore;
import bias.store.PropertiesStore;
import bias.store.ResourceBundlesStore;

public class DefaultConfigurationProvider implements ConfigurationProvider {

	public List<Store> getStores() {
		ArrayList<Store> stores = new ArrayList<Store>();

		PropertiesStore properties = new PropertiesStore(App.class,
				"app.properties");
		stores.add(properties);

		stores.add(new ResourceBundlesStore("i18n"));

		stores.add(new DefaultingStore(PreferencesStore.user(),
				new PropertiesStore(DefaultConfigurationProvider.class,
						"/jorgan/preferences.properties")));

		String version = (String) properties.getValue("jorgan/Info/version",
				String.class);
		stores.add(new DefaultingStore(PreferencesStore.user(version),
				new PropertiesStore(DefaultConfigurationProvider.class,
						"/jorgan/version-preferences.properties")));

		return stores;
	}

}
