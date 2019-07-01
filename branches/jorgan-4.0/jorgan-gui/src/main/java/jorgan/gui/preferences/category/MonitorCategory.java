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
package jorgan.gui.preferences.category;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.gui.dock.MonitorView;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * Category.
 */
public class MonitorCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			MonitorCategory.class);

	private Model<Integer> max = getModel(new Property(MonitorView.class, "max"));

	private Model<Boolean> skip = getModel(new Property(MonitorView.class,
			"skip"));

	private JSpinner maxSpinner = new JSpinner(new SpinnerNumberModel(1, 1,
			100000, 50));

	private JCheckBox skipCheckBox = new JCheckBox();

	public MonitorCategory() {
		config.read(this);
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);
		Column column = builder.column();

		column.term(config.get("max").read(new JLabel()));
		column.definition(maxSpinner);

		column.definition(config.get("skip").read(skipCheckBox));

		return panel;
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected void read() {
		maxSpinner.setValue(max.getValue());
		skipCheckBox.setSelected(skip.getValue());
	}

	@Override
	protected void write() {
		max.setValue((Integer) maxSpinner.getValue());
		skip.setValue(skipCheckBox.isSelected());
	}
}