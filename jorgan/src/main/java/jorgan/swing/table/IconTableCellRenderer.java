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

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * A renderer for icons in a {@link javax.swing.JTable}.
 */
public class IconTableCellRenderer extends DefaultTableCellRenderer {

	/**
	 * Overriden for icon specific behaviour.
	 */
	@Override
	protected void setValue(Object value) {
		setIcon(getIcon(value));
		setHorizontalAlignment(CENTER);
	}

	@Override
	public IconTableCellRenderer getTableCellRendererComponent(JTable table,
			Object value, boolean isSelected, boolean hasFocus, int row,
			int column) {
		return (IconTableCellRenderer) super.getTableCellRendererComponent(
				table, value, isSelected, hasFocus, row, column);
	}

	/**
	 * Get the icon for the given value.
	 * 
	 * @param value
	 *            value to get icon for
	 * @return icon
	 */
	protected Icon getIcon(Object value) {
		return (Icon) value;
	}

	/**
	 * Convinience method for configuration of a JTable. </br> The renderer is
	 * set up as the renderer for specified column, which is also configured to
	 * have the width of the defaultIcon and to be unresizable.
	 * 
	 * @param table
	 *            table to configure
	 * @param columnIndex
	 *            index of column in model
	 */
	public void configureTableColumn(JTable table, int columnIndex) {

		getTableCellRendererComponent(table, getIcon(null), false, false, 0, 0);

		columnIndex = table.convertColumnIndexToView(columnIndex);

		TableColumn column = table.getColumnModel().getColumn(columnIndex);
		column.setCellRenderer(this);
		column.setMaxWidth(this.getPreferredSize().width);
		column.setResizable(false);
	}
}