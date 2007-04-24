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

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import jorgan.disposition.Stop;
import jorgan.swing.table.TableUtils;
import jorgan.util.I18N;

/**
 * A selection of stops.
 */
public class StopSelectionPanel extends JPanel {

	private static I18N i18n = I18N.get(StopSelectionPanel.class);

	private Action allAction = new AllAction();

	private Action noneAction = new NoneAction();

	private JScrollPane scrollPane = new JScrollPane();

	private JTable table = new JTable();

	private StopModel stopModel = new StopModel();

	private List<Stop> stops = new ArrayList<Stop>();

	/**
	 * Constructor.
	 */
	public StopSelectionPanel() {
		setLayout(new BorderLayout(10, 10));

		add(scrollPane, BorderLayout.CENTER);

		table.setModel(stopModel);
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
	 * Set the stops to select from.
	 * 
	 * @param stops
	 *            the stops
	 */
	public void setStops(List<Stop> stops) {
		this.stops = stops;

		stopModel.fireTableDataChanged();
	}

	/**
	 * Get the selected stops.
	 * 
	 * @return selected stops
	 */
	public List<Stop> getSelectedStops() {
		int[] rows = table.getSelectedRows();

		ArrayList<Stop> selectedStops = new ArrayList<Stop>();
		for (int r = 0; r < rows.length; r++) {
			selectedStops.add(stops.get(rows[r]));
		}

		return selectedStops;
	}

	private class AllAction extends AbstractAction {

		private AllAction() {
			putValue(Action.NAME, i18n.getString("allAction/name"));
		}

		public void actionPerformed(ActionEvent ev) {
			table.selectAll();
		}
	}

	private class NoneAction extends AbstractAction {

		private NoneAction() {
			putValue(Action.NAME, i18n.getString("noneAction/name"));
		}

		public void actionPerformed(ActionEvent ev) {
			table.clearSelection();
		}
	}

	private class StopModel extends AbstractTableModel {

		public Class<?> getColumnClass(int columnIndex) {

			return String.class;
		}

		public String getColumnName(int column) {
			if (column == 0) {
				return i18n.getString("stopName");
			} else {
				return i18n.getString("stopProgram");
			}
		}

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return stops.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Stop stop = stops.get(rowIndex);
			if (columnIndex == 0) {
				return stop.getName();
			} else {
				return new Integer(stop.getProgram());
			}
		}
	}
}