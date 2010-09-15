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

	private boolean send(MidiMessage message) {
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
	protected void send(int channel, byte[] datas)
			throws InvalidMidiDataException {

		if (datas.length != 3 || !MessageUtils.isChannelStatus(datas[0])) {
			throw new InvalidMidiDataException("short messages supported only");
		}

		int status = datas[0] & 0xff;
		int data1 = datas[1] & 0xff;
		int data2 = datas[2] & 0xff;

		MidiMessage message = MessageUtils.createMessage(status, data1, data2);
		if (getOrganPlay() != null) {
			getOrganPlay().fireSent(message);
		}

		send(message);
	}
}