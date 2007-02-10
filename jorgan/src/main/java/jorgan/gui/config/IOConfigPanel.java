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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;

import jorgan.io.Configuration;
import jorgan.swing.GridBuilder;
import jorgan.util.I18N;

/**
 * A panel for the {@link jorgan.io.Configuration}.
 */
public class IOConfigPanel extends ConfigurationPanel {

	private static I18N i18n = I18N.get(IOConfigPanel.class);

	private JPanel recentsPanel = new JPanel();

	private JCheckBox recentOpenOnStartupCheckBox = new JCheckBox();

	private JSpinner recentMaxSpinner = new JSpinner(new SpinnerNumberModel(0,
			0, 100, 1));

	private JPanel changesPanel = new JPanel();

	private ButtonGroup changesGroup = new ButtonGroup();

	private JRadioButton confirmChangesRadioButton = new JRadioButton();

	private JRadioButton saveChangesRadioButton = new JRadioButton();

	private JRadioButton ignoreChangesRadioButton = new JRadioButton();

	private JSpinner historySizeSpinner = new JSpinner(new SpinnerNumberModel(
			0, 0, 255, 1));

	/**
	 * Constructor.
	 */
	public IOConfigPanel() {
		setName(i18n.getString("name"));
		setLayout(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();

		recentsPanel.setLayout(new GridBagLayout());
		recentsPanel.setBorder(new TitledBorder(BorderFactory
				.createEtchedBorder(), i18n.getString("recentsPanel.title")));
		add(recentsPanel, builder.nextColumn().gridWidthRemainder()
				.fillHorizontal());

		GridBuilder recentsBuilder = new GridBuilder(
				new double[] { 0.0d, 1.0d });

		recentsBuilder.nextRow();

		recentOpenOnStartupCheckBox.setText(i18n
				.getString("recentOpenOnStartupCheckBox.text"));
		recentsPanel.add(recentOpenOnStartupCheckBox, recentsBuilder
				.nextColumn().gridWidthRemainder());

		recentsBuilder.nextRow();

		JLabel recentMaxLabel = new JLabel(i18n
				.getString("recentMaxLabel.text"));
		recentsPanel.add(recentMaxLabel, recentsBuilder.nextColumn());
		recentsPanel.add(recentMaxSpinner, recentsBuilder.nextColumn());

		builder.nextRow();

		changesPanel.setLayout(new GridBagLayout());
		changesPanel.setBorder(new TitledBorder(BorderFactory
				.createEtchedBorder(), i18n.getString("changesPanel.title")));
		add(changesPanel, builder.nextColumn().gridWidthRemainder()
				.fillHorizontal());

		GridBuilder changesBuilder = new GridBuilder(new double[] { 1.0d });

		changesBuilder.nextRow();

		confirmChangesRadioButton.getModel().setGroup(changesGroup);
		confirmChangesRadioButton.setText(i18n
				.getString("confirmChangesRadioButton.text"));
		changesPanel
				.add(confirmChangesRadioButton, changesBuilder.nextColumn());

		changesBuilder.nextRow();

		saveChangesRadioButton.getModel().setGroup(changesGroup);
		saveChangesRadioButton.setText(i18n
				.getString("saveChangesRadioButton.text"));
		changesPanel.add(saveChangesRadioButton, changesBuilder.nextColumn());

		changesBuilder.nextRow();

		ignoreChangesRadioButton.getModel().setGroup(changesGroup);
		ignoreChangesRadioButton.setText(i18n
				.getString("ignoreChangesRadioButton.text"));
		changesPanel.add(ignoreChangesRadioButton, changesBuilder.nextColumn());

		builder.nextRow();

		JLabel historySizeLabel = new JLabel(i18n
				.getString("historySizeLabel.text"));
		add(historySizeLabel, builder.nextColumn());
		add(historySizeSpinner, builder.nextColumn());
	}

	public void read() {
		Configuration config = (Configuration) getConfiguration();

		recentOpenOnStartupCheckBox
				.setSelected(config.getRecentOpenOnStartup());
		recentMaxSpinner.setValue(new Integer(config.getRecentMax()));
		switch (config.getRegistrationChanges()) {
		case Configuration.REGISTRATION_CHANGES_CONFIRM:
			confirmChangesRadioButton.setSelected(true);
			break;
		case Configuration.REGISTRATION_CHANGES_SAVE:
			saveChangesRadioButton.setSelected(true);
			break;
		case Configuration.REGISTRATION_CHANGES_IGNORE:
			ignoreChangesRadioButton.setSelected(true);
			break;
		}

		historySizeSpinner.setValue(new Integer(config.getHistorySize()));
	}

	/**
	 * Write the configuration.
	 */
	public void write() {
		Configuration config = (Configuration) getConfiguration();

		config.setRecentOpenOnStartup(recentOpenOnStartupCheckBox.isSelected());
		config.setRecentMax(((Integer) recentMaxSpinner.getValue()).intValue());
		if (confirmChangesRadioButton.isSelected()) {
			config
					.setRegistrationChanges(Configuration.REGISTRATION_CHANGES_CONFIRM);
		} else if (saveChangesRadioButton.isSelected()) {
			config
					.setRegistrationChanges(Configuration.REGISTRATION_CHANGES_SAVE);
		} else if (ignoreChangesRadioButton.isSelected()) {
			config
					.setRegistrationChanges(Configuration.REGISTRATION_CHANGES_IGNORE);
		}

		config.setHistorySize(((Integer) historySizeSpinner.getValue())
				.intValue());
	}
}