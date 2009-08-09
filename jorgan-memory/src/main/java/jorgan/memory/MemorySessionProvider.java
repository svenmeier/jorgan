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
package jorgan.memory;

import java.io.File;

import jorgan.problem.ElementProblems;
import jorgan.session.OrganSession;
import jorgan.session.SessionListener;
import jorgan.session.spi.SessionProvider;

public class MemorySessionProvider implements SessionProvider {

	/**
	 * {@link MemoryManager} is always required.
	 */
	public void init(OrganSession session) {
		session.lookup(MemoryManager.class);
	}

	public Object create(final OrganSession session, Class<?> clazz) {
		if (clazz == MemoryManager.class) {
			final MemoryManager manager = new MemoryManager(session.getOrgan(),
					session.lookup(ElementProblems.class)) {
				@Override
				protected File resolve(String name) {
					return session.resolve(name);
				}
			};
			session.addListener(new SessionListener() {
				public void constructingChanged(boolean constructing) {
				}

				public void saved(File file) {
					manager.save();
				}

				public void destroyed() {
				}
			});
			return manager;
		}
		return null;
	}
}
