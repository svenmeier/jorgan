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
package jorgan.lcd.display;

import java.io.File;
import java.io.IOException;

import jorgan.session.OrganSession;
import jorgan.session.SessionListener;
import jorgan.session.spi.SessionProvider;

public class LCDSessionProvider implements SessionProvider {

	public void init(OrganSession session) {
		session.lookup(OrganDisplay.class);
	}

	public Object create(final OrganSession session, Class<?> clazz) {
		if (clazz == OrganDisplay.class) {
			final OrganDisplay display = new OrganDisplay(session.getOrgan());

			session.addListener(new SessionListener() {
				@Override
				public void saved(File file) throws IOException {
				}

				@Override
				public void modified() {
				}

				@Override
				public void destroyed() {
					display.destroy();
				}

				@Override
				public void constructingChanged(boolean constructing) {
				}
			});

			return display;
		}
		return null;
	}
}
