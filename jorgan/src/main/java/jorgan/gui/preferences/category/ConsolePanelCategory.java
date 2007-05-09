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

import java.awt.Color;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.gui.ConsolePanel;
import jorgan.swing.GridBuilder;
import jorgan.swing.color.ColorSelector;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * {@link ConsolePanel} category.
 */
public class ConsolePanelCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			ConsolePanelCategory.class);

	private Model grid = getModel("jorgan/gui/ConsolePanel", new Property(
			ConsolePanel.class, "grid"));

	private Model interpolate = getModel("jorgan/gui/ConsolePanel",
			new Property(ConsolePanel.class, "interpolate"));

	private Model background = getModel("jorgan/gui/ConsolePanel",
			new Property(ConsolePanel.class, "background"));

	private Model foreground = getModel("jorgan/gui/ConsolePanel",
			new Property(ConsolePanel.class, "foreground"));

	private JSpinner gridSpinner = new JSpinner(new SpinnerNumberModel(1, 1,
			256, 1));

	private JCheckBox interpolateCheckBox = new JCheckBox();

	private ColorSelector backgroundSelector = new ColorSelector();

	private ColorSelector foregroundSelector = new ColorSelector();

	public ConsolePanelCategory() {
		config.read(this);
	}

	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();

		panel.add(config.get("gridSpinner").read(new JLabel()), builder
				.nextColumn());
		panel.add(gridSpinner, builder.nextColumn());

		builder.nextRow();

		config.get("interpolateCheckBox").read(interpolateCheckBox);
		panel.add(interpolateCheckBox, builder.nextColumn()
				.gridWidthRemainder());

		builder.nextRow();

		panel.add(config.get("backgroundSelector").read(new JLabel()), builder
				.nextColumn());
		panel.add(backgroundSelector, builder.nextColumn());

		builder.nextRow();

		panel.add(config.get("foregroundSelector").read(new JLabel()), builder
				.nextColumn());
		panel.add(foregroundSelector, builder.nextColumn());

		return panel;
	}

	public Class<? extends Category> getParentCategory() {
		return GUICategory.class;
	}

	protected void read() {
		gridSpinner.setValue(grid.getValue());
		interpolateCheckBox.setSelected((Boolean) interpolate.getValue());
		backgroundSelector.setSelectedColor((Color) background.getValue());
		foregroundSelector.setSelectedColor((Color) foreground.getValue());
	}

	protected void write() {
		grid.setValue(gridSpinner.getValue());
		interpolate.setValue(interpolateCheckBox.isSelected());
		background.setValue(backgroundSelector.getSelectedColor());
		foreground.setValue(foregroundSelector.getSelectedColor());
	}
}