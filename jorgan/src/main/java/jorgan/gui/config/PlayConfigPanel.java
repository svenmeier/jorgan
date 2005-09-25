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

import jorgan.play.Configuration;

/**
 * A panel for the {@link jorgan.play.Configuration}.
 */
public class PlayConfigPanel extends ConfigurationPanel {

  private JCheckBox warnDeviceCheckBox = new JCheckBox();
  private JCheckBox warnMessageCheckBox = new JCheckBox();
  
  private JCheckBox releaseDevicesCheckBox = new JCheckBox();

  public PlayConfigPanel() {
    setLayout(new GridBagLayout());
    setName(resources.getString("config.play.name"));

    warnDeviceCheckBox.setText(resources.getString("config.play.warn.device"));
    add(warnDeviceCheckBox, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));
  
    warnMessageCheckBox.setText(resources.getString("config.play.warn.message"));
    add(warnMessageCheckBox, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));

    releaseDevicesCheckBox.setText(resources.getString("config.play.releaseDevicesWhenDeactivated"));
    add(releaseDevicesCheckBox, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));    
    
    add(new JLabel(), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 512, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, emptyInsets, 0, 0));
  }

  /**
   * Read the configuration.
   */
  public void read() {
    Configuration config = (Configuration)getConfiguration();

    warnDeviceCheckBox.setSelected(config.getWarnWithoutDevice());
    warnMessageCheckBox.setSelected(config.getWarnWithoutMessage());

    releaseDevicesCheckBox.setSelected(config.getReleaseDevicesWhenDeactivated());
  }

  /**
   * Write the configuration.
   */
  public void write() {
    Configuration config = (Configuration)getConfiguration();

    config.setWarnWithoutDevice(warnDeviceCheckBox.isSelected());
    config.setWarnWithoutMessage(warnMessageCheckBox.isSelected());

    config.setReleaseDevicesWhenDeactivated(releaseDevicesCheckBox.isSelected());
  }
}