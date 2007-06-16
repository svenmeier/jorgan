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

import jorgan.disposition.Continuous;
import jorgan.disposition.Message;
import jorgan.disposition.event.OrganEvent;

/**
 * A player for a swell.
 */
public class ContinuousPlayer<E extends Continuous> extends Player<E> {

	private static final Problem warningMessage = new Problem(Problem.WARNING,
			"message");

	public ContinuousPlayer(E slider) {
		super(slider);
	}

	public void messageReceived(ShortMessage shortMessage) {
		Continuous slider = getElement();

		Message message = slider.getMessage();
		if (message != null
				&& message.match(message.getStatus(), shortMessage.getData1(),
						shortMessage.getData2())) {

			int position = message.wildcard(shortMessage.getData1(),
					shortMessage.getData2());
			if (position != -1) {
				if (slider.isReverse()) {
					position = 127 - position;
				}
				if (Math.abs(slider.getValue() - position) > slider
						.getThreshold()) {
					fireInputAccepted();

					slider.setValue(position);
				}
			}
		}
	}

	public void elementChanged(OrganEvent event) {
		Continuous slider = getElement();

		if (slider.getMessage() == null && getWarnMessage()) {
			addProblem(warningMessage.value(null));
		} else {
			removeProblem(warningMessage);
		}
	}
}