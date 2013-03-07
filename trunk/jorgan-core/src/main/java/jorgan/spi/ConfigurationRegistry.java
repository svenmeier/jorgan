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
package jorgan.spi;

import java.io.File;

import jorgan.Version;
import jorgan.util.NativeUtils;
import jorgan.util.PluginUtils;
import bias.Configuration;
import bias.Store;
import bias.store.PropertiesStore;

public class ConfigurationRegistry {

	private static final Store preferences;

	static {
		File home = new File(System.getProperty("user.home"), ".jorgan");
		if (!home.exists()) {
			home.mkdirs();
		}

		String name = String.format("jorgan-%s-%s.properties",
				NativeUtils.getRuntimeArchitecture(),
				new Version().getCompatible());

		preferences = new PropertiesStore(new File(home, name));
	}

	public static void init() {
		Configuration configuration = Configuration.getRoot();

		for (ConfigurationProvider provider : PluginUtils
				.lookup(ConfigurationProvider.class)) {
			for (Store store : provider.getStores(preferences)) {
				configuration.addStore(store);
			}
		}
	}

}