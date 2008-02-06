package jorgan.creative.gui.imports;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

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

	public OptionsPanel(Device[] devices) {
		setLayout(new BorderLayout(2, 2));

		comboBox.setModel(new DefaultComboBoxModel(devices));
		comboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				((BanksModel) table.getModel()).fireTableDataChanged();
				firePropertyChange("stops", null, null);
			}
		});
		add(comboBox, BorderLayout.NORTH);

		scrollPane.getViewport().setBackground(Color.white);
		add(scrollPane, BorderLayout.CENTER);

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

	public Bank getSelectedBank() {
		Device device = (Device) comboBox.getSelectedItem();
		if (device == null) {
			return null;
		}

		int index = table.getSelectedRow();
		if (index == -1) {
			return null;
		}

		return device.banks.get(index);
	}
}