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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midimerger.MidiMergerProvider;
import jorgan.midimerger.merging.Merger;
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

	private JTable table = new JTable();

	private MergersModel tableModel = new MergersModel();

	public MergingPanel(List<Merger> mergers) {
		config.read(this);

		init(mergers);

		table.setModel(tableModel);
		table.setDefaultEditor(Integer.class, new SpinnerCellEditor(0, 16, 1));
		TableUtils.pleasantLookAndFeel(table);
		TableUtils.fixColumnWidth(table, 0, Boolean.TRUE);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(0, 0));
		add(scrollPane, BorderLayout.CENTER);
	}

	public class MergersModel extends AbstractTableModel {

		private String[] columnNames = new String[getColumnCount()];

		public MergersModel() {
			config.get("table").read(tableModel);
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

	private void init(List<Merger> mergers) {

		this.selectedMergers = new HashSet<Merger>(mergers);

		allMergers = new ArrayList<Merger>(mergers);
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
}