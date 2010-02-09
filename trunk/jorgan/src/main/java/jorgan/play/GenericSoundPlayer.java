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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import jorgan.disposition.GenericSound;
import jorgan.midi.MessageUtils;
import jorgan.problem.Severity;

/**
 * A player of a {@link jorgan.disposition.GenericSound}.
 */
public class GenericSoundPlayer<S extends GenericSound> extends SoundPlayer<S> {

	private Receiver receiver;

	public GenericSoundPlayer(S sound) {
		super(sound);
	}

	@Override
	public void update() {
		super.update();

		GenericSound sound = getElement();

		if (sound.getOutput() == null) {
			addProblem(Severity.WARNING, "output", "noDevice", sound
					.getOutput());
		} else {
			removeProblem(Severity.WARNING, "output");
		}
	}

	@Override
	protected void openImpl() {
		GenericSound sound = getElement();

		removeProblem(Severity.ERROR, "output");
		if (sound.getOutput() != null) {
			try {
				receiver = getOrganPlay().createReceiver(sound.getOutput());
			} catch (MidiUnavailableException ex) {
				addProblem(Severity.ERROR, "output", "deviceUnavailable", sound
						.getOutput());
			}
		}
	}

	@Override
	protected void closeImpl() {
		if (receiver != null) {
			receiver.close();
			receiver = null;
		}
	}

	protected boolean send(MidiMessage message) {
		if (receiver == null) {
			return false;
		}

		if (getOrganPlay() != null) {
			getOrganPlay().fireSent(message);
		}

		receiver.send(message, -1);

		return true;
	}

	@Override
	protected boolean send(int channel, int command, int data1, int data2) {
		ShortMessage message;
		try {
			message = MessageUtils
					.createMessage(channel, command, data1, data2);
		} catch (InvalidMidiDataException e) {
			throw new Error(e);
		}
		return send(message);
	}
}