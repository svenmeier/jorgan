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
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 * Helper for lookup of devices.
 */
public class DevicePool {

	private static final DevicePool instance = new DevicePool();

	private List<PooledDevice> devices = new ArrayList<PooledDevice>();

	/**
	 * Get a device by name.
	 * 
	 * @param name
	 *            name of device to get
	 * @param direction
	 *            if <code>true</code> device should support midi-out, otherwise
	 *            it should support midi-in.
	 * @return the named device
	 * @throws MidiUnavailableException
	 */
	public synchronized MidiDevice getMidiDevice(String name,
			Direction direction) throws MidiUnavailableException {

		refreshDevices();

		PooledDevice pooledDevice = getDevice(name, direction);
		if (pooledDevice == null || !pooledDevice.supports(direction)) {
			throw new MidiUnavailableException(name);
		}

		return new ProxyDevice(pooledDevice);
	}

	private PooledDevice getDevice(String name, Direction direction) {
		for (PooledDevice device : devices) {
			if (device.name().equals(name) && device.supports(direction)) {
				return device;
			}
		}
		return null;
	}

	/**
	 * Refresh the pool of devices.
	 */
	private void refreshDevices() {

		List<PooledDevice> oldDevices = devices;
		devices = new ArrayList<PooledDevice>();

		devices: for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
			try {
				PooledDevice device = new PooledDevice(info);
				if (!devices.contains(device)) {
					for (PooledDevice oldDevice : oldDevices) {
						if (oldDevice.equals(device)) {
							devices.add(oldDevice);
							continue devices;
						}
					}
					devices.add(device);
				}
			} catch (MidiUnavailableException skip) {
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
	public synchronized String[] getMidiDeviceNames(Direction direction) {
		refreshDevices();

		List<String> names = new ArrayList<String>();
		for (PooledDevice device : devices) {
			if (device.supports(direction)) {
				names.add(device.name);
			}
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

		public PooledDevice(Info info) throws MidiUnavailableException {
			super(MidiSystem.getMidiDevice(info));

			this.name = info.getName();

			this.out = getMaxReceivers() != 0;
			this.in = getMaxTransmitters() != 0;
		}

		public boolean equals(Object object) {
			if (!(object instanceof PooledDevice)) {
				return false;
			}

			PooledDevice device = (PooledDevice) object;
			return this.name.equals(device.name) && this.out == device.out
					&& this.in == device.in;
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