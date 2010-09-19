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
package jorgan.midi.mpl;

import java.util.Arrays;

/**
 * A tuple of {@link Command}s
 */
public class Tuple {

	private Command[] commands;

	public Tuple(Command... commands) {
		this.commands = commands;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (Command command : commands) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(command.toString());

		}

		return builder.toString();
	}

	public static Tuple fromString(String string) throws ProcessingException {
		String[] tokens = string.split(",", -1);

		try {
			Command[] commands = new Command[tokens.length];
			for (int c = 0; c < commands.length; c++) {
				commands[c] = Command.fromString(tokens[c]);
			}
			return new Tuple(commands);
		} catch (Exception ex) {
			throw new ProcessingException(string);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tuple) {
			return this.toString().equals(obj.toString());
		}

		return false;
	}

	public static Tuple equal(byte[] datas) {
		Command[] commands = new Command[datas.length];
		for (int d = 0; d < datas.length; d++) {
			commands[d] = new Equal(datas[d] & 0xff);
		}
		return new Tuple(commands);
	}

	public int getLength() {
		return commands.length;
	}

	public Command get(int index) {
		if (index >= commands.length) {
			throw new IllegalArgumentException();
		}
		return commands[index];
	}

	public Tuple set(int index, Command command) {
		Command[] commands = Arrays.copyOf(this.commands, this.commands.length);
		commands[index] = command;
		return new Tuple(commands);
	}
}