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

public abstract class Command {

	public abstract float process(float value, Context context);

	public static Command fromString(String string) throws ProcessingException {
		if (string.indexOf("|") != -1) {
			return Chain.fromString(string);
		}

		string = string.trim();

		if (string.length() == 0) {
			return new NoOp();
		}

		try {
			int end = string.indexOf(' ');
			if (end == -1) {
				end = string.length();
			}

			String simpleName = Character.toUpperCase(string.charAt(0))
					+ string.substring(1, end);

			Class<?> type = Class.forName(AbstractCommand.class.getPackage()
					.getName() + "." + simpleName);

			String arguments = string.substring(end).trim();

			return (AbstractCommand) type.getDeclaredConstructor(
					new Class<?>[] { String.class }).newInstance(arguments);
		} catch (Exception ex) {
			throw new ProcessingException(string, ex);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(Command command, Class<T> type) {
		if (type.isInstance(command)) {
			return (T) command;
		}

		if (command instanceof Chain) {
			return ((Chain) command).get(type);
		}

		return null;
	}
}