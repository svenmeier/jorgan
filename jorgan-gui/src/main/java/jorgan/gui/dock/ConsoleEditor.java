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
package jorgan.gui.dock;

import javax.swing.JScrollPane;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.ConsolePanel;
import jorgan.session.OrganSession;
import spin.Spin;

/**
 * Panel that manages views to display a console of an organ.
 */
public class ConsoleEditor extends AbstractEditor {

	private EventHandler eventHandler = new EventHandler();

	private OrganSession session;

	private Console console;

	private ConsolePanel panel;

	public ConsoleEditor(Console console) {
		this.console = console;
	}

	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.getOrgan().removeOrganListener(
					(OrganListener) Spin.over(eventHandler));

			setContent(null);
			panel.dispose();
			panel = null;
		}

		this.session = session;

		if (this.session != null) {
			panel = new ConsolePanel(this.session, console);
			setContent(new JScrollPane(panel));

			updateTitle();

			this.session.getOrgan().addOrganListener(
					(OrganListener) Spin.over(eventHandler));
		}
	}

	private void updateTitle() {
		setTitle(Elements.getDescriptionName(panel.getConsole()));
	}

	private class EventHandler extends OrganAdapter {
		@Override
		public void propertyChanged(Element element, String name) {
			if (element == console) {
				updateTitle();
			}
		}
	}
}