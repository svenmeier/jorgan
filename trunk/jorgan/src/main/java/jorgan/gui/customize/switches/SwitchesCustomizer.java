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
package jorgan.gui.customize.switches;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import jorgan.disposition.Elements;
import jorgan.disposition.Switch;
import jorgan.gui.customize.Customizer;
import jorgan.session.OrganSession;
import jorgan.swing.table.TableUtils;
import bias.Configuration;

/**
 * Customizer of {@link Switch}es.
 */
public class SwitchesCustomizer implements Customizer {

	private static Configuration config = Configuration.getRoot().get(
			SwitchesCustomizer.class);

	private String description;

	private JScrollPane scrollPane;

	private JTable table;

	private SwitchesModel tableModel = new SwitchesModel();

	private List<Switch> switches;

	public SwitchesCustomizer(OrganSession session) {
		config.read(this);

		scrollPane = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));

		table = new JTable();
		config.get("table").read(tableModel);
		table.setModel(tableModel);
		TableUtils.pleasantLookAndFeel(table);
		scrollPane.setViewportView(table);

		switches = new ArrayList<Switch>(session.getOrgan().getElements(
				Switch.class));
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JComponent getComponent() {
		return scrollPane;
	}

	public void apply() {
		// TODO
	}

	public class SwitchesModel extends AbstractTableModel {

		private String[] columnNames = new String[3];

		public void setColumnNames(String[] columnNames) {
			this.columnNames = columnNames;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return switches.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Switch aSwitch = SwitchesCustomizer.this.switches.get(rowIndex);

			switch (columnIndex) {
			case 0:
				return Elements.getDisplayName(aSwitch);
			case 1:
				return null;
			case 2:
				return null;
			}

			throw new Error();
		}
	}
}