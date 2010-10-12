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
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.CellEditor;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.TransferHandler.TransferSupport;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * Utility method for tables.
 */
public class TableUtils {

	/**
	 * Fix the width of a table column.
	 * 
	 * @param table
	 *            table to fix column for
	 * @param column
	 *            the index of the column to fix width
	 * @param prototype
	 *            prototype value to use for calculating the preferred width
	 */
	public static void fixColumnWidth(JTable table, int column, Object prototype) {

		column = table.convertColumnIndexToView(column);

		TableCellRenderer renderer = table.getCellRenderer(0, column);

		Component component = renderer.getTableCellRendererComponent(table,
				prototype, false, false, 0, column);

		int width = component.getPreferredSize().width
				+ table.getIntercellSpacing().width;

		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		tableColumn.setMinWidth(width);
		tableColumn.setPreferredWidth(width);
		tableColumn.setMaxWidth(width);
		tableColumn.setResizable(false);
	}

	/**
	 * Make the look and feel of the given table pleasant.
	 * 
	 * @param table
	 *            table to spice up
	 */
	public static void pleasantLookAndFeel(final JTable table) {

		table.setGridColor(new JLabel().getBackground());
		table.setSurrendersFocusOnKeystroke(true);
		table.setFillsViewportHeight(true);
		// table.putClientProperty("JTable.autoStartsEdit", Boolean.TRUE);
		table.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
	}

	/**
	 * Hide the header of the given table.
	 * 
	 * @param table
	 *            table to hide header of
	 */
	public static void hideHeader(JTable table) {
		table.getTableHeader().setPreferredSize(new Dimension(0, 0));
	}

	/**
	 * Add a listener to actions to the given table, i.e. the given listener is
	 * notified if a cell is double clicked.
	 * 
	 * @param table
	 *            the table to add the listener to
	 * @param listener
	 *            the listener to add
	 */
	public static void addActionListener(final JTable table,
			final ActionListener listener) {
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (table.getSelectedRowCount() == 1) {
						listener.actionPerformed(new ActionEvent(table,
								ActionEvent.ACTION_PERFORMED, null));
					}
				}
			}
		});
	}

	/**
	 * Add a popup to the given table.
	 * 
	 * @param table
	 *            table to add popup to
	 * @param popup
	 *            the popup to add
	 */
	public static void addPopup(final JTable table, final JPopupMenu popup) {
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				checkPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				checkPopup(e);
			}

			public void checkPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					int index = table.rowAtPoint(e.getPoint());
					if (index != -1) {
						table.setRowSelectionInterval(index, index);
						popup.show(table, e.getX(), e.getY());
					}
				}
			}
		});
	}

	public static void stopEdit(JTable table) {
		CellEditor editor = table.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}
		table.setCellEditor(null);
	}

	public static void cancelEdit(JTable table) {
		CellEditor editor = table.getCellEditor();
		if (editor != null) {
			editor.cancelCellEditing();
		}
		table.setCellEditor(null);
	}

	public static int importIndex(JTable table, TransferSupport transferSupport) {
		int index = -1;

		int[] rows = table.getSelectedRows();
		for (int row : rows) {
			index = Math.max(index, row + 1);
		}

		if (transferSupport.isDrop()) {
			JTable.DropLocation location = (JTable.DropLocation) transferSupport
					.getDropLocation();
			index = location.getRow();
		}

		if (index == -1) {
			index = table.getRowCount();
		}

		return index;
	}
}