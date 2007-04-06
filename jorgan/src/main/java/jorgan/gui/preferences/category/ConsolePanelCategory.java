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
import jorgan.util.I18N;
import bias.swing.Category;
import bias.swing.PropertyModel;

/**
 * {@link ConsolePanel} category.
 */
public class ConsolePanelCategory extends JOrganCategory {

	private static I18N i18n = I18N.get(ConsolePanelCategory.class);

	private PropertyModel grid = getModel(ConsolePanel.class, "grid");

	private PropertyModel interpolate = getModel(ConsolePanel.class,
			"interpolate");

	private PropertyModel background = getModel(ConsolePanel.class,
			"background");

	private PropertyModel foreground = getModel(ConsolePanel.class,
			"foreground");

	private JSpinner gridSpinner = new JSpinner(new SpinnerNumberModel(1, 1,
			256, 1));

	private JCheckBox interpolateCheckBox = new JCheckBox();

	private ColorSelector backgroundSelector = new ColorSelector();

	private ColorSelector foregroundSelector = new ColorSelector();

	protected String createName() {
		return i18n.getString("name");
	}

	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();

		panel.add(new JLabel(i18n.getString("gridSpinner.label")), builder
				.nextColumn());
		panel.add(gridSpinner, builder.nextColumn());

		builder.nextRow();

		interpolateCheckBox.setText(i18n.getString("interpolateCheckBox.text"));
		panel.add(interpolateCheckBox, builder.nextColumn()
				.gridWidthRemainder());

		builder.nextRow();

		panel.add(new JLabel(i18n.getString("backgroundSelector.label")),
				builder.nextColumn());
		panel.add(backgroundSelector, builder.nextColumn());

		builder.nextRow();

		panel.add(new JLabel(i18n.getString("foregroundSelector.label")),
				builder.nextColumn());
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