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

import jorgan.disposition.Initiator;
import jorgan.disposition.Initiator.Initiate;
import jorgan.disposition.Message.InputMessage;
import jorgan.midi.mpl.ProcessingException;
import jorgan.midi.mpl.Processor.Context;

/**
 * A player for an {@link jorgan.disposition.Initiator}.
 */
public class InitiatorPlayer<E extends Initiator> extends Player<E> {

	public InitiatorPlayer(E e) {
		super(e);
	}

	@Override
	protected void input(InputMessage message, Context context)
			throws ProcessingException {
		Initiator initiator = getElement();

		if (message instanceof Initiate) {
			initiator.initiate();
		} else {
			super.input(message, context);
		}
	}
}