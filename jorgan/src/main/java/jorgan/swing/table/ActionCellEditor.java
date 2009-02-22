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

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 * Cell editor which performs an {@link Action}.
 */
public class ActionCellEditor extends AbstractCellEditor implements
		TableCellEditor {

	private JButton button;

	private Object value;

	public ActionCellEditor(Action action) {
		this.button = new JButton(action) {
			@Override
			protected void fireActionPerformed(ActionEvent event) {
				super.fireActionPerformed(event);
				
				fireEditingStopped();
			}
		};
	}

	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {

		setValue(value);

		return button;
	}

	public Object getCellEditorValue() {
		return getValue();
	}

	protected void setValue(Object value) {
		this.value = value;
	}

	protected Object getValue() {
		return value;
	}
}