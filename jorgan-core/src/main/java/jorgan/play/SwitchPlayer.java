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
import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.Switch.Activate;
import jorgan.disposition.Switch.Activated;
import jorgan.disposition.Switch.Deactivate;
import jorgan.disposition.Switch.Deactivated;
import jorgan.disposition.Switch.Initiate;
import jorgan.disposition.Switch.Toggle;
import jorgan.midi.mpl.Context;
import jorgan.util.Null;

/**
 * An base for players that control {@link Switch}es.
 */
public class SwitchPlayer<E extends Switch> extends Player<E> {

	private PlayerContext outputContext = new PlayerContext();

	private Boolean active;

	public SwitchPlayer(E element) {
		super(element);
	}

	@Override
	protected void openImpl() {
		active = null;
	}

	@Override
	protected void onInput(InputMessage message, Context context) {
		Switch element = getElement();

		if (message instanceof Activate) {
			element.setActive(true);
		} else if (message instanceof Deactivate) {
			element.setActive(false);
		} else if (message instanceof Toggle) {
			element.setActive(!element.isActive());
		} else if (message instanceof Initiate) {
			element.setActive(true);
			element.setActive(false);
		} else {
			super.onInput(message, context);
		}
	}

	@Override
	public void update() {
		super.update();

		if (isOpen()) {
			boolean active = getElement().isActive();
			if (!Null.safeEquals(this.active, active)) {
				if (active) {
					activated();
				} else {
					deactivated();
				}
				this.active = active;
			}
		}
	}

	private void activated() {
		Switch element = getElement();

		for (Activated message : element.getMessages(Activated.class)) {
			output(message, outputContext);
		}
	}

	private void deactivated() {
		for (Deactivated message : getElement().getMessages(Deactivated.class)) {
			output(message, outputContext);
		}
	}
}
