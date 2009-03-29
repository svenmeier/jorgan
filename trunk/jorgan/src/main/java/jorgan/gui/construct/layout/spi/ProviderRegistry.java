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
package jorgan.gui.construct.layout.spi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import jorgan.gui.construct.layout.ViewLayout;

public class ProviderRegistry {

	private static final Logger logger = Logger
			.getLogger(ProviderRegistry.class.getName());

	/**
	 * Utility method to get all {@link LayoutProvider}s that are registered as
	 * a service.
	 * 
	 * @return providers of layouts
	 */
	public static List<LayoutProvider> lookup() {
		ArrayList<LayoutProvider> providers = new ArrayList<LayoutProvider>();

		Iterator<LayoutProvider> iterator = ServiceRegistry
				.lookupProviders(LayoutProvider.class);

		while (iterator.hasNext()) {
			try {
				providers.add(iterator.next());
			} catch (Throwable providerFailed) {
				logger.log(Level.WARNING, "provider failed", providerFailed);
			}
		}

		return providers;
	}

	public static List<ViewLayout> lookupLayouts() {
		ArrayList<ViewLayout> layout = new ArrayList<ViewLayout>();

		for (LayoutProvider provider : lookup()) {
			layout.addAll(provider.getLayouts());
		}

		return layout;
	}
}