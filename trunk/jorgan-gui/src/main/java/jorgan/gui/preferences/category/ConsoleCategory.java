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
import jorgan.swing.color.ColorSelector;
import jorgan.swing.font.FontSelector;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * Category.
 */
public class ConsoleCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			ConsoleCategory.class);

	private Model<Integer> grid = getModel(new Property(ConsolePanel.class,
			"grid"));

	private Model<Color> background = getModel(new Property(ConsolePanel.class,
			"background"));

	private Model<Color> foreground = getModel(new Property(ConsolePanel.class,
			"foreground"));

	private Model<Color> popupBackground = getModel(new Property(
			ConsolePanel.class, "popupBackground"));

	private Model<Color> elementForeground = getModel(new Property(View.class,
			"defaultColor"));

	private Model<Font> elementFont = getModel(new Property(View.class,
			"defaultFont"));

	private Model<Boolean> showShortcut = getModel(new Property(View.class,
			"showShortcut"));

	private Model<Color> shortcutColor = getModel(new Property(View.class,
			"shortcutColor"));

	private Model<Font> shortcutFont = getModel(new Property(View.class,
			"shortcutFont"));

	private JSpinner gridSpinner = new JSpinner(new SpinnerNumberModel(1, 1,
			256, 1));

	private ColorSelector backgroundSelector = new ColorSelector();

	private ColorSelector foregroundSelector = new ColorSelector();

	private ColorSelector popupBackgroundSelector = new ColorSelector();

	private ColorSelector elementForegroundSelector = new ColorSelector();

	private FontSelector elementFontSelector = new FontSelector();

	private JCheckBox shortcutCheckBox = new JCheckBox();

	private ColorSelector shortcutColorSelector = new ColorSelector();

	private FontSelector shortcutFontSelector = new FontSelector();

	public ConsoleCategory() {
		config.read(this);
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);

		Column column = builder.column();

		column.term(config.get("grid").read(new JLabel()));
		column.definition(gridSpinner);

		column.term(config.get("background").read(new JLabel()));
		column.definition(backgroundSelector);

		column.term(config.get("foreground").read(new JLabel()));
		column.definition(foregroundSelector);

		column.group(config.get("popup").read(new JLabel()));

		column.term(config.get("popupBackground").read(new JLabel()));
		column.definition(popupBackgroundSelector);

		column.group(config.get("element").read(new JLabel()));

		column.term(config.get("elementForeground").read(new JLabel()));
		column.definition(elementForegroundSelector);

		column.term(config.get("elementFont").read(new JLabel()));
		column.definition(elementFontSelector).fillHorizontal();

		shortcutCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				shortcutColorSelector.setEnabled(shortcutCheckBox.isSelected());
				shortcutFontSelector.setEnabled(shortcutCheckBox.isSelected());
			}
		});
		column.group(config.get("shortcut").read(shortcutCheckBox));

		column.term(config.get("shortcutColor").read(new JLabel()));
		column.definition(shortcutColorSelector);

		column.term(config.get("shortcutFont").read(new JLabel()));
		column.definition(shortcutFontSelector).fillHorizontal();

		return panel;
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return GuiCategory.class;
	}

	@Override
	protected void read() {

		gridSpinner.setValue(grid.getValue());
		backgroundSelector.setSelectedColor(background.getValue());
		foregroundSelector.setSelectedColor(foreground.getValue());

		popupBackgroundSelector.setSelectedColor(popupBackground.getValue());

		elementForegroundSelector
				.setSelectedColor(elementForeground.getValue());
		elementFontSelector.setSelectedFont(elementFont.getValue());

		shortcutCheckBox.setSelected(showShortcut.getValue());
		shortcutColorSelector.setSelectedColor(shortcutColor.getValue());
		shortcutFontSelector.setSelectedFont(shortcutFont.getValue());
	}

	@Override
	protected void write() {

		grid.setValue((Integer) gridSpinner.getValue());
		background.setValue(backgroundSelector.getSelectedColor());
		foreground.setValue(foregroundSelector.getSelectedColor());

		popupBackground.setValue(popupBackgroundSelector.getSelectedColor());

		elementForeground
				.setValue(elementForegroundSelector.getSelectedColor());
		elementFont.setValue(elementFontSelector.getSelectedFont());

		showShortcut.setValue(shortcutCheckBox.isSelected());
		shortcutColor.setValue(shortcutColorSelector.getSelectedColor());
		shortcutFont.setValue(shortcutFontSelector.getSelectedFont());
	}
}