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
package jorgan.swing;

import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * Selector of a file.
 */
public class FileSelector extends JPanel {

  /**
   * The textField used to edit the selected file.
   */
  private JTextField textField = new JTextField();

  /**
   * The button used to edit the selected file.
   */
  private JButton button = new JButton("...");
  
  /**
   * The fileChooser to use for file selection.
   */
  private JFileChooser chooser ;
  
  /**
   * The listeners to changes.
   */
  private java.util.List listeners = new ArrayList();
  
  /**
   * The filter for file selection.
   */
  private javax.swing.filechooser.FileFilter filter; 
  
  /**
   * Create a new selector.
   */
  public FileSelector() {
    super(new BorderLayout());

    textField.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
        fireStateChanged();
      }
      public void insertUpdate(DocumentEvent e) {
        fireStateChanged();
      }
      public void removeUpdate(DocumentEvent e) {
        fireStateChanged();
      }
    });
    add(textField, BorderLayout.CENTER);
    
    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        showFileChooser();
      }
    });
    add(button, BorderLayout.EAST);
  }

  public void setEnabled(boolean enabled) {
    textField.setEnabled(enabled);
    button.setEnabled(enabled);
  }

  public void addChangeListener(ChangeListener listener) {
    listeners.add(listener); 
  }
  
  public void removeChangeListener(ChangeListener listener) {
    listeners.remove(listener); 
  }
  
  protected void showFileChooser() {
    if (chooser == null) {
      chooser = new JFileChooser();
    }
    chooser.setSelectedFile(getSelectedFile());
    chooser.setFileFilter  (filter);
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
      textField.setText(chooser.getSelectedFile().getAbsolutePath());
    }
  }
  
  protected void fireStateChanged() {
    for (int l = 0; l < listeners.size(); l++) {
      ChangeListener listener = (ChangeListener)listeners.get(l);
      listener.stateChanged(new ChangeEvent(this));
    }
  }
  
  /**
   * Set the selected file.
   *
   * @param file  the file to select
   */
  public void setSelectedFile(File file) {
    if (file == null) {
      textField.setText("");
    } else {
      textField.setText(file.getAbsolutePath());
    }
  }

  /**
   * Get the selected file.
   *
   * @return  the selected file
   */
  public File getSelectedFile() {
    if ("".equals(textField.getText())) {
      return null;
    } else {
      return new File(textField.getText());
    }
  }
}