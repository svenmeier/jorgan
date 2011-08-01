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
package jorgan.importer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * A selection of an {@link Import}.
 */
public class ImportSelectionPanel extends JPanel {

	private JScrollPane scrollPane = new JScrollPane();

	private JList list = new JList();

	private List<Import> imports = new ArrayList<Import>();

	/**
	 * Constructor.
	 */
	public ImportSelectionPanel() {
		setLayout(new BorderLayout(10, 10));

		scrollPane.getViewport().setBackground(Color.white);
		add(scrollPane, BorderLayout.CENTER);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						firePropertyChange("selectedImport", null, null);
					}
				});
		scrollPane.setViewportView(list);
	}

	/**
	 * Set the {@link Import}s to choose from.
	 * 
	 * @param imports
	 *            providers
	 */
	public void setImports(List<Import> imports) {
		this.imports = imports;

		list.setModel(new ImportsModel());
	}

	/**
	 * Get the selected {@link Import}.
	 * 
	 * @return import the selected import
	 */
	public Import getSelectedImport() {
		int index = list.getSelectedIndex();
		if (index == -1) {
			return null;
		} else {
			return imports.get(index);
		}
	}

	private class ImportsModel extends AbstractListModel {

		public int getSize() {
			return imports.size();
		}

		public Object getElementAt(int index) {
			Import aImport = (Import) imports.get(index);

			return aImport.getName();
		}
	}
}