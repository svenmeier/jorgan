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
package jorgan.gui.dock;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import jorgan.disposition.Elements;
import jorgan.gui.OrganPanel;
import jorgan.session.OrganSession;
import jorgan.session.problem.Problem;
import jorgan.session.problem.ProblemListener;
import jorgan.session.problem.Severity;
import jorgan.swing.BaseAction;
import jorgan.swing.table.IconTableCellRenderer;
import jorgan.swing.table.TableUtils;
import bias.Configuration;

/**
 * Panel shows the {@link Problem}s.
 */
public class ProblemsDockable extends OrganDockable {

	private static Configuration config = Configuration.getRoot().get(
			ProblemsDockable.class);

	/**
	 * Icon used for indication of a warning.
	 */
	private static final Icon warningIcon = new ImageIcon(OrganPanel.class
			.getResource("img/warning.gif"));

	/**
	 * Icon used for indication of an error.
	 */
	private static final Icon errorIcon = new ImageIcon(OrganPanel.class
			.getResource("img/error.gif"));

	private OrganSession session;

	private JTable table = new JTable();

	private ProblemsModel tableModel = new ProblemsModel();

	private List<Problem> problems = new ArrayList<Problem>();

	private JPopupMenu popup = new JPopupMenu();

	private GotoAction gotoAction = new GotoAction();

	/**
	 * Create a tree panel.
	 */
	public ProblemsDockable() {
		config.read(this);

		config.get("table").read(tableModel);
		table.setModel(tableModel);
		TableUtils.addActionListener(table, gotoAction);
		TableUtils.addPopup(table, popup);
		TableUtils.pleasantLookAndFeel(table);
		new IconTableCellRenderer() {
			@Override
			protected Icon getIcon(Object value) {
				if (value == Severity.WARNING) {
					return warningIcon;
				} else {
					return errorIcon;
				}
			}
		}.configureTableColumn(table, 0);
		setContent(new JScrollPane(table));

		popup.add(gotoAction);
	}

	/**
	 * Set the organ.
	 * 
	 * @param session
	 *            the organ
	 */
	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.removeProblemListener(tableModel);

			problems.clear();
		}

		this.session = session;

		if (this.session != null) {
			this.session.addProblemListener(tableModel);

			this.problems.addAll(session.getProblems().getProblems());
		}

		tableModel.fireTableDataChanged();
	}

	public class ProblemsModel extends AbstractTableModel implements
			ProblemListener {

		private String[] columnNames = new String[getColumnCount()];

		public int getColumnCount() {
			return 3;
		}

		public void setColumnNames(String[] columnNames) {
			if (columnNames.length != this.columnNames.length) {
				throw new IllegalArgumentException("length "
						+ columnNames.length);
			}
			this.columnNames = columnNames;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		public int getRowCount() {
			return problems.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Problem problem = problems.get(rowIndex);

			switch (columnIndex) {
			case 0:
				return problem.getSeverity();
			case 1:
				return problem.getMessage();
			case 2:
				return Elements.getDisplayName(problem.getElement());
			}

			return null;
		}

		public void problemAdded(Problem problem) {
			problems.add(problem);

			fireTableDataChanged();
		}

		public void problemRemoved(Problem problem) {
			problems.remove(problem);

			fireTableDataChanged();
		}
	}

	private class GotoAction extends BaseAction {

		private GotoAction() {
			config.get("goto").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			int index = table.getSelectedRow();

			Problem problem = problems.get(index);

			session.getSelection().setSelectedElement(
					problem.getElement(), problem.getLocation());
			
			session.setConstructing(true);
		}
	}
}