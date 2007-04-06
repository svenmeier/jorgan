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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import jorgan.App;
import jorgan.sound.midi.DevicePool;
import jorgan.sound.midi.KeyFormat;
import jorgan.sound.midi.MessageUtils;
import jorgan.sound.midi.MidiLogger;
import jorgan.swing.StandardDialog;
import jorgan.swing.table.TableUtils;
import jorgan.util.I18N;
import spin.Spin;
import swingx.docking.DockedPanel;

/**
 * A monitor of MIDI messages.
 */
public class MidiMonitor extends DockedPanel {

	private static final I18N i18n = I18N.get(MidiMonitor.class);

	private static final KeyFormat keyFormat = new KeyFormat();

	private static final Color[] channelColors = new Color[] {
			new Color(255, 240, 240), new Color(240, 255, 240),
			new Color(240, 255, 255), new Color(240, 240, 255),
			new Color(255, 240, 255), new Color(255, 255, 240),
			new Color(240, 240, 240) };

	private static final String[] channelEvents = new String[] { "NOTE_OFF", // 0x80
			"NOTE_ON", // 0x90
			"POLY_PRESSURE", // 0xa0
			"CONTROL_CHANGE", // 0xb0
			"PROGRAM_CHANGE", // 0xc0
			"CHANNEL_PRESSURE", // 0xd0
			"PITCH_BEND" // 0xe0
	};

	private static final String[] systemEvents = new String[] { "?", // 0xf0
			"MIDI_TIME_CODE", // 0xf1
			"SONG_POSITION_POINTER", // 0xf2
			"SONG_SELECT", // 0xf3
			"?", // 0xf4
			"?", // 0xf5
			"TUNE_REQUEST", // 0xf6
			"END_OF_EXCLUSIVE", // 0xf7
			"TIMING_CLOCK", // 0xf8
			"?", // 0xf9
			"START", // 0xfa
			"CONTINUE", // 0xfb
			"STOP", // 0xfc
			"?", // 0xfd
			"ACTIVE_SENSING", // 0xfe
			"SYSTEM_RESET" // 0xff
	};

	private MidiLogger logger = new InternalMidiLogger();

	private String deviceName;

	private boolean deviceOut;

	private boolean open;

	private int max;

	private List<Message> messages = new ArrayList<Message>();

	private JTable table = new JTable();

	private ButtonGroup baseGroup = new ButtonGroup();

	private JToggleButton hexButton = new JToggleButton();

	private JToggleButton decButton = new JToggleButton();

	private JToggleButton scrollLockButton = new JToggleButton();

	private MessagesModel model = new MessagesModel();

	/**
	 * Constructor.
	 */
	public MidiMonitor() {
		App.getBias().register(this);

		addTool(new DeviceAction());

		addToolSeparator();

		hexButton.setToolTipText(i18n.getString("hexButton.toolTipText"));
		hexButton.setIcon(new ImageIcon(getClass().getResource(
				"/jorgan/gui/img/hexadecimal.gif")));
		hexButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				model.fireTableDataChanged();
			}
		});
		hexButton.setSelected(true);
		baseGroup.add(hexButton);
		addTool(hexButton);

		decButton.setToolTipText(i18n.getString("decButton.toolTipText"));
		decButton.setIcon(new ImageIcon(getClass().getResource(
				"/jorgan/gui/img/decimal.gif")));
		decButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				model.fireTableDataChanged();
			}
		});
		baseGroup.add(decButton);
		addTool(decButton);

		addToolSeparator();

		scrollLockButton.setToolTipText(i18n
				.getString("scrollLockButton.toolTipText"));
		scrollLockButton.setIcon(new ImageIcon(getClass().getResource(
				"/jorgan/gui/img/scrollLock.gif")));
		addTool(scrollLockButton);

		addToolSeparator();

		addTool(new ClearAction());

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
		StandardDialog selectionDialog = new StandardDialog(
				(JFrame) SwingUtilities.getWindowAncestor(this));
		selectionDialog.addCancelAction();
		selectionDialog.addOKAction();
		selectionDialog.setTitle(i18n.getString("selectionDialog.title"));
		selectionDialog.setDescription(i18n
				.getString("selectionDialog.description"));
		if (selectionPanel == null) {
			selectionPanel = new DeviceSelectionPanel();
		}
		selectionDialog.setBody(selectionPanel);
		selectionPanel.setDevice(deviceName, deviceOut);
		selectionDialog.autoPosition();
		selectionDialog.setVisible(true);

		if (!selectionDialog.wasCancelled()) {
			setDevice(selectionPanel.getDeviceName(), selectionPanel
					.getDeviceOut());
		}
	}

	/**
	 * Set the device to log.
	 * 
	 * @param name
	 *            name of device to log
	 * @param out
	 *            should <code>out</code> or <code>in</code> be logged
	 */
	public void setDevice(String name, boolean out) {
		if (this.deviceName == null && name != null || this.deviceName != null
				&& !this.deviceName.equals(name) || this.deviceOut != out) {

			if (this.deviceName != null) {
				try {
					DevicePool.removeLogger((MidiLogger) Spin.over(logger),
							this.deviceName, this.deviceOut);
				} catch (MidiUnavailableException ex) {
					throw new Error();
				}
			}

			this.deviceName = name;
			this.deviceOut = out;

			if (this.deviceName != null) {
				try {
					open = DevicePool.addLogger((MidiLogger) Spin.over(logger),
							name, out);
				} catch (MidiUnavailableException ex) {
					this.deviceName = null;
				}
			}

			clear();
		}
		updateMessagesLabel();
	}

	protected void updateMessagesLabel() {
		String message;
		if (deviceName == null) {
			message = i18n.getString("noDeviceMessage");
		} else {
			message = MessageFormat.format(i18n.getString("deviceMessage"),
					new Object[] { deviceName,
							deviceOut ? new Integer(1) : new Integer(0),
							open ? new Integer(1) : new Integer(0) });
		}
		setMessage(message);
	}

	private void prepareColumn(int index, int width, int align) {
		TableColumn column = table.getColumnModel().getColumn(index);

		column.setCellRenderer(new MessageCellRenderer(align));
		column.setPreferredWidth(width);
	}

	/**
	 * Clear this log.
	 */
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
			if (MessageUtils.isShortMessage(message)) {
				messages.add(new Message(message));

				int row = messages.size() - 1;

				model.fireTableRowsInserted(row, row);

				if (!scrollLockButton.isSelected()) {
					table.scrollRectToVisible(table.getCellRect(row, 0, true));
				}

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

	private class MessagesModel extends AbstractTableModel {

		private String[] names = new String[] { "Status", "Data 1", "Data 2",
				"Channel", "Note", "Event" };

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

			Message message = messages.get(rowIndex);

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

		private int status = -1;

		private int data1 = -1;

		private int data2 = -1;

		/**
		 * Create a message.
		 * 
		 * @param message
		 *            the original message
		 */
		public Message(MidiMessage message) {
			if (message instanceof ShortMessage) {
				ShortMessage shortMessage = (ShortMessage) message;
				this.status = shortMessage.getStatus();
				this.data1 = shortMessage.getData1();
				this.data2 = shortMessage.getData2();
			}
		}

		/**
		 * Get the status.
		 * 
		 * @return status
		 */
		public String getStatus() {
			return format(status);
		}

		/**
		 * Get the data1.
		 * 
		 * @return data1
		 */
		public String getData1() {
			return format(data1);
		}

		/**
		 * Get the data2.
		 * 
		 * @return data2
		 */
		public String getData2() {
			return format(data2);
		}

		/**
		 * Get the channel (if applicable).
		 * 
		 * @return channel
		 */
		public String getChannel() {
			if (status >= 0x80 && status < 0xf0) {
				return Integer.toString((status & 0x0f) + 1);
			} else {
				return "-";
			}
		}

		/**
		 * Get the note (if applicable).
		 * 
		 * @return note
		 */
		public String getNote() {
			if (status >= 0x80 && status < 0xb0) {
				return keyFormat.format(new Integer(data1 & 0xff));
			} else {
				return "-";
			}
		}

		/**
		 * Get the event (if applicable).
		 * 
		 * @return event
		 */
		public String getEvent() {
			if (status >= 0x80 && status < 0xf0) {
				return channelEvents[(status - 0x80) >> 4];
			} else if (status >= 0xf0) {
				return systemEvents[status - 0xf0];
			} else {
				return "?";
			}
		}

		/**
		 * Get the color.
		 * 
		 * @return color
		 */
		public Color getColor() {
			if (status >= 0x80 && status < 0xf0) {
				return channelColors[(status - 0x80) >> 4];
			} else {
				return null;
			}
		}

		private String format(int value) {
			if (value == -1) {
				return "-";
			} else {
				return Integer
						.toString(value, hexButton.isSelected() ? 16 : 10);
			}
		}
	}

	private class MessageCellRenderer extends DefaultTableCellRenderer {

		/**
		 * Create a renderer with the given alingment.
		 * 
		 * @param alignment
		 *            alignment
		 */
		public MessageCellRenderer(int alignment) {
			setHorizontalAlignment(alignment);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			JLabel label = (JLabel) super.getTableCellRendererComponent(table,
					value, isSelected, hasFocus, row, column);

			if (!isSelected) {
				Message message = messages.get(row);
				Color color = message.getColor();
				if (color != null) {
					label.setBackground(color);
				}
			}

			return label;
		}
	}

	private class DeviceAction extends AbstractAction {
		private DeviceAction() {
			putValue(Action.SHORT_DESCRIPTION, i18n
					.getString("deviceAction.shortDescription"));

			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
					"/jorgan/gui/img/filter.gif")));
		}

		public void actionPerformed(ActionEvent e) {
			selectDevice();
		}
	}

	private class ClearAction extends AbstractAction {
		private ClearAction() {
			putValue(Action.SHORT_DESCRIPTION, i18n
					.getString("clearAction.shortDescription"));

			putValue(Action.SMALL_ICON, new ImageIcon(getClass().getResource(
					"/jorgan/gui/img/clear.gif")));
		}

		public void actionPerformed(ActionEvent e) {
			clear();
		}
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}
}