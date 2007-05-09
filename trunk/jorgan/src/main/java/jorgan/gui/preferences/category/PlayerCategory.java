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

import jorgan.play.Player;
import jorgan.swing.GridBuilder;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * {@link Player} category.
 */
public class PlayerCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			PlayerCategory.class);

	private Model warnDevice = getModel("jorgan/play/Player", new Property(
			Player.class, "warnDevice"));

	private Model warnMessage = getModel("jorgan/play/Player", new Property(
			Player.class, "warnMessage"));

	private JCheckBox warnDeviceCheckBox = new JCheckBox();

	private JCheckBox warnMessageCheckBox = new JCheckBox();

	public PlayerCategory() {
		config.read(this);
	}

	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 1.0d });

		builder.nextRow();

		config.get("warnDeviceCheckBox").read(warnDeviceCheckBox);
		panel.add(warnDeviceCheckBox, builder.nextColumn());

		builder.nextRow();

		config.get("warnMessageCheckBox").read(warnMessageCheckBox);
		panel.add(warnMessageCheckBox, builder.nextColumn());

		builder.nextRow();

		return panel;
	}

	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	protected void read() {
		warnDeviceCheckBox.setSelected((Boolean) warnDevice.getValue());
		warnMessageCheckBox.setSelected((Boolean) warnMessage.getValue());
	}

	protected void write() {
		warnDevice.setValue(warnDeviceCheckBox.isSelected());
		warnMessage.setValue(warnMessageCheckBox.isSelected());
	}
}