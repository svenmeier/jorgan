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
package jorgan.midimapper.gui.preferences;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jorgan.gui.preferences.category.AppCategory;
import jorgan.gui.preferences.category.JOrganCategory;
import jorgan.midimapper.MidiMapper;
import jorgan.midimapper.MidiMapperProvider;
import jorgan.midimapper.mapping.Mapping;
import jorgan.swing.BaseAction;
import jorgan.swing.layout.FlowBuilder;
import jorgan.swing.layout.FlowBuilder.Flow;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * {@link MidiMapper} category.
 */
public class MidiMapperCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			MidiMapperCategory.class);

	private Model mappings = getModel(new Property(MidiMapperProvider.class,
			"mappings"));
	private JList list;

	public MidiMapperCategory() {
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
		panel.add(new JScrollPane(list), BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel();
		panel.add(buttonPanel, BorderLayout.EAST);

		Flow flow = new FlowBuilder(buttonPanel, FlowBuilder.TOP).flow();
		flow.add(new JButton(new EditAction()));
		flow.add(new JButton(new AddAction()));
		flow.add(new JButton(new RemoveAction()));

		return panel;
	}

	@Override
	protected void read() {
		list.setModel(new MappingsModel());
	}

	@Override
	protected void write() {
	}

	private class MappingsModel extends AbstractListModel {

		@Override
		public int getSize() {
			return getMappings().size();
		}

		@SuppressWarnings("unchecked")
		private List<Mapping> getMappings() {
			return (List<Mapping>) mappings.getValue();
		}

		@Override
		public Object getElementAt(int index) {
			return getMappings().get(index).getName();
		}
	}

	private class EditAction extends BaseAction {
		public EditAction() {
			config.get("edit").read(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {

		}
	}

	private class AddAction extends BaseAction {
		public AddAction() {
			config.get("add").read(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {

		}
	}

	private class RemoveAction extends BaseAction {
		public RemoveAction() {
			config.get("remove").read(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {

		}
	}
}