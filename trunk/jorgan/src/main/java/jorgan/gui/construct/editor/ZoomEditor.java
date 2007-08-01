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

import java.awt.Component;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * PropertyEditor for a scale property.
 */
public class ZoomEditor extends CustomEditor {

	private JSpinner spinner;

	private NumberFormat format = NumberFormat.getPercentInstance();

	public ZoomEditor() {
		spinner = new JSpinner(new SpinnerNumberModel(100, 50, 150, 1));
		spinner.setBorder(null);

		JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner
				.getEditor();
		editor.getTextField().setBorder(null);
	}

	public String format(Object value) {
		if (value == null) {
			return "";
		} else {
			return format.format(value);
		}
	}

	public Component getCustomEditor(Object value) {

		spinner.setValue(Math.round(100 * ((Number)value).floatValue()));

		return spinner;
	}

	public Object getEditedValue() {

		try {
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner
					.getEditor();
			editor.commitEdit();
		} catch (ParseException ex) {
			// invalid value so keep previous value
		}

		return (((Number)spinner.getValue()).floatValue() / 100);
	}
}
