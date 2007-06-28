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

import javax.sound.midi.ShortMessage;

import jorgan.disposition.Activateable;
import jorgan.disposition.Message;
import jorgan.disposition.event.OrganEvent;

/**
 * An abstract base class for players that control activateable elements.
 */
public abstract class ActivateablePlayer<E extends Activateable> extends Player<E> {

	private static final Problem warningActivateMessage = new Problem(
			Problem.WARNING, "activateMessage");

	private static final Problem warningDeactivateMessage = new Problem(
			Problem.WARNING, "deactivateMessage");

	private int activations = 0;

	public ActivateablePlayer(E activateable) {
		super(activateable);
	}

	protected void closeImpl() {
		super.closeImpl();

		activations = 0;
	}

	public void activate() {
		activations++;

		elementChanged(null);
	}

	public void deactivate() {
		activations--;

		elementChanged(null);
	}

	protected boolean isActive() {
		Activateable activateable = getElement();

		return activations > 0 || activateable.isActive();
	}

	public void messageReceived(ShortMessage shortMessage) {
		Activateable activateable = getElement();

		if (activateable.isActive()) {
			Message offMessage = activateable.getDeactivateMessage();
			if (offMessage != null
					&& offMessage.match(shortMessage.getStatus(),
							shortMessage.getData1(), shortMessage.getData2())) {

				fireInputAccepted();

				activateable.setActive(false);
			}
		} else {
			Message onMessage = activateable.getActivateMessage();
			if (onMessage != null
					&& onMessage.match(shortMessage.getStatus(), shortMessage.getData1(),
							shortMessage.getData2())) {

				fireInputAccepted();

				activateable.setActive(true);
			}
		}
	}

	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		Activateable activateable = getElement();

		if ((activateable.getActivateMessage() == null)
				&& getWarnMessage()) {
			addProblem(warningActivateMessage.value(null));
		} else {
			removeProblem(warningActivateMessage);
		}

		if ((activateable.getDeactivateMessage() == null)
				&& getWarnMessage()) {
			addProblem(warningDeactivateMessage.value(null));
		} else {
			removeProblem(warningDeactivateMessage);
		}
	}
}
