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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.gui.ConsolePanel;
import jorgan.gui.console.View;
import jorgan.swing.GridBuilder;
import jorgan.swing.Separator;
import jorgan.swing.GridBuilder.Row;
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
		JPanel panel = new JPanel();

		GridBuilder builder = new GridBuilder(panel);
		builder.column();
		builder.column().grow().fill();

		Row row = builder.row();

		row.cell(config.get("grid").read(new JLabel()));
		row.cell(gridSpinner);

		row = builder.row();

		config.get("interpolate").read(interpolateCheckBox);
		row.skip().cell(interpolateCheckBox);

		row = builder.row();

		row.cell(config.get("background").read(new JLabel()));
		row.cell(backgroundSelector);

		row = builder.row();

		row.cell(config.get("foreground").read(new JLabel()));
		row.cell(foregroundSelector);

		builder.row(config.get("element").read(new Separator.Label()));

		row = builder.row();

		row.cell(config.get("elementColor").read(new JLabel()));
		row.cell(elementColorSelector);

		row = builder.row();

		row.cell(config.get("elementFont").read(new JLabel()));
		row.cell(elementFontSelector);

		showShortcutCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				shortcutColorSelector.setEnabled(showShortcutCheckBox
						.isSelected());
				shortcutFontSelector.setEnabled(showShortcutCheckBox
						.isSelected());
			}
		});
		builder.row(new Separator<JCheckBox>(config.get("shortcut").read(
				showShortcutCheckBox)));

		row = builder.row();

		row.cell(config.get("shortcutColor").read(new JLabel()));
		row.cell(shortcutColorSelector);

		row = builder.row();

		row.cell(config.get("shortcutFont").read(new JLabel()));
		row.cell(shortcutFontSelector);

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