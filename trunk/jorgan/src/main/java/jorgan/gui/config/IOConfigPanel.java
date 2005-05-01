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

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import jorgan.io.Configuration;

/**
 * A panel for the {@link jorgan.io.Configuration}.
 */
public class IOConfigPanel extends ConfigurationPanel {

  private JPanel recentsPanel = new JPanel();
  private JCheckBox recentOpenOnStartupCheckBox = new JCheckBox();
  private JLabel recentMaxLabel = new JLabel();
  private JSpinner recentMaxSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
  private JPanel changesPanel = new JPanel();
  private ButtonGroup changesGroup = new ButtonGroup();
  private JRadioButton confirmChangesRadioButton = new JRadioButton();
  private JRadioButton saveChangesRadioButton = new JRadioButton();
  private JRadioButton ignoreChangesRadioButton = new JRadioButton();

  public IOConfigPanel() {
    setLayout(new GridBagLayout());

    setName(resources.getString("config.io.name"));

    recentsPanel.setLayout(new GridBagLayout());
    recentsPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), resources.getString("config.io.recents")));
    add(recentsPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));

      recentOpenOnStartupCheckBox.setText(resources.getString("config.io.recentOpenOnStartup"));
      recentsPanel.add(recentOpenOnStartupCheckBox, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));

      recentMaxLabel.setText(resources.getString("config.io.recentMax"));
      recentsPanel.add(recentMaxLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));

      recentsPanel.add(recentMaxSpinner, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));

    changesPanel.setLayout(new GridBagLayout());
    changesPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), resources.getString("config.io.changes")));
    add(changesPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));
      
      confirmChangesRadioButton.getModel().setGroup(changesGroup);
      confirmChangesRadioButton.setText(resources.getString("config.io.changesConfirm"));
      changesPanel.add(confirmChangesRadioButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));

      saveChangesRadioButton.getModel().setGroup(changesGroup);
      saveChangesRadioButton.setText(resources.getString("config.io.changesSave"));
      changesPanel.add(saveChangesRadioButton, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));

      ignoreChangesRadioButton.getModel().setGroup(changesGroup);
      ignoreChangesRadioButton.setText(resources.getString("config.io.changesIgnore"));
      changesPanel.add(ignoreChangesRadioButton, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));
      
    add(new JLabel(), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 512, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, emptyInsets, 0, 0));
  }

  public void read() {
    Configuration config = (Configuration)getConfiguration();

    recentOpenOnStartupCheckBox.setSelected(config.getRecentOpenOnStartup());
    recentMaxSpinner           .setValue(new Integer(config.getRecentMax()));
    switch (config.getRegistrationChanges()) {
      case Configuration.REGISTRATION_CHANGES_CONFIRM:
        confirmChangesRadioButton  .setSelected(true);
        break;
      case Configuration.REGISTRATION_CHANGES_SAVE:
        saveChangesRadioButton  .setSelected(true);
        break;
      case Configuration.REGISTRATION_CHANGES_IGNORE:
        ignoreChangesRadioButton  .setSelected(true);
        break;
    }
  }

  /**
   * Write the configuration.
   */
  public void write() {
    Configuration config = (Configuration)getConfiguration();

    config.setRecentOpenOnStartup       (recentOpenOnStartupCheckBox.isSelected());
    config.setRecentMax                 (((Integer)recentMaxSpinner.getValue()).intValue());
    if (confirmChangesRadioButton.isSelected()) {
      config.setRegistrationChanges(Configuration.REGISTRATION_CHANGES_CONFIRM);
    } else if (saveChangesRadioButton.isSelected()) {
      config.setRegistrationChanges(Configuration.REGISTRATION_CHANGES_SAVE);
    } else if (ignoreChangesRadioButton.isSelected()) {
      config.setRegistrationChanges(Configuration.REGISTRATION_CHANGES_IGNORE);
    }
  }
}