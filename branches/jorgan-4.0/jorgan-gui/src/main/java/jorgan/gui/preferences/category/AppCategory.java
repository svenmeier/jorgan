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

import java.util.Locale;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import jorgan.App;
import jorgan.session.OrganSession;
import jorgan.swing.button.ButtonGroup;
import jorgan.swing.combobox.BaseComboBoxModel;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import jorgan.util.LocaleUtils;
import bias.Configuration;
import bias.util.MessageBuilder;
import bias.util.Property;

/**
 * {@link jorgan.App} category.
 */
public class AppCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			AppCategory.class);

	private Model<Locale> locale = getModel(new Property(App.class, "locale"));

	private Model<Boolean> openRecentOnStartup = getModel(new Property(
			App.class, "openRecentOnStartup"));

	private Model<Boolean> saveOnShutdown = getModel(new Property(
			OrganSession.class, "saveOnShutdown"));

	private JRadioButton localeDefaultRadioButton = new JRadioButton();

	private JRadioButton localeOtherRadioButton = new JRadioButton();

	private JComboBox localeComboBox = new JComboBox();

	private JCheckBox openRecentOnStartupCheckBox = new JCheckBox();

	private JCheckBox saveOnShutdownCheckBox = new JCheckBox();

	public AppCategory() {
		config.read(this);
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);

		Column column = builder.column();

		column.definition(config.get("openRecentOnStartup").read(
				openRecentOnStartupCheckBox));

		column.definition(config.get("saveOnShutdown").read(
				saveOnShutdownCheckBox));

		ButtonGroup localeGroup = new ButtonGroup() {
			@Override
			protected void onSelected(AbstractButton button) {
				localeComboBox.setEnabled(button == localeOtherRadioButton);
			}
		};
		column.term(config.get("locale").read(new JLabel()));

		String message = config.get("localeDefault").read(new MessageBuilder())
				.build(LocaleUtils.getDefault());
		localeDefaultRadioButton.setText(message);
		localeGroup.add(localeDefaultRadioButton);
		column.definition(localeDefaultRadioButton);

		config.get("localeOther").read(localeOtherRadioButton);
		localeGroup.add(localeOtherRadioButton);
		column.definition(localeOtherRadioButton);

		localeComboBox.setEditable(true);
		localeComboBox.setModel(new BaseComboBoxModel<Locale>(LocaleUtils
				.getLocales()) {
			@Override
			protected Locale convert(String element) {
				return new Locale(element);
			}
		});
		column.definition(localeComboBox);

		return panel;
	}

	@Override
	protected void read() {
		Locale locale = this.locale.getValue();
		if (locale == null) {
			localeDefaultRadioButton.setSelected(true);
			localeComboBox.setEnabled(false);
			localeComboBox.setSelectedItem(LocaleUtils.getDefault());
		} else {
			localeOtherRadioButton.setSelected(true);
			localeComboBox.setEnabled(true);
			localeComboBox.setSelectedItem(locale);
		}

		openRecentOnStartupCheckBox.setSelected(openRecentOnStartup.getValue());
		saveOnShutdownCheckBox.setSelected(saveOnShutdown.getValue());
	}

	@Override
	protected void write() {
		if (localeDefaultRadioButton.isSelected()) {
			locale.setValue(null);
		} else {
			locale.setValue((Locale) localeComboBox.getSelectedItem());
		}

		openRecentOnStartup.setValue(openRecentOnStartupCheckBox.isSelected());

		saveOnShutdown.setValue(saveOnShutdownCheckBox.isSelected());
	}
}