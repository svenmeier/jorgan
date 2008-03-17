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
package jorgan.gui.dock;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import jorgan.midi.KeyFormat;
import jorgan.play.event.PlayAdapter;
import jorgan.play.event.PlayListener;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.table.IconTableCellRenderer;
import jorgan.swing.table.TableUtils;
import swingx.docking.Docked;
import bias.Configuration;

/**
 * A monitor of MIDI messages.
 */
public class MonitorDockable extends OrganDockable {

	private static final Configuration config = Configuration.getRoot().get(
			MonitorDockable.class);

	private static final KeyFormat keyFormat = new KeyFormat();

	private static final Color[] colors = new Color[] {
			new Color(255, 240, 240), new Color(240, 255, 240),
			new Color(240, 255, 255), new Color(240, 240, 255),
			new Color(255, 240, 255), new Color(255, 255, 240),
			new Color(240, 240, 240) };

	private static final String[] events = new String[] { "NOTE_OFF", // 0x80
			"Note on", // 0x90
			"Poly pressure", // 0xa0
			"Control Change", // 0xb0
			"Program change", // 0xc0
			"Channel pressure", // 0xd0
			"Pitch bend", // 0xe0
			"System" // 0xf0
	};

	private PlayListener listener = new InternalListener();

	private int max;

	private List<Message> messages = new ArrayList<Message>();

	private JTable table = new JTable();

	private JToggleButton inputButton = new JToggleButton();

	private JToggleButton outputButton = new JToggleButton();

	private JToggleButton scrollLockButton = new JToggleButton();

	private MessagesModel tableModel = new MessagesModel();

	private OrganSession session;

	/**
	 * Constructor.
	 */
	public MonitorDockable() {
		config.read(this);

		config.get("input").read(inputButton);

		config.get("output").read(outputButton);

		config.get("scrollLock").read(scrollLockButton);

		config.get("table").read(tableModel);
		table.setModel(tableModel);
		TableUtils.pleasantLookAndFeel(table);
		setContent(new JScrollPane(table));

		IconTableCellRenderer iconRenderer = new IconTableCellRenderer() {
			@Override
			protected Icon getIcon(Object in) {
				if (Boolean.TRUE.equals(in)) {
					return inputButton.getIcon();
				} else {
					return outputButton.getIcon();
				}
			}

			@Override
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column) {
				JComponent component = (JComponent) super
						.getTableCellRendererComponent(table, value,
								isSelected, hasFocus, row, column);

				if (!isSelected && row < messages.size()) {
					component.setBackground(messages.get(row).getColor());
				}

				return component;
			}
		};
		iconRenderer.configureTableColumn(table, 0);

		prepareColumn(1, 10, SwingConstants.RIGHT);
		prepareColumn(2, 10, SwingConstants.RIGHT);
		prepareColumn(3, 10, SwingConstants.RIGHT);
		prepareColumn(4, 10, SwingConstants.RIGHT);
		prepareColumn(5, 10, SwingConstants.RIGHT);
		prepareColumn(6, 100, SwingConstants.LEFT);
	}

	@Override
	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.removePlayerListener(listener);
		}

		this.session = session;

		if (this.session != null) {
			this.session.addPlayerListener(listener);
		}
	}

	@Override
	public void docked(Docked docked) {
		super.docked(docked);

		docked.addTool(inputButton);
		docked.addTool(outputButton);
		docked.addToolSeparator();
		docked.addTool(scrollLockButton);
		docked.addToolSeparator();
		docked.addTool(new ClearAction());
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

		tableModel.fireTableDataChanged();
	}

	public int getMax() {
		return max;
	}

	public void setMax(int max) {
		this.max = max;
	}

	private class InternalListener extends PlayAdapter {

		@Override
		public void inputAccepted(int channel, int command, int data1, int data2) {

			if (inputButton.isSelected()) {
				add(new Message(true, channel, command, data1, data2));
			}
		}

		@Override
		public void outputProduced(int channel, int command, int data1,
				int data2) {

			if (outputButton.isSelected()) {
				add(new Message(false, channel, command, data1, data2));
			}
		}

		private void add(Message message) {
			messages.add(message);
			int row = messages.size() - 1;

			tableModel.fireTableRowsInserted(row, row);

			if (!scrollLockButton.isSelected()) {
				table.scrollRectToVisible(table.getCellRect(row, 0, true));
			}

			int over = messages.size() - max;
			if (over > 0) {
				for (int m = 0; m < over; m++) {
					messages.remove(0);
				}

				tableModel.fireTableRowsDeleted(0, over - 1);
			}
		}
	}

	public class MessagesModel extends AbstractTableModel {

		private String[] columnNames = new String[getColumnCount()];

		public int getColumnCount() {
			return 7;
		}

		public void setColumnNames(String[] columnNames) {
			if (columnNames.length != this.columnNames.length) {
				throw new IllegalArgumentException("length "
						+ columnNames.length);
			}
			this.columnNames = columnNames;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		public int getRowCount() {
			return messages.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {

			Message message = messages.get(rowIndex);

			switch (columnIndex) {
			case 0:
				return message.isInput();
			case 1:
				return message.getChannel();
			case 2:
				return message.getCommand();
			case 3:
				return message.getData1();
			case 4:
				return message.getData2();
			case 5:
				return message.getNote();
			case 6:
				return message.getEvent();
			}
			return null;
		}
	}

	private class Message {

		private boolean input;

		private String channel;

		private String command;

		private String data1;

		private String data2;

		private String note;

		private String event;

		private Color color;

		public Message(boolean input, int channel, int command, int data1,
				int data2) {
			this.input = input;

			this.channel = format(channel);
			this.command = format(command);
			this.data1 = format(data1);
			this.data2 = format(data2);

			if (command == 144 || command == 128) {
				this.note = keyFormat.format(new Integer(data1 & 0xff));
			} else {
				this.note = "-";
			}

			if (command >= 0x80 && command < 0xf0) {
				this.event = events[(command - 0x80) >> 4];
			} else {
				this.event = "?";
			}

			if (command >= 0x80 && command < 0xf0) {
				this.color = colors[(command - 0x80) >> 4];
			} else {
				this.color = Color.white;
			}
		}

		public boolean isInput() {
			return input;
		}

		public String getChannel() {
			return channel;
		}

		public String getCommand() {
			return command;
		}

		public String getData1() {
			return data1;
		}

		public String getData2() {
			return data2;
		}

		/**
		 * Get the note (if applicable).
		 * 
		 * @return note
		 */
		public String getNote() {
			return note;
		}

		/**
		 * Get the event (if applicable).
		 * 
		 * @return event
		 */
		public String getEvent() {
			return event;
		}

		/**
		 * Get the color.
		 * 
		 * @return color
		 */
		public Color getColor() {
			return color;
		}

		private String format(int value) {
			if (value == -1) {
				return "-";
			} else {
				return Integer.toString(value);
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

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			JComponent component = (JComponent) super
					.getTableCellRendererComponent(table, value, isSelected,
							hasFocus, row, column);

			if (!isSelected && row < messages.size()) {
				component.setBackground(messages.get(row).getColor());
			}

			return component;
		}
	}

	private class ClearAction extends BaseAction {
		private ClearAction() {
			config.get("clear").read(this);
		}

		public void actionPerformed(ActionEvent e) {
			clear();
		}
	}
}