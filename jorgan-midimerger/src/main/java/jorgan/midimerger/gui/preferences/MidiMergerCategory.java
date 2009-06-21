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
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import jorgan.gui.preferences.category.AppCategory;
import jorgan.gui.preferences.category.JOrganCategory;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midimerger.MergeInput;
import jorgan.midimerger.MidiMerger;
import jorgan.midimerger.MidiMergerProvider;
import jorgan.swing.table.SpinnerCellEditor;
import jorgan.swing.table.TableUtils;
import jorgan.swing.text.MultiLineLabel;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * {@link MidiMerger} category.
 */
public class MidiMergerCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			MidiMergerCategory.class);

	private Model inputs = getModel(new Property(MidiMerger.class, "inputs"));

	/**
	 * All available inputs.
	 */
	private List<MergeInput> allInputs = new ArrayList<MergeInput>();

	/**
	 * All currently selected inputs - this is a subset of {@link #allInputs}.
	 */
	private List<MergeInput> selectedInputs;

	private JScrollPane scrollPane = new JScrollPane();

	private JTable table = new JTable();

	/**
	 * The table model for the mergeInputs.
	 */
	private InputsModel tableModel = new InputsModel();

	public MidiMergerCategory() {
		config.read(this);

		config.get("table").read(tableModel);
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel(new BorderLayout());

		panel.add(config.get("description").read(new MultiLineLabel()),
				BorderLayout.NORTH);

		table.setModel(tableModel);
		table.setDefaultEditor(Integer.class, new SpinnerCellEditor(0, 16, 1));
		TableUtils.pleasantLookAndFeel(table);
		TableUtils.fixColumnWidth(table, 0, Boolean.TRUE);
		scrollPane.setViewportView(table);
		scrollPane.setPreferredSize(new Dimension(0, 0));
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * The table model for handling of inputs to the Midi-Merger.
	 */
	public class InputsModel extends AbstractTableModel {

		private String[] columnNames = new String[getColumnCount()];

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
			return allInputs.size();
		}

		public Object getValueAt(int row, int column) {
			MergeInput input = allInputs.get(row);

			switch (column) {
			case 0:
				return selectedInputs.contains(input) ? Boolean.TRUE
						: Boolean.FALSE;
			case 1:
				return input.getDevice();
			case 2:
				return new Integer(input.getChannel() + 1);
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			MergeInput input = allInputs.get(row);

			return column == 0
					|| (selectedInputs.contains(input) && column == 2);
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			MergeInput input = allInputs.get(row);

			switch (column) {
			case 0:
				Boolean selected = (Boolean) aValue;
				if (selected.booleanValue()) {
					if (!selectedInputs.contains(input)) {
						selectedInputs.add(input);
					}
				} else {
					if (selectedInputs.contains(input)) {
						input.setChannel(-1);
						selectedInputs.remove(input);
					}
				}
				break;
			case 2:
				Integer channel = (Integer) aValue;
				input.setChannel(channel.intValue() - 1);
				break;
			}

			this.fireTableRowsUpdated(row, row);
		}
	}

	private int indexOfMergeInput(String device) {
		for (int i = 0; i < allInputs.size(); i++) {
			MergeInput input = allInputs.get(i);
			if (device.equals(input.getDevice())) {
				return i;
			}
		}
		return -1;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void read() {
		// create inputs for all devices (excluding MidiMerger)
		allInputs = new ArrayList<MergeInput>();

		String[] devices = DevicePool.instance().getMidiDeviceNames(
				Direction.IN);
		for (String device : devices) {
			if (!MidiMergerProvider.INFO.getName().equals(device)) {
				allInputs.add(new MergeInput(device, -1));
			}
		}

		// get all currently selected inputs
		selectedInputs = (List<MergeInput>) inputs.getValue();
		for (MergeInput selectedInput : selectedInputs) {
			int index = indexOfMergeInput(selectedInput.getDevice());
			if (index == -1) {
				allInputs.add(selectedInput);
			} else {
				allInputs.set(index, selectedInput);
			}
		}

		tableModel.fireTableDataChanged();
	}

	@Override
	protected void write() {
		inputs.setValue(selectedInputs);
	}
}