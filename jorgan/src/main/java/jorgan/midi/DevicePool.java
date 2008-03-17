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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 * Helper for lookup of devices.
 */
public class DevicePool {

	private static final DevicePool instance = new DevicePool();

	private Map<Direction, List<PooledDevice>> devices = new HashMap<Direction, List<PooledDevice>>();

	private DevicePool() {
		devices.put(Direction.IN, new ArrayList<PooledDevice>());
		devices.put(Direction.OUT, new ArrayList<PooledDevice>());
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
	public MidiDevice getMidiDevice(String name, Direction direction)
			throws MidiUnavailableException {

		refreshDevices(direction);

		PooledDevice pooledDevice = getDevice(devices.get(direction), name);
		if (pooledDevice == null) {
			throw new MidiUnavailableException(name);
		}

		return new ProxyDevice(pooledDevice);
	}

	private PooledDevice getDevice(List<PooledDevice> devices, String name) {
		for (PooledDevice device : devices) {
			if (device.name().equals(name)) {
				return device;
			}
		}
		return null;
	}

	/**
	 * Refresh the pool of devices for the given direction.
	 * 
	 * @param direction
	 *            {@link IN} or {@link OUT}
	 */
	private synchronized void refreshDevices(Direction direction) {

		List<PooledDevice> oldDevices = devices.get(direction);
		List<PooledDevice> newDevices = new ArrayList<PooledDevice>();

		for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
			PooledDevice device = getDevice(newDevices, info.getName());
			if (device == null) {
				device = getDevice(oldDevices, info.getName());
				if (device == null) {
					try {
						device = new PooledDevice(info.getName(), MidiSystem
								.getMidiDevice(info));
					} catch (MidiUnavailableException skipDevice) {
						continue;
					}
					if (!device.supports(direction)) {
						continue;
					}
				}
				newDevices.add(device);
			}
		}

		devices.put(direction, newDevices);
	}

	/**
	 * Get the name of all devices that support midi-out or midi-in.
	 * 
	 * @param direction
	 *            direction of midi, {@link IN} or {@link OUT}
	 * @return list of device names
	 */
	public String[] getMidiDeviceNames(Direction direction) {
		refreshDevices(direction);

		List<String> names = new ArrayList<String>();
		for (PooledDevice device : devices.get(direction)) {
			names.add(device.name);
		}

		return names.toArray(new String[names.size()]);
	}

	private static class ProxyDevice extends DeviceWrapper {
		private boolean open = false;

		private ProxyDevice(MidiDevice device) {
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

		public boolean supports(Direction direction) {
			return this.out && direction == Direction.OUT || this.in
					&& direction == Direction.IN;
		}

		@Override
		public void open() throws MidiUnavailableException {
			if (openCount == 0) {
				super.open();
			}
			openCount++;
		}

		@Override
		public void close() {
			openCount--;

			if (openCount == 0) {
				super.close();
			}
		}
	}

	public static DevicePool instance() {
		return instance;
	}
}