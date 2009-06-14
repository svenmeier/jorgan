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
package jorgan.customizer.gui.preferences;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import jorgan.customizer.gui.CustomizeAction;
import jorgan.gui.GUI;
import jorgan.gui.preferences.category.GuiCategory;
import jorgan.gui.preferences.category.JOrganCategory;
import jorgan.swing.button.ButtonGroup;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * {@link GUI} category.
 */
public class CustomizerCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			CustomizerCategory.class);

	private Model handleErrors = getModel(new Property(CustomizeAction.class,
			"handleErrors"));

	private JRadioButton errorsOfferRadioButton = new JRadioButton();

	private JRadioButton errorsCustomizeRadioButton = new JRadioButton();

	private JRadioButton errorsIgnoreRadioButton = new JRadioButton();

	public CustomizerCategory() {
		config.read(this);
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);

		Column column = builder.column();

		ButtonGroup errorGroup = new ButtonGroup();
		column.term(config.get("handleErrors").read(new JLabel()));

		config.get("errorsOffer").read(errorsOfferRadioButton);
		errorGroup.add(errorsOfferRadioButton);
		column.definition(errorsOfferRadioButton);

		config.get("errorsCustomize").read(errorsCustomizeRadioButton);
		errorGroup.add(errorsCustomizeRadioButton);
		column.definition(errorsCustomizeRadioButton);

		config.get("errorsIgnore").read(errorsIgnoreRadioButton);
		errorGroup.add(errorsIgnoreRadioButton);
		column.definition(errorsIgnoreRadioButton);

		return panel;
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return GuiCategory.class;
	}

	@Override
	protected void read() {
		switch ((Integer) handleErrors.getValue()) {
		case CustomizeAction.ERROR_OFFER:
			errorsOfferRadioButton.setSelected(true);
			break;
		case CustomizeAction.ERROR_CUSTOMIZE:
			errorsCustomizeRadioButton.setSelected(true);
			break;
		case CustomizeAction.ERROR_IGNORE:
			errorsIgnoreRadioButton.setSelected(true);
			break;
		}
	}

	@Override
	protected void write() {
		int errors = 0;
		if (errorsOfferRadioButton.isSelected()) {
			errors = CustomizeAction.ERROR_OFFER;
		} else if (errorsCustomizeRadioButton.isSelected()) {
			errors = CustomizeAction.ERROR_CUSTOMIZE;
		} else if (errorsIgnoreRadioButton.isSelected()) {
			errors = CustomizeAction.ERROR_IGNORE;
		}
		handleErrors.setValue(errors);
	}
}