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
package jorgan.gui.imports;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import jorgan.disposition.Rank;
import jorgan.swing.BaseAction;
import jorgan.swing.table.TableUtils;
import bias.Configuration;

/**
 * A selection of stops.
 */
public class RankSelectionPanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			RankSelectionPanel.class);

	private Action allAction = new AllAction();

	private Action noneAction = new NoneAction();

	private JScrollPane scrollPane = new JScrollPane();

	private JTable table = new JTable();

	private RanksModel tableModel = new RanksModel();

	private List<Rank> ranks = new ArrayList<Rank>();

	/**
	 * Constructor.
	 */
	public RankSelectionPanel() {
		setLayout(new BorderLayout(10, 10));

		add(scrollPane, BorderLayout.CENTER);

		table.setModel(tableModel);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						firePropertyChange("selectedStops", null, null);
					}
				});
		TableUtils.pleasantLookAndFeel(table);
		scrollPane.setViewportView(table);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		add(buttonPanel, BorderLayout.SOUTH);

		JPanel gridPanel = new JPanel(new GridLayout(1, 0, 2, 2));
		buttonPanel.add(gridPanel, BorderLayout.EAST);

		gridPanel.add(new JButton(allAction));

		gridPanel.add(new JButton(noneAction));
	}

	/**
	 * Set the ranks to select from.
	 * 
	 * @param ranks
	 *            the ranks
	 */
	public void setRanks(List<Rank> ranks) {
		this.ranks = ranks;

		tableModel.fireTableDataChanged();
	}

	/**
	 * Get the selected ranks.
	 * 
	 * @return selected ranks
	 */
	public List<Rank> getSelectedRanks() {
		int[] rows = table.getSelectedRows();

		ArrayList<Rank> selectedRanks = new ArrayList<Rank>();
		for (int r = 0; r < rows.length; r++) {
			selectedRanks.add(ranks.get(rows[r]));
		}

		return selectedRanks;
	}

	private class AllAction extends BaseAction {

		private AllAction() {
			config.get("all").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			table.selectAll();
		}
	}

	private class NoneAction extends BaseAction {

		private NoneAction() {
			config.get("none").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			table.clearSelection();
		}
	}

	public class RanksModel extends AbstractTableModel {

		private String[] columnNames = new String[getColumnCount()];
		
		public RanksModel() {
			config.get("table").read(this);
		}
		
		public void setColumnNames(String[] columnNames) {
			if (columnNames.length != this.columnNames.length) {
				throw new IllegalArgumentException("length " + columnNames.length);
			}
			this.columnNames = columnNames;
		}
		
		@Override
		public Class<?> getColumnClass(int columnIndex) {

			return String.class;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return ranks.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Rank rank = ranks.get(rowIndex);
			if (columnIndex == 0) {
				return rank.getName();
			} else {
				return rank.getProgram();
			}
		}
	}
}