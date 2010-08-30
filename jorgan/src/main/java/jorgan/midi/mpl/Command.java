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

	private Command successor;

	protected Command() {
	}

	protected Command(Command successor) {
		if (successor != null && successor.getClass() != NoOp.class) {
			this.successor = successor;
		}
	}

	public final float process(float value, Context context) {
		float f = processImpl(value, context);
		if (!Float.isNaN(f) && successor != null) {
			f = successor.process(f, context);
		}
		return f;
	}

	protected abstract float processImpl(float value, Context context);

	@SuppressWarnings("unchecked")
	public <T> T get(Class<T> clazz) {
		if (clazz.isInstance(this)) {
			return (T) this;
		} else {
			if (successor == null) {
				return null;
			} else {
				return successor.get(clazz);
			}
		}
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		toString(buffer);
		return buffer.toString();
	}

	private void toString(StringBuffer buffer) {
		buffer.append(typeToString(getClass()));
		buffer.append(" ");
		buffer.append(getArguments());

		if (successor != null) {
			buffer.append(" | ");
			successor.toString(buffer);
		}
	}

	protected abstract String getArguments();

	public static Command create(String terms) throws ProcessingException {
		terms = terms.trim();

		try {
			return createCommands(terms.trim());
		} catch (Exception ex) {
			throw new ProcessingException(terms, ex);
		}
	}

	private static Command createCommands(String terms) throws Exception {

		int pipe = terms.indexOf('|');
		if (pipe == -1) {
			return createCommand(terms.trim());
		} else {
			Command successor = createCommands(terms.substring(pipe + 1).trim());

			Command command = createCommand(terms.substring(0, pipe).trim());
			command.successor = successor;

			return command;
		}
	}

	private static Command createCommand(String term) throws Exception {
		if (term.length() == 0) {
			return new NoOp();
		}

		int space = term.indexOf(' ');

		Class<?> type = Command.stringToType(term.substring(0, space).trim());
		String arguments = term.substring(space + 1).trim();

		return (Command) type.getDeclaredConstructor(
				new Class<?>[] { String.class }).newInstance(arguments);
	}

	private static Class<?> stringToType(String string)
			throws ClassNotFoundException {
		String simpleName = Character.toUpperCase(string.charAt(0))
				+ string.substring(1);

		return Class.forName(Command.class.getPackage().getName() + "."
				+ simpleName);
	}

	private static String typeToString(Class<?> type) {
		String simpleName = type.getSimpleName();

		return Character.toLowerCase(simpleName.charAt(0))
				+ simpleName.substring(1);
	}

	protected static String toString(float value) {
		int integer = (int) value;

		if (integer == value) {
			return Integer.toString(integer);
		} else {
			return Float.toString(value);
		}
	}
}