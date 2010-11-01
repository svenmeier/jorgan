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

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/**
 * A wrapper of a transmitter.
 */
public class TransmitterWrapper implements Transmitter {

	private Transmitter transmitter;

	private Receiver receiver;

	/**
	 * Wrap the given transmitter.
	 * 
	 * @param transmitter
	 *            transmitter to wrap
	 */
	public TransmitterWrapper(Transmitter transmitter) {
		this.transmitter = transmitter;

		transmitter.setReceiver(new Receiver() {
			public void close() {
			}

			public void send(MidiMessage message, long timeStamp) {
				TransmitterWrapper.this.send(message, timeStamp);
			}
		});
	}

	public void close() {
		transmitter.close();
	}

	public Receiver getReceiver() {
		return receiver;
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	protected void send(MidiMessage message, long timeStamp) {
		if (receiver != null) {
			receiver.send(message, timeStamp);
		}
	}
}