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

import javax.sound.midi.*;

/**
 * BugFix for the JavaSound midi implementation. <br>
 * Reminder for Bug #4851018: don't use MidiMessage.getMessage() and
 * MidiMessage.getLength() as they may return bogus values from external
 * devices - fixed in 1.5 (Tiger).
 */
public class BugFix {

	/**
	 * Workaround for BUG #4716323 in JavaSound:<br>
	 * ShortMessage.getStatus() delivers bogus value so compute the status from
	 * <code>command OR channel</code> instead - fixed in 1.4.2 (mantis).
	 * 
	 * @param message
	 *            message to get status from
	 * @return status of message
	 */
	public static int getStatus(ShortMessage message) {
		return message.getCommand() | message.getChannel();
	}
}