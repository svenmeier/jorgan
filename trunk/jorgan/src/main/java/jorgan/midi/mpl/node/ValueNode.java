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
package jorgan.midi.mpl.node;

import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.Processor.Node;

public abstract class ValueNode extends Node {

	private String name;

	private float value = Float.NaN;

	protected ValueNode(String term) throws Exception {
		int space = term.indexOf(' ');
		if (space == -1) {
			if (Character.isDigit(term.charAt(0)) || '-' == term.charAt(0)) {
				value = Float.parseFloat(term);
			} else {
				name = term;
			}
		} else {
			name = term.substring(0, space);
			value = Float.parseFloat(term.substring(space + 1));
		}
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
}