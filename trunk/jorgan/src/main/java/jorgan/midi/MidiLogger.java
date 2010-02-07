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

import java.util.logging.Logger;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;

/**
 */
public class MidiLogger extends Loopback {

	private static final Logger logger = Logger.getLogger(MidiLogger.class
			.getName());

	public MidiLogger(MidiDevice.Info info) {
		super(info, true, true);
	}

	@Override
	protected void onLoopIn(MidiMessage message) {
		log(message);

		super.onLoopIn(message);
	}

	private void log(MidiMessage message) {
		logger.info("Midi message: [" + format(message) + "]");
	}

	private String format(MidiMessage message) {
		StringBuilder string = new StringBuilder();

		for (byte b : message.getMessage()) {
			if (string.length() > 0) {
				string.append(", ");
			}
			string.append(((int) b) & 0xff);
		}

		return string.toString();
	}
}