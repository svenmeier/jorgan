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

import java.util.Map;
import java.util.Set;

import jorgan.disposition.Activateable;
import jorgan.disposition.Console;
import jorgan.disposition.Activateable.Activate;
import jorgan.disposition.Activateable.Activated;
import jorgan.disposition.Activateable.Deactivate;
import jorgan.disposition.Activateable.Deactivated;
import jorgan.disposition.Message.InputMessage;
import jorgan.disposition.Message.OutputMessage;
import jorgan.disposition.event.OrganEvent;

/**
 * An abstract base class for players that control activateable elements.
 */
public class ActivateablePlayer<E extends Activateable> extends Player<E> {

	public ActivateablePlayer(E activateable) {
		super(activateable);
	}

	@Override
	protected void input(InputMessage message, Map<String, Float> values) {
		Activateable activateable = getElement();

		if (message instanceof Activate) {
			if (!activateable.isActive()) {
				activateable.setActive(true);
			}
		} else if (message instanceof Deactivate) {
			if (activateable.isActive()) {
				activateable.setActive(false);
			}
		} else {
			super.input(message, values);
		}
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		Activateable activateable = getElement();

		Set<Console> consoles = activateable.getReferrer(Console.class);

		Class<? extends OutputMessage> clazz;
		if (activateable.isActive()) {
			clazz = Activated.class;
		} else {
			clazz = Deactivated.class;
		}

		for (OutputMessage message : getElement().getMessages(clazz)) {
			Map<String, Float> values = getValues();

			for (Console console : consoles) {
				getOrganPlay().getPlayer(console).output(message, values);
			}
		}
	}
}
