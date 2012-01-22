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

import java.util.Collections;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 */
public class NullDevice implements MidiDevice {

	/**
	 * The info of this midiDevice.
	 */
	private Info info;

	/**
	 * Is this device open.
	 */
	private boolean open;

	public NullDevice(MidiDevice.Info info) {
		this.info = info;
	}

	/**
	 * Get the info about this device.
	 */
	public MidiDevice.Info getDeviceInfo() {
		return info;
	}

	/**
	 * This device supports unlimited receivers.
	 * 
	 * @return always <code>-1</code>
	 */
	public int getMaxReceivers() {
		return -1;
	}

	/**
	 * This device supports unlimited transmitters.
	 * 
	 * @return always <code>-1</code>
	 */
	public int getMaxTransmitters() {
		return -1;
	}

	/**
	 * @return receivers
	 * @since 1.5
	 */
	public List<Receiver> getReceivers() {
		return Collections.emptyList();
	}

	/**
	 * @return transmitters
	 * @since 1.5
	 */
	public List<Transmitter> getTransmitters() {
		return Collections.emptyList();
	}

	public long getMicrosecondPosition() {
		return -1;
	}

	public void open() throws MidiUnavailableException {
		open = true;
	}

	/**
	 * Is this device currently open.
	 * 
	 * @return <code>true</code> if device is open
	 */
	public boolean isOpen() {
		return open;
	}

	/**
	 * Get a new receiver.
	 * 
	 * @throws MidiUnavailableException
	 *             is receivers are not allowed
	 */
	public synchronized Receiver getReceiver() throws MidiUnavailableException {
		if (!isOpen()) {
			throw new IllegalStateException("not open");
		}

		return new NullReceiver();
	}

	/**
	 * Get a new transmitter.
	 * 
	 * @throws MidiUnavailableException
	 *             is transmitters are not allowed
	 */
	public synchronized Transmitter getTransmitter()
			throws MidiUnavailableException {
		if (!isOpen()) {
			throw new IllegalStateException("not open");
		}

		return new NullTransmitter();
	}

	/**
	 * Close this device.
	 */
	public void close() {
		if (open) {
			open = false;
		}
	}

	private class NullTransmitter implements Transmitter {

		private boolean closed = false;

		/**
		 * The receiver to transmit messages to.
		 */
		private Receiver receiver;

		/**
		 * Set the reciver.
		 * 
		 * @param receiver
		 *            receiver
		 */
		public void setReceiver(Receiver receiver) {
			this.receiver = receiver;
		}

		/**
		 * Get the receiver.
		 * 
		 * @return the receiver
		 */
		public Receiver getReceiver() {
			return receiver;
		}

		/**
		 * Close this transmitter.
		 */
		public void close() {
			if (closed) {
				throw new IllegalStateException("already closed");
			}
			closed = true;
		}
	}

	private class NullReceiver implements Receiver {

		private boolean closed = false;

		public void send(MidiMessage message, long timeStamp) {
		}

		/**
		 * Close this transmitter.
		 */
		public void close() {
			if (closed) {
				throw new IllegalStateException("already closed");
			}
			closed = true;
		}
	}
}