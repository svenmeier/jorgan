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

import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.Loopback;
import jorgan.midi.MidiGate;
import jorgan.midimerger.merging.Merger;
import jorgan.midimerger.merging.Merging;

/**
 * <code>MidiDevice</code> for merging of mutiple other devices.
 */
public class MidiMerger extends Loopback {

	private MidiGate gate = new MidiGate();

	private Merging merging;

	private List<MergeReceiver> receivers = new ArrayList<MergeReceiver>();

	/**
	 * Create a new midiMerger.
	 * 
	 * @param info
	 *            info to use
	 * @param merging
	 */
	public MidiMerger(MidiDevice.Info info, Merging merging) {
		super(info, false, true);

		this.merging = merging;
	}

	@Override
	public void open() throws MidiUnavailableException {
		super.open();

		gate.open();
	}

	@Override
	protected synchronized void openImpl() throws MidiUnavailableException {
		if (merging.isEmpty()) {
			throw new MidiUnavailableException();
		}

		for (Merger merger : merging.getMergers()) {
			receivers.add(new MergeReceiver(merger));
		}
	}

	@Override
	public void close() {
		gate.close();

		super.close();
	}

	@Override
	protected synchronized void closeImpl() {
		for (Receiver merger : receivers) {
			merger.close();
		}
		receivers.clear();

		super.closeImpl();
	}

	private class MergeReceiver implements Receiver {

		/**
		 * The input device to receive messages from.
		 */
		private MidiDevice device;

		/**
		 * The channel to map message to or <code>-1</code> if no mapping should
		 * be performed.
		 */
		private int channel;

		public MergeReceiver(Merger merger) throws MidiUnavailableException {

			this.device = DevicePool.instance().getMidiDevice(
					merger.getDevice(), Direction.IN);
			this.device.open();

			this.device.getTransmitter().setReceiver(gate.guard(this));

			this.channel = merger.getChannel();
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
			device.close();
		}
	}
}