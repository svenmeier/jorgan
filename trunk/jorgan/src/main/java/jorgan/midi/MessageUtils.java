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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

/**
 * Utils for messages.
 */
public class MessageUtils {

	/**
	 * Test if the given message is a channel message.
	 * 
	 * @param message
	 *            message to test
	 * @return <code>true</cde> if channel message
	 */
	public static boolean isChannelMessage(MidiMessage message) {
		if (message instanceof ShortMessage) {
			ShortMessage shortMessage = (ShortMessage) message;

			int status = shortMessage.getStatus();
			if (status >= 0x80 && status < 0xF0) {
				return true;
			}
		}

		return false;
	}

	public static ShortMessage createMessage(int channel, int command,
			int data1, int data2) throws InvalidMidiDataException {

		return createMessage(channel | command, data1, data2);
	}

	public static ShortMessage createMessage(int status, int data1, int data2)
			throws InvalidMidiDataException {

		ShortMessage shortMessage = new ShortMessage();

		if (status < 0x00 || status >= 0xF0) {
			throw new InvalidMidiDataException("no channel status: " + status);
		}
		shortMessage.setMessage(status, data1, data2);

		return shortMessage;
	}

	public static ShortMessage newMessage(int status, int data1, int data2) {

		try {
			return createMessage(status, data1, data2);
		} catch (InvalidMidiDataException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public static MidiMessage newMetaMessage(int type, byte[] data) {
		MetaMessage message = new MetaMessage();
		
		try {
			message.setMessage(type, data, data.length);
		} catch (InvalidMidiDataException e) {
			throw new IllegalArgumentException(e);
		}
		
		return message;
	}
}