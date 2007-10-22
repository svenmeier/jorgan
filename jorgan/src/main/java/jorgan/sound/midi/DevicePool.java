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
package jorgan.sound.midi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private static Map<DeviceKey, SharedDevice> sharedDevices = new HashMap<DeviceKey, SharedDevice>();

	private DevicePool() {
	}

	/**
	 * Get a device by name.
	 * 
	 * @param name
	 *            name of device to get
	 * @param out
	 *            if <code>true</code> device should support midi-out,
	 *            otherwise it should support midi-in.
	 * @return the named device
	 * @throws MidiUnavailableException
	 */
	public static MidiDevice getMidiDevice(String name, boolean out)
			throws MidiUnavailableException {

		SharedDevice sharedDevice = getSharedDevice(name, out);

		return new ProxyDevice(sharedDevice);
	}

	private static SharedDevice getSharedDevice(String name, boolean out)
			throws MidiUnavailableException {

		DeviceKey key = new DeviceKey(name, out);

		SharedDevice sharedDevice = sharedDevices.get(key);
		if (sharedDevice == null) {
			for (MidiDevice device : getMidiDevices(out)) {
				if (name.equals(device.getDeviceInfo().getName())) {
					sharedDevice = new SharedDevice(device);

					sharedDevices.put(key, sharedDevice);

					break;
				}
			}
		}

		if (sharedDevice == null) {
			throw new MidiUnavailableException(name);
		}

		return sharedDevice;
	}

	/**
	 * Get all devices that support midi-out or midi-in.
	 * 
	 * @param out
	 *            if <code>true</code> devices should support midi-out,
	 *            otherwise they should support midi-in.
	 * @return list of devices
	 * @throws MidiUnavailableException
	 */
	private static MidiDevice[] getMidiDevices(boolean out)
			throws MidiUnavailableException {

		List<MidiDevice> devices = new ArrayList<MidiDevice>();

		for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {

			MidiDevice device = MidiSystem.getMidiDevice(info);
			if (out && device.getMaxReceivers() != 0 || !out
					&& device.getMaxTransmitters() != 0) {
				devices.add(device);
			}
		}

		return devices.toArray(new MidiDevice[0]);
	}

	/**
	 * Get the name of all devices that support midi-out or midi-in.
	 * 
	 * @param out
	 *            if <code>true</code> devices should support midi-out,
	 *            otherwise they should support midi-in.
	 * @return list of device names
	 */
	public static String[] getMidiDeviceNames(boolean out) {

		try {
			List<String> names = new ArrayList<String>();
			for (MidiDevice device : getMidiDevices(out)) {
				String name = device.getDeviceInfo().getName();
				if (!names.contains(name)) {
					names.add(name);
				}
			}

			return names.toArray(new String[names.size()]);
		} catch (MidiUnavailableException ex) {
			return new String[0];
		}
	}

	public static boolean addLogger(MidiLogger logger, String name, boolean out)
			throws MidiUnavailableException {
		SharedDevice device = getSharedDevice(name, out);

		if (out) {
			device.outLoggers.add(logger);
		} else {
			device.inLoggers.add(logger);
		}

		return device.isOpen();
	}

	public static void removeLogger(MidiLogger logger, String name, boolean out)
			throws MidiUnavailableException {
		SharedDevice device = getSharedDevice(name, out);

		if (out) {
			device.outLoggers.remove(logger);
		} else {
			device.inLoggers.remove(logger);
		}
	}

	private static class ProxyDevice extends DeviceWrapper {
		private boolean open = false;

		private ProxyDevice(SharedDevice device) {
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

	private static class DeviceKey {

		private String deviceName;

		private boolean out;

		public DeviceKey(String deviceName, boolean out) {
			this.deviceName = deviceName;
			this.out = out;
		}

		@Override
		public boolean equals(Object obj) {

			if (!(obj instanceof DeviceKey)) {
				return false;
			}

			DeviceKey key = (DeviceKey) obj;

			return key.deviceName.equals(deviceName) && key.out == out;
		}

		@Override
		public int hashCode() {
			return deviceName.hashCode();
		}
	}

	private static class SharedDevice extends DeviceWrapper {

		private List<MidiLogger> inLoggers = new ArrayList<MidiLogger>();

		private List<MidiLogger> outLoggers = new ArrayList<MidiLogger>();

		private int openCount;

		public SharedDevice(MidiDevice device) {
			super(device);
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