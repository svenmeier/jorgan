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
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;

import jorgan.gui.dock.AbstractView;
import jorgan.memory.Storage;
import jorgan.memory.StorageListener;
import jorgan.memory.disposition.Memory;
import jorgan.memory.gui.file.MemoryFileFilter;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.table.StringCellEditor;
import jorgan.swing.table.TableUtils;
import spin.Spin;
import swingx.docking.Docked;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * Dockable for editing of a {@link Memory}'s
 * {@link jorgan.disposition.MemoryState}.
 */
public class MemoryView extends AbstractView {

	private static Logger logger = Logger.getLogger(MemoryView.class
			.getName());

	private static Configuration config = Configuration.getRoot().get(
			MemoryView.class);

	private JTable table = new JTable();

	private MemoryModel model = new MemoryModel();

	private EventHandler eventHandler = new EventHandler();

	private OrganSession session;

	private Storage storage;

	private EjectAction ejectAction = new EjectAction();

	/**
	 * Constructor.
	 */
	public MemoryView() {
		config.read(this);

		table.setModel(model);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.getSelectionModel().addListSelectionListener(eventHandler);
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
			storage.removeListener((StorageListener) Spin.over(eventHandler));
			storage = null;
		}

		this.session = session;

		if (this.session != null) {
			storage = session.lookup(Storage.class);
			storage.addListener((StorageListener) Spin.over(eventHandler));
		}

		update();
	}

	private void update() {
		TableUtils.cancelEdit(table);

		model.fireTableDataChanged();

		updateIndex();

		ejectAction.update();

		if (storage != null && storage.isLoaded()) {
			setStatus(MemoryFileFilter.removeSuffix(storage.getFile()));
		} else {
			setStatus(null);
		}
	}

	@Override
	protected void addTools(Docked docked) {

		docked.addTool(new SwapAction());
		docked.addTool(new ClearAction());
		docked.addToolSeparator();
		docked.addTool(ejectAction);
	}

	private void updateIndex() {
		if (storage != null) {
			int index = storage.getIndex();
			if (index == -1) {
				table.clearSelection();
			} else {
				if (index != table.getSelectedRow()) {
					TableUtils.cancelEdit(table);
					table.setColumnSelectionInterval(0, 0);
					table.getSelectionModel()
							.setSelectionInterval(index, index);
					table.scrollRectToVisible(table
							.getCellRect(index, 0, false));
				}
			}
		}
	}

	private boolean canEject() {
		int option = showBoxMessage("eject/confirm",
				MessageBox.OPTIONS_YES_NO_CANCEL);
		if (option == MessageBox.OPTION_YES) {
			return save();
		} else if (option == MessageBox.OPTION_NO) {
			return true;
		}

		return false;
	}

	private boolean save() {
		try {
			storage.save();
		} catch (IOException ex) {
			logger.log(Level.INFO, "saving storage failed", ex);

			showBoxMessage("saveException", MessageBox.OPTIONS_OK, session
					.getFile().getName());

			return false;
		}

		return true;
	}

	protected int showBoxMessage(String key, int options, Object... args) {
		return config.get(key).read(new MessageBox(options)).show(
				getContent().getTopLevelAncestor(), args);
	}

	private class EventHandler implements StorageListener,
			ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			if (table.getSelectedRowCount() == 1) {
				int row = table.getSelectedRow();
				if (row != -1) {
					storage.setIndex(row);
				}
			}
		}

		public void indexChanged(int index) {
			updateIndex();
		}

		public void changed() {
			update();
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

			storage.swap(rows[0], rows[1]);
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
			table.removeEditor();

			if (confirm()) {
				int[] rows = table.getSelectedRows();
				for (int r = 0; r < rows.length; r++) {
					storage.clear(rows[r]);
				}
			}
		}

		private boolean confirm() {

			int option = showBoxMessage("clear/confirm",
					MessageBox.OPTIONS_YES_NO_CANCEL);
			return option == MessageBox.OPTION_YES;
		}

		public void valueChanged(ListSelectionEvent e) {
			setEnabled(table.getSelectedRowCount() > 0);
		}
	}

	private class EjectAction extends BaseAction {
		private EjectAction() {
			config.get("eject").read(this);

			setEnabled(false);
		}

		public void update() {
			setEnabled(storage != null && storage.isEnabled());
		}

		public void actionPerformed(ActionEvent e) {
			if (storage.isLoaded() && storage.isModified() && !canEject()) {
				return;
			}

			File file;
			JFileChooser chooser = new JFileChooser(session.getFile());
			config.get("eject/chooser").read(chooser);
			chooser.setFileFilter(new MemoryFileFilter());
			if (chooser.showDialog(getContent().getTopLevelAncestor(), chooser
					.getApproveButtonText()) == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
				if (!file.exists()) {
					file = MemoryFileFilter.addSuffix(file);
				}
			} else {
				file = null;
			}
			storage.setFile(file);
		}
	}

	private class MemoryModel extends AbstractTableModel {

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			if (storage == null) {
				return 0;
			} else {
				return storage.getSize();
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (columnIndex == 0) {
				return "" + (rowIndex + 1);
			}
			return storage.getTitle(rowIndex);
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return (columnIndex == 1);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			storage.setTitle(rowIndex, (String) aValue);

			table.getColumnModel().getSelectionModel().setSelectionInterval(0,
					0);
		}
	}
}