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
package jorgan.executor;

import java.io.IOException;

import bias.Configuration;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.executor.disposition.Executor;
import jorgan.session.OrganSession;
import jorgan.session.spi.SessionProvider;

public class ExecutorSessionProvider implements SessionProvider {

	private static final Configuration config = Configuration.getRoot().get(ExecutorSessionProvider.class);
	
	private boolean allowExecute = false;

	public ExecutorSessionProvider() {
		config.read(this);
	}
	
	public void setExecute(boolean allowExecute) {
		this.allowExecute = allowExecute;
	}

	public void init(final OrganSession session) {
		session.getOrgan().addOrganListener(new OrganAdapter() {
			@Override
			public void propertyChanged(Element element, String name) {
				if (Executor.class.isInstance(element)
						&& "active".equals(name)) {
					Executor executorSwitch = ((Executor) element);

					if (!executorSwitch.isActive() && allowExecute) {
						execute(session, executorSwitch);
					}
				}
			}
		});
	}

	public Object create(OrganSession session, Class<?> clazz) {
		return null;
	}

	private void execute(OrganSession session, Executor executorSwitch) {
		try {
			if (executorSwitch.getSave()) {
				session.save();
			}

			String command = executorSwitch.getCommand();

			if (command != null) {
				Runtime.getRuntime().exec(command, null,
						session.getFile().getParentFile());
			}
		} catch (IOException e) {
			// what to do ??
		}
	}
}
