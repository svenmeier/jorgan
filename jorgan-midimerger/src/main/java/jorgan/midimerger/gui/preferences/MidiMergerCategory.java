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
package jorgan.midimerger.gui.preferences;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.gui.preferences.category.AppCategory;
import jorgan.gui.preferences.category.JOrganCategory;
import jorgan.midimerger.MidiMerger;
import jorgan.midimerger.MidiMergerProvider;
import jorgan.midimerger.merging.Merging;
import jorgan.swing.BaseAction;
import jorgan.swing.layout.FlowBuilder;
import jorgan.swing.layout.FlowBuilder.Flow;
import jorgan.swing.list.ListUtils;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * {@link MidiMerger} category.
 */
public class MidiMergerCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			MidiMergerCategory.class);

	private Model<Set<Merging>> mergings = getModel(new Property(
			MidiMergerProvider.class, "mergings"));

	private JList list;

	private EditAction editAction = new EditAction();

	private AddAction addAction = new AddAction();

	private RemoveAction removeAction = new RemoveAction();

	public MidiMergerCategory() {
		config.read(this);
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel(new BorderLayout());

		list = new JList();
		list.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				editAction.update();
				addAction.update();
				removeAction.update();
			}
		});
		ListUtils.addActionListener(list, 2, editAction);
		panel.add(new JScrollPane(list), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		panel.add(buttonPanel, BorderLayout.EAST);

		Flow flow = new FlowBuilder(buttonPanel, FlowBuilder.TOP).flow();
		flow.add(new JButton(editAction));
		flow.add(new JButton(addAction));
		flow.add(new JButton(removeAction));

		return panel;
	}

	@Override
	protected void read() {
		initModel();
	}

	@Override
	protected void write() {
	}

	private void initModel() {
		list.setModel(new MergingsModel());
	}

	private class MergingsModel extends AbstractListModel {

		private List<Merging> mergings;

		public MergingsModel() {
			mergings = new ArrayList<Merging>(MidiMergerCategory.this.mergings
					.getValue());
		}

		@Override
		public int getSize() {
			return mergings.size();
		}

		@Override
		public Object getElementAt(int index) {
			return mergings.get(index);
		}
	}

	private class EditAction extends BaseAction {
		public EditAction() {
			config.get("edit").read(this);

			setEnabled(false);
		}

		public void update() {
			setEnabled(list.getSelectedValue() != null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			MergingPanel.showInDialog(list, (Merging) list.getSelectedValue());

			initModel();
		}
	}

	private class AddAction extends BaseAction {
		public AddAction() {
			config.get("add").read(this);
		}

		public void update() {
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Merging merging = new Merging();

			if (MergingPanel.showInDialog(list, merging)) {
				mergings.getValue().add(merging);
			}

			initModel();
		}
	}

	private class RemoveAction extends BaseAction {
		public RemoveAction() {
			config.get("remove").read(this);

			setEnabled(false);
		}

		public void update() {
			setEnabled(list.getSelectedValue() != null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			mergings.getValue().remove(list.getSelectedValue());

			initModel();
		}
	}
}