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
package jorgan.executor.gui.preferences.category;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;
import jorgan.executor.Executions;
import jorgan.gui.preferences.category.AppCategory;
import jorgan.gui.preferences.category.JOrganCategory;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import jorgan.swing.text.MultiLineLabel;

/**
 * {@link jorgan.App} category.
 */
public class ExecutorCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot()
			.get(ExecutorCategory.class);

	private Model<Boolean> allowed = getModel(
			new Property(Executions.class, "allowed"));

	private Model<Boolean> poll = getModel(
			new Property(Executions.class, "poll"));

	private JCheckBox executionsAllowedCheckBox = new JCheckBox();

	private JCheckBox pollOutputCheckBox = new JCheckBox();

	public ExecutorCategory() {
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

		column.box(config.get("description").read(new MultiLineLabel()));

		column.definition(
				config.get("allowExecute").read(executionsAllowedCheckBox));

		column.definition(config.get("pollOutput").read(pollOutputCheckBox));

		return panel;
	}

	@Override
	protected void read() {
		executionsAllowedCheckBox.setSelected(allowed.getValue());
		pollOutputCheckBox.setSelected(poll.getValue());
	}

	@Override
	protected void write() {
		allowed.setValue(executionsAllowedCheckBox.isSelected());
		poll.setValue(pollOutputCheckBox.isSelected());
	}
}