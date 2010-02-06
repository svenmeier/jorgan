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

import java.util.List;

import javax.sound.midi.ShortMessage;

import jorgan.midi.MessageUtils;
import jorgan.sams.Magnets.Tab;

public class NoteOnOffEncoding implements Encoding {

	public void decodeChangeTab(List<Tab> tabs, ShortMessage message) {
		int index = message.getData1();

		if (message.getCommand() == ShortMessage.NOTE_ON) {
			tabs.get(index).change(true);
		} else if (message.getCommand() == ShortMessage.NOTE_OFF) {
			tabs.get(index).change(false);
		}
	}

	@Override
	public void decodeTabChanged(List<Tab> tabs, ShortMessage message) {
		int index = message.getData1();

		if (message.getCommand() == ShortMessage.NOTE_ON) {
			tabs.get(index).onChanged(true);
		} else if (message.getCommand() == ShortMessage.NOTE_OFF) {
			tabs.get(index).onChanged(false);
		}
	}

	@Override
	public ShortMessage encodeOffMagnet(int index, boolean on) {
		return MessageUtils.newMessage(ShortMessage.NOTE_OFF, index, on ? 127
				: 0);
	}

	@Override
	public ShortMessage encodeOnMagnet(int index, boolean on) {
		return MessageUtils.newMessage(ShortMessage.NOTE_ON, index, on ? 127
				: 0);
	}

	@Override
	public ShortMessage encodeTabChanged(int index, boolean on) {
		return MessageUtils.newMessage(on ? ShortMessage.NOTE_ON
				: ShortMessage.NOTE_OFF, index, 127);
	}
}