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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.sound.midi.MidiMessage;

/**
 * A text in a {@link MidiMessage}, encoding in <code>UTF-8</code>.
 */
public class Text {

	public static final int TYPE_TEXT = 1;

	private byte[] bytes;

	public Text(byte[] bytes) {
		this.bytes = bytes;
	}

	public Text(String string) {
		try {
			bytes = string.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}

	public int getLength() {
		return bytes.length;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public boolean equals(Text text) {
		return Arrays.equals(this.bytes, text.bytes);
	}

	public String toString() {
		try {
			return new String(bytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new Error(e);
		}
	}
}
