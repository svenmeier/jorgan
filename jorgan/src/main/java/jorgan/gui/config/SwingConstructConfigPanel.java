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

import jorgan.gui.construct.Configuration;
import jorgan.swing.color.*;

/**
 * A panel for the {@link jorgan.swing.construct.Configuration}.
 */
public class SwingConstructConfigPanel extends ConfigurationPanel {

  private JLabel gridLabel = new JLabel();
  private JSpinner gridSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 256, 1)); 
  private JLabel        colorLabel = new JLabel();
  private ColorSelector colorSelector = new ColorSelector();

  public SwingConstructConfigPanel() {
    setLayout(new GridBagLayout());

    setName(resources.getString("config.construct.name"));

    gridLabel.setText(resources.getString("config.construct.grid"));
    add(gridLabel,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
    add(gridSpinner, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));

    colorLabel.setText(resources.getString("config.construct.color"));
    add(colorLabel   , new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
    add(colorSelector, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));

    add(new JLabel(), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, emptyInsets, 0, 0));
  }

  public void read() {
    Configuration config = (Configuration)getConfiguration();

    gridSpinner.setValue(new Integer(config.getGrid()));

    colorSelector.setSelectedColor(config.getColor());
  }

  /**
   * Write the configuration.
   */
  public void write() {
    Configuration config = (Configuration)getConfiguration();

    config.setGrid(((Integer)gridSpinner.getValue()).intValue());

    config.setColor(colorSelector.getSelectedColor());
  }
}