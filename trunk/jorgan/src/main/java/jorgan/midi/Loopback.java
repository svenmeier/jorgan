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
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 * A <code>MidiDevice</code> serving as a loopback.
 */
public class Loopback implements MidiDevice {

	/**
	 * The info of this midiDevice.
	 */
	private Info info;

	/**
	 * Is this device open.
	 */
	private boolean open;

	private boolean allowReceivers;

	private boolean allowTransmitters;

	/**
	 * The created transmitters.
	 */
	private List<LoopbackTransmitter> transmitters = new ArrayList<LoopbackTransmitter>();

	/**
	 * The created receivers.
	 */
	private List<LoopbackReceiver> receivers = new ArrayList<LoopbackReceiver>();

	/**
	 * Create a virtual loopback.
	 * 
	 * @param info
	 *            info of this loopback
	 * @param allowReceivers
	 *            are receivers allowed
	 * @param allowTransmitters
	 *            are transmitters allowed
	 */
	public Loopback(MidiDevice.Info info, boolean allowReceivers,
			boolean allowTransmitters) {
		this.info = info;

		this.allowReceivers = allowReceivers;
		this.allowTransmitters = allowTransmitters;
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
		return allowReceivers ? -1 : 0;
	}

	/**
	 * This device supports unlimited transmitters.
	 * 
	 * @return always <code>-1</code>
	 */
	public int getMaxTransmitters() {
		return allowTransmitters ? -1 : 0;
	}

	/**
	 * @return receivers
	 * @since 1.5
	 */
	public List<Receiver> getReceivers() {
		return new ArrayList<Receiver>(receivers);
	}

	/**
	 * @return transmitters
	 * @since 1.5
	 */
	public List<Transmitter> getTransmitters() {
		return new ArrayList<Transmitter>(transmitters);
	}

	public long getMicrosecondPosition() {
		return -1;
	}

	public void open() throws MidiUnavailableException {
		if (isOpen()) {
			return;
		}

		try {
			openImpl();
		} finally {
			close();
		}

		open = true;
	}

	protected synchronized void openImpl() throws MidiUnavailableException {
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
		if (!allowReceivers) {
			throw new MidiUnavailableException("no receivers allowed");
		}

		return new LoopbackReceiver();
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
		if (!allowTransmitters) {
			throw new MidiUnavailableException("no transmitters allowed");
		}

		return new LoopbackTransmitter();
	}

	/**
	 * Default implementation delegates to {@link #loopOut(MidiMessage, long)}.
	 */
	protected void onLoopIn(MidiMessage message) {
		loopOut(message);
	}

	protected synchronized void loopOut(MidiMessage message) {
		if (isOpen()) {
			for (int r = 0; r < transmitters.size(); r++) {
				LoopbackTransmitter transmitter = transmitters.get(r);

				transmitter.transmit(message);
			}
		}
	}

	/**
	 * Close this device.
	 */
	public void close() {
		if (open) {
			open = false;
			closeImpl();
		}
	}

	protected synchronized void closeImpl() {
		// important: work on copy of transmitter list as closing of
		// transmitter removes it from list
		Iterator<Transmitter> transmitters = getTransmitters().iterator();
		while (transmitters.hasNext()) {
			transmitters.next().close();
		}

		// important: work on copy of receiver list as closing of
		// receiver removes it from list
		Iterator<Receiver> receivers = getReceivers().iterator();
		while (receivers.hasNext()) {
			receivers.next().close();
		}
	}

	/**
	 * The transmitter implementation handed to clients of this midi device.
	 * 
	 * @see #getTransmitter()
	 */
	private class LoopbackTransmitter implements Transmitter {

		private boolean closed = false;

		/**
		 * The receiver to transmit messages to.
		 */
		private Receiver receiver;

		private LoopbackTransmitter() {
			transmitters.add(this);
		}

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
		 * Transmit the given message if a receiver is set.
		 * 
		 * @param message
		 *            message to transmit
		 */
		protected void transmit(MidiMessage message) {
			if (receiver != null) {
				receiver.send(message, -1);
			}
		}

		/**
		 * Close this transmitter.
		 */
		public void close() {
			synchronized (Loopback.this) {
				if (closed) {
					throw new IllegalStateException("already closed");
				}
				closed = true;

				transmitters.remove(this);
			}
		}
	}

	/**
	 * The receiver implementation handed to clients of this midi device.
	 * 
	 * @see #getReceiver()
	 */
	private class LoopbackReceiver implements Receiver {

		private boolean closed = false;

		protected LoopbackReceiver() {
			receivers.add(this);
		}

		/**
		 * Send the given message to all registered receivers.
		 * 
		 * @param message
		 *            message to send
		 * @param timeStamp
		 *            timeStamp
		 */
		public void send(MidiMessage message, long timeStamp) {
			if (closed) {
				throw new IllegalStateException("already closed");
			}

			onLoopIn(message);
		}

		/**
		 * Close this transmitter.
		 */
		public void close() {
			synchronized (Loopback.this) {
				if (closed) {
					throw new IllegalStateException("already closed");
				}
				closed = true;

				receivers.remove(this);
			}
		}
	}
}