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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import bias.Configuration;
import bias.swing.MessageBox;
import jorgan.customizer.builder.MomentaryBuilder;
import jorgan.customizer.builder.TupleBuilder;
import jorgan.disposition.Elements;
import jorgan.disposition.Message;
import jorgan.disposition.Switch;
import jorgan.disposition.Switch.Activate;
import jorgan.disposition.Switch.Deactivate;
import jorgan.disposition.Switch.Toggle;
import jorgan.midi.MessageRecorder;
import jorgan.midi.MessageUtils;
import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.Tuple;
import jorgan.swing.BaseAction;
import jorgan.swing.table.ActionCellEditor;
import jorgan.swing.table.BaseTableModel;
import jorgan.swing.table.TableUtils;

public abstract class SwitchesPanel extends JPanel {

	private static Configuration config = Configuration.getRoot()
			.get(SwitchesPanel.class);

	private SwitchesModel model = new SwitchesModel();

	private List<Row> rows = new ArrayList<Row>();

	private RecordAction recordAction = new RecordAction();

	private JTable table;

	private TestContext context = new TestContext();

	private Set<Message> received = new HashSet<Message>();

	public SwitchesPanel(List<Switch> switches) {
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
		TableUtils.pleasantLookAndFeel(table);
		MessageCellRenderer renderer = new MessageCellRenderer() {
			@Override
			protected boolean isHighlighted(Message message) {
				return received.contains(message);
			}
		};
		table.getColumnModel().getColumn(1).setCellRenderer(renderer);
		table.getColumnModel().getColumn(1)
				.setCellEditor(new ActionCellEditor(recordAction));
		table.getColumnModel().getColumn(2).setCellRenderer(renderer);
		table.getColumnModel().getColumn(2)
				.setCellEditor(new ActionCellEditor(recordAction));
		table.getColumnModel().getColumn(3).setCellRenderer(renderer);
		table.getColumnModel().getColumn(3)
				.setCellEditor(new ActionCellEditor(recordAction));
		scrollPane.setViewportView(table);

		for (Switch aSwitch : switches) {
			rows.add(new Row(aSwitch));
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
		for (Row switchRow : rows) {
			switchRow.apply();
		}
	}

	public class SwitchesModel extends BaseTableModel<Row> {

		public int getColumnCount() {
			return 4;
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
		protected Object getValue(Row switchRow, int columnIndex) {

			switch (columnIndex) {
			case 0:
				return switchRow.getDisplayName();
			case 1:
				return switchRow.activate;
			case 2:
				return switchRow.deactivate;
			case 3:
				return switchRow.toggle;
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
			int index = table.getEditingColumn();

			try {
				final TupleBuilder builder = new MomentaryBuilder();

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

				int result = messageBox.show(SwitchesPanel.this);

				recorder.close();

				if (result != MessageBox.OPTION_CANCEL) {
					row.setTuple(index, builder.decide());
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

		private Switch aSwitch;

		private Message activate;

		private Message deactivate;

		private Message toggle;

		public Row(Switch aSwitch) {
			this.aSwitch = aSwitch;

			List<Activate> activates = aSwitch.getMessages(Activate.class);
			if (!activates.isEmpty()) {
				activate = activates.get(0);
			}

			List<Deactivate> deactivates = aSwitch
					.getMessages(Deactivate.class);
			if (!deactivates.isEmpty()) {
				deactivate = deactivates.get(0);
			}

			List<Toggle> toggles = aSwitch.getMessages(Toggle.class);
			if (!toggles.isEmpty()) {
				toggle = toggles.get(0);
			}
		}

		public String getDisplayName() {
			return Elements.getDisplayName(aSwitch);
		}

		public void setTuple(int index, Tuple tuple) {
			switch (index) {
			case 1:
				if (tuple == null) {
					activate = null;
				} else {
					activate = new Switch.Activate().change(tuple);
				}
				break;
			case 2:
				if (tuple == null) {
					deactivate = null;
				} else {
					deactivate = new Switch.Deactivate().change(tuple);
				}
				break;
			case 3:
				if (tuple == null) {
					toggle = null;
				} else {
					toggle = new Switch.Toggle().change(tuple);
				}
				break;
			}

			changed = true;
		}

		public void received(byte[] datas) {
			if (activate != null && context.process(activate, datas)) {
				received.add(activate);
			}

			if (deactivate != null && context.process(deactivate, datas)) {
				received.add(deactivate);
			}
		}

		public void apply() {
			if (changed) {
				aSwitch.removeMessages(Deactivate.class);
				if (deactivate != null) {
					aSwitch.addMessage(deactivate);
				}

				aSwitch.removeMessages(Activate.class);
				if (activate != null) {
					aSwitch.addMessage(activate);
				}

				aSwitch.removeMessages(Toggle.class);
				if (toggle != null) {
					aSwitch.addMessage(toggle);
				}
			}
		}
	}

	protected abstract String getDeviceName();
}