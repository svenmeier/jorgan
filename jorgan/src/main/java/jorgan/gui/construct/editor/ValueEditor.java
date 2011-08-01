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
package jorgan.gui.construct.editor;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.SpinnerNumberModel;

/**
 * PropertyEditor for a value property.
 */
public class ValueEditor extends SpinnerEditor {

	private NumberFormat format = new DecimalFormat();

	public ValueEditor() {
		super(new SpinnerNumberModel(new Float(0.0f), new Float(0.0f),
				new Float(1.0f), new Float(0.01f)));
	}

	@Override
	public String format(Object value) {
		if (value == null) {
			return "";
		} else {
			return format.format(value);
		}
	}
}
