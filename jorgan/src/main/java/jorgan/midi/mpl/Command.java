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

	Command successor;

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

	private String typeToString(Class<?> type) {
		String simpleName = type.getSimpleName();

		return Character.toLowerCase(simpleName.charAt(0))
				+ simpleName.substring(1);
	}

	protected String valueToString(float value) {
		int integer = (int) value;

		if (integer == value) {
			return Integer.toString(integer);
		} else {
			return Float.toString(value);
		}
	}

	protected abstract String getArguments();

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Command) {
			return this.toString().equals(obj.toString());
		}

		return false;
	}

	public static Command[] equal(byte[] datas) {
		Command[] commands = new Command[datas.length];
		for (int d = 0; d < datas.length; d++) {
			commands[d] = new Equal(datas[d] & 0xff);
		}
		return commands;
	}
}