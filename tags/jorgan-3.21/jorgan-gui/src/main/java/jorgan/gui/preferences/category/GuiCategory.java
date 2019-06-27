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

import jorgan.gui.FullScreen;
import jorgan.gui.GUI;
import jorgan.gui.LAF;
import jorgan.gui.OrganFrame;
import jorgan.gui.OrganFrame.Changes;
import jorgan.gui.action.FullScreenAction;
import jorgan.skin.SkinManager;
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

	private Model<LAF> lookAndFeel = getModel(new Property(GUI.class,
			"lookAndFeel"));

	private Model<Boolean> showAboutOnStartup = getModel(new Property(
			GUI.class, "showAboutOnStartup"));

	private Model<Boolean> fullScreenOnLoad = getModel(new Property(
			FullScreenAction.class, "onLoad"));

	private Model<Boolean> fullScreenAutoScroll = getModel(new Property(
			FullScreen.class, "autoScroll"));

	private Model<Boolean> flushImagesOnClose = getModel(new Property(
			SkinManager.class, "flushImagesOnClose"));

	private JComboBox lookAndFeelComboBox = new JComboBox();

	private JCheckBox showAboutOnStartupCheckBox = new JCheckBox();

	private Model<OrganFrame.Changes> changes = getModel(new Property(
			OrganFrame.class, "changes"));

	private JCheckBox fullScreenOnLoadCheckBox = new JCheckBox();

	private JCheckBox fullScreenAutoScrollCheckBox = new JCheckBox();

	private JRadioButton changesDiscardRadioButton = new JRadioButton();

	private JRadioButton changesSaveRegistrationsRadioButton = new JRadioButton();

	private JRadioButton changesConfirmRadioButton = new JRadioButton();

	private JRadioButton changesSaveRadioButton = new JRadioButton();

	private JCheckBox flushImagesOnCloseCheckBox = new JCheckBox();

	public GuiCategory() {
		config.read(this);
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);

		Column column = builder.column();

		column.term(config.get("lookAndFeel").read(new JLabel()));
		lookAndFeelComboBox.setModel(new DefaultComboBoxModel(LAF.values()));
		column.definition(lookAndFeelComboBox);

		column.definition(config.get("showAboutOnStartup").read(
				showAboutOnStartupCheckBox));

		column.definition(config.get("flushImagesOnClose").read(
				flushImagesOnCloseCheckBox));

		column.term(config.get("fullScreen").read(new JLabel()));

		column.definition(config.get("fullScreenOnLoad").read(
				fullScreenOnLoadCheckBox));

		column.definition(config.get("fullScreenAutoScroll").read(
				fullScreenAutoScrollCheckBox));

		ButtonGroup changesGroup = new ButtonGroup();
		column.term(config.get("changes").read(new JLabel()));

		config.get("changesDiscard").read(changesDiscardRadioButton);
		changesGroup.add(changesDiscardRadioButton);
		column.definition(changesDiscardRadioButton);

		config.get("changesSaveRegistrations").read(
				changesSaveRegistrationsRadioButton);
		changesGroup.add(changesSaveRegistrationsRadioButton);
		column.definition(changesSaveRegistrationsRadioButton);

		config.get("changesConfirm").read(changesConfirmRadioButton);
		changesGroup.add(changesConfirmRadioButton);
		column.definition(changesConfirmRadioButton);

		config.get("changesSave").read(changesSaveRadioButton);
		changesGroup.add(changesSaveRadioButton);
		column.definition(changesSaveRadioButton);

		return panel;
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected void read() {
		lookAndFeelComboBox.setSelectedItem(lookAndFeel.getValue());
		showAboutOnStartupCheckBox.setSelected(showAboutOnStartup.getValue());
		fullScreenOnLoadCheckBox.setSelected(fullScreenOnLoad.getValue());
		fullScreenAutoScrollCheckBox.setSelected(fullScreenAutoScroll
				.getValue());

		flushImagesOnCloseCheckBox.setSelected(flushImagesOnClose.getValue());

		switch (changes.getValue()) {
		case DISCARD:
			changesDiscardRadioButton.setSelected(true);
			break;
		case SAVE_REGISTRATIONS:
			changesSaveRegistrationsRadioButton.setSelected(true);
			break;
		case CONFIRM:
			changesConfirmRadioButton.setSelected(true);
			break;
		case SAVE:
			changesSaveRadioButton.setSelected(true);
			break;
		}
	}

	@Override
	protected void write() {
		lookAndFeel.setValue((LAF) lookAndFeelComboBox.getSelectedItem());
		showAboutOnStartup.setValue(showAboutOnStartupCheckBox.isSelected());
		fullScreenOnLoad.setValue(fullScreenOnLoadCheckBox.isSelected());
		fullScreenAutoScroll
				.setValue(fullScreenAutoScrollCheckBox.isSelected());

		flushImagesOnClose.setValue(flushImagesOnCloseCheckBox.isSelected());

		Changes value = null;
		if (changesDiscardRadioButton.isSelected()) {
			value = Changes.DISCARD;
		} else if (changesSaveRegistrationsRadioButton.isSelected()) {
			value = Changes.SAVE_REGISTRATIONS;
		} else if (changesConfirmRadioButton.isSelected()) {
			value = Changes.CONFIRM;
		} else if (changesSaveRadioButton.isSelected()) {
			value = Changes.SAVE;
		}
		changes.setValue(value);
	}
}