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
package jorgan.fluidsynth.midi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import jorgan.fluidsynth.Fluidsynth;

/**
 * Java Wrapper for a Fluidsynth.
 */
public class FluidsynthMidiDevice implements MidiDevice {

	private Info info;

	private List<ReceiverImpl> receivers = new ArrayList<ReceiverImpl>();

	private boolean open;

	private Fluidsynth synth;

	public FluidsynthMidiDevice(Info info, Fluidsynth synth) {
		this.info = info;
		this.synth = synth;
	}

	public Info getDeviceInfo() {
		return info;
	}

	public void close() {
		open = false;

		for (ReceiverImpl receiver : new ArrayList<ReceiverImpl>(receivers)) {
			receiver.close();
		}
		receivers.clear();
	}

	public int getMaxReceivers() {
		return -1;
	}

	public int getMaxTransmitters() {
		return 0;
	}

	public long getMicrosecondPosition() {
		return 0;
	}

	public List<Receiver> getReceivers() {
		return new ArrayList<Receiver>(receivers);
	}

	public List<Transmitter> getTransmitters() {
		return Collections.emptyList();
	}

	public Receiver getReceiver() throws MidiUnavailableException {
		if (!open) {
			throw new IllegalStateException();
		}

		return new ReceiverImpl();
	}

	public Transmitter getTransmitter() throws MidiUnavailableException {
		throw new MidiUnavailableException();
	}

	public boolean isOpen() {
		return open;
	}

	public void open() throws MidiUnavailableException {
		open = true;
	}

	private class ReceiverImpl implements Receiver {
		private boolean closed;

		public ReceiverImpl() {
			receivers.add(this);
		}

		public void close() {
			closed = true;
			receivers.remove(this);
		}

		public void send(MidiMessage message, long timeStamp) {
			if (closed) {
				throw new IllegalStateException();
			}

			if (message instanceof ShortMessage) {
				ShortMessage shortMessage = (ShortMessage) message;

				int channel = shortMessage.getChannel();

				switch (shortMessage.getCommand()) {
				case ShortMessage.NOTE_ON:
					synth.noteOn(channel, shortMessage.getData1(), shortMessage
							.getData2());
					break;
				case ShortMessage.NOTE_OFF:
					synth.noteOff(channel, shortMessage.getData1());
					break;
				case ShortMessage.PROGRAM_CHANGE:
					synth.programChange(channel, shortMessage.getData1());
					break;
				case ShortMessage.CONTROL_CHANGE:
					synth.controlChange(channel, shortMessage.getData1(),
							shortMessage.getData2());
					break;
				case ShortMessage.PITCH_BEND:
					synth.pitchBend(channel, shortMessage.getData1());
					break;
				}
			}
		}
	}
}
