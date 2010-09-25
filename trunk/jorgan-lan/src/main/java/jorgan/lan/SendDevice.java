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

import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;

import jorgan.lan.net.MessageSender;
import jorgan.midi.Loopback;

/**
 * A remote {@link MidiDevice} over LAN.
 */
public class SendDevice extends Loopback {

	private int index;

	private MessageSender sender;

	public SendDevice(int index, Info info) {
		super(info, true, false);

		this.index = index;
	}

	@Override
	protected synchronized void openImpl() throws MidiUnavailableException {
		try {
			sender = new MessageSender(IpMidi.GROUP, IpMidi.port(index));

			probe();
		} catch (Exception ex) {
			MidiUnavailableException exception = new MidiUnavailableException();
			exception.initCause(ex);
			throw exception;
		}
	}

	private void probe() throws IOException {
		ShortMessage message = new ShortMessage();
		try {
			message.setMessage(ShortMessage.ACTIVE_SENSING);
		} catch (InvalidMidiDataException e) {
			throw new Error(e);
		}
		sender.send(message);
	}

	@Override
	protected synchronized void closeImpl() {
		if (sender != null) {
			sender.close();
			sender = null;
		}

		super.closeImpl();
	}

	@Override
	protected void onLoopIn(MidiMessage message) {
		try {
			sender.send(message);
		} catch (IOException e) {
			// nothing we can do about it, receivers are expected to work
			// flawlessly, #probe() must have worked
		}
	}
}