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
package jorgan.swing.spinner;

import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.border.EmptyBorder;

/**
 * Utility method for spinners.
 */
public class SpinnerUtils {

	private static EmptyBorder EMPTY = new EmptyBorder(0, 0, 0, 0);

	public static SpinnerModel create(long value, long min, long max, long step) {
		return new SpinnerNumberModel((Long) value, (Long) min, (Long) max,
				(Long) step);
	}

	public static void setColumns(JSpinner spinner, int columns) {
		DefaultEditor editor = getEditor(spinner);
		if (editor != null) {
			editor.getTextField().setColumns(columns);
		}
	}

	public static void commitEdit(JSpinner spinner) {
		DefaultEditor editor = getEditor(spinner);
		if (editor != null) {
			try {
				editor.commitEdit();
			} catch (Exception invalidValue) {
			}
		}
	}

	public static void emptyBorder(JSpinner spinner) {
		spinner.setBorder(EMPTY);

		DefaultEditor editor = getEditor(spinner);
		if (editor != null) {
			editor.setBorder(EMPTY);
			editor.getTextField().setBorder(EMPTY);
		}
	}

	private static DefaultEditor getEditor(JSpinner spinner) {
		try {
			return (DefaultEditor) spinner.getEditor();
		} catch (ClassCastException ex) {
			return null;
		}
	}
}