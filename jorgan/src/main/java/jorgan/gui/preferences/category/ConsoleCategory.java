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
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import jorgan.gui.ConsolePanel;
import jorgan.gui.console.View;
import jorgan.swing.GridBuilder;
import jorgan.swing.color.ColorSelector;
import jorgan.swing.font.FontSelector;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * Category.
 */
public class ConsoleCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			ConsoleCategory.class);

	private Model grid = getModel(new Property(ConsolePanel.class, "grid"));

	private Model interpolate = getModel(new Property(ConsolePanel.class,
			"interpolate"));

	private Model background = getModel(new Property(ConsolePanel.class,
			"background"));

	private Model foreground = getModel(new Property(ConsolePanel.class,
			"foreground"));

	private Model elementColor = getModel(new Property(View.class,
			"defaultColor"));

	private Model elementFont = getModel(new Property(View.class, "defaultFont"));

	private Model showShortcut = getModel(new Property(View.class,
			"showShortcut"));

	private Model shortcutColor = getModel(new Property(View.class,
			"shortcutColor"));

	private Model shortcutFont = getModel(new Property(View.class,
			"shortcutFont"));

	private JSpinner gridSpinner = new JSpinner(new SpinnerNumberModel(1, 1,
			256, 1));

	private JCheckBox interpolateCheckBox = new JCheckBox();

	private ColorSelector backgroundSelector = new ColorSelector();

	private ColorSelector foregroundSelector = new ColorSelector();

	private ColorSelector elementColorSelector = new ColorSelector();

	private FontSelector elementFontSelector = new FontSelector();

	private JCheckBox showShortcutCheckBox = new JCheckBox();

	private ColorSelector shortcutColorSelector = new ColorSelector();

	private FontSelector shortcutFontSelector = new FontSelector();

	public ConsoleCategory() {
		config.read(this);
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();

		builder.nextRow();

		panel.add(config.get("grid").read(new JLabel()), builder.nextColumn());
		panel.add(gridSpinner, builder.nextColumn());

		builder.nextRow();

		config.get("interpolate").read(interpolateCheckBox);
		panel.add(interpolateCheckBox, builder.nextColumn()
				.gridWidthRemainder());

		builder.nextRow();

		panel.add(config.get("background").read(new JLabel()), builder
				.nextColumn());
		panel.add(backgroundSelector, builder.nextColumn());

		builder.nextRow();

		panel.add(config.get("foreground").read(new JLabel()), builder
				.nextColumn());
		panel.add(foregroundSelector, builder.nextColumn());

		builder.nextRow();

		panel.add(createElementsPanel(), builder.nextColumn()
				.gridWidthRemainder().fillHorizontal());

		builder.nextRow();

		panel.add(createShortcutsPanel(), builder.nextColumn()
				.gridWidthRemainder().fillHorizontal());

		return panel;
	}

	private JPanel createElementsPanel() {

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(config.get("element").read(
				new TitledBorder(BorderFactory.createEtchedBorder())));

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();

		panel.add(config.get("elementColor").read(new JLabel()), builder
				.nextColumn());
		panel.add(elementColorSelector, builder.nextColumn());

		builder.nextRow();

		panel.add(config.get("elementFont").read(new JLabel()), builder
				.nextColumn());
		panel.add(elementFontSelector, builder.nextColumn().fillHorizontal());

		return panel;
	}

	private JPanel createShortcutsPanel() {

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(config.get("shortcut").read(
				new TitledBorder(BorderFactory.createEtchedBorder())));

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();

		config.get("showShortcut").read(showShortcutCheckBox);
		showShortcutCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				shortcutColorSelector.setEnabled(showShortcutCheckBox
						.isSelected());
				shortcutFontSelector.setEnabled(showShortcutCheckBox
						.isSelected());
			}
		});
		panel.add(showShortcutCheckBox, builder.nextColumn()
				.gridWidthRemainder());

		builder.nextRow();

		panel.add(config.get("shortcutColor").read(new JLabel()), builder
				.nextColumn());
		panel.add(shortcutColorSelector, builder.nextColumn());

		builder.nextRow();

		panel.add(config.get("shortcutFont").read(new JLabel()), builder
				.nextColumn());
		panel.add(shortcutFontSelector, builder.nextColumn().fillHorizontal());

		return panel;
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return GuiCategory.class;
	}

	@Override
	protected void read() {

		gridSpinner.setValue(grid.getValue());
		interpolateCheckBox.setSelected((Boolean) interpolate.getValue());
		backgroundSelector.setSelectedColor((Color) background.getValue());
		foregroundSelector.setSelectedColor((Color) foreground.getValue());

		elementColorSelector.setSelectedColor((Color) elementColor.getValue());
		elementFontSelector.setSelectedFont((Font) elementFont.getValue());

		showShortcutCheckBox.setSelected((Boolean) showShortcut.getValue());
		shortcutColorSelector
				.setSelectedColor((Color) shortcutColor.getValue());
		shortcutFontSelector.setSelectedFont((Font) shortcutFont.getValue());
	}

	@Override
	protected void write() {

		grid.setValue(gridSpinner.getValue());
		interpolate.setValue(interpolateCheckBox.isSelected());
		background.setValue(backgroundSelector.getSelectedColor());
		foreground.setValue(foregroundSelector.getSelectedColor());

		elementColor.setValue(elementColorSelector.getSelectedColor());
		elementFont.setValue(elementFontSelector.getSelectedFont());

		showShortcut.setValue(showShortcutCheckBox.isSelected());
		shortcutColor.setValue(shortcutColorSelector.getSelectedColor());
		shortcutFont.setValue(shortcutFontSelector.getSelectedFont());
	}
}