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
package jorgan.gui.play;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.gui.OrganPanel;
import jorgan.play.Problem;
import jorgan.session.OrganSession;
import jorgan.session.SessionAware;
import jorgan.session.event.ProblemListener;
import jorgan.session.event.Warning;
import jorgan.swing.BaseAction;
import jorgan.swing.table.IconTableCellRenderer;
import jorgan.swing.table.TableUtils;
import swingx.docking.DockedPanel;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * Panel shows the problems.
 */
public class ProblemsPanel extends DockedPanel implements SessionAware {

	private static Configuration config = Configuration.getRoot().get(
			ProblemsPanel.class);

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

	private List<Row> rows = new ArrayList<Row>();

	private JPopupMenu popup = new JPopupMenu();

	private GotoAction gotoAction = new GotoAction();

	/**
	 * Create a tree panel.
	 */
	public ProblemsPanel() {

		config.get("table").read(tableModel);
		table.setModel(tableModel);
		TableUtils.addActionListener(table, gotoAction);
		TableUtils.addPopup(table, popup);
		TableUtils.pleasantLookAndFeel(table);
		new IconTableCellRenderer() {
			@Override
			protected Icon getIcon(Object value) {
				if (value instanceof Warning) {
					return warningIcon;
				} else {
					return errorIcon;
				}
			}
		}.configureTableColumn(table, 0);
		setScrollableBody(table, true, false);

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

			rows.clear();
		}

		this.session = session;

		if (this.session != null) {
			this.session.addProblemListener(tableModel);

			for (Problem problem : session.getProblems().getProblems()) {
				rows.add(new Row(problem));
			}
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
			return rows.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Row row = rows.get(rowIndex);

			switch (columnIndex) {
			case 0:
				return row.getProblem();
			case 1:
				return row.getMessage();
			case 2:
				return Elements.getDisplayName(row.getElement());
			}

			return null;
		}

		public void problemAdded(Problem problem) {
			rows.add(new Row(problem));

			fireTableDataChanged();
		}

		public void problemRemoved(Problem problem) {
			rows.remove(new Row(problem));

			fireTableDataChanged();
		}
	}

	private class Row {

		private Problem problem;

		private String message;

		private Row(Problem problem) {
			this.problem = problem;

			message = config.get(problem.toString()).read(new MessageBuilder())
					.build(problem.getValue());
		}

		private Element getElement() {
			return problem.getElement();
		}

		private Problem getProblem() {
			return problem;
		}

		private String getMessage() {
			return message;
		}

		@Override
		public int hashCode() {
			return problem.hashCode();
		}

		@Override
		public boolean equals(Object object) {
			if (!(object instanceof Row)) {
				return false;
			}

			Row row = (Row) object;

			return row.problem.equals(this.problem);
		}
	}

	private class GotoAction extends BaseAction {

		private GotoAction() {
			config.get("goto").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			int index = table.getSelectedRow();

			Row row = rows.get(index);

			session.getSelectionModel().setSelectedElement(row.getElement(),
					row.getProblem().getProperty());
		}
	}
}