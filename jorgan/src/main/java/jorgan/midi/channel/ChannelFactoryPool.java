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
package jorgan.midi.channel;

import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;

import jorgan.midi.DevicePool;

/**
 * A pool of {@link ChannelFactory}s.
 */
public abstract class ChannelFactoryPool {

	private static Map<String, PooledChannelFactory> factories = new HashMap<String, PooledChannelFactory>();

	/**
	 * Get the instance for a MIDI device.
	 * 
	 * @param deviceName
	 *            the name of device to get a pool for
	 * @return channel pool
	 * @throws MidiUnavailableException
	 *             if device is not available
	 */
	public static ChannelFactory getPool(String deviceName)
			throws MidiUnavailableException {
		PooledChannelFactory factory = factories.get(deviceName);
		if (factory == null) {
			factory = new PooledChannelFactory(deviceName);

			factories.put(deviceName, factory);
		}

		return new ProxyChannelFactory(factory);
	}

	private static class PooledChannelFactory implements ChannelFactory {

		private String deviceName;

		/**
		 * Device to use.
		 */
		private MidiDevice device;

		/**
		 * Receiver to use by this factory.
		 */
		private Receiver receiver;

		/**
		 * Count of openings.
		 */
		private int opened = 0;

		/**
		 * Created channels.
		 */
		private ChannelImpl[] channels = new ChannelImpl[16];

		/**
		 * Use {@link #instance(String)}.
		 */
		protected PooledChannelFactory(String deviceName)
				throws MidiUnavailableException {

			this.deviceName = deviceName;

			this.device = DevicePool.getMidiDevice(deviceName, DevicePool.OUT);
		}

		public String getDeviceName() {
			return deviceName;
		}

		/**
		 * Open this pool of channels. <br>
		 * Opens the MIDI device on first call.
		 * 
		 * @throws MidiUnavailableException
		 *             if device is not available
		 */
		public void open() throws MidiUnavailableException {
			if (opened == 0) {
				device.open();

				receiver = device.getReceiver();
			}
			opened++;
		}

		/**
		 * Create a channel.
		 * 
		 * @return created channel or <code>null</code> if no channel is
		 *         available
		 */
		public Channel createChannel(ChannelFilter filter) {

			if (opened == 0) {
				throw new IllegalStateException("not opened");
			}

			for (int c = 0; c < channels.length; c++) {
				if (channels[c] == null && filter.accept(c)) {
					return new ChannelImpl(c);
				}
			}

			return null;
		}

		/**
		 * Close this pool of channels.
		 */
		public void close() {
			opened--;

			if (opened == 0) {
				receiver.close();
				receiver = null;

				for (int c = 0; c < channels.length; c++) {
					channels[c] = null;
				}

				device.close();
			}
		}

		/**
		 * A channel implementation.
		 */
		private class ChannelImpl implements Channel {

			/**
			 * The MIDI channel of this sound.
			 */
			private int channel;

			/**
			 * Create a channel.
			 * 
			 * @param channel
			 *            the channel to use
			 */
			public ChannelImpl(int channel) {
				this.channel = channel;

				channels[channel] = this;
			}

			public int getNumber() {
				return channel + 1;
			}

			/**
			 * Release.
			 */
			public void release() {
				channels[channel] = null;
			}

			/**
			 * Send a message.
			 * 
			 * @param message
			 *            message
			 */
			public void sendMessage(ShortMessage message) {
				if (receiver != null) {
					try {
						message.setMessage(message.getCommand(), channel,
								message.getData1(), message.getData2());
					} catch (InvalidMidiDataException ex) {
						throw new Error(ex);
					}
					receiver.send(message, -1);
				}
			}
		}
	}

	private static class ProxyChannelFactory implements ChannelFactory {
		
		private boolean open = false;

		private ChannelFactory factory;

		protected ProxyChannelFactory(ChannelFactory pool) {

			this.factory = pool;
		}

		public String getDeviceName() {
			return factory.getDeviceName();
		}

		public void open() throws MidiUnavailableException {
			assertClosed();

			factory.open();

			open = true;
		}

		/**
		 * Create a channel.
		 * 
		 * @return created channel or <code>null</code> if no channel is
		 *         available
		 */
		public Channel createChannel(ChannelFilter filter) {
			assertOpen();

			return factory.createChannel(filter);
		}

		/**
		 * Close this pool of channels.
		 */
		public void close() {
			assertOpen();

			factory.close();

			open = false;
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
}