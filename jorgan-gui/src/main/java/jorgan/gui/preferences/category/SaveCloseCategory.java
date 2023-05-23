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

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.gui.OrganFrame;
import jorgan.gui.OrganFrame.Changes;
import jorgan.session.OrganSession;
import jorgan.skin.SkinManager;
import jorgan.swing.button.ButtonGroup;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 */
public class SaveCloseCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
		SaveCloseCategory.class);

	private Model<OrganFrame.Changes> changes = getModel(new Property(
		OrganFrame.class, "changes"));

	private Model<Integer> backupCount = getModel(new Property(
		OrganSession.class, "backupCount"));

	private Model<Boolean> saveOnShutdown = getModel(new Property(
		OrganSession.class, "saveOnShutdown"));

	private Model<Boolean> flushImagesOnClose = getModel(new Property(
		SkinManager.class, "flushImagesOnClose"));

	private JRadioButton changesDiscardRadioButton = new JRadioButton();

	private JRadioButton changesSaveRegistrationsRadioButton = new JRadioButton();

	private JRadioButton changesConfirmRadioButton = new JRadioButton();

	private JRadioButton changesSaveRadioButton = new JRadioButton();

	private JSpinner backupCountSpinner = new JSpinner(new SpinnerNumberModel(
			0, 0, 255, 1));

	private JCheckBox saveOnShutdownCheckBox = new JCheckBox();

	private JCheckBox flushImagesOnCloseCheckBox = new JCheckBox();

	public SaveCloseCategory() {
		config.read(this);
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		DefinitionBuilder builder = new DefinitionBuilder(panel);
		Column column = builder.column();

		ButtonGroup changesGroup = new ButtonGroup();
		column.term(config.get("changes").read(new JLabel()));

		config.get("changesDiscard").read(changesDiscardRadioButton);
		changesGroup.add(changesDiscardRadioButton);
		column.definition(changesDiscardRadioButton);

		config.get("changesSaveRegistrations")
				.read(changesSaveRegistrationsRadioButton);
		changesGroup.add(changesSaveRegistrationsRadioButton);
		column.definition(changesSaveRegistrationsRadioButton);

		config.get("changesConfirm").read(changesConfirmRadioButton);
		changesGroup.add(changesConfirmRadioButton);
		column.definition(changesConfirmRadioButton);

		config.get("changesSave").read(changesSaveRadioButton);
		changesGroup.add(changesSaveRadioButton);
		column.definition(changesSaveRadioButton);

		column.term(config.get("backupCount").read(new JLabel()));
		column.definition(backupCountSpinner);

		column.definition(config.get("saveOnShutdown").read(saveOnShutdownCheckBox));

		column.definition(config.get("flushImagesOnClose").read(flushImagesOnCloseCheckBox));

		return panel;
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected void read() {
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

		backupCountSpinner.setValue(backupCount.getValue());
		saveOnShutdownCheckBox.setSelected(saveOnShutdown.getValue());
		flushImagesOnCloseCheckBox.setSelected(flushImagesOnClose.getValue());
	}

	@Override
	protected void write() {
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
		
		backupCount.setValue((Integer) backupCountSpinner.getValue());
		saveOnShutdown.setValue(saveOnShutdownCheckBox.isSelected());
		flushImagesOnClose.setValue(flushImagesOnCloseCheckBox.isSelected());
	}
}