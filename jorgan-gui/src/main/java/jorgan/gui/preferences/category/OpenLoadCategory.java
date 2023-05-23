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

import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jorgan.App;
import jorgan.gui.action.FullScreenAction;
import jorgan.gui.FullScreen;
import jorgan.gui.GUI;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 */
public class OpenLoadCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
		OpenLoadCategory.class);

	private Model<Boolean> showAboutOnStartup = getModel(new Property(
		GUI.class, "showAboutOnStartup"));

	private Model<Boolean> openRecentOnStartup = getModel(new Property(
		App.class, "openRecentOnStartup"));

	private Model<Boolean> fullScreenOnLoad = getModel(new Property(
		FullScreenAction.class, "onLoad"));

	private JCheckBox showAboutOnStartupCheckBox = new JCheckBox();

	private JCheckBox openRecentOnStartupCheckBox = new JCheckBox();

	private JCheckBox fullScreenOnLoadCheckBox = new JCheckBox();

	public OpenLoadCategory() {
		config.read(this);
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		DefinitionBuilder builder = new DefinitionBuilder(panel);
		Column column = builder.column();

		column.group(config.get("startupGroup").read(new JLabel()));
		column.definition(config.get("showAboutOnStartup")
				.read(showAboutOnStartupCheckBox));
		column.definition(config.get("openRecentOnStartup").read(
				openRecentOnStartupCheckBox));

		column.group(config.get("loadGroup").read(new JLabel()));
		column.definition(config.get("fullScreenOnLoad").read(fullScreenOnLoadCheckBox));

		return panel;
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected void read() {
		showAboutOnStartupCheckBox.setSelected(showAboutOnStartup.getValue());
		openRecentOnStartupCheckBox.setSelected(openRecentOnStartup.getValue());
		fullScreenOnLoadCheckBox.setSelected(fullScreenOnLoad.getValue());
	}

	@Override
	protected void write() {
		showAboutOnStartup.setValue(showAboutOnStartupCheckBox.isSelected());
		openRecentOnStartup.setValue(openRecentOnStartupCheckBox.isSelected());
		fullScreenOnLoad.setValue(fullScreenOnLoadCheckBox.isSelected());
	}
}