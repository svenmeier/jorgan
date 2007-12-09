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
package jorgan.midi;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 * Helper for lookup of devices.
 */
public class DevicePool {

	private static List<PooledDevice> devices = new ArrayList<PooledDevice>();

	public static final int IN = 0;

	public static final int OUT = 1;

	private DevicePool() {
	}

	/**
	 * Get a device by name.
	 * 
	 * @param name
	 *            name of device to get
	 * @param direction
	 *            if <code>true</code> device should support midi-out,
	 *            otherwise it should support midi-in.
	 * @return the named device
	 * @throws MidiUnavailableException
	 */
	public static MidiDevice getMidiDevice(String name, int direction)
			throws MidiUnavailableException {

		PooledDevice sharedDevice = getPooledDevice(name, direction);
		if (sharedDevice == null) {
			initDevicePool(direction);

			sharedDevice = getPooledDevice(name, direction);
			if (sharedDevice == null) {
				throw new MidiUnavailableException(name);
			}
		}

		return new ProxyDevice(sharedDevice);
	}

	private static PooledDevice getPooledDevice(String name, int out) {

		for (PooledDevice device : devices) {
			if (device.name().equals(name)) {
				if (device.supports(out)) {
					return device;
				}
			}
		}
		return null;
	}

	/**
	 * Initialize the pool of devices for the given direction.
	 * 
	 * @param direction
	 *            {@link IN} or {@link OUT}
	 */
	private static void initDevicePool(int direction) {
		for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
			PooledDevice device = getPooledDevice(info.getName(), direction);
			if (device == null) {
				try {
					device = new PooledDevice(info.getName(), MidiSystem
							.getMidiDevice(info));
				} catch (MidiUnavailableException skipDevice) {
					continue;
				}
				if (device.supports(direction)) {
					devices.add(device);
				}
			}
		}
	}

	/**
	 * Get the name of all devices that support midi-out or midi-in.
	 * 
	 * @param direction
	 *            direction of midi, {@link IN} or {@link OUT}
	 * @return list of device names
	 */
	public static String[] getMidiDeviceNames(int direction) {
		initDevicePool(direction);

		List<String> names = new ArrayList<String>();
		for (PooledDevice device : devices) {
			if (device.supports(direction)) {
				names.add(device.name);
			}
		}

		return names.toArray(new String[names.size()]);
	}

	public static boolean addLogger(MidiLogger logger, String name,
			int direction) throws MidiUnavailableException {
		PooledDevice device = getPooledDevice(name, direction);

		device.addLogger(logger, direction);

		return device.isOpen();
	}

	public static void removeLogger(MidiLogger logger, String name,
			int direction) throws MidiUnavailableException {
		PooledDevice device = getPooledDevice(name, direction);

		device.removeLogger(logger, direction);
	}

	private static class ProxyDevice extends DeviceWrapper {
		private boolean open = false;

		private ProxyDevice(PooledDevice device) {
			super(device);
		}

		@Override
		public void close() {
			assertOpen();

			super.close();

			open = false;
		}

		@Override
		public void open() throws MidiUnavailableException {
			assertClosed();

			super.open();

			open = true;
		}

		@Override
		public boolean isOpen() {
			return open;
		}

		@Override
		public Receiver getReceiver() throws MidiUnavailableException {
			assertOpen();
			return super.getReceiver();
		}

		@Override
		public Transmitter getTransmitter() throws MidiUnavailableException {
			assertOpen();
			return super.getTransmitter();
		}

		protected void assertOpen() throws IllegalStateException {
			if (!open) {
				throw new IllegalStateException("not open");
			}
		}

		protected void assertClosed() throws IllegalStateException {
			if (open) {
				throw new IllegalStateException("open");
			}
		}
	}

	/**
	 * A pooled device.
	 */
	private static class PooledDevice extends DeviceWrapper {

		private List<MidiLogger> inLoggers = new ArrayList<MidiLogger>();

		private List<MidiLogger> outLoggers = new ArrayList<MidiLogger>();

		private int openCount;

		private boolean out;

		private boolean in;

		private String name;

		public PooledDevice(String name, MidiDevice device) {
			super(device);

			this.name = name;

			this.out = device.getMaxReceivers() != 0;
			this.in = device.getMaxTransmitters() != 0;
		}

		public String name() {
			return name;
		}

		public boolean supports(int direction) {
			return this.out && direction == OUT || this.in && direction == IN;
		}

		public void addLogger(MidiLogger logger, int direction) {
			if (direction == IN) {
				inLoggers.add(logger);
			} else if (direction == OUT) {
				outLoggers.add(logger);
			} else {
				throw new IllegalArgumentException("direction " + direction);
			}
		}

		public void removeLogger(MidiLogger logger, int direction) {
			if (direction == IN) {
				inLoggers.remove(logger);
			} else if (direction == OUT) {
				outLoggers.remove(logger);
			} else {
				throw new IllegalArgumentException("direction " + direction);
			}
		}

		@Override
		public void open() throws MidiUnavailableException {
			if (openCount == 0) {
				super.open();

				for (MidiLogger inLogger : inLoggers) {
					inLogger.opened();
				}
				for (MidiLogger outLogger : outLoggers) {
					outLogger.opened();
				}
			}
			openCount++;
		}

		@Override
		public Receiver getReceiver() throws MidiUnavailableException {
			return new ReceiverWrapper(super.getReceiver()) {
				@Override
				public void send(MidiMessage message, long timeStamp) {
					super.send(message, timeStamp);

					for (MidiLogger outLogger : outLoggers) {
						outLogger.log(message);
					}
				}
			};
		}

		@Override
		public Transmitter getTransmitter() throws MidiUnavailableException {
			return new TransmitterWrapper(super.getTransmitter()) {

				@Override
				protected void send(MidiMessage message, long timeStamp) {
					super.send(message, timeStamp);

					// TODO if multiple transmitters are get, we will log
					// multiple times :(
					for (MidiLogger inLogger : inLoggers) {
						inLogger.log(message);
					}
				}
			};
		}

		@Override
		public void close() {
			openCount--;

			if (openCount == 0) {
				super.close();

				for (MidiLogger inLogger : inLoggers) {
					inLogger.closed();
				}
				for (MidiLogger outLogger : outLoggers) {
					outLogger.closed();
				}
			}
		}
	}
}