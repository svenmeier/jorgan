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
package jorgan.time;

import java.io.File;

import jorgan.session.OrganSession;
import jorgan.session.SessionListener;
import jorgan.session.spi.SessionProvider;

public class TimeSessionProvider implements SessionProvider {

	/**
	 * {@link Clock} is required.
	 */
	public void init(OrganSession session) {
		session.lookup(Clock.class);
	}

	public Object create(final OrganSession session, Class<?> clazz) {
		if (clazz == Clock.class) {
			final Clock clock = new Clock(session.getOrgan());
			session.addListener(new SessionListener() {
				public void constructingChanged(boolean constructing) {
					if (constructing) {
						clock.stop();
					} else {
						clock.start();
					}
				}

				public void modified() {
				}

				public void saved(File file) {
				}

				public void destroyed() {
					clock.stop();
				}
			});

			if (!session.isConstructing()) {
				clock.start();
			}

			return clock;
		}
		return null;
	}
}