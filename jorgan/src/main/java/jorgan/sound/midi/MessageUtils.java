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

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

/**
 * BugFix for the JavaSound midi implementation.
 */
public class MessageUtils {

	/**
	 * Workaround for BUG #4716323 in JavaSound:<br>
	 * ShortMessage.getStatus() delivers bogus value so compute the status from
	 * <code>command OR channel</code> instead - fixed in 1.4.2 (mantis).
	 * 
	 * @param message
	 *            message to get status from
	 * @return status of message
	 * @deprecated
	 */
	public static int getStatusBugFix(ShortMessage message) {
		return message.getCommand() | message.getChannel();
	}

	/**
	 * Reminder for Bug #4851018 in JavaSound:<br>
	 * Don't use MidiMessage.getMessage() as it may return bogus values from
	 * external devices - fixed in 1.5 (Tiger).
	 * 
	 * @param message
	 *            message to get message from
	 * @return never
	 * @throws java.lang.UnsupportedOperationException
	 *             always
	 * @deprecated
	 */
	public static byte[] getMessageBugFix(ShortMessage message) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Reminder for Bug #4851018 in JavaSound:<br>
	 * Don't use MidiMessage.getLength() as it may return bogus values from
	 * external devices - fixed in 1.5 (Tiger).
	 * 
	 * @param message
	 *            message to get message from
	 * @return never
	 * @throws java.lang.UnsupportedOperationException
	 *             always
	 * @deprecated
	 */
	public static int getLengthBugFix(ShortMessage message) {
		throw new UnsupportedOperationException();
	}

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

			int status = getStatusBugFix(shortMessage);
			if (status != ShortMessage.ACTIVE_SENSING
					&& status != ShortMessage.TIMING_CLOCK) {
				return true;
			}
		}

		return false;
	}
}