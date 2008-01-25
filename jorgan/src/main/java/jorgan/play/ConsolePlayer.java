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
import javax.sound.midi.Transmitter;

import jorgan.disposition.Console;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.DevicePool;
import jorgan.midi.mpl.Context;
import jorgan.session.event.Severity;

/**
 * A player of an console.
 */
public class ConsolePlayer extends Player<Console> {

	private PlayerContext context = new PlayerContext();

	/**
	 * The midiDevice to receive input from.
	 */
	private MidiDevice in;

	/**
	 * The midiDevice to send output to.
	 */
	private MidiDevice out;

	/**
	 * The transmitter {@link #in}.
	 */
	private Transmitter transmitter;

	/**
	 * The receiver {@link #out}.
	 */
	private Receiver receiver;

	public ConsolePlayer(Console console) {
		super(console);
	}

	@Override
	protected void openImpl() {
		Console console = getElement();

		removeProblem(Severity.ERROR, "input");
		removeProblem(Severity.ERROR, "output");

		String input = console.getInput();
		if (input != null) {
			try {
				// Important: assure successfull opening of MIDI device
				// before storing reference in instance variable
				MidiDevice toBeOpened = DevicePool.getMidiDevice(input,
						DevicePool.IN);
				toBeOpened.open();
				this.in = toBeOpened;

				transmitter = in.getTransmitter();
				transmitter.setReceiver(getOrganPlay().createReceiver(this));
			} catch (MidiUnavailableException ex) {
				addProblem(Severity.ERROR, "input", input, "inputUnavailable");
			}
		}

		String output = console.getOutput();
		if (output != null) {
			try {
				// Important: assure successfull opening of MIDI device
				// before storing reference in instance variable
				MidiDevice toBeOpened = DevicePool.getMidiDevice(output,
						DevicePool.OUT);
				toBeOpened.open();
				this.out = toBeOpened;

				receiver = out.getReceiver();
			} catch (MidiUnavailableException ex) {
				addProblem(Severity.ERROR, "output", output, "outputUnavailable");
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

		if (receiver != null) {
			receiver.close();
			out.close();

			receiver = null;
			out = null;
		}
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		Console console = getElement();

		if (console.getInput() == null && getWarnDevice()) {
			removeProblem(Severity.ERROR, "input");
			addProblem(Severity.WARNING, "input", null, "inputMissing");
		} else {
			removeProblem(Severity.WARNING, "input");
		}

		if (console.getOutput() == null && getWarnDevice()) {
			removeProblem(Severity.ERROR, "output");
			addProblem(Severity.WARNING, "output", null, "outputMissing");
		} else {
			removeProblem(Severity.WARNING, "output");
		}
	}

	@Override
	public void received(ShortMessage message) {
		Console console = getElement();

		for (int r = 0; r < console.getReferenceCount(); r++) {
			Reference reference = console.getReference(r);

			Player<?> player = getOrganPlay().getPlayer(reference.getElement());
			if (player != null) {
				player.input(message, context);
			}
		}
	}

	@Override
	public void output(ShortMessage message, Context context) {
		if (receiver != null) {
			receiver.send(message, -1);
		}
	}
}