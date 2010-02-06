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
package jorgan.sysex;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;
import javax.sound.midi.Transmitter;

import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.Loopback;
import bias.Configuration;

/**
 */
public class SysexMapper extends Loopback {

	private static Configuration config = Configuration.getRoot().get(
			SysexMapper.class);

	private String input;

	private String output;

	private File mapping;

	private Encoding encoding;

	private SysexReceiver receiver;

	private SysexTransmitter transmitter;

	private List<SysexMessage> messages = new ArrayList<SysexMessage>();

	public SysexMapper(MidiDevice.Info info) {
		super(info, true, true);
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public void setMapping(File mapping) {
		this.mapping = mapping;
	}

	public File getMapping() {
		return mapping;
	}

	@Override
	public void open() throws MidiUnavailableException {
		super.open();

		config.read(this);

		try {
			if (input != null) {
				receiver = new SysexReceiver();
			}

			if (output != null) {
				transmitter = new SysexTransmitter();
			}
		} catch (MidiUnavailableException ex) {
			close();

			throw ex;
		}

		if (mapping != null) {
			try {
				Sequence sequence = MidiSystem.getSequence(mapping);

				Track track = sequence.getTracks()[0];
				for (int e = 0; e < track.size(); e++) {
					MidiEvent event = track.get(e);

					MidiMessage message = event.getMessage();
					if (message instanceof SysexMessage) {
						messages.add((SysexMessage) message);
					}
				}
			} catch (Exception e) {
				close();

				MidiUnavailableException ex = new MidiUnavailableException();
				ex.initCause(e);
				throw ex;
			}
		}
	}

	@Override
	public synchronized void close() {

		messages.clear();

		if (receiver != null) {
			receiver.close();
			receiver = null;
		}

		if (transmitter != null) {
			transmitter.close();
			transmitter = null;
		}

		super.close();
	}

	@Override
	protected void onLoopIn(MidiMessage message) {
		if (message instanceof ShortMessage) {
			ShortMessage shortMessage = (ShortMessage) message;

			transmitter.transmit(shortMessage);
		}
	}

	private class SysexReceiver implements Receiver {

		private MidiDevice device;

		private Transmitter transmitter;

		public SysexReceiver() throws MidiUnavailableException {

			this.device = DevicePool.instance().getMidiDevice(input,
					Direction.IN);
			this.device.open();

			try {
				transmitter = this.device.getTransmitter();
				transmitter.setReceiver(this);
			} catch (MidiUnavailableException ex) {
				this.device.close();

				throw ex;
			}
		}

		@Override
		public void send(MidiMessage message, long timeStamp) {
			if (message instanceof SysexMessage) {
				SysexMessage sysexMessage = (SysexMessage) message;

				ShortMessage mapped = map(sysexMessage);
				if (mapped != null) {
					loopOut(mapped);
				}
			}
		}

		@Override
		public void close() {
			transmitter.close();
			device.close();
		}
	}

	private class SysexTransmitter {

		private MidiDevice device;

		private Receiver receiver;

		public SysexTransmitter() throws MidiUnavailableException {
			this.device = DevicePool.instance().getMidiDevice(output,
					Direction.OUT);
			this.device.open();

			try {
				this.receiver = this.device.getReceiver();
			} catch (MidiUnavailableException ex) {
				this.device.close();

				throw ex;
			}
		}

		public void close() {
			device.close();
		}

		public void transmit(ShortMessage message) {
			SysexMessage mapped = map(message);
			if (mapped != null) {
				receiver.send(mapped, -1);
			}
		}
	}

	public ShortMessage map(SysexMessage message) {
		byte[] data = message.getMessage();

		for (int d = 0; d < messages.size(); d++) {
			if (Arrays.equals(messages.get(d).getMessage(), data)) {
				ShortMessage encode = encoding.encode(d);
				if (encode == null) {
					break;
				} else {
					return encode;
				}
			}
		}

		return null;
	}

	public SysexMessage map(ShortMessage message) {
		int d = encoding.decode(message);

		if (d != -1 && d < messages.size()) {
			return messages.get(d);
		}

		return null;
	}
}