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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellEditor;

/**
 * Cell editor for strings.
 */
public class StringCellEditor extends AbstractCellEditor implements
		TableCellEditor {

	private JTextField textField;

	/**
	 * Constructor.
	 */
	public StringCellEditor() {
		textField = new JTextField();
		textField.setBorder(new EmptyBorder(0, 0, 0, 0));
		textField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				stopCellEditing();
			}
		});
	}

	/**
	 * Editable via {@link MouseEvent} only.
	 */
	@Override
	public boolean isCellEditable(EventObject e) {
		return e instanceof MouseEvent;
	}

	protected JTextField getTextField() {
		return textField;
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		textField.setText((String) value);
		return textField;
	}

	public Object getCellEditorValue() {
		return textField.getText();
	}
}