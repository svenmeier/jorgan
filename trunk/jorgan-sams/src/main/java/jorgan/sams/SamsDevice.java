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
import javax.sound.midi.MidiUnavailableException;
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

	private Sams sam = new Sams("jOrgan Keyboard");

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
			new SAMReceiver(sam);
		} catch (MidiUnavailableException ex) {
			close();

			throw ex;
		}
	}

	/**
	 * One receiver used for each input device.
	 */
	private class SAMReceiver extends LoopbackReceiver {

		/**
		 * The input device to receive messages from.
		 */
		private MidiDevice device;

		/**
		 * The transmitter of the input device.
		 */
		private Transmitter transmitter;

		/**
		 * Create a new receiver for the given input.
		 * 
		 * @param device
		 *            name of device to create receiver for
		 * @throws MidiUnavailableException
		 *             if input device is unavailable
		 */
		public SAMReceiver(Sams sam) throws MidiUnavailableException {

			// Important: assure successfull opening of MIDI device
			// before storing reference in instance variable
			MidiDevice toBeOpened = DevicePool.instance().getMidiDevice(
					sam.getDevice(), Direction.OUT);
			toBeOpened.open();
			this.device = toBeOpened;

			transmitter = this.device.getTransmitter();
			transmitter.setReceiver(this);
		}

		/**
		 * Closing this receiver also closes the device transmitted to.
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

	public Sams getSAM() {
		return sam;
	}

	public void setSAM(Sams sam) {
		if (sam == null) {
			throw new IllegalArgumentException("must not be null");
		}

		this.sam = sam;
	}
}