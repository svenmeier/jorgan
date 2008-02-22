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
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import jorgan.disposition.Element;
import jorgan.disposition.Input;
import jorgan.disposition.MidiInput;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.MessageUtils;
import jorgan.session.event.Severity;

/**
 * A player of an {@link MidiInput}.
 */
public class MidiInputPlayer extends Player<MidiInput> {

	/**
	 * The midiDevice to receive input from.
	 */
	private MidiDevice device;

	/**
	 * The transmitter {@link #device}.
	 */
	private Transmitter transmitter;

	public MidiInputPlayer(MidiInput input) {
		super(input);
	}

	@Override
	public void elementChanged(OrganEvent event) {
		MidiInput input = getElement();

		if (input.getDevice() == null) {
			addProblem(Severity.WARNING, "device", "noDevice", input
					.getDevice());
		} else {
			removeProblem(Severity.WARNING, "device");
		}
	}

	@Override
	protected void openImpl() {
		MidiInput input = getElement();

		removeProblem(Severity.ERROR, "device");

		if (input.getDevice() != null) {
			try {
				// Important: assure successfull opening of MIDI device
				// before storing reference in instance variable
				MidiDevice toBeOpened = DevicePool.instance().getMidiDevice(
						input.getDevice(), Direction.IN);
				toBeOpened.open();
				this.device = toBeOpened;

				transmitter = device.getTransmitter();
				transmitter
						.setReceiver(getOrganPlay().new SynchronizedReceiver(
								new ReceiverImpl()));
			} catch (MidiUnavailableException ex) {
				addProblem(Severity.ERROR, "device", "deviceUnavailable", input
						.getDevice());
			}
		}
	}

	@Override
	protected void closeImpl() {
		if (transmitter != null) {
			transmitter.close();
			device.close();

			transmitter = null;
			device = null;
		}
	}

	private class ReceiverImpl implements Receiver {
		public void close() {
		}

		public void send(MidiMessage message, long timeStamp) {
			if (MessageUtils.isShortMessage(message)) {
				Input input = getElement();

				for (int r = 0; r < input.getReferenceCount(); r++) {
					Reference<? extends Element> reference = input
							.getReference(r);

					Player<?> player = getOrganPlay().getPlayer(
							reference.getElement());
					if (player != null) {
						player.received((ShortMessage) message);
					}
				}
			}
		}
	}
}