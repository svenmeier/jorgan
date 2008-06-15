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
package jorgan.gui.console.spi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import jorgan.disposition.Displayable;
import jorgan.gui.console.View;

public class ProviderRegistry {

	private static final Logger logger = Logger
			.getLogger(ProviderRegistry.class.getName());

	/**
	 * Utility method to get all registered providers.
	 * 
	 * @return providers of import
	 */
	public static List<ViewProvider> lookup() {
		ArrayList<ViewProvider> providers = new ArrayList<ViewProvider>();

		Iterator<ViewProvider> iterator = ServiceRegistry
				.lookupProviders(ViewProvider.class);

		while (iterator.hasNext()) {
			try {
				providers.add(iterator.next());
			} catch (Throwable providerFailed) {
				logger.log(Level.WARNING, "provider failed", providerFailed);
			}
		}

		return providers;
	}

	@SuppressWarnings("unchecked")
	public static View<?> createView(Displayable element) {
		View<?> view = null;
		for (ViewProvider provider : lookup()) {
			try {
				view = provider.createView(element);
				if (view != null) {
					return view;
				}
			} catch (Throwable providerFailed) {
				logger.log(Level.WARNING, "provider failed", providerFailed);
			}
		}

		return new View(element);
	}
}