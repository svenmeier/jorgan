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
package jorgan.creative.gui.imports;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;

/**
 * A panel for options.
 */
public class OptionsPanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			OptionsPanel.class);

	private JComboBox comboBox = new JComboBox();

	private JScrollPane scrollPane = new JScrollPane();

	private JTable table = new JTable();

	private JCheckBox stopsCheckBox = new JCheckBox();

	private JCheckBox touchSensitiveCheckBox = new JCheckBox();

	public OptionsPanel(Device[] devices) {
		DefinitionBuilder builder = new DefinitionBuilder(this);
		Column column = builder.column();

		column.term(config.get("device").read(new JLabel()));

		comboBox.setModel(new DefaultComboBoxModel(devices));
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				((BanksModel) table.getModel()).fireTableDataChanged();
				firePropertyChange("stops", null, null);
			}
		});
		column.definition(comboBox).fillHorizontal();

		column.term(config.get("bank").read(new JLabel()));

		scrollPane.getViewport().setBackground(Color.white);
		column.definition(scrollPane).growVertical().fillBoth();

		table.setModel(new BanksModel());
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						firePropertyChange("stops", null, null);
					}
				});
		scrollPane.setViewportView(table);

		column.definition(config.get("stops").read(stopsCheckBox));

		column.definition(config.get("touchSensitive").read(
				touchSensitiveCheckBox));
	}

	public class BanksModel extends AbstractTableModel {

		private String[] columnNames = new String[2];

		public BanksModel() {
			config.get("table").read(this);
		}

		public void setColumnNames(String[] columnNames) {
			if (columnNames.length != this.columnNames.length) {
				throw new IllegalArgumentException("length "
						+ columnNames.length);
			}
			this.columnNames = columnNames;
		}

		public Class<?> getColumnClass(int columnIndex) {
			if (columnIndex == 0) {
				return Integer.class;
			} else {
				return String.class;
			}
		}

		public String getColumnName(int column) {
			return columnNames[column];
		}

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			Device device = (Device) comboBox.getSelectedItem();
			if (device == null) {
				return 0;
			} else {
				return device.banks.size();
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Device device = (Device) comboBox.getSelectedItem();

			Bank bank = device.banks.get(rowIndex);
			if (columnIndex == 0) {
				return new Integer(bank.number);
			} else {
				return bank.name;
			}
		}
	}

	public Device getSelectedDevice() {
		return (Device) comboBox.getSelectedItem();
	}
	
	public Bank getSelectedBank() {
		Device device = getSelectedDevice();
		if (device == null) {
			return null;
		}

		int index = table.getSelectedRow();
		if (index == -1) {
			return null;
		}

		return device.banks.get(index);
	}

	public boolean getCreateStops() {
		return stopsCheckBox.isSelected();
	}

	public boolean getTouchSensitive() {
		return touchSensitiveCheckBox.isSelected();
	}
}