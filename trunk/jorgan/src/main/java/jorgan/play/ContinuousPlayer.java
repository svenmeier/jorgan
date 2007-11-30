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

import jorgan.disposition.Console;
import jorgan.disposition.Continuous;
import jorgan.disposition.Continuous.Change;
import jorgan.disposition.Continuous.Changed;
import jorgan.disposition.Message.InputMessage;
import jorgan.disposition.event.OrganEvent;
import jorgan.util.math.ProcessingException;
import jorgan.util.math.NumberProcessor.Context;

/**
 * A player for a swell.
 */
public class ContinuousPlayer<E extends Continuous> extends Player<E> {

	private PlayerContext context = new PlayerContext();

	public ContinuousPlayer(E slider) {
		super(slider);
	}

	@Override
	protected void input(InputMessage message, Context context)
			throws ProcessingException {
		Continuous continuous = getElement();

		if (message instanceof Change) {
			float value = context.get(Change.VALUE);

			if (Math.abs(continuous.getValue() - value) > continuous
					.getThreshold()) {
				continuous.setValue(value);
			}
		} else {
			super.input(message, context);
		}
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		changed();
	}

	private void changed() {
		Continuous continuous = getElement();
		context.set(Changed.VALUE, continuous.getValue());

		Set<Console> consoles = continuous.getReferrer(Console.class);

		for (Changed changed : getElement().getMessages(Changed.class)) {
			for (Console console : consoles) {
				Player player = getOrganPlay().getPlayer(console);
				player.output(changed, context);
			}
		}
	}
}