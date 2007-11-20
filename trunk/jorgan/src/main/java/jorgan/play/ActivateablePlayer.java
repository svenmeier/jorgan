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

import java.util.Set;

import jorgan.disposition.Activateable;
import jorgan.disposition.Console;
import jorgan.disposition.Matcher;
import jorgan.disposition.Activateable.Activate;
import jorgan.disposition.Activateable.Engaged;
import jorgan.disposition.Activateable.Deactivate;
import jorgan.disposition.Activateable.Disengaged;
import jorgan.disposition.event.OrganEvent;

/**
 * An abstract base class for players that control activateable elements.
 */
public class ActivateablePlayer<E extends Activateable> extends Player<E> {

	public ActivateablePlayer(E activateable) {
		super(activateable);
	}

	@Override
	protected void input(Matcher matcher) {
		Activateable activateable = getElement();

		if (matcher instanceof Activate) {
			if (!activateable.isActive()) {
				activateable.setActive(true);
			}
		} else if (matcher instanceof Deactivate) {
			if (activateable.isActive()) {
				activateable.setActive(false);
			}
		}

		super.input(matcher);
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		Activateable activateable = getElement();

		Set<Console> consoles = activateable.getReferrer(Console.class);

		Class<? extends Matcher> clazz;
		if (activateable.isActive()) {
			clazz = Engaged.class;
		} else {
			clazz = Disengaged.class;
		}

		for (Matcher matcher : getElement().getMessages(clazz)) {
			for (Console console : consoles) {
				getOrganPlay().getPlayer(console).output(matcher);
			}
		}
	}
}
