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

import java.util.List;

public class Chain extends Command {

	private Command[] commands;

	public Chain(List<Command> commands) {
		this.commands = commands.toArray(new Command[commands.size()]);
	}

	public Chain(Command... commands) {
		this.commands = commands;
	}

	public int length() {
		return this.commands.length;
	}

	@Override
	public final float process(float value, Context context) {
		// don't use iterator for performance
		for (int c = 0; c < commands.length; c++) {
			value = commands[c].process(value, context);
			if (Float.isNaN(value)) {
				break;
			}
		}
		return value;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (Command command : commands) {
			if (builder.length() > 0) {
				builder.append(" | ");
			}
			builder.append(command.toString());

		}

		return builder.toString();
	}

	public static Command fromString(String string) throws ProcessingException {
		String[] tokens = string.split("\\|", -1);

		try {
			Command[] commands = new Command[tokens.length];
			for (int c = 0; c < commands.length; c++) {
				commands[c] = AbstractCommand.fromString(tokens[c]);
			}
			return new Chain(commands);
		} catch (Exception ex) {
			throw new ProcessingException(string);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Chain) {
			return this.toString().equals(obj.toString());
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz) {
		for (Command command : commands) {
			if (clazz.isInstance(command)) {
				return (T) command;
			}
		}

		return null;
	}
}