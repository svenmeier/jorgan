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
package jorgan.lan;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

import jorgan.lan.net.MessageReceiver;
import jorgan.midi.Loopback;

/**
 * A remote {@link MidiDevice} over LAN.
 */
public class ReceiveDevice extends Loopback {

	private int index;

	private MessageReceiver receiver;

	public ReceiveDevice(int index, Info info) {
		super(info, false, true);

		this.index = index;
	}

	@Override
	public synchronized void open() throws MidiUnavailableException {
		super.open();

		try {
			receiver = new MessageReceiver(IpMidi.GROUP, IpMidi.port(index)) {
				@Override
				protected void onReceived(ShortMessage message) {
					loopOut(message, -1);
				}
			};
		} catch (Exception ex) {
			close();

			MidiUnavailableException exception = new MidiUnavailableException();
			exception.initCause(ex);
			throw exception;
		}
	}

	@Override
	public synchronized void close() {
		if (receiver != null) {
			receiver.close();
			receiver = null;
		}

		super.close();
	}
}