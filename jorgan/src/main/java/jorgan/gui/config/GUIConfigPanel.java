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

import jorgan.gui.Configuration;

/**
 * A panel for the {@link jorgan.gui.Configuration}.
 */
public class GUIConfigPanel extends ConfigurationPanel {

  private JCheckBox useSystemLookAndFeelCheckBox = new JCheckBox();
  private JCheckBox showAboutOnStartupCheckBox = new JCheckBox();

  public GUIConfigPanel() {
    setLayout(new GridBagLayout());

    setName(resources.getString("config.swing.name"));

    useSystemLookAndFeelCheckBox.setText(resources.getString("config.swing.useSystemLookAndFeel"));
    add(useSystemLookAndFeelCheckBox, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));

    showAboutOnStartupCheckBox.setText(resources.getString("config.swing.showAboutOnStartup"));
    add(showAboutOnStartupCheckBox, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));

    add(new JLabel(), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 512, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, emptyInsets, 0, 0));
  }

  public void read() {
    Configuration config = (Configuration)getConfiguration();
    useSystemLookAndFeelCheckBox.setSelected(config.getUseSystemLookAndFeel());
    showAboutOnStartupCheckBox  .setSelected(config.getShowAboutOnStartup());
  }

  /**
   * Write the configuration.
   */
  public void write() {
    Configuration config = (Configuration)getConfiguration();
    config.setUseSystemLookAndFeel(useSystemLookAndFeelCheckBox.isSelected());
    config.setShowAboutOnStartup  (showAboutOnStartupCheckBox  .isSelected());
  }
}