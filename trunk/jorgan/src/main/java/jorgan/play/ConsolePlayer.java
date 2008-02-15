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

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Output;
import jorgan.disposition.Reference;
import jorgan.midi.mpl.Context;

/**
 * A player of an console.
 */
public class ConsolePlayer extends Player<Console> {

	private PlayerContext context = new PlayerContext();

	public ConsolePlayer(Console console) {
		super(console);
	}

	@Override
	public void received(ShortMessage message) {
		Console console = getElement();

		for (int r = 0; r < console.getReferenceCount(); r++) {
			Reference<? extends Element> reference = console.getReference(r);
			Element element = reference.getElement();

			if (!(element instanceof Output)) {
				Player<?> player = getOrganPlay().getPlayer(
						reference.getElement());
				if (player != null) {
					player.onInput(message, context);
				}
			}
		}
	}

	@Override
	public void onOutput(ShortMessage message, Context context) {
		Console console = getElement();

		for (int r = 0; r < console.getReferenceCount(); r++) {
			Reference<? extends Element> reference = console.getReference(r);
			Element element = reference.getElement();

			if (element instanceof Output) {
				OutputPlayer<?> player = (OutputPlayer<?>)getOrganPlay().getPlayer(
						reference.getElement());
				if (player != null) {
					player.send(message);
				}
			}
		}
	}
}