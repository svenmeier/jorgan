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
package jorgan.sams;

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
 */
public class SamsDevice extends Loopback {

	private static Configuration config = Configuration.getRoot().get(
			SamsDevice.class);

	private Sams sams;

	private SamsReceiver receiver;

	private SamsTransmitter transmitter;

	/**
	 * Create a new midiMerger.
	 * 
	 * @param info
	 *            info to use
	 */
	public SamsDevice(MidiDevice.Info info) {
		super(info, true, true);

		config.read(this);
	}

	/**
	 * Overriden to create receivers for all devices to merge.
	 */
	@Override
	public void open() throws MidiUnavailableException {
		super.open();

		try {
			receiver = new SamsReceiver(sams);
			transmitter = new SamsTransmitter(sams);
		} catch (MidiUnavailableException ex) {
			close();

			throw ex;
		}
	}

	@Override
	protected void onLoopIn(MidiMessage message, long timeStamp) {
		if (message instanceof ShortMessage) {
			ShortMessage shortMessage = (ShortMessage) message;
			magnetOff(sams.inverse(shortMessage));

			magnetOn(shortMessage);
		}
	}

	@Override
	public synchronized void close() {
		super.close();

		if (receiver != null) {
			receiver.close();
			receiver = null;
		}

		if (transmitter != null) {
			transmitter.close();
			transmitter = null;
		}
	}

	private class SamsReceiver implements Receiver {

		private MidiDevice device;

		private Transmitter transmitter;

		public SamsReceiver(Sams sam) throws MidiUnavailableException {

			this.device = DevicePool.instance().getMidiDevice(sam.getDevice(),
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
			if (message instanceof ShortMessage) {
				magnetOff((ShortMessage) message);
			}

			loopOut(message, timeStamp);
		}

		@Override
		public void close() {
			transmitter.close();
			device.close();
		}
	}

	private class SamsTransmitter {

		private MidiDevice device;

		private Receiver receiver;

		public SamsTransmitter(Sams sam) throws MidiUnavailableException {
			this.device = DevicePool.instance().getMidiDevice(sam.getDevice(),
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

		public void transmit(MidiMessage message, long timeStamp) {
			receiver.send(message, timeStamp);
		}
	}

	public Sams getSAM() {
		return sams;
	}

	public void setSAM(Sams sam) {
		if (sam == null) {
			throw new IllegalArgumentException("must not be null");
		}

		this.sams = sam;
	}

	private void magnetOn(ShortMessage message) {
		transmitter.transmit(message, -1);

		delayMagnetOff(message);
	}

	private void delayMagnetOff(final ShortMessage message) {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				magnetOff((ShortMessage) message);
			}
		};
	}

	private void magnetOff(ShortMessage message) {
		transmitter.transmit(sams.reverse(message), -1);
	}
}