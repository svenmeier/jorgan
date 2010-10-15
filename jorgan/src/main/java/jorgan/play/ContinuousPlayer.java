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
import jorgan.problem.Severity;

/**
 * A player for a {@link Continuous} element.
 */
public class ContinuousPlayer<E extends Continuous> extends Player<E> {

	private PlayerContext outputContext = new PlayerContext();

	private Long alarmTime;

	public ContinuousPlayer(E continuous) {
		super(continuous);
	}

	@Override
	public void onAlarm(long time) {
		if (alarmTime != null && alarmTime == time) {
			getElement().setValue(0.0f);
		}
	}

	@Override
	protected void onInput(InputMessage message, Context context) {
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
			super.onInput(message, context);
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

		for (Changed message : continuous.getMessages(Changed.class)) {
			outputContext.set(Changed.VALUE, continuous.getValue());

			output(message, outputContext);
		}

		int duration = continuous.getDuration();
		if (duration > Continuous.DURATION_NONE) {
			alarmTime = System.currentTimeMillis() + duration;

			getOrganPlay().getClock().alarm(continuous, alarmTime);
		} else {
			alarmTime = null;
		}
	}
}