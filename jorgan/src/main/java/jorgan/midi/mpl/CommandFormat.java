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

public class CommandFormat {

	public Command[] parse(String string) throws ProcessingException {
		String[] tokens = string.split(",", -1);

		try {
			Command[] commands = new Command[tokens.length];
			for (int c = 0; c < commands.length; c++) {
				commands[c] = commands(tokens[c]);
			}
			return commands;
		} catch (Exception ex) {
			throw new ProcessingException(string);
		}
	}

	public String format(Command... commands) {
		StringBuilder builder = new StringBuilder();

		for (Command command : commands) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(command.toString());
		}

		return builder.toString();
	}

	private Command commands(String terms) throws Exception {

		int pipe = terms.indexOf('|');
		if (pipe == -1) {
			return command(terms.trim());
		} else {
			Command successor = commands(terms.substring(pipe + 1).trim());

			Command command = command(terms.substring(0, pipe).trim());
			command.successor = successor;

			return command;
		}
	}

	private Command command(String term) throws Exception {
		if (term.length() == 0) {
			return new NoOp();
		}

		int space = term.indexOf(' ');

		Class<?> type = type(term.substring(0, space).trim());
		String arguments = term.substring(space + 1).trim();

		return (Command) type.getDeclaredConstructor(
				new Class<?>[] { String.class }).newInstance(arguments);
	}

	private static Class<?> type(String string) throws ClassNotFoundException {
		String simpleName = Character.toUpperCase(string.charAt(0))
				+ string.substring(1);

		return Class.forName(Command.class.getPackage().getName() + "."
				+ simpleName);
	}
}