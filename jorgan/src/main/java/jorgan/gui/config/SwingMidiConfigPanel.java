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

import jorgan.gui.midi.Configuration;

/**
 * A panel for the {@link jorgan.swing.midi.Configuration}.
 */
public class SwingMidiConfigPanel extends ConfigurationPanel {

  /*
   * The components.
   */
  private JLabel maxLabel = new JLabel();
  private JSpinner maxSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 50));
  private JLabel numbersLabel = new JLabel();
  private JRadioButton numbersDecRadioButton = new JRadioButton();
  private JRadioButton numbersHexRadioButton = new JRadioButton();
  
  /**
   * Create this panel.
   */
  public SwingMidiConfigPanel() {
    setLayout(new GridBagLayout());
    setName(resources.getString("config.swing.midi.name"));

    maxLabel.setText(resources.getString("config.swing.midi.max"));
    add(maxLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));
    
    add(maxSpinner, new GridBagConstraints(1, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));    
  
    numbersLabel.setText(resources.getString("config.swing.midi.numbers"));
    add(numbersLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));
    
    ButtonGroup group = new ButtonGroup();
    group.add(numbersDecRadioButton);
    numbersDecRadioButton.setText(resources.getString("config.swing.midi.numbersDec"));
    add(numbersDecRadioButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));    
    group.add(numbersHexRadioButton);
    numbersHexRadioButton.setText(resources.getString("config.swing.midi.numbersHex"));
    add(numbersHexRadioButton, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, standardInsets, 0, 0));    

    add(new JLabel(), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 512, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, emptyInsets, 0, 0));
  }

  /**
   * Read the configuration.
   */
  public void read() {
    Configuration config = (Configuration)getConfiguration();

    maxSpinner.setValue(new Integer(config.getMidiLogMax()));
    
    numbersDecRadioButton.setSelected(!config.getMidiLogHex());
    numbersHexRadioButton.setSelected(config.getMidiLogHex());
  }

  /**
   * Write the configuration.
   */
  public void write() {
    Configuration config = (Configuration)getConfiguration();

    config.setMidiLogMax(((Integer)maxSpinner.getValue()).intValue());
    
    config.setMidiLogHex(numbersHexRadioButton.isSelected());
  }  
}