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
package jorgan.io.soundfont;

/**
 * The information about a preset contained in the
 * {@jorgan.io.soundfont.PresetHeaderChunk}.
 */
public class Preset implements Comparable<Preset> {

	private String name;

	private short program;

	private short bank;

	/**
	 * Create a preset.
	 * 
	 * @param name
	 *            name of preset
	 * @param program
	 *            program number
	 * @param bank
	 *            bank number
	 */
	public Preset(String name, short program, short bank) {
		this.name = name;
		this.program = program;
		this.bank = bank;
	}

	public short getBank() {
		return bank;
	}

	public String getName() {
		return name;
	}

	public short getProgram() {
		return program;
	}

	/**
	 * Natural ordering by bank an program number.
	 */
	public int compareTo(Preset preset) {
		if (bank < preset.bank) {
			return -1;
		} else if (bank > preset.bank) {
			return 1;
		}

		if (program < preset.program) {
			return -1;
		} else if (program > preset.program) {
			return 1;
		}

		return 0;
	}
}
