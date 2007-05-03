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

import jorgan.gui.OrganFrame;
import jorgan.swing.GridBuilder;
import bias.swing.Category;
import bias.swing.PropertyModel;

import com.sun.imageio.plugins.common.I18N;

/**
 * {@link OrganFrame} category.
 */
public class OrganFrameCategory extends JOrganCategory {

	private static I18N i18n = I18N.get(OrganFrameCategory.class);

	private PropertyModel fullScreenOnLoad = getModel(OrganFrame.class,
			"fullScreenOnLoad");

	private PropertyModel handleRegistrationChanges = getModel(
			OrganFrame.class, "handleRegistrationChanges");

	private JCheckBox fullScreenOnLoadCheckBox = new JCheckBox();

	private ButtonGroup changesGroup = new ButtonGroup();

	private JRadioButton confirmChangesRadioButton = new JRadioButton();

	private JRadioButton saveChangesRadioButton = new JRadioButton();

	private JRadioButton ignoreChangesRadioButton = new JRadioButton();

	public String createName() {
		return i18n.getString("name");
	}

	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 1.0d });

		builder.nextRow();

		fullScreenOnLoadCheckBox.setText(i18n
				.getString("fullScreenOnLoadCheckBox/text"));
		panel.add(fullScreenOnLoadCheckBox, builder.nextColumn());

		builder.nextRow();

		panel.add(createChangesPanel(), builder.nextColumn()
				.gridWidthRemainder().fillHorizontal());

		builder.nextRow();

		return panel;
	}

	private JPanel createChangesPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
				i18n.getString("changesPanel/title")));

		GridBuilder builder = new GridBuilder(new double[] { 1.0d });

		builder.nextRow();

		confirmChangesRadioButton.getModel().setGroup(changesGroup);
		confirmChangesRadioButton.setText(i18n
				.getString("confirmChangesRadioButton/text"));
		panel.add(confirmChangesRadioButton, builder.nextColumn());

		builder.nextRow();

		saveChangesRadioButton.getModel().setGroup(changesGroup);
		saveChangesRadioButton.setText(i18n
				.getString("saveChangesRadioButton/text"));
		panel.add(saveChangesRadioButton, builder.nextColumn());

		builder.nextRow();

		ignoreChangesRadioButton.getModel().setGroup(changesGroup);
		ignoreChangesRadioButton.setText(i18n
				.getString("ignoreChangesRadioButton/text"));
		panel.add(ignoreChangesRadioButton, builder.nextColumn());

		return panel;
	}

	public Class<? extends Category> getParentCategory() {
		return GUICategory.class;
	}

	protected void read() {
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
		fullScreenOnLoadCheckBox.setSelected((Boolean) fullScreenOnLoad
				.getValue());
	}

	protected void write() {
		int handle = 0;
		if (confirmChangesRadioButton.isSelected()) {
			handle = OrganFrame.REGISTRATION_CHANGES_CONFIRM;
		} else if (saveChangesRadioButton.isSelected()) {
			handle = OrganFrame.REGISTRATION_CHANGES_SAVE;
		} else if (ignoreChangesRadioButton.isSelected()) {
			handle = OrganFrame.REGISTRATION_CHANGES_IGNORE;
		}
		handleRegistrationChanges.setValue(handle);
		fullScreenOnLoad.setValue(fullScreenOnLoadCheckBox.isSelected());
	}
}