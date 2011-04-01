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

import jorgan.swing.spinner.SpinnerUtils;

/**
 * PropertyEditor for a long property.
 */
public class LongEditor extends SpinnerEditor {

	private NumberFormat format = new DecimalFormat();

	public LongEditor() {
		this(Long.MIN_VALUE, 1, Long.MAX_VALUE);
	}

	public LongEditor(long min, long delta, long max) {
		this(min, min, delta, max);
	}

	public LongEditor(long value, long min, long delta, long max) {
		super(SpinnerUtils.create(value, min, max, delta));
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
