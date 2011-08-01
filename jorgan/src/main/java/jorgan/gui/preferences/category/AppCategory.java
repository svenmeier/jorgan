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
import javax.swing.JPanel;

import jorgan.App;
import jorgan.session.OrganSession;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.util.Property;

/**
 * {@link jorgan.App} category.
 */
public class AppCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			AppCategory.class);

	private Model<Boolean> openRecentOnStartup = getModel(new Property(
			App.class, "openRecentOnStartup"));

	private Model<Boolean> saveOnShutdown = getModel(new Property(
			OrganSession.class, "saveOnShutdown"));

	private JCheckBox openRecentOnStartupCheckBox = new JCheckBox();

	private JCheckBox saveOnShutdownCheckBox = new JCheckBox();

	public AppCategory() {
		config.read(this);
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);

		Column column = builder.column();

		column.definition(config.get("openRecentOnStartup").read(
				openRecentOnStartupCheckBox));

		column.definition(config.get("saveOnShutdown").read(
				saveOnShutdownCheckBox));

		return panel;
	}

	@Override
	protected void read() {
		openRecentOnStartupCheckBox.setSelected(openRecentOnStartup.getValue());
		saveOnShutdownCheckBox.setSelected(saveOnShutdown.getValue());
	}

	@Override
	protected void write() {
		openRecentOnStartup.setValue(openRecentOnStartupCheckBox.isSelected());

		saveOnShutdown.setValue(saveOnShutdownCheckBox.isSelected());
	}
}