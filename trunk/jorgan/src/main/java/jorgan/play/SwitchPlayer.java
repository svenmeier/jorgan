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

import jorgan.disposition.Switch;
import jorgan.disposition.Switch.Activate;
import jorgan.disposition.Switch.Activated;
import jorgan.disposition.Switch.Deactivate;
import jorgan.disposition.Switch.Deactivated;
import jorgan.disposition.Switch.Initiate;
import jorgan.disposition.Switch.Toggle;
import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.mpl.Context;

/**
 * An base for players that control {@link Switch}es.
 */
public class SwitchPlayer<E extends Switch> extends Player<E> {

	private PlayerContext activeContext = new PlayerContext();

	public SwitchPlayer(E element) {
		super(element);
	}

	@Override
	protected void input(InputMessage message, Context context) {
		Switch element = getElement();

		if (message instanceof Activate) {
			element.setActive(true);
		} else if (message instanceof Deactivate) {
			element.setActive(false);
		} else if (message instanceof Toggle) {
			element.toggle();
		} else if (message instanceof Initiate) {
			element.initiate();
		} else {
			super.input(message, context);
		}
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		if (isOpen()) {
			Switch element = getElement();

			if (element.isActive()) {
				activated();
			} else {
				deactivated();
			}
		}
	}

	private void activated() {
		for (Activated message : getElement().getMessages(Activated.class)) {
			output(message, activeContext);
		}
	}

	private void deactivated() {
		for (Deactivated message : getElement().getMessages(Deactivated.class)) {
			output(message, activeContext);
		}
	}
}
