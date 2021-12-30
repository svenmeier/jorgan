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
package jorgan.customizer.gui.connector;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import bias.Configuration;
import bias.swing.MessageBox;
import jorgan.customizer.builder.ContinuousBuilder;
import jorgan.customizer.builder.TupleBuilder;
import jorgan.disposition.Connector;
import jorgan.disposition.Continuous;
import jorgan.disposition.Continuous.Change;
import jorgan.disposition.Elements;
import jorgan.disposition.Message;
import jorgan.midi.MessageRecorder;
import jorgan.midi.MessageUtils;
import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.Tuple;
import jorgan.swing.BaseAction;
import jorgan.swing.table.ActionCellEditor;
import jorgan.swing.table.BaseTableModel;
import jorgan.swing.table.TableUtils;

/**
 * A panel for a {@link Connector}.
 */
public abstract class ContinuousPanel extends AbstractConnectorPanel {

	private static Configuration config = Configuration.getRoot()
			.get(ContinuousPanel.class);

	private ContinuousModel model = new ContinuousModel();

	private List<Row> rows = new ArrayList<Row>();

	private RecordAction recordAction = new RecordAction();

	private JTable table;

	private TestContext context = new TestContext();

	private Set<Message> received = new HashSet<Message>();

	public ContinuousPanel(List<Continuous> continuous) {
		setLayout(new BorderLayout());

		JScrollPane scrollPane = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(128, 32));
		add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		config.get("table").read(model);
		table.setModel(model);
		ToolTipManager.sharedInstance().registerComponent(table);
		MessageCellRenderer renderer = new MessageCellRenderer() {
			@Override
			protected boolean isHighlighted(Message value) {
				return received.contains(value);
			}
		};
		table.getColumnModel().getColumn(1).setCellRenderer(renderer);
		table.getColumnModel().getColumn(1)
				.setCellEditor(new ActionCellEditor(recordAction));
		TableUtils.pleasantLookAndFeel(table);
		scrollPane.setViewportView(table);

		for (Continuous aContinuous : continuous) {
			rows.add(new Row(aContinuous));
		}
	}

	public void received(MidiMessage message) {
		received.clear();

		byte[] datas = MessageUtils.getDatas(message);

		for (Row row : rows) {
			row.received(datas);
		}
		table.repaint();
	}

	public void apply() {
		for (Row row : rows) {
			row.apply();
		}
	}

	public class ContinuousModel extends BaseTableModel<Row> {

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return rows.size();
		}

		@Override
		protected Row getRow(int rowIndex) {
			return rows.get(rowIndex);
		}

		@Override
		protected boolean isEditable(Row row, int columnIndex) {
			return getDeviceName() != null && (columnIndex >= 1);
		}

		@Override
		protected Object getValue(Row continuousRow, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return continuousRow.getDisplayName();
			case 1:
				return continuousRow.change;
			}

			throw new Error();
		}
	}

	private class RecordAction extends BaseAction {

		private MessageBox messageBox = new MessageBox(
				MessageBox.OPTIONS_OK_CANCEL);

		protected RecordAction() {
			config.get("record").read(this);

			config.get("record/message").read(messageBox);
		}

		public void actionPerformed(ActionEvent e) {
			Row row = rows.get(table.getEditingRow());

			try {
				final TupleBuilder builder = new ContinuousBuilder();

				MessageRecorder recorder = new MessageRecorder(
						getDeviceName()) {
					@Override
					public boolean messageRecorded(MidiMessage message) {
						if (builder.analyse(MessageUtils.getDatas(message))) {
							return true;
						} else {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									messageBox.hide();

									table.requestFocus();
								}
							});
							return false;
						}
					}
				};

				int result = messageBox.show(ContinuousPanel.this);

				recorder.close();

				if (result != MessageBox.OPTION_CANCEL) {
					row.setTuple(builder.decide());
				}
			} catch (MidiUnavailableException cannotRecord) {
			}
		}
	}

	private class TestContext implements Context {

		private Map<String, Float> map = new HashMap<String, Float>();

		public float get(String name) {
			Float temp = map.get(name);
			if (temp == null) {
				return Float.NaN;
			} else {
				return temp;
			}
		}

		public void set(String name, float value) {
			map.put(name, value);
		}

		public boolean process(Message message, byte[] datas) {
			if (message.getLength() != datas.length) {
				return false;
			}

			for (int d = 0; d < datas.length; d++) {
				float processed = message.process(datas[d] & 0xff, this, d);
				if (Float.isNaN(processed)) {
					return false;
				}
				int rounded = Math.round(processed);
				if (rounded < 0 || rounded > 255) {
					return false;
				}
			}
			return true;
		}
	}

	private class Row {

		private boolean changed = false;

		private Continuous aContinuous;

		private Message change;

		public Row(Continuous aContinuous) {
			this.aContinuous = aContinuous;

			List<Change> changes = aContinuous.getMessages(Change.class);
			if (!changes.isEmpty()) {
				change = changes.get(0);
			}
		}

		public String getDisplayName() {
			return Elements.getDisplayName(aContinuous);
		}

		public void setTuple(Tuple tuple) {
			if (tuple == null) {
				change = null;
			} else {
				change = new Continuous.Change().change(tuple);
			}

			changed = true;
		}

		public void received(byte[] datas) {
			if (change != null && context.process(change, datas)) {
				received.add(change);
			}
		}

		public void apply() {
			if (changed) {
				aContinuous.removeMessages(Change.class);
				if (change != null) {
					aContinuous.addMessage(change);
				}
			}
		}
	}

	protected abstract String getDeviceName();
}