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
package jorgan.gui.config;

import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import jorgan.gui.console.Configuration;
import jorgan.swing.GridBuilder;
import jorgan.swing.color.ColorSelector;
import jorgan.swing.font.FontSelector;

/**
 * A panel for the {@link jorgan.gui.console.Configuration}.
 */
public class GUIConsoleConfigPanel extends ConfigurationPanel {

	private JCheckBox interpolateCheckBox = new JCheckBox();

	private JLabel fontLabel = new JLabel();

	private FontSelector fontSelector = new FontSelector();

	private JPanel shortcutsPanel = new JPanel();

	private JCheckBox showShortcutCheckBox = new JCheckBox();

	private JLabel shortcutColorLabel = new JLabel();

	private ColorSelector shortcutColorSelector = new ColorSelector();

	private JLabel shortcutFontLabel = new JLabel();

	private FontSelector shortcutFontSelector = new FontSelector();

	public GUIConsoleConfigPanel() {
		setName(resources.getString("config.console.name"));
		setLayout(new GridBagLayout());
		
		GridBuilder builder = new GridBuilder(new double[]{0.0d, 1.0d});

		builder.nextRow();
		
		interpolateCheckBox.setText(resources
				.getString("config.console.interpolate"));
		add(interpolateCheckBox, builder.nextColumn().gridWidthRemainder());

		builder.nextRow();

		fontLabel.setText(resources.getString("config.console.font"));
		add(fontLabel, builder.nextColumn());
		add(fontSelector, builder.nextColumn().fillHorizontal());

		builder.nextRow();

		shortcutsPanel.setLayout(new GridBagLayout());
		shortcutsPanel.setBorder(new TitledBorder(BorderFactory
				.createEtchedBorder(), resources
				.getString("config.console.shortcuts")));
		add(shortcutsPanel, builder.nextColumn().gridWidthRemainder().fillHorizontal());

		GridBuilder shortcutsBuilder = new GridBuilder(new double[]{0.0d, 1.0d});
		
		shortcutsBuilder.nextRow();
		
		showShortcutCheckBox.setText(resources
				.getString("config.console.shortcuts.show"));
		showShortcutCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				shortcutColorSelector.setEnabled(showShortcutCheckBox
						.isSelected());
				shortcutFontSelector.setEnabled(showShortcutCheckBox
						.isSelected());
			}
		});
		shortcutsPanel.add(showShortcutCheckBox, shortcutsBuilder.nextColumn().gridWidthRemainder());

		shortcutsBuilder.nextRow();

		shortcutColorLabel.setText(resources
				.getString("config.console.shortcuts.color"));
		shortcutsPanel.add(shortcutColorLabel, shortcutsBuilder.nextColumn());
		shortcutsPanel.add(shortcutColorSelector, shortcutsBuilder.nextColumn());

		shortcutsBuilder.nextRow();

		shortcutFontLabel.setText(resources
				.getString("config.console.shortcuts.font"));
		shortcutsPanel.add(shortcutFontLabel, shortcutsBuilder.nextColumn());
		shortcutsPanel.add(shortcutFontSelector, shortcutsBuilder.nextColumn().fillHorizontal());
	}

	public void read() {
		Configuration config = (Configuration) getConfiguration();

		interpolateCheckBox.setSelected(config.getInterpolate());

		fontSelector.setSelectedFont(config.getFont());

		showShortcutCheckBox.setSelected(config.getShowShortcut());
		shortcutColorSelector.setSelectedColor(config.getShortcutColor());
		shortcutColorSelector.setEnabled(config.getShowShortcut());
		shortcutFontSelector.setSelectedFont(config.getShortcutFont());
		shortcutFontSelector.setEnabled(config.getShowShortcut());
	}

	/**
	 * Write the configuration.
	 */
	public void write() {
		Configuration config = (Configuration) getConfiguration();

		config.setInterpolate(interpolateCheckBox.isSelected());

		config.setFont(fontSelector.getSelectedFont());

		config.setShowShortcut(showShortcutCheckBox.isSelected());
		config.setShortcutColor(shortcutColorSelector.getSelectedColor());
		config.setShortcutFont(shortcutFontSelector.getSelectedFont());
	}
}