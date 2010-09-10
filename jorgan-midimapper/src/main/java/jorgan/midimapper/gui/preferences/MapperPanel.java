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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
		JScrollPane fromScrollPane = new JScrollPane(fromTable,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
			@Override
			public Dimension getPreferredSize() {
				Dimension size = new Dimension();
				size.height = fromTable.getPreferredSize().height
						+ fromTable.getTableHeader().getPreferredSize().height;
				size.width = 64;
				return size;
			}
		};
		TableUtils.pleasantLookAndFeel(fromTable);
		fromScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		add(fromScrollPane);

		toTable = new JTable();
		JScrollPane toScrollPane = new JScrollPane(toTable,
				JScrollPane.VERTICAL_SCROLLBAR_NEVER,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
			@Override
			public Dimension getPreferredSize() {
				Dimension size = new Dimension();
				size.height = toTable.getPreferredSize().height
						+ toTable.getTableHeader().getPreferredSize().height;
				size.width = 64;
				return size;
			}
		};
		TableUtils.pleasantLookAndFeel(toTable);
		toScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		toScrollPane.setPreferredSize(new Dimension(64, 64));
		add(toScrollPane);

		read();
	}

	private void read() {
		fromTable.setModel(new MessageModel(mapper.getFrom()));
		toTable.setModel(new MessageModel(mapper.getFrom()));
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