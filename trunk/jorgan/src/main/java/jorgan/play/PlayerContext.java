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
package jorgan.play;

import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;

import jorgan.disposition.Message;
import jorgan.midi.mpl.Context;

public class PlayerContext implements Context {

	private Map<String, Float> map = new HashMap<String, Float>();

	public float get(String name) {
		Float temp = map.get(name);
		if (temp == null) {
			return Float.NaN;
		} else {
			return temp;
		}
	}

	public void set(String name, float value) {
		map.put(name, value);
	}

	public void clear() {
		map.clear();
	}

	public boolean process(Message message, byte[] datas)
			throws InvalidMidiDataException {
		if (message.getLength() != datas.length) {
			return false;
		}

		boolean valid = true;
		for (int d = 0; d < datas.length; d++) {
			float processed = message.process(datas[d] & 0xff, this, d);
			if (Float.isNaN(processed)) {
				return false;
			}
			int rounded = Math.round(processed);
			if (rounded < 0 || rounded > 255) {
				valid = false;
			}
			datas[d] = (byte) rounded;
		}

		if (!valid) {
			throw new InvalidMidiDataException();
		}
		return true;
	}
}