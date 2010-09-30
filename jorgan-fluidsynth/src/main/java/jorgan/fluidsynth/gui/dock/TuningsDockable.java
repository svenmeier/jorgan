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

import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganListener;
import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.fluidsynth.disposition.Tuning;
import jorgan.fluidsynth.gui.construct.CreateTuningWizard;
import jorgan.gui.dock.OrganDockable;
import jorgan.gui.selection.ElementSelection;
import jorgan.gui.selection.SelectionListener;
import jorgan.gui.undo.Compound;
import jorgan.gui.undo.UndoManager;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.table.BaseTableModel;
import jorgan.swing.table.SimpleCellRenderer;
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
			public boolean importData(final TransferSupport support) {
				try {
					final Tuning[] subTunings = (Tuning[]) ObjectTransferable
							.getObject(support.getTransferable());

					session.lookup(UndoManager.class).compound(new Compound() {
						@Override
						public void run() {
							int index = sound.getTuningCount();
							if (support.isDrop()) {
								JTable.DropLocation location = (JTable.DropLocation) support
										.getDropLocation();
								index = location.getRow();
							}
							for (Tuning tuning : subTunings) {
								sound.addTuning(tuning.clone(), index);
								index++;
							}
						}
					});

					return true;
				} catch (Exception noImport) {
					return false;
				}

			}
		});
		table.getSelectionModel().addListSelectionListener(selectionHandler);
		table.getColumnModel().getColumn(0).setCellRenderer(
				new SimpleCellRenderer<Tuning>() {
					@Override
					protected void init(Tuning value) {
						setIcon(TuningsDockable.this.getIcon());
					}
				});
		TableUtils.fixColumnWidth(table, 0, null);
		table.getColumnModel().getColumn(1).setCellEditor(
				new StringCellEditor());
		for (int p = 0; p < Tuning.COUNT; p++) {
			table.getColumnModel().getColumn(p + 2).setCellEditor(
					new SpinnerCellEditor(-100.0d, +100.0d, 1.0d));
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

			TableUtils.stopEdit(table);

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

				Object location = session.lookup(ElementSelection.class)
						.getLocation();
				if (location instanceof Tuning) {
					int row = tunings.indexOf(location);
					if (row != -1) {
						table.getSelectionModel()
								.setSelectionInterval(row, row);
					}
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

	public class TuningsModel extends BaseTableModel<Tuning> implements
			OrganListener {

		private void update() {
			fireTableDataChanged();
		}

		public int getColumnCount() {
			return 14;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex >= 2) {
				return Double.class;
			}

			return super.getColumnClass(columnIndex);
		}

		public int getRowCount() {
			return tunings.size();
		}

		@Override
		protected Tuning getRow(int rowIndex) {
			return tunings.get(rowIndex);
		}

		@Override
		protected boolean isEditable(Tuning row, int columnIndex) {
			return columnIndex > 0;
		}

		@Override
		protected Object getValue(Tuning tuning, int columnIndex) {
			if (columnIndex == 0) {
				return null;
			} else if (columnIndex == 1) {
				return tuning.getName();
			} else {
				return tuning.getDerivation(columnIndex - 2);
			}
		}

		@Override
		protected void setValue(Tuning tuning, int columnIndex, Object value) {
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
				updateTunings();
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
				updateTunings();
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

		public void update() {
			setEnabled(sound != null);
		}

		public void actionPerformed(ActionEvent ev) {
			CreateTuningWizard.showInDialog(getContent(), session, sound);
		}
	}

	private class RemoveAction extends BaseAction implements Compound {

		private RemoveAction() {
			config.get("remove").read(this);

			setEnabled(false);
		}

		public void update() {
			setEnabled(table.getSelectedRow() != -1);
		}

		public void actionPerformed(ActionEvent ev) {
			table.removeEditor();

			session.lookup(UndoManager.class).compound(this);
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