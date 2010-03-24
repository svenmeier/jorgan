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
package jorgan.midimerger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.Loopback;
import jorgan.midi.MessageUtils;
import jorgan.midimerger.Mapping.Mode;
import bias.Configuration;

/**
 * <code>MidiDevice</code> for merging of mutiple other devices.
 */
public class MidiMerger extends Loopback {

	private static Configuration config = Configuration.getRoot().get(
			MidiMerger.class);

	private Mapping mapping = new Mapping();

	private List<Receiver> mergers = new ArrayList<Receiver>();

	/**
	 * Create a new midiMerger.
	 * 
	 * @param info
	 *            info to use
	 */
	public MidiMerger(MidiDevice.Info info) {
		super(info, false, true);

		config.read(this);
	}

	public Mapping getMapping() {
		return mapping;
	}

	/**
	 * Set the mapping. <br>
	 * This change has immediate effect only if this midiMerger is not currently
	 * open, otherwise it is delayed until the next opening.
	 * 
	 * @param mapping
	 *            the channel mapping
	 */
	public void setMapping(Mapping mapping) {
		if (mapping == null) {
			throw new IllegalArgumentException("mapping must not be null");
		}

		this.mapping = mapping;
	}

	@Override
	public synchronized void open() throws MidiUnavailableException {
		super.open();

		try {
			int index = 0;
			for (String name : getDeviceNames()) {
				Mode mode = mapping.getMode(index);
				if (mode != Mode.SKIP) {
					mergers.add(new Merger(name, mode));
				}
				index++;
			}
		} catch (MidiUnavailableException ex) {
			close();

			throw ex;
		}

		if (mergers.isEmpty()) {
			close();

			throw new MidiUnavailableException();
		}
	}

	@Override
	public synchronized void close() {
		super.close();

		for (Receiver merger : mergers) {
			merger.close();
		}
		mergers.clear();
	}

	private class Merger implements Receiver {

		/**
		 * The input device to receive messages from.
		 */
		private MidiDevice device;

		/**
		 * The transmitter of the input device.
		 */
		private Transmitter transmitter;

		private Mode mode;

		/**
		 * Create a new receiver for the given input.
		 * 
		 * @param device
		 *            name of device to create receiver for
		 * @param mode
		 *            channel to map messages to
		 * @throws MidiUnavailableException
		 *             if input device is unavailable
		 */
		public Merger(String device, Mode mode) throws MidiUnavailableException {

			this.device = DevicePool.instance().getMidiDevice(device,
					Direction.IN);
			this.device.open();

			this.mode = mode;

			try {
				transmitter = this.device.getTransmitter();
				transmitter.setReceiver(this);
			} catch (MidiUnavailableException ex) {
				this.device.close();

				throw ex;
			}
		}

		@Override
		public void send(MidiMessage message, long timestamp) {
			if (MessageUtils.isChannelMessage(message)) {
				message = mapChannel((ShortMessage) message);
			}
			loopOut(message);
		}

		/**
		 * Map the channel of the given message.
		 * 
		 * @param message
		 *            message to map channel
		 * @return new message with mapped channel
		 */
		private MidiMessage mapChannel(ShortMessage message) {

			int command = message.getCommand();
			int channel = message.getChannel();
			int data1 = message.getData1();
			int data2 = message.getData2();

			message = MessageUtils.newMessage(command, mode.map(channel),
					data1, data2);

			return message;
		}

		@Override
		public void close() {
			transmitter.close();

			device.close();
		}
	}

	public static List<String> getDeviceNames() {
		List<String> names = new ArrayList<String>();

		for (String name : DevicePool.instance().getMidiDeviceNames(
				Direction.IN)) {
			if (!MidiMergerProvider.INFO.getName().equals(name)) {
				names.add(name);
			}
		}

		Collections.sort(names);

		return names;
	}
}