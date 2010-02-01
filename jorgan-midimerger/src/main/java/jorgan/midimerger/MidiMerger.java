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
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.Loopback;
import bias.Configuration;

/**
 * <code>MidiDevice</code> for merging of mutiple other devices.
 */
public class MidiMerger extends Loopback {

	private static Configuration config = Configuration.getRoot().get(
			MidiMerger.class);

	/**
	 * The list of inputs to merge.
	 */
	private List<MergeInput> inputs = new ArrayList<MergeInput>();

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

	@Override
	public synchronized void open() throws MidiUnavailableException {
		super.open();

		try {
			for (MergeInput input : inputs) {
				mergers.add(new Merger(input.getDevice(), input.getChannel()));
			}
		} catch (MidiUnavailableException ex) {
			close();

			throw ex;
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

		/**
		 * The channel to map message to or <code>-1</code> if no mapping should
		 * be performed.
		 */
		private int channel;

		/**
		 * Create a new receiver for the given input.
		 * 
		 * @param device
		 *            name of device to create receiver for
		 * @param channel
		 *            channel to map messages to
		 * @throws MidiUnavailableException
		 *             if input device is unavailable
		 */
		public Merger(String device, int channel)
				throws MidiUnavailableException {

			this.device = DevicePool.instance().getMidiDevice(device,
					Direction.IN);
			this.device.open();

			this.channel = channel;

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
			if (message instanceof ShortMessage) {
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
			int data1 = message.getData1();
			int data2 = message.getData2();

			if (command < 0xF0 && channel != -1) {
				try {
					ShortMessage mapped = new ShortMessage();
					mapped.setMessage(command, channel, data1, data2);
					message = mapped;
				} catch (InvalidMidiDataException ex) {
					throw new Error(
							"unexpected invalid data in MidiMerger channel mapping");
				}
			}
			return message;
		}

		@Override
		public void close() {
			transmitter.close();

			device.close();
		}
	}

	public List<MergeInput> getInputs() {
		return inputs;
	}

	/**
	 * Set the inputs to merge. <br>
	 * This change has immediate effect only If this midiMerger is not currently
	 * open, otherwise it is delayed until the next opening.
	 * 
	 * @param inputs
	 *            the inputs to merge
	 */
	public void setInputs(List<MergeInput> inputs) {
		if (inputs == null) {
			throw new IllegalArgumentException("inputs must not be null");
		}

		this.inputs = inputs;
	}
}