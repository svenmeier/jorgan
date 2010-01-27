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

public class NoteOnOffEncoding implements Encoding {

	public void changeTab(SamsDevice device, ShortMessage message) {
		int index = message.getData1();

		if (message.getCommand() == ShortMessage.NOTE_ON) {
			device.getTab(index).change(true);
		} else if (message.getCommand() == ShortMessage.NOTE_OFF) {
			device.getTab(index).change(false);
		}
	}

	@Override
	public void tabChanged(SamsDevice device, ShortMessage message) {
		int index = message.getData1();

		if (message.getCommand() == ShortMessage.NOTE_ON) {
			device.getTab(index).onChanged(true);
		} else if (message.getCommand() == ShortMessage.NOTE_OFF) {
			device.getTab(index).onChanged(false);
		}
	}

	@Override
	public ShortMessage encode(int index, boolean onMagnet, boolean on) {
		int status;
		if (onMagnet) {
			status = ShortMessage.NOTE_ON;
		} else {
			status = ShortMessage.NOTE_OFF;
		}

		int data1 = index;

		int data2;
		if (on) {
			data2 = 1;
		} else {
			data2 = 0;
		}

		return MessageUtils.newMessage(status, data1, data2);
	}
}