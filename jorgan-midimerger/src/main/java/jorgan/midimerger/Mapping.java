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
package jorgan.midimerger;

public class Mapping {

	private Mode[] modes;

	public Mapping() {
		this.modes = new Mode[0];
	}

	/**
	 * @param colonDelimitedModes
	 *            modes separated by a colon
	 * @see #toString()
	 */
	public Mapping(String colonDelimitedModes) {

		String[] split = colonDelimitedModes.split(":");

		this.modes = new Mode[split.length];
		for (int c = 0; c < this.modes.length; c++) {
			this.modes[c] = Mode.valueOf(split[c].trim());
		}
	}

	public Mode getMode(int index) {
		ensureIndex(index);

		return modes[index];
	}

	public void setMode(int index, Mode mode) {
		ensureIndex(index);

		modes[index] = mode;
	}

	private void ensureIndex(int index) {
		if (index >= modes.length) {
			Mode[] temp = new Mode[index + 1];
			System.arraycopy(modes, 0, temp, 0, modes.length);
			modes = temp;
		}
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();

		for (Mode mode : modes) {
			if (string.length() > 0) {
				string.append(":");
			}
			string.append(mode.name());
		}

		return string.toString();
	}

	public static enum Mode {
		SKIP, OMNI, C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12, C13, C14, C15, C16;

		private int channel = -1;

		private Mode() {
			String name = name();
			if (name.startsWith("C")) {
				channel = Integer.parseInt(name.substring(1)) - 1;
			}
		}

		public String toString() {
			String name = name();
			if (name.startsWith("C")) {
				return name.substring(1);
			}
			return name;
		}

		public int map(int channel) {
			if (this.channel == -1) {
				return channel;
			}

			return this.channel;
		}
	}
}