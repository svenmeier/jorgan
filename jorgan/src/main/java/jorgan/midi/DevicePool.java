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
import javax.sound.midi.MidiDevice.Info;

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

		refreshDevice(name);

		PooledDevice pooledDevice = getDevice(name, direction);
		if (pooledDevice == null) {
			throw new MidiUnavailableException(name);
		}

		return new ProxyDevice(pooledDevice);
	}

	private PooledDevice getDevice(String name, Direction direction) {
		for (PooledDevice device : devices) {
			if (device.name.equals(name) && device.direction == direction) {
				return device;
			}
		}
		return null;
	}

	/**
	 * Refresh pooled devices for the given name.
	 */
	private void refreshDevice(String name) {

		List<PooledDevice> newDevices = new ArrayList<PooledDevice>();

		for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
			if (name.equals(info.getName())) {
				PooledDevice oldIn = getDevice(name, Direction.IN);
				if (oldIn != null) {
					newDevices.add(oldIn);
				}

				PooledDevice oldOut = getDevice(name, Direction.OUT);
				if (oldOut != null) {
					newDevices.add(oldOut);
				}

				if (oldIn == null && oldOut == null) {
					// seeing info for the first time

					try {
						MidiDevice device = MidiSystem.getMidiDevice(info);
						if (device.getMaxReceivers() != 0) {
							newDevices.add(new PooledDevice(name,
									Direction.OUT, device));
						}
						if (device.getMaxTransmitters() != 0) {
							newDevices.add(new PooledDevice(name, Direction.IN,
									device));
						}
					} catch (MidiUnavailableException skip) {
					}
				}
			}
		}

		this.devices.addAll(newDevices);
	}

	/**
	 * Refresh all pooled devices.
	 */
	private void refreshDevices() {

		List<PooledDevice> newDevices = new ArrayList<PooledDevice>();

		for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
			String name = info.getName();

			PooledDevice oldIn = getDevice(name, Direction.IN);
			if (oldIn != null) {
				newDevices.add(oldIn);
			}

			PooledDevice oldOut = getDevice(name, Direction.OUT);
			if (oldOut != null) {
				newDevices.add(oldOut);
			}

			if (oldIn == null && oldOut == null) {
				// seeing info for the first time

				try {
					MidiDevice device = MidiSystem.getMidiDevice(info);
					if (device.getMaxReceivers() != 0) {
						newDevices.add(new PooledDevice(name, Direction.OUT,
								device));
					}
					if (device.getMaxTransmitters() != 0) {
						newDevices.add(new PooledDevice(name, Direction.IN,
								device));
					}
				} catch (MidiUnavailableException skip) {
				}
			}
		}

		this.devices = newDevices;
	}

	/**
	 * Get the name of all devices that support the given {@link Direction}.
	 * 
	 * @param direction
	 *            direction of midi, {@link IN} or {@link OUT}
	 * @return list of device names
	 */
	public synchronized String[] getMidiDeviceNames(Direction direction) {
		refreshDevices();

		List<String> names = new ArrayList<String>();
		for (PooledDevice device : devices) {
			if (device.direction == direction) {
				names.add(device.name);
			}
		}

		return names.toArray(new String[names.size()]);
	}

	private static class ProxyDevice extends DeviceWrapper {
		private boolean open = false;

		private List<Receiver> receivers = new ArrayList<Receiver>();

		private List<Transmitter> transmitters = new ArrayList<Transmitter>();

		private ProxyDevice(MidiDevice device) {
			super(device);
		}

		@Override
		public void close() {
			assertOpen();

			for (Receiver receiver : new ArrayList<Receiver>(receivers)) {
				receiver.close();
			}

			for (Transmitter transmitter : new ArrayList<Transmitter>(
					transmitters)) {
				transmitter.close();
			}

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

			ReceiverWrapper receiver = new ReceiverWrapper(super.getReceiver()) {
				@Override
				public void close() {
					super.close();

					receivers.remove(this);
				}
			};
			receivers.add(receiver);
			return receiver;
		}

		@Override
		public Transmitter getTransmitter() throws MidiUnavailableException {
			assertOpen();

			TransmitterWrapper transmitter = new TransmitterWrapper(super
					.getTransmitter()) {
				@Override
				public void close() {
					super.close();

					transmitters.remove(this);
				}
			};
			transmitters.add(transmitter);
			return transmitter;
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
	 * A pooled device for the name of a group of {@link Info}s and a
	 * {@link Direction}.
	 */
	private static class PooledDevice extends DeviceWrapper {

		public final String name;

		public final Direction direction;

		private int openCount;

		public PooledDevice(String name, Direction direction, MidiDevice device)
				throws MidiUnavailableException {
			super(device);

			this.name = name;
			this.direction = direction;
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