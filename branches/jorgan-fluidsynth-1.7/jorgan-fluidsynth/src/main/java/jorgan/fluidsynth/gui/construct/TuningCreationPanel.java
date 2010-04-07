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
package jorgan.fluidsynth.gui.construct;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.fluidsynth.disposition.Tuning;

/**
 * A panel for a tuning.
 */
public class TuningCreationPanel extends JPanel {

	private JList list = new JList();

	private List<Tuning> tunings = new ArrayList<Tuning>();

	/**
	 * Constructor.
	 */
	public TuningCreationPanel() {
		setLayout(new BorderLayout());

		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				firePropertyChange("tuning", null, null);
			}
		});

		add(new JScrollPane(list), BorderLayout.CENTER);
	}

	/**
	 * Set the tunings to choose from.
	 */
	public void setTunings(List<Tuning> tunings) {
		this.tunings = tunings;

		list.setModel(new TuningsModel());
		if (!tunings.isEmpty()) {
			list.setSelectedIndex(0);
		}
	}

	public List<Tuning> getTunings() {
		List<Tuning> tunings = new ArrayList<Tuning>();

		for (int index : list.getSelectedIndices()) {
			tunings.add(this.tunings.get(index));
		}

		return tunings;
	}

	private class TuningsModel extends AbstractListModel {

		public int getSize() {
			return tunings.size();
		}

		public Object getElementAt(int index) {
			return tunings.get(index).getName();
		}
	}
}