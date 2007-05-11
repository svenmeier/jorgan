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
import javax.swing.JPanel;

import jorgan.App;
import jorgan.swing.GridBuilder;
import bias.Configuration;
import bias.util.Property;

/**
 * {@link jorgan.App} category.
 */
public class AppCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			AppCategory.class);

	private Model openRecentOnStartup = getModel(new Property(App.class,
			"openRecentOnStartup"));

	private JCheckBox openRecentOnStartupCheckBox = new JCheckBox();

	public AppCategory() {
		config.read(this);
	}

	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 1.0d });

		builder.nextRow();

		config.get("openRecentOnStartup").read(
				openRecentOnStartupCheckBox);
		panel.add(openRecentOnStartupCheckBox, builder.nextColumn()
				.gridWidthRemainder());

		return panel;
	}

	protected void read() {
		openRecentOnStartupCheckBox.setSelected((Boolean) openRecentOnStartup
				.getValue());
	}

	protected void write() {
		openRecentOnStartup.setValue(openRecentOnStartupCheckBox.isSelected());
	}
}