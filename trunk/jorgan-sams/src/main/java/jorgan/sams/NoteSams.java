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
package jorgan.sams;

import javax.sound.midi.ShortMessage;

import jorgan.midi.MessageUtils;

public class NoteSams implements Sams {

	private String device;

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public boolean accepts(ShortMessage message) {
		return message.getCommand() == ShortMessage.NOTE_ON
				&& message.getData2() == 1;
	}

	public ShortMessage reverse(ShortMessage message) {
		if (!accepts(message)) {
			throw new IllegalArgumentException("not accepted");
		}

		int command;
		if (message.getCommand() == ShortMessage.NOTE_ON) {
			command = ShortMessage.NOTE_OFF;
		} else {
			command = ShortMessage.NOTE_ON;
		}

		return MessageUtils.newMessage(message.getChannel(), command, message
				.getData1(), message.getData2());
	}

	public ShortMessage inverse(ShortMessage message) {
		if (!accepts(message)) {
			throw new IllegalArgumentException("not accepted");
		}

		int data2;
		if (message.getData2() == 0) {
			data2 = 1;
		} else {
			data2 = 0;
		}

		return MessageUtils.newMessage(message.getChannel(), message
				.getCommand(), message.getData1(), data2);
	}
}