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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import jorgan.config.ConfigurationEvent;
import jorgan.config.ConfigurationListener;
import jorgan.midi.log.MidiLogProvider;

import jorgan.sound.midi.KeyFormat;


/**
 * A log of MIDI messages. 
 */
public class MidiLog extends JPanel {

  protected static final ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");
  
  private static final KeyFormat keyFormat = new KeyFormat();

  private static final Color[] channelColors = new Color[] {
                                                new Color(255, 240, 240),
                                                new Color(240, 255, 240),
                                                new Color(240, 255, 255),
                                                new Color(240, 240, 255),
                                                new Color(240, 240, 255),
                                                new Color(240, 240, 255),
                                                new Color(240, 240, 255)
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

  /**
   * Maximum message count. 
   */
  private int max = 256;

  /**
   * Should data be displayed in hex. 
   */
  private boolean hex = true;
  
  private Transmitter transmitter;
  
  private Receiver receiver = new LogReceiver();

  private List messages = new ArrayList();

  private JScrollPane scrollPane = new JScrollPane();
  
  private JTable table = new JTable();

  private JPopupMenu popupMenu;
  private JCheckBoxMenuItem hexMenuItem;
  private JMenuItem clearMenuItem;
  
  private MessagesModel model = new MessagesModel();

  public MidiLog() {
    setLayout(new BorderLayout());

    scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);    
    scrollPane.getViewport().setBackground(table.getBackground());
    add(scrollPane, BorderLayout.CENTER);
    
    table.setModel(model);
    scrollPane.setViewportView(table);
    
    prepareColumn(0, 10, SwingConstants.RIGHT);
    prepareColumn(1, 10, SwingConstants.RIGHT);
    prepareColumn(2, 10, SwingConstants.RIGHT);
    prepareColumn(3, 10, SwingConstants.RIGHT);
    prepareColumn(4, 10, SwingConstants.RIGHT);
    prepareColumn(5, 100, SwingConstants.LEFT);

    MouseHandler handler = new MouseHandler(); 
    table.addMouseListener(handler);
    
    max = Configuration.instance().getMidiLogMax();
    hex = Configuration.instance().getMidiLogHex();
    
    setTransmitter(MidiLogProvider.getLoopback().loopbackTransmitter);
  }
  
  public void addNotify() {
      super.addNotify();

      Configuration configuration = Configuration.instance();
      configuration.addConfigurationListener(model);
    }
    
    
    public void removeNotify() {
      Configuration.instance().removeConfigurationListener(model);
      
      super.removeNotify();
    }
  
  private void prepareColumn(int index, int width, int align) {
    TableColumn column = table.getColumnModel().getColumn(index); 

    column.setCellRenderer(new MessageCellRenderer(align));
    column.setPreferredWidth(width);
  }
  
  public void setTransmitter(Transmitter transmitter) {
    this.transmitter = transmitter;
    
    transmitter.setReceiver(receiver);
  }
  
  public Transmitter getTransmitter() {
    return transmitter;
  }

  public boolean getHex() {
    return hex;
  }
  
  public void setHex(boolean hex) {
    if (this.hex != hex) {
      this.hex = hex;
      
      model.fireTableDataChanged();
    }
  }
  
  public void clear() {
    messages.clear();

    model.fireTableDataChanged();
  }
  
  protected void showPopup(int x, int y) {
      if (popupMenu == null) {
          popupMenu = createPopup();
      }

      hexMenuItem.setSelected(hex);      
        
      popupMenu.show(table, x, y);    
  }

  protected JPopupMenu createPopup() {
      JPopupMenu popupMenu = new JPopupMenu();

      hexMenuItem = new JCheckBoxMenuItem(resources.getString("log.hex"));
      hexMenuItem.addItemListener(new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
              setHex(hexMenuItem.isSelected());
          }
      });
      popupMenu.add(hexMenuItem);

      popupMenu.addSeparator();

      clearMenuItem = new JMenuItem(resources.getString("log.clear"));
      clearMenuItem.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
              clear();
          }
      });
      popupMenu.add(clearMenuItem);
        
      return popupMenu;
  }

  /**
   * The receiver registered to set transmitters.
   * 
   * @see #setTransmitter(javax.sound.midi.Transmitter)
   */
  private class LogReceiver implements Receiver, Runnable {

    private List tempMessages = new ArrayList();
    
    public void close() {
    }

    public synchronized void send(MidiMessage message, long timeStamp) {
      if (tempMessages.size() == 0) {
        SwingUtilities.invokeLater(this);
      }

      tempMessages.add(new Message(message.getMessage(), message.getLength(), timeStamp));     
    }
    
    public synchronized void run() {

      if (tempMessages.size() > 0) {
        int firstRow = messages.size();
      
        messages.addAll(tempMessages);

        tempMessages.clear();
      
        int lastRow = messages.size() - 1;
      
        model.fireTableRowsInserted(firstRow, lastRow);
        
        table.scrollRectToVisible(table.getCellRect(lastRow, 0, true));
        
        int over = messages.size() - max;
        if (over > 0) { 
            for (int m = 0; m < over; m++) {
                messages.remove(0);
            }
            
            model.fireTableRowsDeleted(0, over - 1);
        }
      }
    }
  }
  
  private class MessagesModel extends AbstractTableModel implements ConfigurationListener {

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
    
    public void configurationBackup(ConfigurationEvent event) { }
    
    public void configurationChanged(ConfigurationEvent event) {
      setHex(Configuration.instance().getMidiLogHex());
    }
  }
  
  private class Message {

    private byte[] data;
    private int    length;
    
    public Message(byte[] data, int length, long timestamp) {
        
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
      return Integer.toString(value, hex ? 16 : 10); 
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
  
  private class MouseHandler extends MouseAdapter {
        
      public void mousePressed(MouseEvent e) {
          showPopup(e);
      }

      public void mouseReleased(MouseEvent e) {
          showPopup(e);        
      }

      private void showPopup(MouseEvent e) {
          if (e.isPopupTrigger()) {
              MidiLog.this.showPopup(e.getX(), e.getY());
          }
      }
  }
}