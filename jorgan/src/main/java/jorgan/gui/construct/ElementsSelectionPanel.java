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
package jorgan.gui.construct;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Element;
import jorgan.gui.ElementListCellRenderer;

/**
 * A panel for selection of elements.
 */
public class ElementsSelectionPanel extends JPanel {

	/**
	 * The resource bundle.
	 */
	protected static ResourceBundle resources = ResourceBundle
			.getBundle("jorgan.gui.resources");

	private Action allAction = new AllAction();

	private Action noneAction = new NoneAction();

	private JList elementsList = new JList();

	private List elements = new ArrayList();

	/**
	 * Constructor.
	 */
	public ElementsSelectionPanel() {
		setLayout(new BorderLayout(10, 10));

		elementsList
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		elementsList.setCellRenderer(new ElementListCellRenderer());
		elementsList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				firePropertyChange("selectedElements", null, null);
			}
		});
		add(new JScrollPane(elementsList), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		add(buttonPanel, BorderLayout.SOUTH);

		JPanel gridPanel = new JPanel(new GridLayout(1, 0, 2, 2));
		buttonPanel.add(gridPanel, BorderLayout.EAST);

		gridPanel.add(new JButton(allAction));

		gridPanel.add(new JButton(noneAction));
	}

	/**
	 * Set the elements to select from.
	 * 
	 * @param elements
	 *            the elements
	 */
	public void setElements(List elements) {
		this.elements = elements;

		Collections.sort(elements, new ElementComparator(false));

		elementsList.setModel(new ElementsModel());

		allAction.setEnabled(!elements.isEmpty());
		noneAction.setEnabled(!elements.isEmpty());
	}

	/**
	 * Set the selected elements.
	 * 
	 * @param elements
	 *            elements to be selected
	 */
	public void setSelectedElements(List elements) {
		for (int e = 0; e < elements.size(); e++) {
			Element element = (Element) elements.get(e);

			int index = elements.indexOf(element);
			if (index != -1) {
				elementsList.addSelectionInterval(index, index);
			}
		}
	}

	/**
	 * Get the selected elements.
	 * 
	 * @return the selected elements
	 */
	public List getSelectedElements() {

		return Arrays.asList(elementsList.getSelectedValues());
	}

	private class ElementsModel extends AbstractListModel {

		public int getSize() {
			return elements.size();
		}

		public Object getElementAt(int index) {
			return elements.get(index);
		}
	}

	private class AllAction extends AbstractAction {

		private AllAction() {
			putValue(Action.NAME, resources
					.getString("construct.create.references.elements.all"));
		}

		public void actionPerformed(ActionEvent ev) {
			elementsList.setSelectionInterval(0, elementsList.getModel()
					.getSize() - 1);
		}
	}

	private class NoneAction extends AbstractAction {

		private NoneAction() {
			putValue(Action.NAME, resources
					.getString("construct.create.references.elements.none"));
		}

		public void actionPerformed(ActionEvent ev) {
			elementsList.clearSelection();
		}
	}
}