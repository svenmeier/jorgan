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
package jorgan.exporter.gui;

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

import jorgan.exporter.gui.spi.ExportRegistry;

/**
 * A selection of an {@link Export}.
 */
public class ExportSelectionPanel extends JPanel {

	private JScrollPane scrollPane = new JScrollPane();

	private JList list = new JList();

	private List<Export> exports = new ArrayList<Export>();

	/**
	 * Constructor.
	 */
	public ExportSelectionPanel() {
		setLayout(new BorderLayout(10, 10));

		scrollPane.getViewport().setBackground(Color.white);
		add(scrollPane, BorderLayout.CENTER);

		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						firePropertyChange("selectedImportProvider", null, null);
					}
				});
		scrollPane.setViewportView(list);

		setExports(ExportRegistry.getImports());
	}

	/**
	 * Set the {@link Export}s to choose from.
	 * 
	 * @param exports
	 */
	public void setExports(List<Export> exports) {
		this.exports = exports;

		list.setModel(new ExportsModel());
	}

	/**
	 * Get the selected {@link Export}.
	 * 
	 * @return the selected export
	 */
	public Export getSelectedImport() {
		int index = list.getSelectedIndex();
		if (index == -1) {
			return null;
		} else {
			return exports.get(index);
		}
	}

	private class ExportsModel extends AbstractListModel {

		public int getSize() {
			return exports.size();
		}

		public Object getElementAt(int index) {
			Export aExport = (Export) exports.get(index);

			return aExport.getName();
		}
	}
}