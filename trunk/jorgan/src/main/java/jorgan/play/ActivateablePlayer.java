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

import jorgan.disposition.Activateable;
import jorgan.disposition.Activateable.Activate;
import jorgan.disposition.Activateable.Activated;
import jorgan.disposition.Activateable.Deactivate;
import jorgan.disposition.Activateable.Deactivated;
import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.mpl.Processor.Context;

/**
 * An abstract base class for players that control activateable elements.
 */
public class ActivateablePlayer<E extends Activateable> extends Player<E> {

	private PlayerContext context = new PlayerContext();

	public ActivateablePlayer(E activateable) {
		super(activateable);
	}

	@Override
	protected void input(InputMessage message, Context context) {
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
			super.input(message, context);
		}
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		if (isOpen()) {
			Activateable activateable = getElement();

			if (activateable.isActive()) {
				activated();
			} else {
				deactivated();
			}
		}
	}

	private void activated() {
		for (Activated message : getElement().getMessages(Activated.class)) {
			output(message, context);
		}
	}

	private void deactivated() {
		for (Deactivated message : getElement().getMessages(Deactivated.class)) {
			output(message, context);
		}
	}
}
