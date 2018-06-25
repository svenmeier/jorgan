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
package jorgan.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

public class PluginUtils {

	private static final Logger logger = Logger.getLogger(PluginUtils.class
			.getName());

	private static Map<Class<?>, List<?>> cache = new HashMap<Class<?>, List<?>>();

	/**
	 * Utility method to get all registered providers.
	 * 
	 * @return providers
	 */
	@SuppressWarnings("unchecked")
	public static <P> List<P> lookup(Class<P> clazz) {
		List<P> providers = (List<P>) cache.get(clazz);
		if (providers == null) {
			providers = lookupImpl(clazz);
			cache.put(clazz, providers);
		}
		return providers;
	}

	private static <P> List<P> lookupImpl(Class<P> clazz) {
		List<P> providers = new ArrayList<P>();

		for (P provider : ServiceLoader.load(clazz)) {
			try {
				if (clazz.isInstance(provider)) {
					providers.add(0, provider);
				} else {
					logger.log(Level.WARNING, "provider not instance of "
							+ clazz);
				}
			} catch (Throwable failure) {
				logger.log(Level.WARNING, "provider failed", failure);
			}
		}

		if (Ordering.class.isAssignableFrom(clazz)) {
			Collections.sort(providers, new Comparator<P>() {
				public int compare(P provider1, P provider2) {
					return ((Ordering) provider1).getOrder()
							- ((Ordering) provider2).getOrder();
				};
			});
		}

		return providers;
	}

	/**
	 * Optional interface for providers which define an ordering.
	 */
	public static interface Ordering {
		/**
		 * Get order, <code>0</code> highest.
		 */
		public int getOrder();
	}
}
