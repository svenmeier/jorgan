/*
 * jOrgan - Java Virtual Pipe Organ
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
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.text.MessageFormat;

import jorgan.shell.Configuration;

/**
 * A panel for the {@link jorgan.shell.Configuration}.
 */
public class ShellConfigPanel extends ConfigurationPanel {

  private static final String[] encodings = new String[]{"Cp1252", "Cp850", "ISO-8859-1", "US-ASCII", "UTF-16", "UTF-8"};
		
  private JPanel       encodingPanel              = new JPanel(); 
  private JRadioButton encodingDefaultRadioButton = new JRadioButton();
  private JRadioButton encodingOtherRadioButton   = new JRadioButton();
  private ButtonGroup  buttonGroup                = new ButtonGroup();
  private JComboBox    encodingComboBox           = new JComboBox();

  public ShellConfigPanel() {
    setLayout(new GridBagLayout());

    setName(resources.getString("config.shell.name"));

		encodingPanel.setLayout(new GridBagLayout());
		encodingPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), resources.getString("config.shell.encoding")));
		add(encodingPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));

			String defaultEncoding = System.getProperty("file.encoding");
			encodingDefaultRadioButton.setText(MessageFormat.format(resources.getString("config.shell.encodingDefault"), new Object[]{defaultEncoding}));
			buttonGroup.add(encodingDefaultRadioButton);
			encodingPanel.add(encodingDefaultRadioButton, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));

			encodingOtherRadioButton.setText(resources.getString("config.shell.encodingOther"));
			buttonGroup.add(encodingOtherRadioButton);		
			encodingPanel.add(encodingOtherRadioButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));    
	    
	    encodingComboBox.setEditable(true);
	    encodingComboBox.setModel(new DefaultComboBoxModel(encodings));
			encodingOtherRadioButton.addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent ev) {
					encodingComboBox.setEnabled(encodingOtherRadioButton.isSelected());
				}
			});
			encodingPanel.add(encodingComboBox, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));

    add(new JLabel(), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, emptyInsets, 0, 0));
  }

  public void read() {
    Configuration config = (Configuration)getConfiguration();

		encodingDefaultRadioButton.setSelected(config.getUseDefaultEncoding());
		encodingOtherRadioButton  .setSelected(!config.getUseDefaultEncoding());
		encodingComboBox.setEnabled          (!config.getUseDefaultEncoding());
		encodingComboBox.setSelectedItem     (config.getEncoding());
  }

  /**
   * Write the configuration.
   */
  public void write() {
    Configuration config = (Configuration)getConfiguration();

    config.setUseDefaultEncoding(encodingDefaultRadioButton.isSelected());
    config.setEncoding          ((String)encodingComboBox.getSelectedItem());
  }
}