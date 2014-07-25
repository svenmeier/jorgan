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

public abstract class AbstractCommand extends Command {

	protected AbstractCommand() {
	}

	public abstract float process(float value, Context context);

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
		if (obj instanceof AbstractCommand) {
			return this.toString().equals(obj.toString());
		}

		return false;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();

		String simpleName = getClass().getSimpleName();
		buffer.append(Character.toLowerCase(simpleName.charAt(0))
				+ simpleName.substring(1));

		String arguments = getArguments();
		if (!arguments.isEmpty()) {
			buffer.append(" ");
			buffer.append(arguments);
		}
		return buffer.toString();
	}
}