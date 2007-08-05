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
			MidiDevice[] devices = getMidiDevices(out);

			for (int d = 0; d < devices.length; d++) {
				if (name.equals(devices[d].getDeviceInfo().getName())) {
					sharedDevice = new SharedDevice(devices[d]);

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
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();

		List<MidiDevice> devices = new ArrayList<MidiDevice>();

		for (int i = 0; i < infos.length; i++) {
			MidiDevice.Info info = infos[i];

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

		String[] names = null;
		try {
			MidiDevice[] devices = getMidiDevices(out);

			names = new String[devices.length];

			for (int d = 0; d < devices.length; d++) {
				names[d] = devices[d].getDeviceInfo().getName();
			}
		} catch (MidiUnavailableException ex) {
			names = new String[0];
		}

		return names;
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

		public void close() {
			assertOpen();

			super.close();

			open = false;
		}

		public void open() throws MidiUnavailableException {
			assertClosed();

			super.open();

			open = true;
		}

		public boolean isOpen() {
			return open;
		}

		public Receiver getReceiver() throws MidiUnavailableException {
			assertOpen();
			return super.getReceiver();
		}

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

		public boolean equals(Object obj) {

			if (!(obj instanceof DeviceKey)) {
				return false;
			}

			DeviceKey key = (DeviceKey) obj;

			return key.deviceName.equals(deviceName) && key.out == out;
		}

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

		public void open() throws MidiUnavailableException {
			if (openCount == 0) {
				super.open();

				for (int l = 0; l < inLoggers.size(); l++) {
					inLoggers.get(l).opened();
				}
				for (int l = 0; l < outLoggers.size(); l++) {
					outLoggers.get(l).opened();
				}
			}
			openCount++;
		}

		public Receiver getReceiver() throws MidiUnavailableException {
			return new ReceiverWrapper(super.getReceiver()) {
				public void send(MidiMessage message, long timeStamp) {
					super.send(message, timeStamp);

					for (int l = 0; l < outLoggers.size(); l++) {
						outLoggers.get(l).log(message);
					}
				}
			};
		}

		public Transmitter getTransmitter() throws MidiUnavailableException {
			return new TransmitterWrapper(super.getTransmitter()) {

				protected void send(MidiMessage message, long timeStamp) {
					super.send(message, timeStamp);

					// TODO if multiple transmitters are get, we will log multiple
					// times :(
					for (int l = 0; l < inLoggers.size(); l++) {
						inLoggers.get(l).log(message);
					}
				}
			};
		}

		public void close() {
			openCount--;

			if (openCount == 0) {
				super.close();

				for (int l = 0; l < inLoggers.size(); l++) {
					inLoggers.get(l).closed();
				}
				for (int l = 0; l < outLoggers.size(); l++) {
					outLoggers.get(l).closed();
				}
			}
		}
	}
}