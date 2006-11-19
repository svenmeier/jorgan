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

import java.util.*;

import javax.sound.midi.*;

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
	 * The explicit transmitter for the loopback.
	 */
	public final LoopbackTransmitter loopbackTransmitter = new LoopbackTransmitter();

	/**
	 * The explicit receiver for the loopback.
	 */
	public final LoopbackReceiver loopbackReceiver = new LoopbackReceiver();

	/**
	 * The created transmitters.
	 */
	protected List transmitters = new ArrayList();

	/**
	 * The created receivers.
	 */
	protected List receivers = new ArrayList();

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
	public List getReceivers() {
		return new ArrayList(receivers);
	}

	/**
	 * @return transmitters
	 * @since 1.5
	 */	
	public List getTransmitters() {
		return new ArrayList(transmitters);
	}

	public long getMicrosecondPosition() {
		return -1;
	}

	public synchronized void open() throws MidiUnavailableException {
		if (isOpen()) {
			return;
		}
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
		if (!allowReceivers) {
			throw new MidiUnavailableException("no receivers allowed");
		}

		Receiver receiver = new LoopbackReceiver();
		receivers.add(receiver);

		return receiver;
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

		Transmitter transmitter = new LoopbackTransmitter();
		transmitters.add(transmitter);

		return transmitter;
	}

	private synchronized void loopbackMessage(MidiMessage message,
			long timestamp) {
		if (isOpen()) {
			loopbackTransmitter.transmit(message, timestamp);

			for (int r = 0; r < transmitters.size(); r++) {
				LoopbackTransmitter transmitter = (LoopbackTransmitter) transmitters
						.get(r);

				transmitter.transmit(message, timestamp);
			}
		}
	}

	/**
	 * Close this device.
	 */
	public synchronized void close() {
		if (open) {
			open = false;

			for (int t = 0; t < transmitters.size(); t++) {
				((Transmitter) transmitters.get(t)).close();
			}
			transmitters.clear();

			for (int r = 0; r < receivers.size(); r++) {
				((Receiver) receivers.get(r)).close();
			}
			receivers.clear();
		}
	}

	/**
	 * The transmitter implementation handed to clients of this midi device.
	 * 
	 * @see #getTransmitter()
	 */
	protected class LoopbackTransmitter implements Transmitter {

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
		 * Transmit the given message if a receiver is set.
		 * 
		 * @param message
		 *            message to transmit
		 */
		protected void transmit(MidiMessage message, long timeStamp) {
			if (receiver != null) {
				receiver.send(message, timeStamp);
			}
		}

		/**
		 * Close this transmitter.
		 */
		public void close() {
			synchronized(Loopback.this) {
				if (closed) {
					throw new IllegalStateException("already closed");
				}
				
				transmitters.remove(this);
				closed = true;
			}
		}
	}

	/**
	 * The receiver implementation handed to clients of this midi device.
	 * 
	 * @see #getReceiver()
	 */
	protected class LoopbackReceiver implements Receiver {

		private boolean closed = false;
		
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
			
			loopbackMessage(message, timeStamp);
		}

		/**
		 * Close this transmitter.
		 */
		public void close() {
			synchronized(Loopback.this) {
				if (closed) {
					throw new IllegalStateException("already closed");
				}
				closed = true;
				
				receivers.remove(this);
			}
		}
	}
}