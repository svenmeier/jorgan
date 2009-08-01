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
package jorgan.memory.gui.console;

import jorgan.gui.console.IndexedContinuousView;
import jorgan.memory.Memory;
import jorgan.memory.disposition.MemorySwitcher;
import jorgan.session.OrganSession;

/**
 * A view that shows an {@link MemorySwitcher}.
 */
public class MemorySwitcherView extends IndexedContinuousView<MemorySwitcher> {

	private OrganSession session;

	/**
	 * Constructor.
	 * 
	 * @param session
	 * 
	 * @param memory
	 *            memory to view
	 */
	public MemorySwitcherView(OrganSession session, MemorySwitcher element) {
		super(element);

		this.session = session;
	}

	protected String getTitle(int index) {
		Memory memory = session.lookup(Memory.class);

		return memory.getLevel(index).getTitle();
	}
}