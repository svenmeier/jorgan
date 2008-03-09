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

import javax.swing.Icon;

import jorgan.disposition.Console;
import jorgan.disposition.Elements;
import jorgan.gui.ConsolePanel;
import jorgan.session.OrganSession;
import jorgan.session.SessionAware;
import swingx.docking.AbstractDockable;
import swingx.docking.Docked;

/**
 * Panel that manages views to display a console of an organ.
 */
public class ConsoleDockable extends AbstractDockable implements SessionAware {

	private ConsolePanel panel;

	public ConsoleDockable(Console console) {
		panel = new ConsolePanel(console);
	}

	public void setSession(OrganSession session) {
		panel.setSession(session);
	}

	public String getTitle() {
		return Elements.getDisplayName(panel.getConsole());
	}

	public Icon getIcon() {
		return null;
	}

	@Override
	public void docked(Docked docked) {
		super.docked(docked);

		docked.setContent(panel, true);
	}
}