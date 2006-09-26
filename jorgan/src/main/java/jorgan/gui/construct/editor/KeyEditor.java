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

import java.util.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.sound.midi.*;

import jorgan.sound.midi.ShortMessageRecorder;
import jorgan.disposition.*;

/**
 * Property editor for a key property.
 */
public class KeyEditor extends CustomEditor implements ElementAwareEditor, ActionListener {

  private static ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  private Keyboard keyboard;

  private KeyFormatter formatter = new KeyFormatter();
  private KeyModel     model     = new KeyModel();

  private JPanel   panel   = new JPanel();
  private JSpinner spinner = new JSpinner(model);
  private JButton  button  = new JButton("...");

  private JDialog              dialog;
  private ShortMessageRecorder recorder;

  public KeyEditor() {
    panel.setLayout(new BorderLayout());

    button.setFocusable(false);
    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(this);
    panel.add(button, BorderLayout.EAST);

    spinner.setBorder(null);
    panel.add(spinner, BorderLayout.CENTER);

    JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)spinner.getEditor();
    editor.getTextField().setBorder(null);
    editor.getTextField().setEditable(true);
    editor.getTextField().setFormatterFactory(new DefaultFormatterFactory(formatter));
  }

  public void setElement(Element element) {
    keyboard = (Keyboard)element;
  }

  public Component getCustomEditor(Object value) {

    spinner.setValue(value);
    button.setEnabled(keyboard.getDevice() != null);

    return panel;
  }

  protected Object getEditedValue() {
    try {
      JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)spinner.getEditor();
      editor.commitEdit();
    } catch (ParseException ex) {
      // invalid format so keep previous value
    }

    return spinner.getValue();
  }

  public void actionPerformed(ActionEvent ev) {
    try {
      recorder = new KeyRecorder(keyboard.getDevice());
    } catch (MidiUnavailableException ex) {
      // cannot record
      return;
    }

    JOptionPane optionPane = new JOptionPane(resources.getString("construct.editor.key.description"),
                                             JOptionPane.INFORMATION_MESSAGE, -1, null,
                                             new Object[]{resources.getString("construct.editor.key.cancel")});

    dialog = optionPane.createDialog(panel.getTopLevelAncestor(),
                                     resources.getString("construct.editor.key.title"));
    dialog.setVisible(true);
    dialog = null;

    recorder.close();
  }

  protected String format(Object value) {
    return formatter.valueToString(value);
  }

  private class KeyModel extends AbstractSpinnerModel {

    private Key key;

    public Object getValue() {
      return key;
    }

    public void setValue(Object value) {

      if (value == null && key != null ||
          value != null && key == null ||
          value != null && !value.equals(key)) {
        this.key = (Key)value;
        fireStateChanged();
      }
    }

    public Object getNextValue() {
      if (key == null) {
        return Key.C4;
      } else {
        return key.halftoneUp();
      }
    }

    public Object getPreviousValue() {
      if (key == null) {
        return Key.C4;
      } else {
        return key.halftoneDown();
      }
    }
  }

  private class KeyFormatter extends JFormattedTextField.AbstractFormatter {
    public Object stringToValue(String text) throws ParseException {
      if ("".equals(text)) {
        return null;
      } else{
        try {
          return new Key(text);
        } catch (IllegalArgumentException ex) {
          throw new ParseException("no key with name '" + text + "'", 0);
        }
      }
    }

    public String valueToString(Object value) {
      if (value == null) {
        return "";
      } else {
        return ((Key)value).getName();
      }
    }
  }

  /**
   * Recorder of a key.
   */
  private class KeyRecorder extends ShortMessageRecorder implements Runnable {

    private int pitch;

    public KeyRecorder(String deviceName) throws MidiUnavailableException {
      super(deviceName);
    }

    public void messageRecorded(ShortMessage message) {
      if (message.getCommand() == ShortMessage.NOTE_ON) {
        pitch = message.getData1();

        SwingUtilities.invokeLater(this);
      }
    }

    public void run() {
      Key key = new Key(pitch);

      spinner.setValue(key);

      dialog.setVisible(false);
    }
  }
}
