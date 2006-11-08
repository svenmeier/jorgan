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
package jorgan.gui.midi;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import swingx.docking.DockedPanel;

import jorgan.midi.log.Configuration;

import jorgan.sound.midi.DevicePool;
import jorgan.sound.midi.KeyFormat;
import jorgan.sound.midi.MidiLogger;
import jorgan.swing.StandardDialog;
import jorgan.swing.table.TableUtils;


/**
 * A log of MIDI messages. 
 */
public class MidiLog extends DockedPanel {

  protected static final ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");
  
  private static final KeyFormat keyFormat = new KeyFormat();

  private static final Color[] channelColors = new Color[] {
                                                new Color(255, 240, 240),
                                                new Color(240, 255, 240),
                                                new Color(240, 255, 255),
                                                new Color(240, 240, 255),
                                                new Color(255, 240, 255),
                                                new Color(255, 255, 240),
                                                new Color(240, 240, 240)
                                               };

  private static final String[] channelEvents = new String[]{
                                                "NOTE_OFF",              // 0x80
                                                "NOTE_ON",               // 0x90
                                                "POLY_PRESSURE",         // 0xa0
                                                "CONTROL_CHANGE",        // 0xb0
                                                "PROGRAM_CHANGE",        // 0xc0
                                                "CHANNEL_PRESSURE",      // 0xd0
                                                "PITCH_BEND"             // 0xe0
                                                };

  private static final String[] systemEvents = new String[]{
                                                "?",                     // 0xf0
                                                "MIDI_TIME_CODE",        // 0xf1
                                                "SONG_POSITION_POINTER", // 0xf2
                                                "SONG_SELECT",           // 0xf3
                                                "?",                     // 0xf4
                                                "?",                     // 0xf5
                                                "TUNE_REQUEST",          // 0xf6
                                                "END_OF_EXCLUSIVE",      // 0xf7
                                                "TIMING_CLOCK",          // 0xf8
                                                "?",                     // 0xf9
                                                "START",                 // 0xfa
                                                "CONTINUE",              // 0xfb
                                                "STOP",                  // 0xfc
                                                "?",                     // 0xfd
                                                "ACTIVE_SENSING",        // 0xfe
                                                "SYSTEM_RESET"           // 0xff
                                               };

  private MidiLogger logger = new InternalMidiLogger();
  
  private String deviceName;
  private boolean deviceOut;
  private boolean open;

  private List messages = new ArrayList();

  private JTable table = new JTable();
  
  private ButtonGroup baseGroup = new ButtonGroup();
  private JToggleButton hexButton = new JToggleButton();
  private JToggleButton decButton = new JToggleButton();
  
  private JButton deviceButton = new JButton();

  private JToggleButton scrollLockButton = new JToggleButton();
  private JButton clearButton = new JButton();
  
  private MessagesModel model = new MessagesModel();

  public MidiLog() {

    deviceButton.setToolTipText(resources.getString("log.device"));
    deviceButton.setIcon(new ImageIcon(getClass().getResource("/jorgan/gui/img/filter.gif")));
    deviceButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            selectDevice();
        }
    });
    addTool(deviceButton);
    
    addToolSeparator();

    hexButton.setToolTipText(resources.getString("log.hexadecimal"));
    hexButton.setIcon(new ImageIcon(getClass().getResource("/jorgan/gui/img/hexadecimal.gif")));
    hexButton.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
            model.fireTableDataChanged();
        }
    });
    hexButton.setSelected(true);    
    baseGroup.add(hexButton);
    addTool(hexButton);
    
    decButton.setToolTipText(resources.getString("log.decimal"));
    decButton.setIcon(new ImageIcon(getClass().getResource("/jorgan/gui/img/decimal.gif")));
    decButton.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent e) {
            model.fireTableDataChanged();
        }
    });  
    baseGroup.add(decButton);
    addTool(decButton);
    
    addToolSeparator();

    scrollLockButton.setToolTipText(resources.getString("log.scrollLock"));
    scrollLockButton.setIcon(new ImageIcon(getClass().getResource("/jorgan/gui/img/scrollLock.gif")));
    addTool(scrollLockButton);
    
    addToolSeparator();

    clearButton.setToolTipText(resources.getString("log.clear"));
    clearButton.setIcon(new ImageIcon(getClass().getResource("/jorgan/gui/img/clear.gif")));
    clearButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            clear();
        }
    });    
    addTool(clearButton);
    
    table.setModel(model);
    TableUtils.pleasantLookAndFeel(table);
    setScrollableBody(table, true, false);
    
    prepareColumn(0, 10, SwingConstants.RIGHT);
    prepareColumn(1, 10, SwingConstants.RIGHT);
    prepareColumn(2, 10, SwingConstants.RIGHT);
    prepareColumn(3, 10, SwingConstants.RIGHT);
    prepareColumn(4, 10, SwingConstants.RIGHT);
    prepareColumn(5, 100, SwingConstants.LEFT);
    
    setDevice(null, false);
  }
  
  private DeviceSelectionPanel selectionPanel;
  
  protected void selectDevice() {
      StandardDialog dialog = new StandardDialog((JFrame)SwingUtilities.getWindowAncestor(this));
      dialog.addCancelAction();
      dialog.addOKAction(true);
      dialog.setTitle(resources.getString("log.select.title"));
      dialog.setDescription(resources.getString("log.select.description"));
      if (selectionPanel == null) {
          selectionPanel = new DeviceSelectionPanel();
      }
      dialog.setContent(selectionPanel);
      selectionPanel.setDevice(deviceName, deviceOut);
      dialog.start();

      if (!dialog.wasCancelled()) {
          setDevice(selectionPanel.getDeviceName(), selectionPanel.getDeviceOut());
      }
  }

  public void setDevice(String deviceName, boolean out) {
      if (this.deviceName == null && deviceName != null ||
          this.deviceName != null && !this.deviceName.equals(deviceName) ||
          this.deviceOut != out) {

          if (this.deviceName != null) {
              try {
                  DevicePool.removeLogger(logger, this.deviceName, this.deviceOut);
              } catch (MidiUnavailableException ex) {
                  throw new Error();
              }
          }
          
          this.deviceName = deviceName;
          this.deviceOut  = out;          
          
          if (this.deviceName != null) {
              try {
                  open = DevicePool.addLogger(logger, deviceName, out);
              } catch (MidiUnavailableException ex) {
                  this.deviceName = null;
              }
          }

          clear();
      }
      updateMessagesLabel();
  }
  
  protected void updateMessagesLabel() {
      String text;
      if (deviceName == null) {
          text = resources.getString("log.header.noDevice");
      } else {
          text = deviceName + ", " +
                 (deviceOut ? "out" : "in") + ", " +
                 (open ? resources.getString("log.header.open") : resources.getString("log.header.closed")); 
      }
      setMessage(text);
  }
  
  private void prepareColumn(int index, int width, int align) {
    TableColumn column = table.getColumnModel().getColumn(index); 

    column.setCellRenderer(new MessageCellRenderer(align));
    column.setPreferredWidth(width);
  }
  
  public void clear() {
    messages.clear();

    model.fireTableDataChanged();
  }
  
  private class InternalMidiLogger implements MidiLogger {

    public void opened() {
        open = true;
        
        updateMessagesLabel();
    }

    public void closed() {
        open = false;
        
        updateMessagesLabel();
    }

    public void log(MidiMessage message) {
      messages.add(new Message(message.getMessage(), message.getLength()));     

      int row = messages.size() - 1;
      
      model.fireTableRowsInserted(row, row);
        
      if (!scrollLockButton.isSelected()) {
        table.scrollRectToVisible(table.getCellRect(row, 0, true));
      }
        
      int over = messages.size() - Configuration.instance().getMax();
      if (over > 0) { 
          for (int m = 0; m < over; m++) {
              messages.remove(0);
          }
            
          model.fireTableRowsDeleted(0, over - 1);
      }
    }
  }
  
  private class MessagesModel extends AbstractTableModel {

    private String[] names = new String[]{"Status", "Data 1", "Data 2", "Channel", "Note", "Event"};
    
    public int getColumnCount() {
      return names.length;
    }

    public String getColumnName(int column) {
      return names[column];
    }

    public int getRowCount() {
      return messages.size();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {

      Message message = (Message)messages.get(rowIndex);

      switch (columnIndex) {
        case 0:
          return message.getStatus();
        case 1:
          return message.getData1();
        case 2:
          return message.getData2();
        case 3:
          return message.getChannel();
        case 4:
          return message.getNote();
        case 5:
          return message.getEvent();
      }
      return null;
    }
  }
  
  private class Message {

    private byte[] data;
    private int    length;
    
    public Message(byte[] data, int length) {
        
      this.data      = data;
      this.length    = length;
    }
    
    public String getStatus() {
      return format(data[0] & 0xff);    
    }

    public String getData1() {
      if (length > 1) {
        return format(data[1] & 0xff);    
      } else {
        return "-";
      }
    }
    
    public String getData2() {
      if (length > 2) {
        return format(data[2] & 0xff);
      } else {
        return "-";    
      }
    }

    public String getChannel() {
      int status  = (data[0] & 0xff);
      if (status >= 0x80 && status < 0xf0) {
        return Integer.toString((data[0] & 0x0f) + 1);    
      } else {
        return "-";    
      }
    }

    public String getNote() {
      int status  = (data[0] & 0xff);
      if (status >= 0x80 && status < 0xb0) {
        return keyFormat.format(new Integer(data[1] & 0xff));    
      } else {
        return "-";    
      }
    }

    public String getEvent() {
      int status  = (data[0] & 0xff);
      if (status >= 0x80 && status < 0xf0) {
        return channelEvents[(status - 0x80) >> 4 ];    
      } else if (status >= 0xf0) {
        return systemEvents[status - 0xf0];    
      } else {
        return "-";    
      }
    }

    public Color getColor() {
      int status  = (data[0] & 0xff);

      if (status >= 0x80 && status < 0xf0) {
        return channelColors[(status - 0x80) >> 4 ];    
      } else {
        return Color.WHITE;    
      }
    }
    
    private String format(int value) {
      return Integer.toString(value, hexButton.isSelected() ? 16 : 10); 
    }    
  }
  
  private class MessageCellRenderer extends DefaultTableCellRenderer {

    public MessageCellRenderer(int alignment) {
        setHorizontalAlignment(alignment);
    }

    public Component getTableCellRendererComponent(
        JTable table, Object value, boolean isSelected, boolean hasFocus,
        int row, int column) {

        JLabel label = (JLabel)super.getTableCellRendererComponent(
            table, value, isSelected, hasFocus, row, column);

        if (!isSelected) {
            Message message = (Message)messages.get(row);
            label.setBackground(message.getColor());
        }
                    
        return label;
    }
  }
}