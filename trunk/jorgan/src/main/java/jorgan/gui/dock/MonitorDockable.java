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
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.table.TableColumn;

import jorgan.disposition.Element;
import jorgan.midi.MessageUtils;
import jorgan.play.OrganPlay;
import jorgan.play.event.PlayListener;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.table.BaseTableModel;
import jorgan.swing.table.SimpleCellRenderer;
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

		for (int c = 0; c < table.getColumnCount(); c++) {
			TableColumn column = table.getColumnModel().getColumn(c);

			column.setCellRenderer(new MessageCellRenderer());
		}
		TableUtils.fixColumnWidth(table, 0, new Message(false,
				new ShortMessage()));
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
		public void received(Element element, MidiMessage message) {
			if (inputButton.isSelected()) {
				add(true, message);
			}
		}

		@Override
		public void sent(Element element, MidiMessage message) {
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
			return message;
		}
	}

	private class Message {

		public final boolean input;

		public final String channel;

		public final String status;

		public final String data1;

		public final String data2;

		public final int length;

		private final String description;

		private Color color;

		public Message(boolean input, MidiMessage message) {
			this.input = input;

			int status = message.getStatus();

			if (message instanceof ShortMessage) {
				ShortMessage shortMessage = (ShortMessage) message;

				if (MessageUtils.isChannelStatus(status)) {
					this.status = String.valueOf(status & 0xf0);
					this.channel = String.valueOf(shortMessage.getChannel());
				} else {
					this.status = String.valueOf(status);
					this.channel = "-";
				}

				this.data1 = String.valueOf(shortMessage.getData1());
				this.data2 = String.valueOf(shortMessage.getData2());
			} else {
				this.status = String.valueOf(status);
				this.data1 = "-";
				this.data2 = "-";
				this.channel = "-";
			}
			this.length = message.getLength();

			this.description = buildDescription();
			this.color = buildColor(status);
		}

		private String buildDescription() {
			MessageBuilder builder = new MessageBuilder();

			config.get(status + ">" + data1 + ">" + data2).read(builder);
			if (!builder.hasPattern()) {
				config.get(status + ">" + data1).read(builder);
				if (!builder.hasPattern()) {
					config.get("" + status).read(builder);
				}
			}
			return builder.build(status, data1, data2, length);
		}

		public Color buildColor(int status) {
			if (status >= 0x80 && status < 0xf0) {
				return colors[(status - 0x80) >> 4];
			} else {
				return Color.white;
			}
		}
	}

	private class MessageCellRenderer extends SimpleCellRenderer<Message> {

		private transient Message message;

		public MessageCellRenderer() {
			setHorizontalAlignment(SwingConstants.RIGHT);
		}

		@Override
		protected void init(Message message, boolean isSelected,
				boolean hasFocus, int row, int column) {

			this.message = message;

			if (!isSelected) {
				setBackground(message.color);
			}

			switch (column) {
			case 0:
				if (message.input) {
					setIcon(inputButton.getIcon());
				} else {
					setIcon(outputButton.getIcon());
				}
				break;
			case 1:
				setText(message.channel);
				break;
			case 2:
				setText(message.status);
				break;
			case 3:
				setText(message.data1);
				break;
			case 4:
				setText(message.data2);
				break;
			}
		}

		@Override
		public String getToolTipText() {
			if (message != null) {
				return message.description;
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