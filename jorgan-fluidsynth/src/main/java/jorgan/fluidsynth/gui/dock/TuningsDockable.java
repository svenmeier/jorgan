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
package jorgan.fluidsynth.gui.dock;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.CellEditor;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganListener;
import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.fluidsynth.disposition.Tuning;
import jorgan.gui.dock.OrganDockable;
import jorgan.gui.selection.ElementSelection;
import jorgan.gui.selection.SelectionListener;
import jorgan.gui.undo.Compound;
import jorgan.gui.undo.UndoManager;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.table.IconTableCellRenderer;
import jorgan.swing.table.SpinnerCellEditor;
import jorgan.swing.table.StringCellEditor;
import jorgan.swing.table.TableUtils;
import spin.Spin;
import swingx.dnd.ObjectTransferable;
import swingx.docking.Docked;
import bias.Configuration;

/**
 * Panel shows the {@link Tuning}s of a {@link FluidsynthSound}.
 */
public class TuningsDockable extends OrganDockable {

	private static Configuration config = Configuration.getRoot().get(
			TuningsDockable.class);

	private OrganSession session;

	private FluidsynthSound sound;

	private ObjectTransferable transferable;

	private List<Tuning> tunings = new ArrayList<Tuning>();

	/**
	 * The listener to selection changes.
	 */
	private SelectionHandler selectionHandler = new SelectionHandler();

	private AddAction addAction = new AddAction();

	private RemoveAction removeAction = new RemoveAction();

	private JTable table = new JTable();

	private TuningsModel tableModel = new TuningsModel();

	public TuningsDockable() {
		config.read(this);

		config.get("table").read(tableModel);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setModel(tableModel);
		table.setDragEnabled(true);
		table.setDropMode(DropMode.INSERT_ROWS);
		table.setTransferHandler(new TransferHandler() {
			@Override
			public int getSourceActions(JComponent c) {
				return COPY | MOVE;
			}

			@Override
			public boolean canImport(TransferSupport support) {
				return true;
			}

			@Override
			protected Transferable createTransferable(JComponent c) {
				int[] rows = table.getSelectedRows();

				Tuning[] subTunings = new Tuning[rows.length];
				for (int t = 0; t < subTunings.length; t++) {
					subTunings[t] = tunings.get(rows[t]);
				}

				return new ObjectTransferable(subTunings);
			}

			@Override
			protected void exportDone(JComponent source, Transferable t,
					int action) {
				try {
					if (action == MOVE) {
						Tuning[] subTunings = (Tuning[]) ObjectTransferable
								.getObject(t);
						for (Tuning tuning : subTunings) {
							sound.removeTuning(tuning);
						}
					}
				} catch (Exception noExport) {
				}
			}

			@Override
			public void exportToClipboard(JComponent comp, Clipboard clip,
					int action) throws IllegalStateException {
				int[] rows = table.getSelectedRows();
				if (rows.length > 0) {
					Tuning[] subTunings = new Tuning[rows.length];
					for (int t = 0; t < subTunings.length; t++) {
						subTunings[t] = tunings.get(rows[t]);
					}

					for (Tuning tuning : subTunings) {
						if (action == DnDConstants.ACTION_MOVE) {
							sound.removeTuning(tuning);
						}
					}

					transferable = new ObjectTransferable(subTunings);

					clip.setContents(transferable, null);
				}
			}

			@Override
			public boolean importData(TransferSupport support) {
				try {
					int index = sound.getTuningCount();
					if (support.isDrop()) {
						JTable.DropLocation location = (JTable.DropLocation) support
								.getDropLocation();
						index = location.getRow();
					}
					Tuning[] subTunings = (Tuning[]) ObjectTransferable
							.getObject(support.getTransferable());
					for (Tuning tuning : subTunings) {
						sound.addTuning(tuning.clone(), index);
						index++;
					}

					return true;
				} catch (Exception noImport) {
					return false;
				}
			}
		});
		table.getSelectionModel().addListSelectionListener(selectionHandler);
		new IconTableCellRenderer(getIcon()).configureTableColumn(table, 0);
		table.getColumnModel().getColumn(1).setCellEditor(
				new StringCellEditor());
		for (int p = 0; p < Tuning.COUNT; p++) {
			table.getColumnModel().getColumn(p + 2).setCellEditor(
					new SpinnerCellEditor(-128.0d, 128.d, 1.0d));
		}
		TableUtils.pleasantLookAndFeel(table);

		setContent(new JScrollPane(table));
	}

	@Override
	public boolean forPlay() {
		return false;
	}

	@Override
	protected void addTools(Docked docked) {
		docked.addTool(addAction);
		docked.addTool(removeAction);
	}

	private void commitEdit() {
		CellEditor editor = table.getCellEditor();
		if (editor != null) {
			editor.stopCellEditing();
		}
	}

	/**
	 * Set the organ to be edited.
	 * 
	 * @param session
	 *            session to be edited
	 */
	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.getOrgan().removeOrganListener(
					(OrganListener) Spin.over(tableModel));
			this.session.lookup(ElementSelection.class).removeListener(
					selectionHandler);
		}

		this.session = session;

		if (this.session != null) {
			this.session.getOrgan().addOrganListener(
					(OrganListener) Spin.over(tableModel));
			this.session.lookup(ElementSelection.class).addListener(
					selectionHandler);
		}

		if (transferable != null) {
			transferable.clear();
			transferable = null;
		}

		updateTunings();
	}

	private boolean updating = false;

	private void updateTunings() {
		if (updating) {
			return;
		}

		try {
			updating = true;
			commitEdit();

			sound = null;
			tunings.clear();
			tableModel.update();
			table.setVisible(false);

			if (session != null
					&& session.lookup(ElementSelection.class)
							.getSelectionCount() == 1) {

				Element element = session.lookup(ElementSelection.class)
						.getSelectedElement();
				if (element instanceof FluidsynthSound) {
					sound = (FluidsynthSound) element;

					for (Tuning tuning : sound.getTunings()) {
						tunings.add(tuning);
					}

					tableModel.update();
					table.setVisible(true);
				}
			}

			addAction.update();
		} finally {
			updating = false;
		}
	}

	/**
	 * The handler of selections.
	 */
	private class SelectionHandler implements SelectionListener,
			ListSelectionListener {

		public void valueChanged(ListSelectionEvent e) {
			// TODO should change ElementSelection ?
			if (session != null) {
				session.lookup(UndoManager.class).compound();
			}

			removeAction.update();
		}

		public void selectionChanged() {
			updateTunings();
		}
	}

	public class TuningsModel extends AbstractTableModel implements
			OrganListener {

		private String[] columnNames = new String[14];

		private void update() {
			fireTableDataChanged();
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		public void setColumnNames(String[] columnNames) {
			if (columnNames.length != this.columnNames.length) {
				throw new IllegalArgumentException();
			}

			this.columnNames = columnNames;
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return tunings.size();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex > 0;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Tuning tuning = tunings.get(rowIndex);

			if (columnIndex == 0) {
				return null;
			} else if (columnIndex == 1) {
				return tuning.getName();
			} else {
				return tuning.getDerivation(columnIndex - 2);
			}
		}

		@Override
		public void setValueAt(Object value, int rowIndex, int columnIndex) {
			Tuning tuning = tunings.get(rowIndex);

			String name = tuning.getName();
			double[] derivations = tuning.getDerivations();
			if (columnIndex == 1) {
				name = (String) value;
			} else {
				derivations[columnIndex - 2] = (Double) value;
			}

			sound.changeTuning(tuning, name, derivations);
		}

		public void indexedPropertyAdded(Element element, String name,
				Object value) {
			if (FluidsynthSound.TUNINGS.equals(name)
					&& element == TuningsDockable.this.sound) {
				Tuning tuning = (Tuning) value;

				updateTunings();

				int index = tunings.indexOf(tuning);
				table.getSelectionModel().setSelectionInterval(index, index);
			}
		}

		public void indexedPropertyRemoved(Element element, String name,
				Object value) {
			if (FluidsynthSound.TUNINGS.equals(name)
					&& element == TuningsDockable.this.sound) {
				updateTunings();
			}
		}

		public void indexedPropertyChanged(Element element, String name,
				Object value) {
			if (FluidsynthSound.TUNINGS.equals(name)
					&& element == TuningsDockable.this.sound) {
				Tuning tuning = (Tuning) value;

				updateTunings();

				int index = tunings.indexOf(tuning);
				table.getSelectionModel().setSelectionInterval(index, index);
			}
		}

		public void elementAdded(Element element) {
		}

		public void elementRemoved(Element element) {
		}

		public void propertyChanged(Element element, String name) {
		}
	}

	private class AddAction extends BaseAction {

		private AddAction() {
			config.get("add").read(this);

			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			sound.addTuning(new Tuning());
		}

		public void update() {
			setEnabled(sound != null);
		}
	}

	private class RemoveAction extends BaseAction implements Compound {

		private RemoveAction() {
			config.get("remove").read(this);

			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			session.lookup(UndoManager.class).compound(this);
		}

		public void update() {
			setEnabled(table.getSelectedRow() != -1);
		}

		public void run() {
			int[] indices = table.getSelectedRows();
			if (indices != null) {
				for (int i = indices.length - 1; i >= 0; i--) {
					Tuning tuning = tunings.get(indices[i]);

					sound.removeTuning(tuning);
				}
			}
		}
	}
}