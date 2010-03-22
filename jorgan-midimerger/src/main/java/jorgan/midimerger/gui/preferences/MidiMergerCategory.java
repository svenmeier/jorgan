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
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import jorgan.gui.preferences.category.AppCategory;
import jorgan.gui.preferences.category.JOrganCategory;
import jorgan.midimerger.Mapping;
import jorgan.midimerger.MidiMerger;
import jorgan.midimerger.Mapping.Mode;
import jorgan.swing.table.EnumCellEditor;
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

	private Model mapping = getModel(new Property(MidiMerger.class, "mapping"));

	private List<String> names;

	private JScrollPane scrollPane = new JScrollPane();

	private JTable table = new JTable();

	/**
	 * The table model for the mergeInputs.
	 */
	private ChannelsModel tableModel = new ChannelsModel();

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
		table.getColumnModel().getColumn(1).setCellEditor(
				new EnumCellEditor(Mode.class));
		TableUtils.pleasantLookAndFeel(table);
		scrollPane.setViewportView(table);
		scrollPane.setPreferredSize(new Dimension(0, 0));
		panel.add(scrollPane, BorderLayout.CENTER);

		return panel;
	}

	public class ChannelsModel extends AbstractTableModel {

		private String[] columnNames = new String[getColumnCount()];

		public void setColumnNames(String[] names) {
			if (columnNames.length != this.columnNames.length) {
				throw new IllegalArgumentException("length "
						+ columnNames.length);
			}
			this.columnNames = names;
		}

		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		public int getRowCount() {
			return names.size();
		}

		public Object getValueAt(int row, int column) {
			switch (column) {
			case 0:
				return names.get(row);
			case 1:
				return ((Mapping) mapping.getValue()).getMode(row);
			}
			return null;
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return column == 1;
		}

		@Override
		public void setValueAt(Object aValue, int row, int column) {
			Mode mode = (Mode) aValue;

			((Mapping) mapping.getValue()).setMode(row, mode);

			this.fireTableRowsUpdated(row, row);
		}
	}

	@Override
	protected void read() {
		names = MidiMerger.getDeviceNames();

		tableModel.fireTableDataChanged();
	}

	@Override
	protected void write() {
	}
}