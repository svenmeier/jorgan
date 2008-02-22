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
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

/**
 * Utils for messages.
 */
public class MessageUtils {

	/**
	 * Test if the given message is a {@link ShortMessage}.
	 * 
	 * @param message
	 *            message to test
	 * @return <code>true</cde> if short message
	 */
	public static boolean isShortMessage(MidiMessage message) {
		if (message instanceof ShortMessage) {
			ShortMessage shortMessage = (ShortMessage) message;

			int status = shortMessage.getStatus();
			if (status != ShortMessage.ACTIVE_SENSING
					&& status != ShortMessage.TIMING_CLOCK) {
				return true;
			}
		}

		return false;
	}

	public static ShortMessage createShortMessage(int status, int data1,
			int data2) throws InvalidMidiDataException {

		ShortMessage shortMessage = new ShortMessage();

		// status isn't checked in ShortMessage#setMessage(int, int, int)
		if (status < 0 || status > 255) {
			throw new InvalidMidiDataException("status out of range: " + status);
		}
		shortMessage.setMessage(status, data1, data2);

		return shortMessage;
	}
}