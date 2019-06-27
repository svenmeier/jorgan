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
import java.util.logging.Logger;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import bias.Configuration;

/**
 * Helper for lookup of devices.
 */
public class DevicePool {

	private static final Logger log = Logger.getLogger(DevicePool.class
			.getName());

	private static Configuration configuration = Configuration.getRoot().get(
			DevicePool.class);

	private static final DevicePool instance = new DevicePool();

	private DeviceMap devices = new DeviceMap();

	private boolean cache = true;

	private boolean enumerate = false;

	private DevicePool() {
		configuration.read(this);
	}

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

		PooledDevice pooledDevice = devices.get(name, direction);
		if (pooledDevice == null) {
			throw new MidiUnavailableException(name);
		}

		return new ProxyDevice(pooledDevice);
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
		for (PooledDevice device : devices.filter(direction)) {
			names.add(device.name);
		}

		return names.toArray(new String[names.size()]);
	}

	/**
	 * Refresh all pooled devices.
	 */
	private void refreshDevices() {

		if (cache && !this.devices.isEmpty()) {
			return;
		}

		DeviceMap oldDevices = this.devices;

		this.devices = new DeviceMap();

		int index = 0;
		for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
			try {
				DeviceAnalyser analyser = new DeviceAnalyser(info, index);

				analyser.extract(this.devices, oldDevices, Direction.IN);
				analyser.extract(this.devices, oldDevices, Direction.OUT);

				index++;
			} catch (MidiUnavailableException ex) {
				log.info("failed device '" + info.getName() + "'");
			}
		}
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

			TransmitterWrapper transmitter = new TransmitterWrapper(
					super.getTransmitter()) {
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

		public final boolean in;

		public final boolean out;

		private int openCount;

		public PooledDevice(String name, boolean in, boolean out,
				MidiDevice device) throws MidiUnavailableException {
			super(device);

			this.name = name;
			this.in = in;
			this.out = out;
		}

		public boolean supports(Direction direction) {
			if (direction == Direction.IN) {
				return in;
			} else {
				return out;
			}
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

	private class DeviceMap {

		private List<PooledDevice> devices = new ArrayList<PooledDevice>();

		public void add(PooledDevice device) {
			this.devices.add(device);
		}

		public boolean isEmpty() {
			return devices.isEmpty();
		}

		public PooledDevice get(String name, Direction direction) {
			for (PooledDevice device : devices) {
				if (device.name.equals(name) && device.supports(direction)) {
					return device;
				}
			}
			return null;
		}

		public List<PooledDevice> filter(Direction direction) {
			List<PooledDevice> filtered = new ArrayList<PooledDevice>();
			for (PooledDevice device : devices) {
				if (device.supports(direction)) {
					filtered.add(device);
				}
			}
			return filtered;
		}
	}

	private class DeviceAnalyser {

		private Info info;

		private int index;

		private MidiDevice device;

		private PooledDevice pooledDevice;

		public DeviceAnalyser(Info info, int index) {
			this.info = info;
			this.index = index;
		}

		public void extract(DeviceMap newDevices, DeviceMap oldDevices,
				Direction direction) throws MidiUnavailableException {
			String name = createName();

			if (newDevices.get(name, direction) == null) {
				PooledDevice old = oldDevices.get(name, direction);
				if (old != null) {
					newDevices.add(old);
				} else {
					if (supports(direction)) {
						newDevices.add(pooledDevice());
					}
				}
			}
		}

		private boolean supports(Direction direction)
				throws MidiUnavailableException {
			if (direction == Direction.IN) {
				return device().getMaxTransmitters() != 0;
			} else {
				return device().getMaxReceivers() != 0;
			}
		}

		private MidiDevice device() throws MidiUnavailableException {
			if (this.device == null) {
				this.device = MidiSystem.getMidiDevice(info);
			}

			return device;
		}

		private PooledDevice pooledDevice() throws MidiUnavailableException {
			if (this.pooledDevice == null) {
				this.pooledDevice = new PooledDevice(createName(),
						supports(Direction.IN), supports(Direction.OUT),
						device());
			}
			return this.pooledDevice;
		}

		private String createName() {
			String name = this.info.getName();
			if (enumerate) {
				name += " #" + index;
			}
			return name;
		}
	}
}