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
package jorgan.swing.table;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;

import jorgan.swing.spinner.SpinnerUtils;

/**
 * Cell editor for <code>Integer</code> values customizing a JSpinner.
 * 
 * @see javax.swing.JSpinner
 */
public class SpinnerCellEditor extends AbstractCellEditor implements
		TableCellEditor {

	private JSpinner spinner = new JSpinner();

	public SpinnerCellEditor(int min, int max, int stepSize) {
		spinner.setModel(new SpinnerNumberModel(min, min, max, stepSize));

		initSpinner();
	}

	public SpinnerCellEditor(double min, double max, double stepSize) {
		spinner.setModel(new SpinnerNumberModel(min, min, max, stepSize));

		initSpinner();
	}

	/**
	 * Must be called after setting the model.
	 */
	private void initSpinner() {
		try {
			spinner.setBorder(new EmptyBorder(0, 0, 0, 0));
			JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor) spinner
					.getEditor();
			editor.getTextField().setBorder(new EmptyBorder(0, 0, 0, 0));
		} catch (Exception keepBorders) {
		}
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

		spinner.setValue(value);

		return spinner;
	}

	public Object getCellEditorValue() {
		SpinnerUtils.commitEdit(spinner);

		return spinner.getValue();
	}

}