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

import jorgan.play.Configuration;

/**
 * A panel for the {@link jorgan.play.Configuration}.
 */
public class PlayConfigPanel extends ConfigurationPanel {
  private JPanel warningPanel = new JPanel();

  private JCheckBox warnKeyboardWithoutDeviceCheckBox = new JCheckBox();
  private JCheckBox warnConsoleWithoutDeviceCheckBox = new JCheckBox();
  private JCheckBox warnSoundSourceWithoutDeviceCheckBox = new JCheckBox();
  private JCheckBox warnStopWithoutMessageCheckBox = new JCheckBox();
  private JCheckBox warnCouplerWithoutMessageCheckBox = new JCheckBox();
  private JCheckBox warnTremulantWithoutMessageCheckBox = new JCheckBox();
  private JCheckBox warnSwellWithoutMessageCheckBox = new JCheckBox();
  private JCheckBox warnVariationWithoutMessageCheckBox = new JCheckBox();
  private JCheckBox warnPistonWithoutMessageCheckBox = new JCheckBox();

  private JCheckBox releaseDevicesCheckBox = new JCheckBox();

  public PlayConfigPanel() {
    setLayout(new GridBagLayout());
    setName(resources.getString("config.play.name"));

    warningPanel.setLayout(new GridBagLayout());
    warningPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), resources.getString("config.play.warnings")));
    add(warningPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));

      warnKeyboardWithoutDeviceCheckBox.setText(resources.getString("config.play.warnKeyboardWithoutDevice"));
      warningPanel.add(warnKeyboardWithoutDeviceCheckBox, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));
  
      warnConsoleWithoutDeviceCheckBox.setText(resources.getString("config.play.warnConsoleWithoutDevice"));
      warningPanel.add(warnConsoleWithoutDeviceCheckBox, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));
  
      warnSoundSourceWithoutDeviceCheckBox.setText(resources.getString("config.play.warnSoundSourceWithoutDevice"));
      warningPanel.add(warnSoundSourceWithoutDeviceCheckBox, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));

      warnStopWithoutMessageCheckBox.setText(resources.getString("config.play.warnStopWithoutMessage"));
      warningPanel.add(warnStopWithoutMessageCheckBox, new GridBagConstraints(0, 6, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));

      warnCouplerWithoutMessageCheckBox.setText(resources.getString("config.play.warnCouplerWithoutMessage"));
      warningPanel.add(warnCouplerWithoutMessageCheckBox, new GridBagConstraints(0, 8, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));

      warnTremulantWithoutMessageCheckBox.setText(resources.getString("config.play.warnTremulantWithoutMessage"));
      warningPanel.add(warnTremulantWithoutMessageCheckBox, new GridBagConstraints(0, 9, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));

      warnSwellWithoutMessageCheckBox.setText(resources.getString("config.play.warnSwellWithoutMessage"));
      warningPanel.add(warnSwellWithoutMessageCheckBox, new GridBagConstraints(0, 10, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));

      warnVariationWithoutMessageCheckBox.setText(resources.getString("config.play.warnVariationWithoutMessage"));
      warningPanel.add(warnVariationWithoutMessageCheckBox, new GridBagConstraints(0, 11, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));

      warnPistonWithoutMessageCheckBox.setText(resources.getString("config.play.warnPistonWithoutMessage"));
      warningPanel.add(warnPistonWithoutMessageCheckBox, new GridBagConstraints(0, 12, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));

    releaseDevicesCheckBox.setText(resources.getString("config.play.releaseDevicesWhenDeactivated"));
    add(releaseDevicesCheckBox, new GridBagConstraints(0, 13, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));    
    
    add(new JLabel(), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 512, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, emptyInsets, 0, 0));
  }

  /**
   * Read the configuration.
   */
  public void read() {
    Configuration config = (Configuration)getConfiguration();

    warnKeyboardWithoutDeviceCheckBox  .setSelected(config.getWarnKeyboardWithoutDevice());
    warnConsoleWithoutDeviceCheckBox   .setSelected(config.getWarnConsoleWithoutDevice());
    warnSoundSourceWithoutDeviceCheckBox.setSelected(config.getWarnSoundSourceWithoutDevice());
    warnStopWithoutMessageCheckBox     .setSelected(config.getWarnStopWithoutMessage());
    warnCouplerWithoutMessageCheckBox  .setSelected(config.getWarnCouplerWithoutMessage());
    warnTremulantWithoutMessageCheckBox.setSelected(config.getWarnTremulantWithoutMessage());
    warnSwellWithoutMessageCheckBox    .setSelected(config.getWarnSwellWithoutMessage());
    warnVariationWithoutMessageCheckBox.setSelected(config.getWarnVariationWithoutMessage());
    warnPistonWithoutMessageCheckBox   .setSelected(config.getWarnPistonWithoutMessage());

    releaseDevicesCheckBox.setSelected(config.getReleaseDevicesWhenDeactivated());
  }

  /**
   * Write the configuration.
   */
  public void write() {
    Configuration config = (Configuration)getConfiguration();

    config.setWarnKeyboardWithoutDevice   (warnKeyboardWithoutDeviceCheckBox   .isSelected());
    config.setWarnConsoleWithoutDevice    (warnConsoleWithoutDeviceCheckBox    .isSelected());
    config.setWarnSoundSourceWithoutDevice(warnSoundSourceWithoutDeviceCheckBox.isSelected());
    config.setWarnStopWithoutMessage      (warnStopWithoutMessageCheckBox      .isSelected());
    config.setWarnCouplerWithoutMessage   (warnCouplerWithoutMessageCheckBox   .isSelected());
    config.setWarnTremulantWithoutMessage (warnTremulantWithoutMessageCheckBox .isSelected());
    config.setWarnSwellWithoutMessage     (warnSwellWithoutMessageCheckBox     .isSelected());
    config.setWarnVariationWithoutMessage (warnVariationWithoutMessageCheckBox .isSelected());
    config.setWarnPistonWithoutMessage    (warnPistonWithoutMessageCheckBox    .isSelected());

    config.setReleaseDevicesWhenDeactivated(releaseDevicesCheckBox.isSelected());
  }
}