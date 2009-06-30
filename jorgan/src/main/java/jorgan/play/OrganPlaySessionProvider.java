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
package jorgan.play;

import java.io.File;
import java.io.IOException;

import jorgan.problem.ElementProblems;
import jorgan.session.OrganSession;
import jorgan.session.SessionListener;
import jorgan.session.spi.SessionProvider;

public class OrganPlaySessionProvider implements SessionProvider {

	public Object create(final OrganSession session, Class<?> clazz) {
		if (clazz == OrganPlay.class) {
			final OrganPlay play = new OrganPlay(session.getOrgan(), session
					.get(ElementProblems.class)) {
				@Override
				public File resolve(String name) throws IOException {
					return session.resolve(name);
				}
			};
			session.addListener(new SessionListener() {
				public void constructingChanged(boolean constructing) {
					if (constructing) {
						if (play.isOpen()) {
							play.close();
						}
					} else {
						if (!play.isOpen()) {
							play.open();
						}
					}
				}

				public void destroyed() {
					play.destroy();
				}
			});

			play.open();
			return play;
		}
		return null;
	}
}