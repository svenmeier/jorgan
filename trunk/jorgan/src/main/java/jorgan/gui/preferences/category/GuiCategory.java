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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import jorgan.gui.GUI;
import jorgan.gui.OrganFrame;
import jorgan.swing.button.ButtonGroup;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * {@link GUI} category.
 */
public class GuiCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			GuiCategory.class);

	private Model lookAndFeel = getModel(new Property(GUI.class, "lookAndFeel"));

	private Model showAboutOnStartup = getModel(new Property(GUI.class,
			"showAboutOnStartup"));

	private JComboBox lookAndFeelComboBox = new JComboBox();

	private JCheckBox showAboutOnStartupCheckBox = new JCheckBox();

	private Model fullScreenOnLoad = getModel(new Property(OrganFrame.class,
			"fullScreenOnLoad"));

	private Model handleRegistrationChanges = getModel(new Property(
			OrganFrame.class, "handleRegistrationChanges"));

	private JCheckBox fullScreenOnLoadCheckBox = new JCheckBox();

	private JRadioButton confirmChangesRadioButton = new JRadioButton();

	private JRadioButton saveChangesRadioButton = new JRadioButton();

	private JRadioButton ignoreChangesRadioButton = new JRadioButton();

	public GuiCategory() {
		config.read(this);
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);

		Column column = builder.column();

		column.term(config.get("lookAndFeel").read(new JLabel()));
		lookAndFeelComboBox
				.setModel(new DefaultComboBoxModel(GUI.LAF.values()));
		column.definition(lookAndFeelComboBox);

		column.definition(config.get("showAboutOnStartup").read(
				showAboutOnStartupCheckBox));

		column.definition(config.get("fullScreenOnLoad").read(
				fullScreenOnLoadCheckBox));

		ButtonGroup changesGroup = new ButtonGroup();
		column.term(config.get("changes").read(new JLabel()));

		config.get("confirmChanges").read(confirmChangesRadioButton);
		changesGroup.add(confirmChangesRadioButton);
		column.definition(confirmChangesRadioButton);

		config.get("saveChanges").read(saveChangesRadioButton);
		changesGroup.add(saveChangesRadioButton);
		column.definition(saveChangesRadioButton);

		config.get("ignoreChanges").read(ignoreChangesRadioButton);
		changesGroup.add(ignoreChangesRadioButton);
		column.definition(ignoreChangesRadioButton);

		return panel;
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected void read() {
		lookAndFeelComboBox.setSelectedItem(lookAndFeel.getValue());
		showAboutOnStartupCheckBox.setSelected((Boolean) showAboutOnStartup
				.getValue());
		fullScreenOnLoadCheckBox.setSelected((Boolean) fullScreenOnLoad
				.getValue());

		switch ((Integer) handleRegistrationChanges.getValue()) {
		case OrganFrame.REGISTRATION_CHANGES_CONFIRM:
			confirmChangesRadioButton.setSelected(true);
			break;
		case OrganFrame.REGISTRATION_CHANGES_SAVE:
			saveChangesRadioButton.setSelected(true);
			break;
		case OrganFrame.REGISTRATION_CHANGES_IGNORE:
			ignoreChangesRadioButton.setSelected(true);
			break;
		}
	}

	@Override
	protected void write() {
		lookAndFeel.setValue(lookAndFeelComboBox.getSelectedItem());
		showAboutOnStartup.setValue(showAboutOnStartupCheckBox.isSelected());
		fullScreenOnLoad.setValue(fullScreenOnLoadCheckBox.isSelected());

		int handle = 0;
		if (confirmChangesRadioButton.isSelected()) {
			handle = OrganFrame.REGISTRATION_CHANGES_CONFIRM;
		} else if (saveChangesRadioButton.isSelected()) {
			handle = OrganFrame.REGISTRATION_CHANGES_SAVE;
		} else if (ignoreChangesRadioButton.isSelected()) {
			handle = OrganFrame.REGISTRATION_CHANGES_IGNORE;
		}
		handleRegistrationChanges.setValue(handle);
	}
}