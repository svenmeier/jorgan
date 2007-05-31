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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import jorgan.gui.GUI;
import jorgan.gui.OrganFrame;
import jorgan.gui.construct.editor.MessageEditor;
import jorgan.swing.GridBuilder;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * {@link GUI} category.
 */
public class GuiCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			GuiCategory.class);

	private Model useSystemLookAndFeel = getModel(new Property(GUI.class,
			"useSystemLookAndFeel"));

	private Model showAboutOnStartup = getModel(new Property(GUI.class,
			"showAboutOnStartup"));

	private Model hexMessage = getModel(new Property(MessageEditor.class, "hex"));

	private JCheckBox useSystemLookAndFeelCheckBox = new JCheckBox();

	private JCheckBox showAboutOnStartupCheckBox = new JCheckBox();

	private JCheckBox hexMessageCheckBox = new JCheckBox();

	private Model fullScreenOnLoad = getModel(new Property(OrganFrame.class,
			"fullScreenOnLoad"));

	private Model handleRegistrationChanges = getModel(new Property(
			OrganFrame.class, "handleRegistrationChanges"));

	private JCheckBox fullScreenOnLoadCheckBox = new JCheckBox();

	private ButtonGroup changesGroup = new ButtonGroup();

	private JRadioButton confirmChangesRadioButton = new JRadioButton();

	private JRadioButton saveChangesRadioButton = new JRadioButton();

	private JRadioButton ignoreChangesRadioButton = new JRadioButton();

	public GuiCategory() {
		config.read(this);
	}

	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 1.0d });

		builder.nextRow();

		config.get("useSystemLookAndFeel").read(useSystemLookAndFeelCheckBox);
		panel.add(useSystemLookAndFeelCheckBox, builder.nextColumn());

		builder.nextRow();

		config.get("showAboutOnStartup").read(showAboutOnStartupCheckBox);
		panel.add(showAboutOnStartupCheckBox, builder.nextColumn());

		builder.nextRow();

		config.get("fullScreenOnLoad").read(fullScreenOnLoadCheckBox);
		panel.add(fullScreenOnLoadCheckBox, builder.nextColumn());

		builder.nextRow();

		panel.add(createChangesPanel(), builder.nextColumn()
				.gridWidthRemainder().fillHorizontal());

		builder.nextRow();

		config.get("hexMessage").read(hexMessageCheckBox);
		panel.add(hexMessageCheckBox, builder.nextColumn());

		builder.nextRow();

		return panel;
	}

	private JPanel createChangesPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(config.get("changes").read(
				new TitledBorder(BorderFactory.createEtchedBorder())));

		GridBuilder builder = new GridBuilder(new double[] { 1.0d });

		builder.nextRow();

		config.get("confirmChanges").read(confirmChangesRadioButton);
		confirmChangesRadioButton.getModel().setGroup(changesGroup);
		panel.add(confirmChangesRadioButton, builder.nextColumn());

		builder.nextRow();

		config.get("saveChanges").read(saveChangesRadioButton);
		saveChangesRadioButton.getModel().setGroup(changesGroup);
		panel.add(saveChangesRadioButton, builder.nextColumn());

		builder.nextRow();

		config.get("ignoreChanges").read(ignoreChangesRadioButton);
		ignoreChangesRadioButton.getModel().setGroup(changesGroup);
		panel.add(ignoreChangesRadioButton, builder.nextColumn());

		return panel;
	}

	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	protected void read() {
		useSystemLookAndFeelCheckBox.setSelected((Boolean) useSystemLookAndFeel
				.getValue());
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
		
		hexMessageCheckBox.setSelected((Boolean) hexMessage.getValue());
	}

	protected void write() {
		useSystemLookAndFeel
				.setValue(useSystemLookAndFeelCheckBox.isSelected());
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
		
		hexMessage.setValue(hexMessageCheckBox.isSelected());
	}
}