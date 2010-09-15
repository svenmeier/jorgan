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

public abstract class ValueCommand extends Command {

	private String name;

	private float value = Float.NaN;

	protected ValueCommand(String arguments) {
		int space = arguments.indexOf(' ');
		if (space == -1) {
			if (Character.isDigit(arguments.charAt(0))
					|| '-' == arguments.charAt(0)) {
				value = Float.parseFloat(arguments);
			} else {
				name = arguments;
			}
		} else {
			name = arguments.substring(0, space);
			value = Float.parseFloat(arguments.substring(space + 1));
		}
	}
	
	protected ValueCommand(String name, float value) {
		this.name = name;
		this.value = value;
	}

	protected ValueCommand(String name, float value, Command successor) {
		super(successor);
		
		this.name = name;
		this.value = value;
	}


	public String getName() {
		return name;
	}
	
	public float getValue() {
		return value;
	}
	
	protected float getValue(Context context) {
		float value = Float.NaN;
		if (name != null) {
			value = context.get(name);
		}
		if (Float.isNaN(value)) {
			value = this.value;
		}
		return value;
	}

	@Override
	protected String getArguments() {
		if (name == null) {
			return format(value);
		} else if (Float.isNaN(value)) {
			return name;
		} else {
			return name + " " + format(value);
		}
	}
}