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

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.table.TableColumn;

import jorgan.midi.MessageUtils;
import jorgan.play.OrganPlay;
import jorgan.play.event.PlayListener;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.table.BaseTableModel;
import jorgan.swing.table.IconTableCellRenderer;
import jorgan.swing.table.TableUtils;
import spin.Spin;
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
			this.session.lookup(OrganPlay.class).removePlayerListener(
					(PlayListener) Spin.over(listener));
		}

		this.session = session;

		if (this.session != null) {
			this.session.lookup(OrganPlay.class).addPlayerListener(
					(PlayListener) Spin.over(listener));
		}
	}

	@Override
	protected void addTools(Docked docked) {

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

		@Override
		public void received(MidiMessage message) {
			if (inputButton.isSelected()) {
				add(true, message);
			}
		}

		@Override
		public void sent(MidiMessage message) {
			if (outputButton.isSelected()) {
				add(false, message);
			}
		}

		private void add(boolean input, MidiMessage message) {
			messages.add(new Message(input, message));
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

	public class MessagesModel extends BaseTableModel<Message> {

		public int getColumnCount() {
			return 5;
		}

		public int getRowCount() {
			return messages.size();
		}

		@Override
		protected Message getRow(int rowIndex) {
			return messages.get(rowIndex);
		}

		@Override
		protected Object getValue(Message message, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return message.isInput();
			case 1:
				return message.getChannel();
			case 2:
				return message.getStatus();
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

		private int channel = -1;

		private int status;

		private int data1 = -1;

		private int data2 = -1;

		public Message(boolean input, MidiMessage message) {
			this.input = input;

			this.status = message.getStatus();
			if (message instanceof ShortMessage) {
				ShortMessage shortMessage = (ShortMessage) message;
				this.data1 = shortMessage.getData1();
				this.data2 = shortMessage.getData2();

				if (MessageUtils.isChannelMessage(shortMessage)) {
					this.channel = shortMessage.getChannel();
				}
			}
		}

		public boolean isInput() {
			return input;
		}

		public int getChannel() {
			return channel;
		}

		public int getStatus() {
			return status;
		}

		public int getData1() {
			return data1;
		}

		public int getData2() {
			return data2;
		}

		public String getDescription() {
			MessageBuilder builder = new MessageBuilder();

			int status = this.status;
			if (channel != -1) {
				status -= channel;
			}

			config.get(status + ">" + data1 + ">" + data2).read(builder);
			if (!builder.hasPattern()) {
				config.get(status + ">" + data1).read(builder);
				if (!builder.hasPattern()) {
					config.get("" + status).read(builder);
				}
			}
			return builder.build(status, data1, data2);
		}

		public Color getColor() {
			if (status >= 0x80 && status < 0xf0) {
				return colors[(status - 0x80) >> 4];
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