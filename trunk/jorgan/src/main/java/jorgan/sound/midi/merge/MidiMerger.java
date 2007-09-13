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
package jorgan.sound.midi.merge;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import jorgan.sound.midi.DevicePool;
import jorgan.sound.midi.Loopback;
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

	/**
	 * Overriden to create receivers for all devices to merge.
	 */
	@Override
	public void open() throws MidiUnavailableException {
		super.open();

		try {
			for (MergeInput input : inputs) {
				new MergeReceiver(input.getDevice(), input.getChannel());
			}
		} catch (MidiUnavailableException ex) {
			close();

			throw ex;
		}
	}

	/**
	 * One receiver used for each input device.
	 */
	private class MergeReceiver extends LoopbackReceiver {

		/**
		 * The input device to receive messages from.
		 */
		private MidiDevice device;

		/**
		 * The transmitter of the input device.
		 */
		private Transmitter transmitter;

		/**
		 * The channel to map message to or <code>-1</code> if no mapping
		 * should be performed.
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
		public MergeReceiver(String device, int channel)
				throws MidiUnavailableException {

			// Important: assure successfull opening of MIDI device
			// before storing reference in instance variable
			MidiDevice toBeOpened = DevicePool.getMidiDevice(device, false);
			toBeOpened.open();
			this.device = toBeOpened;

			transmitter = this.device.getTransmitter();
			transmitter.setReceiver(this);

			this.channel = channel;
		}

		/**
		 * Apply channel mapping.
		 */
		@Override
		protected MidiMessage filter(MidiMessage message) {
			if (message instanceof ShortMessage) {
				message = mapChannel((ShortMessage) message);
			}
			return message;
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

		/**
		 * Closing this receiver also closes the device listened to.
		 */
		@Override
		public void close() {
			super.close();

			if (transmitter != null) {
				transmitter.close();
			}

			if (device != null) {
				device.close();
			}
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