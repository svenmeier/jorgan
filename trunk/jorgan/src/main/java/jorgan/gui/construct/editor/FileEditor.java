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
package jorgan.gui.construct.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

/**
 * PropertyEditor for a file property.
 */
public class FileEditor extends CustomEditor implements ActionListener {

  private JPanel     panel     = new JPanel();
  private JTextField textField = new JTextField();
  private JButton    button    = new JButton("...");
  
  private JFileChooser fileChooser;

  public FileEditor() {
    panel.setLayout(new BorderLayout());

    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(this);
    panel.add(button, BorderLayout.EAST);

    panel.add(textField, BorderLayout.CENTER);
  }

  public Component getCustomEditor(Object value) {

    textField.setText(format(value));

    return panel;
  }

  protected Object getEditedValue() {

    String file = textField.getText();
    if ("".equals(file)) {
      return null;
    } else {
      return file;
    }
  }

  public void actionPerformed(ActionEvent ev) {

    if (fileChooser == null) {
      fileChooser = new JFileChooser();
      fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
      fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
      fileChooser.setMultiSelectionEnabled(false);
    }
    
    if (!"".equals(textField.getText())) {
      fileChooser.setSelectedFile(new File(textField.getText()));
    }
    
    if (fileChooser.showOpenDialog(button) == JFileChooser.APPROVE_OPTION) {
      textField.setText(fileChooser.getSelectedFile().getPath());
    }
  }

  protected String format(Object value) {
      
    if (value == null) {
      return "";
    } else {
      return value.toString();
    }
  }
}