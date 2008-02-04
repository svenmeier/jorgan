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

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import jorgan.disposition.BasicOutput;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.session.event.Severity;

/**
 * A player of a {@link jorgan.disposition.BasicOutput}.
 */
public class BasicOutputPlayer<O extends BasicOutput> extends OutputPlayer<O> {

	private MidiDevice device;

	private Receiver receiver;

	public BasicOutputPlayer(O output) {
		super(output);
	}

	@Override
	protected void openImpl() {
		BasicOutput output = getElement();

		if (output.getDevice() != null) {
			try {
				// Important: assure successfull opening of MIDI device
				// before storing reference in instance variable
				MidiDevice toBeOpened = DevicePool.instance().getMidiDevice(
						output.getDevice(), Direction.OUT);
				toBeOpened.open();
				device = toBeOpened;
				receiver = device.getReceiver();
			} catch (MidiUnavailableException ex) {
				addProblem(Severity.ERROR, "device", output.getDevice(),
						"deviceUnavailable");
			}
		}
	}

	@Override
	protected void closeImpl() {
		if (device != null) {
			receiver.close();
			receiver = null;

			device.close();
			device = null;
		}
	}

	@Override
	public void output(ShortMessage message) {
		if (receiver != null) {
			receiver.send(message, -1);
		}
	}
}