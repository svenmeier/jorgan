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
package jorgan.memory.gui.dock;

import java.awt.event.ActionEvent;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.dock.OrganDockable;
import jorgan.memory.Memory;
import jorgan.memory.disposition.MemorySwitcher;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.table.StringCellEditor;
import jorgan.swing.table.TableUtils;
import spin.Spin;
import swingx.docking.Docked;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * Dockable for editing of a {@link jorgan.disposition.Memory}.
 */
public class MemoryDockable extends OrganDockable {

	private static Configuration config = Configuration.getRoot().get(
			MemoryDockable.class);

	private JTable table = new JTable();

	private EventListener listener = new EventListener();

	private OrganSession session;

	private Memory memory;

	/**
	 * Constructor.
	 */
	public MemoryDockable() {
		config.read(this);

		table.setModel(new MemoryModel());
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.getSelectionModel().addListSelectionListener(listener);
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
		table.getColumnModel().getColumn(0).setCellRenderer(renderer);
		table.getColumnModel().getColumn(1).setCellEditor(
				new StringCellEditor());
		TableUtils.fixColumnWidth(table, 0, "888");
		TableUtils.hideHeader(table);
		TableUtils.pleasantLookAndFeel(table);
		setContent(new JScrollPane(table));
	}

	/**
	 * Set the organ to control memory of.
	 * 
	 * @param session
	 *            session
	 */
	public void setSession(OrganSession session) {
		if (this.session != null) {
			session.getOrgan().removeOrganListener(
					(OrganListener) Spin.over(listener));
			setMemory(null);
		}

		this.session = session;

		if (this.session != null) {
			setMemory(session.lookup(Memory.class));
			session.getOrgan().addOrganListener(
					(OrganListener) Spin.over(listener));
		}
	}

	@Override
	public void docked(Docked docked) {
		super.docked(docked);

		docked.addTool(new ImportAction());
		docked.addTool(new ExportAction());
		docked.addToolSeparator();
		docked.addTool(new SwapAction());
		docked.addTool(new ClearAction());
	}

	private void setMemory(Memory memory) {
		this.memory = memory;

		((MemoryModel) table.getModel()).fireTableDataChanged();
		table.setVisible(memory != null);

		updateSelection();
	}

	private MemorySwitcher getSwitcher() {
		for (MemorySwitcher switcher : session.getOrgan().getElements(
				MemorySwitcher.class)) {
			return switcher;
		}
		return null;
	}

	private int getIndex() {
		MemorySwitcher switcher = getSwitcher();
		if (switcher == null) {
			return 0;
		}
		return switcher.getIndex();
	}

	private void setIndex(int index) {
		MemorySwitcher switcher = getSwitcher();
		if (switcher != null) {
			switcher.setIndex(index);
		}
	}

	private int getSize() {
		MemorySwitcher switcher = getSwitcher();
		if (switcher == null) {
			return 0;
		}
		return switcher.getSize();
	}

	private void updateSelection() {
		if (memory == null) {
			return;
		}

		if (getSwitcher() == null) {
			return;
		}

		int index = getIndex();
		if (index != table.getSelectedRow()) {
			if (table.getCellEditor() != null) {
				table.getCellEditor().cancelCellEditing();
				table.setCellEditor(null);
			}
			table.getSelectionModel().setSelectionInterval(index, index);
			table.scrollRectToVisible(table.getCellRect(index, 0, false));
		}
		table.setColumnSelectionInterval(0, 0);
	}

	private class EventListener extends OrganAdapter implements
			ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			if (table.getSelectedRowCount() == 1) {
				int row = table.getSelectedRow();
				if (row != -1) {
					setIndex(row);
				}
			}
		}

		@Override
		public void propertyChanged(Element element, String name) {
			if (element instanceof MemorySwitcher) {
				if ("value".equals(name)) {
					updateSelection();
				} else if ("size".equals(name)) {
					((MemoryModel) table.getModel()).fireTableDataChanged();
				}
			}
		}
	}

	private class SwapAction extends BaseAction implements
			ListSelectionListener {
		private SwapAction() {
			config.get("swap").read(this);

			setEnabled(false);

			table.getSelectionModel().addListSelectionListener(this);
		}

		public void actionPerformed(ActionEvent ev) {
			int[] rows = table.getSelectedRows();
			memory.swap(rows[0], rows[1]);
		}

		public void valueChanged(ListSelectionEvent e) {
			setEnabled(table.getSelectedRowCount() == 2);
		}
	}

	private class ClearAction extends BaseAction implements
			ListSelectionListener {
		private ClearAction() {
			config.get("clear").read(this);

			setEnabled(false);

			table.getSelectionModel().addListSelectionListener(this);
		}

		public void actionPerformed(ActionEvent ev) {
			if (confirm()) {
				int[] rows = table.getSelectedRows();
				for (int r = 0; r < rows.length; r++) {
					memory.clear(rows[r]);
				}
			}
		}

		private boolean confirm() {

			MessageBox box = config.get("clearConfirm").read(
					new MessageBox(MessageBox.OPTIONS_YES_NO));
			return box.show(table) == MessageBox.OPTION_YES;
		}

		public void valueChanged(ListSelectionEvent e) {
			setEnabled(table.getSelectedRowCount() > 0);
		}
	}

	private class ImportAction extends BaseAction {
		private ImportAction() {
			config.get("import").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
		}
	}

	private class ExportAction extends BaseAction {
		private ExportAction() {
			config.get("export").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
		}
	}

	private class MemoryModel extends AbstractTableModel {

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			if (memory == null) {
				return 0;
			} else {
				return getSize();
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return "" + (rowIndex + 1);
			}
			return memory.getLevel(rowIndex).getTitle();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (columnIndex == 1);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			memory.getLevel(rowIndex).setTitle((String) aValue);
		}
	}
}