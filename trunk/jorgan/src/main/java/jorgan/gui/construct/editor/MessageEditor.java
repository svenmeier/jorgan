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

import java.util.List;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.*;
import javax.swing.*;
import javax.sound.midi.*;

import jorgan.disposition.*;
import jorgan.sound.midi.ShortMessageRecorder;

/**
 * PropertyEditor for a message property.
 */
public class MessageEditor extends CustomEditor implements ElementAwareEditor, ActionListener {

  private static ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  private String  device;

  private JPanel     panel     = new JPanel();
  private JTextField textField = new JTextField();
  private JButton    button    = new JButton("...");

  private JDialog    dialog;

  public MessageEditor() {
    panel.setLayout(new BorderLayout());

    button.setMargin(new Insets(0, 0, 0, 0));
    button.addActionListener(this);
    panel.add(button, BorderLayout.EAST);

    panel.add(textField, BorderLayout.CENTER);
  }

  public void setElement(Element element) {
    
    device = null;
    List consoles = element.getReferrer(Console.class);
    for (int c = 0; c < consoles.size(); c++) {
      device = ((Console)consoles.get(0)).getDevice();
      if (device != null) {
        break;
      }
    }
  }

  public Component getCustomEditor(Object value) {

    textField.setText(format(value));
    button.setEnabled(device != null);

    return panel;
  }

  protected Object getEditedValue() {

    Message message = null;

    try {
      StringTokenizer tokens = new StringTokenizer(textField.getText(), ",");

      if (tokens.countTokens() == 0) {
        message = null;
      } else {
        String token0 = tokens.nextToken().trim();
        String token1 = tokens.nextToken().trim();
        String token2 = tokens.nextToken().trim();

        int status = "*".equals(token0) ? -1 : Integer.parseInt(token0);
        int data1  = "*".equals(token1) ? -1 : Integer.parseInt(token1);
        int data2  = "*".equals(token2) ? -1 : Integer.parseInt(token2);

        message = new Message(status, data1, data2);
      }
    } catch (Exception ex) {
      // invalid format results in null message
    }

    return message;
  }

  public void actionPerformed(ActionEvent ev) {
    ShortMessageRecorder recorder;
    try {
      recorder = new MessageRecorder(device);
    } catch (MidiUnavailableException ex) {
      // cannot record
      return;
    }

    JOptionPane optionPane = new JOptionPane(resources.getString("construct.editor.message.description"),
                                             JOptionPane.INFORMATION_MESSAGE, -1, null,
                                             new Object[]{resources.getString("construct.editor.message.cancel")});

    dialog = optionPane.createDialog(panel.getTopLevelAncestor(),
                                     resources.getString("construct.editor.message.title"));
    dialog.setVisible(true);
    dialog = null;

    recorder.close();
  }

  protected String format(Object value) {
    Message message = (Message)value;
    if (message == null) {
      return "";
    } else {
      StringBuffer buffer = new StringBuffer();

      if (message.getStatus() == -1) {
        buffer.append("*");
      } else {
        buffer.append(message.getStatus());
      }
      buffer.append(", ");
      if (message.getData1() == -1) {
        buffer.append("*");
      } else {
        buffer.append(message.getData1());
      }
      buffer.append(", ");
      if (message.getData2() == -1) {
        buffer.append("*");
      } else {
        buffer.append(message.getData2());
      }

      return buffer.toString();
    }
  }

  /**
   * Recorder of a message.
   */
  private class MessageRecorder extends ShortMessageRecorder implements Runnable {

    private int status;
    private int data1;
    private int data2;

    public MessageRecorder(String deviceName) throws MidiUnavailableException {
      super(deviceName);
    }

    public void messageRecorded(ShortMessage message) {
      status = message.getCommand() | message.getChannel();
      data1  = message.getData1();
      data2  = message.getData2();

      SwingUtilities.invokeLater(this);
    }

    public void run() {
      Message message = new Message(status, data1, data2);

      textField.setText(format(message));

      dialog.setVisible(false);
    }
  }
}
