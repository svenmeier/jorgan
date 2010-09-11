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
package jorgan.midimapper.gui.preferences;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import jorgan.midimapper.mapping.Mapper;
import jorgan.midimapper.mapping.Message;
import jorgan.swing.table.BaseTableModel;
import jorgan.swing.table.TableUtils;

/**
 */
public class MapperPanel extends JPanel {

	private Mapper mapper;
	private JTable toTable;
	private JTable fromTable;

	public MapperPanel(Mapper mapper) {
		this.mapper = mapper;

		setLayout(new GridLayout(0, 2));

		fromTable = new JTable();
		TableUtils.pleasantLookAndFeel(fromTable);
		add(createWrapper(fromTable, new Insets(0, 0, 0, 2)));

		toTable = new JTable();
		TableUtils.pleasantLookAndFeel(toTable);
		add(createWrapper(toTable, new Insets(0, 2, 0, 0)));

		read();
	}

	private JPanel createWrapper(JTable table, Insets insets) {
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBorder(new EmptyBorder(insets));
		wrapper.add(table, BorderLayout.CENTER);
		wrapper.add(table.getTableHeader(), BorderLayout.NORTH);
		return wrapper;
	}

	private void read() {
		fromTable.setModel(new MessageModel(mapper.getFrom()));
		toTable.setModel(new MessageModel(mapper.getTo()));
	}

	private class MessageModel extends BaseTableModel<Message> {

		private List<Message> messages;

		public MessageModel(List<Message> messages) {
			this.messages = messages;
		}

		@Override
		public int getColumnCount() {
			return 3;
		}

		@Override
		public int getRowCount() {
			return messages.size();
		}

		@Override
		protected Message getRow(int rowIndex) {
			return messages.get(rowIndex);
		}

		@Override
		protected Object getValue(Message message, int columnIndex) {
			return message.getData(columnIndex);
		}
	}
}