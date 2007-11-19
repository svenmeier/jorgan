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
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import jorgan.disposition.Console;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.DevicePool;

/**
 * A player of an console.
 */
public class ConsolePlayer extends Player<Console> {

	/**
	 * The midiDevice to receive input from.
	 */
	private MidiDevice in;

	/**
	 * The transmitter of the opened midiDevice.
	 */
	private Transmitter transmitter;

	public ConsolePlayer(Console console) {
		super(console);
	}

	@Override
	protected void openImpl() {
		Console console = getElement();

		removeProblem(new Error("device"));

		String device = console.getDevice();
		if (device != null) {
			try {
				in = DevicePool.getMidiDevice(device, false);
				in.open();

				transmitter = in.getTransmitter();
				transmitter.setReceiver(getOrganPlay().createReceiver(this));
			} catch (MidiUnavailableException ex) {
				addProblem(new Error("device", device));
			}
		}
	}

	@Override
	protected void closeImpl() {
		if (transmitter != null) {
			transmitter.close();
			in.close();

			transmitter = null;
			in = null;
		}
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		Console console = getElement();

		if (console.getDevice() == null && getWarnDevice()) {
			removeProblem(new Error("device"));
			addProblem(new Warning("device"));
		} else {
			removeProblem(new Warning("device"));
		}
	}

	@Override
	public void received(ShortMessage message) {
		Console console = getElement();

		for (int r = 0; r < console.getReferenceCount(); r++) {
			Reference reference = console.getReference(r);

			Player player = getOrganPlay().getPlayer(reference.getElement());
			if (player != null && !(player instanceof ConsolePlayer)) {
				player.input(message.getCommand(), message.getData1(), message
						.getData2());
			}
		}
	}

	@Override
	protected void output(int status, int data1, int data2) {
		// TODO write message into new midi output
	}
}