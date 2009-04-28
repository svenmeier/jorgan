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

import jorgan.disposition.Continuous;
import jorgan.disposition.Continuous.Change;
import jorgan.disposition.Continuous.Changed;
import jorgan.disposition.Input.InputMessage;
import jorgan.midi.mpl.Context;
import jorgan.session.problem.Severity;

/**
 * A player for a swell.
 */
public class ContinuousPlayer<E extends Continuous> extends Player<E> {

	private PlayerContext valueContext = new PlayerContext();

	public ContinuousPlayer(E continuous) {
		super(continuous);
	}

	@Override
	protected void input(InputMessage message, Context context) {
		Continuous continuous = getElement();

		if (message instanceof Change) {
			float value = context.get(Change.VALUE);
			if (value < 0.0f || value > 1.0f) {
				addProblem(Severity.ERROR, message, "valueInvalid", value);
				return;
			}

			if (Math.abs(continuous.getValue() - value) > continuous
					.getThreshold()) {
				continuous.setValue(value);
			}
		} else {
			super.input(message, context);
		}
	}

	@Override
	public void update() {
		super.update();

		if (isOpen()) {
			changed();
		}
	}

	private void changed() {
		Continuous continuous = getElement();
		valueContext.set(Changed.VALUE, continuous.getValue());

		for (Changed message : getElement().getMessages(Changed.class)) {
			output(message, valueContext);
		}
	}
}