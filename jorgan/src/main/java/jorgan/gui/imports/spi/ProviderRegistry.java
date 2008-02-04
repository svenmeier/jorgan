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
package jorgan.gui.imports.spi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.spi.ServiceRegistry;


public class ProviderRegistry {

	/**
	 * Utility method to get all importProviders that are registered as a
	 * service.
	 * 
	 * @return providers of import
	 */
	public static List<ImportProvider> lookup() {
		ArrayList<ImportProvider> providers = new ArrayList<ImportProvider>();

		Iterator<ImportProvider> iterator = ServiceRegistry.lookupProviders(ImportProvider.class);

		while (iterator.hasNext()) {
			try {
				providers.add(iterator.next());
			} catch (Throwable providerFailed) {
			}			
		}

		return providers;
	}
}