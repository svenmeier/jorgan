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
package jorgan.fluidsynth.gui.preferences;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jorgan.fluidsynth.Fluidsynth;
import jorgan.fluidsynth.windows.BackendManager;
import jorgan.gui.preferences.category.AppCategory;
import jorgan.gui.preferences.category.JOrganCategory;
import jorgan.swing.combobox.BaseComboBoxModel;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * {@link Fluidsynth} category.
 */
public class FluidsynthCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			FluidsynthCategory.class);

	private Model<String> backend = getModel(new Property(BackendManager.class,
			"backend"));

	private JComboBox<String> backendComboBox;

	public FluidsynthCategory() {
		config.read(this);
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);

		Column column = builder.column();

		column.term(config.get("backend").read(new JLabel()));

		backendComboBox = new JComboBox<String>();
		column.definition(backendComboBox).fillHorizontal();

		return panel;
	}

	@Override
	protected void read() {
		BackendManager manager = new BackendManager();

		backendComboBox.setModel(new BaseComboBoxModel<String>(manager
				.getBackends()));

		backendComboBox.setSelectedItem(backend.getValue());
	}

	@Override
	protected void write() {
		backend.setValue((String) backendComboBox.getSelectedItem());
	}
}