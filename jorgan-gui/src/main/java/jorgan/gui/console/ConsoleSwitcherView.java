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
package jorgan.gui.console;

import jorgan.disposition.Console;
import jorgan.disposition.ConsoleSwitcher;
import jorgan.disposition.Switch;

/**
 * A view for a {@link ConsoleSwitcher}.
 */
public class ConsoleSwitcherView extends SwitchView<ConsoleSwitcher> {

	/**
	 * Constructor.
	 * 
	 * @param element
	 *            the relement to view
	 */
	public ConsoleSwitcherView(ConsoleSwitcher element) {
		super(element);
	}

	@Override
	public void update(String name) {
		super.update(name);

		if ("engaged".equals(name)) {
			ViewContainer container = getContainer();
			if (container != null) {
				ConsoleSwitcher switcher = getElement();
				if (shouldSwitch(switcher)) {
					for (Console console : switcher
							.getReferenced(Console.class)) {
						container.toFront(console);
						break;
					}
				}
			}
		}
	}

	private boolean shouldSwitch(ConsoleSwitcher switcher) {
		if (switcher.getDuration() == Switch.DURATION_NONE) {
			// switch when disengaged only, so user can see the pressed
			// animation
			return !switcher.isEngaged();
		} else {
			// switch immediately, since disengaged might come much later
			return switcher.isEngaged();
		}
	}
}