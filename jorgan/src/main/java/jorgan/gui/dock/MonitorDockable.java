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
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;

import jorgan.play.event.PlayListener;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.table.IconTableCellRenderer;
import jorgan.swing.table.TableUtils;
import swingx.docking.Docked;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * A monitor of MIDI messages.
 */
public class MonitorDockable extends OrganDockable {

	private static final Configuration config = Configuration.getRoot().get(
			MonitorDockable.class);

	private static final Color[] colors = new Color[] {
			new Color(255, 240, 240), // 0x80
			new Color(240, 255, 240), // 0x90
			new Color(240, 255, 255), // 0xa0
			new Color(240, 240, 255), // 0xb0
			new Color(255, 240, 255), // 0xc0
			new Color(255, 255, 240), // 0xd0
			new Color(240, 240, 240) // 0xe0
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
		ToolTipManager.sharedInstance().registerComponent(table);
		table.setModel(tableModel);
		TableUtils.pleasantLookAndFeel(table);
		setContent(new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));

		new MessageCellRenderer(true).configureTableColumn(table, 0);
		for (int c = 1; c < table.getColumnCount(); c++) {
			TableColumn column = table.getColumnModel().getColumn(c);

			column.setCellRenderer(new MessageCellRenderer(false));
		}
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

	private class InternalListener implements PlayListener {

		public void received(int channel, int command, int data1, int data2) {

			if (inputButton.isSelected()) {
				add(new Message(true, channel, command, data1, data2));
			}
		}

		public void sent(int channel, int command, int data1, int data2) {

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
			return 5;
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
			}
			return null;
		}
	}

	private class Message {

		private boolean input;

		private int channel;

		private int command;

		private int data1;

		private int data2;

		public Message(boolean input, int channel, int command, int data1,
				int data2) {
			this.input = input;

			this.channel = channel;
			this.command = command;
			this.data1 = data1;
			this.data2 = data2;
		}

		public boolean isInput() {
			return input;
		}

		public int getChannel() {
			return channel;
		}

		public int getCommand() {
			return command;
		}

		public int getData1() {
			return data1;
		}

		public int getData2() {
			return data2;
		}

		public String getDescription() {
			MessageBuilder builder = new MessageBuilder();

			config.get(command + ">" + data1 + ">" + data2).read(builder);
			if (!builder.hasPattern()) {
				config.get(command + ">" + data1).read(builder);
				if (!builder.hasPattern()) {
					config.get("" + command).read(builder);
				}
			}
			return builder.build(command, data1, data2);
		}

		public Color getColor() {
			if (command >= 0x80 && command < 0xf0) {
				return colors[(command - 0x80) >> 4];
			} else {
				return Color.white;
			}
		}
	}

	private class MessageCellRenderer extends IconTableCellRenderer {

		private boolean showIcon;
		private transient Message message;

		public MessageCellRenderer(boolean showIcon) {
			setHorizontalAlignment(SwingConstants.RIGHT);

			this.showIcon = showIcon;
		}

		@Override
		public MessageCellRenderer getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			super.getTableCellRendererComponent(table, value, isSelected,
					hasFocus, row, column);

			if (row < messages.size()) {
				message = messages.get(row);

				if (!isSelected) {
					setBackground(message.getColor());
				}
			} else {
				message = null;
			}

			return this;
		}

		@Override
		protected Icon getIcon(Object input) {
			if (Boolean.TRUE.equals(input)) {
				return inputButton.getIcon();
			} else {
				return outputButton.getIcon();
			}
		}

		protected void setValue(Object value) {
			if (showIcon) {
				super.setValue(value);
			} else {
				setText((value == null) ? "" : value.toString());
			}
		}

		@Override
		public String getToolTipText() {
			if (message != null) {
				return message.getDescription();
			}
			return null;
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