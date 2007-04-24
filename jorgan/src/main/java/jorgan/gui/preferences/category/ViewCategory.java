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
import javax.swing.border.TitledBorder;

import jorgan.gui.console.View;
import jorgan.swing.GridBuilder;
import jorgan.swing.color.ColorSelector;
import jorgan.swing.font.FontSelector;
import jorgan.util.I18N;
import bias.swing.Category;
import bias.swing.PropertyModel;

/**
 * {@link View} cateogry.
 */
public class ViewCategory extends JOrganCategory {

	private static I18N i18n = I18N.get(ViewCategory.class);

	private PropertyModel defaultColor = getModel(View.class, "defaultColor");

	private PropertyModel defaultFont = getModel(View.class, "defaultFont");

	private PropertyModel showShortcut = getModel(View.class, "showShortcut");

	private PropertyModel shortcutColor = getModel(View.class,
			"shortcutColor");

	private PropertyModel shortcutFont = getModel(View.class, "shortcutFont");

	private ColorSelector defaultColorSelector = new ColorSelector();

	private FontSelector defaultFontSelector = new FontSelector();

	private JCheckBox showShortcutCheckBox = new JCheckBox();

	private ColorSelector shortcutColorSelector = new ColorSelector();

	private FontSelector shortcutFontSelector = new FontSelector();

	protected String createName() {
		return i18n.getString("name");
	}

	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();

		panel.add(new JLabel(i18n.getString("defaultColorSelector/label")),
				builder.nextColumn());
		panel.add(defaultColorSelector, builder.nextColumn());

		builder.nextRow();

		panel.add(new JLabel(i18n.getString("defaultFontSelector/label")),
				builder.nextColumn());
		panel.add(defaultFontSelector, builder.nextColumn().fillHorizontal());

		builder.nextRow();

		panel.add(createShortcutsPanel(), builder.nextColumn()
				.gridWidthRemainder().fillHorizontal());

		return panel;
	}

	private JPanel createShortcutsPanel() {

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
				i18n.getString("shortcutPanel/title")));

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();

		showShortcutCheckBox.setText(i18n
				.getString("showShortcutCheckBox/text"));
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

		panel.add(new JLabel(i18n.getString("shortcutColorSelector/label")),
				builder.nextColumn());
		panel.add(shortcutColorSelector, builder.nextColumn());

		builder.nextRow();

		panel.add(new JLabel(i18n.getString("shortcutFontSelector/label")),
				builder.nextColumn());
		panel.add(shortcutFontSelector, builder.nextColumn().fillHorizontal());

		return panel;
	}

	public Class<? extends Category> getParentCategory() {
		return ConsolePanelCategory.class;
	}

	protected void read() {

		defaultColorSelector.setSelectedColor((Color) defaultColor.getValue());
		defaultFontSelector.setSelectedFont((Font) defaultFont.getValue());
		showShortcutCheckBox.setSelected((Boolean) showShortcut.getValue());
		shortcutColorSelector
				.setSelectedColor((Color) shortcutColor.getValue());
		shortcutFontSelector.setSelectedFont((Font) shortcutFont.getValue());
	}

	protected void write() {

		defaultColor.setValue(defaultColorSelector.getSelectedColor());
		defaultFont.setValue(defaultFontSelector.getSelectedFont());
		showShortcut.setValue(showShortcutCheckBox.isSelected());
		shortcutColor.setValue(shortcutColorSelector.getSelectedColor());
		shortcutFont.setValue(shortcutFontSelector.getSelectedFont());
	}
}