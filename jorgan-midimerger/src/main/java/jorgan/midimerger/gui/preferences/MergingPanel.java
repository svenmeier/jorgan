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
package jorgan.midimerger.gui.preferences;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midimerger.MidiMergerProvider;
import jorgan.midimerger.merging.Merger;
import jorgan.midimerger.merging.Merging;
import jorgan.swing.StandardDialog;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import jorgan.swing.table.SpinnerCellEditor;
import jorgan.swing.table.TableUtils;
import bias.Configuration;

/**
 */
public class MergingPanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			MergingPanel.class);

	private List<Merger> allMergers;

	/**
	 * All currently selected mergers - this is a subset of {@link #allMergers}.
	 */
	private Set<Merger> selectedMergers;

	private JTextField nameTextField;

	private JTable mergersTable;

	private Merging merging;

	public MergingPanel(Merging merging) {
		config.read(this);

		this.merging = merging;

		DefinitionBuilder builder = new DefinitionBuilder(this);

		Column column = builder.column();

		column.term(config.get("name").read(new JLabel()));

		nameTextField = new JTextField();
		column.definition(nameTextField).fillHorizontal();

		mergersTable = new JTable();
		mergersTable.setModel(new MergersModel());
		mergersTable.setDefaultEditor(Integer.class, new SpinnerCellEditor(0,
				16, 1));
		TableUtils.pleasantLookAndFeel(mergersTable);
		TableUtils.fixColumnWidth(mergersTable, 0, Boolean.TRUE);
		JScrollPane scrollPane = new JScrollPane(mergersTable);
		scrollPane.setPreferredSize(new Dimension(0, 0));
		column.box(scrollPane).growVertical();

		read();
	}

	private void read() {
		nameTextField.setText(merging.getName());
	}

	private void write() {
		merging.setName(nameTextField.getText());
	}

	public class MergersModel extends AbstractTableModel {

		private String[] columnNames = new String[getColumnCount()];

		public MergersModel() {
			config.get("mergers").read(this);

			selectedMergers = new HashSet<Merger>(merging.getMergers());

			allMergers = new ArrayList<Merger>(selectedMergers);
			String[] devices = DevicePool.instance().getMidiDeviceNames(
					Direction.IN);
			for (String device : devices) {
				if (!MidiMergerProvider.isMerger(device)) {
					if (!hasMerger(device)) {
						allMergers.add(new Merger(device, -1));
					}
				}
			}
		}

		public void setColumnNames(String[] names) {
			if (columnNames.length != this.columnNames.length) {
				throw new IllegalArgumentException("length "
						+ columnNames.length);
			}
			this.columnNames = names;
		}

		public int getColumnCount() {
			return 3;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		@Override
		public Class<?> getColumnClass(int column) {
			switch (column) {
			case 0:
				return Boolean.class;
			case 1:
				return String.class;
			case 2:
				return Integer.class;
			}
			return null;
		}

		public int getRowCount() {
			return allMergers.size();
		}

		public Object getValueAt(int row, int column) {
			Merger merger = allMergers.get(row);

			switch (column) {
			case 0:
				return selectedMergers.contains(merger) ? true : false;
			case 1:
				return merger.getDevice();
			case 2:
				return merger.getChannel() + 1;
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			Merger merger = allMergers.get(row);

			return column == 0
					|| (selectedMergers.contains(merger) && column == 2);
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			Merger merger = allMergers.get(row);

			switch (column) {
			case 0:
				if ((Boolean) aValue) {
					selectedMergers.add(merger);
				} else {
					merger.setChannel(-1);
					selectedMergers.remove(merger);
				}
				break;
			case 2:
				Integer channel = (Integer) aValue;
				merger.setChannel(channel.intValue() - 1);
				break;
			}

			this.fireTableRowsUpdated(row, row);
		}
	}

	private boolean hasMerger(String device) {
		for (Merger merger : allMergers) {
			if (device.equals(merger.getDevice())) {
				return true;
			}
		}
		return false;
	}

	public static boolean showInDialog(Component owner, Merging merging) {
		StandardDialog dialog = StandardDialog.create(owner);

		dialog.addOKAction();
		dialog.addCancelAction();

		MergingPanel mergingPanel = new MergingPanel(merging);
		dialog.setBody(mergingPanel);
		dialog.autoPosition();

		dialog.setVisible(true);

		if (dialog.wasCancelled()) {
			return false;
		} else {
			mergingPanel.write();
			return true;
		}
	}
}