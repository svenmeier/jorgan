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
package jorgan.gui.customize.console;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import jorgan.disposition.Console;
import jorgan.disposition.Continuous;
import jorgan.disposition.Elements;
import jorgan.disposition.Message;
import jorgan.disposition.Switch;
import jorgan.disposition.Continuous.Change;
import jorgan.disposition.Switch.Activate;
import jorgan.disposition.Switch.Deactivate;
import jorgan.gui.construct.editor.ValueEditor;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.ShortMessageRecorder;
import jorgan.midi.mpl.Context;
import jorgan.midi.mpl.ProcessingException;
import jorgan.swing.BaseAction;
import jorgan.swing.beans.PropertyCellEditor;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import jorgan.swing.table.ActionCellEditor;
import jorgan.swing.table.IconTableCellRenderer;
import jorgan.swing.table.TableUtils;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * A panel for a {@link console}.
 */
public class ConsolePanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			ConsolePanel.class);

	private Console console;

	private JComboBox deviceComboBox;

	private SwitchesModel switchesModel = new SwitchesModel();

	private List<Switch> switches = new ArrayList<Switch>();

	private ContinuousModel continuousModel = new ContinuousModel();

	private List<Continuous> continuous = new ArrayList<Continuous>();

	private ActivateAction activateAction = new ActivateAction();

	private DeactivateAction deactivateAction = new DeactivateAction();

	private ChangeAction changeAction = new ChangeAction();

	private JTable switchesTable;

	private JTable continuousTable;

	private ShortMessageRecorder recorder;

	private TestContext context = new TestContext();

	public ConsolePanel(Console console) {
		this.console = console;

		DefinitionBuilder builder = new DefinitionBuilder(this);

		Column column = builder.column();

		column.term(config.get("device").read(new JLabel()));
		deviceComboBox = new JComboBox();
		deviceComboBox.setEditable(false);
		deviceComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				record((String) deviceComboBox.getSelectedItem());
			}
		});
		column.definition(deviceComboBox).fillHorizontal();

		initSwitches(column);

		initContinuous(column);

		read();
	}

	private void initSwitches(Column column) {
		column.group(config.get("switches").read(new JLabel()));

		JScrollPane scrollPane = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPane.setPreferredSize(new Dimension(160, 160));
		column.box(scrollPane);

		switchesTable = new JTable();
		config.get("switchesTable").read(switchesModel);
		switchesTable.setCellSelectionEnabled(true);
		switchesTable.setModel(switchesModel);
		TableUtils.pleasantLookAndFeel(switchesTable);
		switchesTable.getColumnModel().getColumn(1).setCellRenderer(
				new IconTableCellRenderer() {
					@Override
					protected Icon getIcon(Object value) {
						if (((Switch) value).hasMessages(Activate.class)) {
							return activateAction.getSmallIcon();
						} else {
							return null;
						}
					}
				});
		switchesTable.getColumnModel().getColumn(1).setCellEditor(
				new ActionCellEditor(activateAction));
		switchesTable.getColumnModel().getColumn(2).setCellRenderer(
				new IconTableCellRenderer() {
					@Override
					protected Icon getIcon(Object value) {
						if (((Switch) value).hasMessages(Deactivate.class)) {
							return deactivateAction.getSmallIcon();
						} else {
							return null;
						}
					}
				});
		switchesTable.getColumnModel().getColumn(2).setCellEditor(
				new ActionCellEditor(deactivateAction));
		scrollPane.setViewportView(switchesTable);
	}

	private void initContinuous(Column column) {
		column.group(config.get("continuous").read(new JLabel()));

		JScrollPane scrollPane = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		scrollPane.setPreferredSize(new Dimension(160, 160));
		column.box(scrollPane);

		continuousTable = new JTable();
		config.get("continuousTable").read(continuousModel);
		continuousTable.setCellSelectionEnabled(true);
		continuousTable.setModel(continuousModel);
		continuousTable.getColumnModel().getColumn(1).setCellRenderer(
				new IconTableCellRenderer() {
					@Override
					protected Icon getIcon(Object value) {
						if (((Continuous) value).hasMessages(Change.class)) {
							return changeAction.getSmallIcon();
						} else {
							return null;
						}
					}
				});
		continuousTable.getColumnModel().getColumn(1).setCellEditor(
				new ActionCellEditor(changeAction));
		continuousTable.getColumnModel().getColumn(2).setCellEditor(
				new PropertyCellEditor() {
					private ValueEditor editor = new ValueEditor();

					@Override
					protected PropertyEditor getEditor(int row) {
						return editor;
					}
				});
		TableUtils.pleasantLookAndFeel(continuousTable);
		scrollPane.setViewportView(continuousTable);
	}

	private void read() {
		String[] deviceNames = DevicePool.instance().getMidiDeviceNames(
				Direction.IN);
		String[] items = new String[1 + deviceNames.length];
		System.arraycopy(deviceNames, 0, items, 1, deviceNames.length);
		this.deviceComboBox.setModel(new DefaultComboBoxModel(items));
		this.deviceComboBox.setSelectedItem(console.getOutput());

		this.switches = new ArrayList<Switch>(console
				.getReferenced(Switch.class));

		this.continuous = new ArrayList<Continuous>(console
				.getReferenced(Continuous.class));
	}

	private void record(String device) {
		if (recorder != null) {
			recorder.close();
			recorder = null;
		}

		if (device != null) {
			try {
				recorder = new ShortMessageRecorder(device) {
					@Override
					public boolean messageRecorded(ShortMessage message) {
						if (isShowing()) {
							checkSwitches(message);
							checkContinuous(message);
						}
						return true;
					}
				};
			} catch (MidiUnavailableException notAvailable) {
			}
		}
	}

	private void checkSwitches(ShortMessage message) {
		switchesTable.clearSelection();

		for (Switch aSwitch : switches) {
			List<Activate> activates = aSwitch.getMessages(Activate.class);
			for (Activate activate : activates) {
				if (context.process(activate, message.getStatus(), message
						.getData1(), message.getData2())) {
					switchesTable.changeSelection(this.switches
							.indexOf(aSwitch), 1, true, false);
					break;
				}
			}

			List<Deactivate> deactivates = aSwitch
					.getMessages(Deactivate.class);
			for (Deactivate activate : deactivates) {
				if (context.process(activate, message.getStatus(), message
						.getData1(), message.getData2())) {
					switchesTable.changeSelection(this.switches
							.indexOf(aSwitch), 2, true, false);
					break;
				}
			}
		}
	}

	private void checkContinuous(ShortMessage message) {
		continuousTable.clearSelection();

		for (Continuous continuous : this.continuous) {
			List<Change> changes = continuous.getMessages(Change.class);
			for (Change change : changes) {
				if (context.process(change, message.getStatus(), message
						.getData1(), message.getData2())) {
					continuousTable.changeSelection(this.continuous
							.indexOf(continuous), 1, true, false);
					break;
				}
			}
		}
	}

	public void apply() {
		console.setInput((String) deviceComboBox.getSelectedItem());
	}

	private String getDeviceName() {
		return (String) deviceComboBox.getSelectedItem();
	}

	public class SwitchesModel extends AbstractTableModel {

		private String[] columnNames = new String[3];

		public void setColumnNames(String[] columnNames) {
			this.columnNames = columnNames;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return switches.size();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return getDeviceName() != null
					&& (columnIndex == 1 || columnIndex == 2);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Switch aSwitch = switches.get(rowIndex);

			switch (columnIndex) {
			case 0:
				return Elements.getDisplayName(aSwitch);
			case 1:
				return aSwitch;
			case 2:
				return aSwitch;
			}

			throw new Error();
		}
	}

	public class ContinuousModel extends AbstractTableModel {

		private String[] columnNames = new String[3];

		public void setColumnNames(String[] columnNames) {
			this.columnNames = columnNames;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return continuous.size();
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return getDeviceName() != null
					&& (columnIndex == 1 || columnIndex == 2);
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			Continuous aContinuous = continuous.get(rowIndex);

			if (columnIndex == 2) {
				aContinuous.setThreshold((Float) aValue);
			}
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			Continuous aContinuous = continuous.get(rowIndex);

			switch (columnIndex) {
			case 0:
				return Elements.getDisplayName(aContinuous);
			case 1:
				return aContinuous;
			case 2:
				return aContinuous.getThreshold();
			}

			throw new Error();
		}
	}

	private abstract class RecordAction extends BaseAction {

		private MessageBox messageBox = new MessageBox(MessageBox.OPTIONS_OK);

		protected RecordAction(String name) {
			config.get(name).read(this);

			config.get(name + "/message").read(messageBox);
		}

		public void actionPerformed(ActionEvent e) {
			beforeRecording();

			try {
				ShortMessageRecorder recorder = new ShortMessageRecorder(
						(String) deviceComboBox.getSelectedItem()) {
					@Override
					public boolean messageRecorded(ShortMessage message) {
						if (recorded(message)) {
							return true;
						} else {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									messageBox.hide();
								}
							});
							return false;
						}
					}
				};

				messageBox.show(ConsolePanel.this);

				recorder.close();
			} catch (MidiUnavailableException cannotRecord) {
			}

			afterRecording();
		}

		protected void beforeRecording() {
		}

		protected abstract boolean recorded(ShortMessage message);

		protected void afterRecording() {
		}
	}

	private class ActivateAction extends RecordAction {

		public ActivateAction() {
			super("activate");
		}

		@Override
		protected boolean recorded(ShortMessage message) {
			Switch aSwitch = switches.get(switchesTable.getEditingRow());

			aSwitch.setActivate(message.getStatus(), message.getData1(),
					message.getData2());

			return false;
		}
	}

	private class DeactivateAction extends RecordAction {

		public DeactivateAction() {
			super("deactivate");
		}

		@Override
		protected boolean recorded(ShortMessage message) {
			Switch aSwitch = switches.get(switchesTable.getEditingRow());

			aSwitch.setDeactivate(message.getStatus(), message.getData1(),
					message.getData2());

			return false;
		}
	}

	private class ChangeAction extends RecordAction {

		private State state;

		private int status;

		private int data1;

		private int data2;

		private int min;

		private int max;

		public ChangeAction() {
			super("change");
		}

		@Override
		protected void beforeRecording() {
			state = new Undecided();
			status = -1;
			data1 = -1;
			data2 = -1;
			min = 127;
			max = 0;
		}

		@Override
		protected boolean recorded(ShortMessage message) {

			state.recorded(message);

			return true;
		}

		@Override
		protected void afterRecording() {
			state.afterRecording();

		}

		private abstract class State {
			protected abstract void recorded(ShortMessage message);

			protected abstract void afterRecording();
		}

		private class Undecided extends State {

			@Override
			protected void recorded(ShortMessage message) {
				if (message.getStatus() != status
						&& message.getData1() == data1
						&& message.getData2() == data2) {
					state = new StatusMinMax();
				}
				if (message.getStatus() == status
						&& message.getData1() != data1
						&& message.getData2() == data2) {
					state = new Data1MinMax();
				}
				if (message.getStatus() == status
						&& message.getData1() == data1
						&& message.getData2() != data2) {
					state = new Data2MinMax();
				}
				status = message.getStatus();
				data1 = message.getData1();
				data2 = message.getData2();
			}

			@Override
			protected void afterRecording() {
				Continuous aContinuous = continuous.get(continuousTable
						.getEditingRow());

				aContinuous.removeMessages(Change.class);
			}
		}

		private abstract class Decided extends State {
			@Override
			protected void recorded(ShortMessage message) {
				int value = getValue(message);

				min = Math.min(min, value);
				max = Math.max(max, value);
			}

			protected abstract int getValue(ShortMessage message);
		}

		private class StatusMinMax extends Decided {
			@Override
			protected int getValue(ShortMessage message) {
				return message.getStatus();
			}

			@Override
			protected void afterRecording() {
				Continuous aContinuous = continuous.get(continuousTable
						.getEditingRow());

				aContinuous.setChangeWithStatus(min, max, data1, data2);
			}
		}

		private class Data1MinMax extends Decided {
			@Override
			protected int getValue(ShortMessage message) {
				return message.getData1();
			}

			@Override
			protected void afterRecording() {
				Continuous aContinuous = continuous.get(continuousTable
						.getEditingRow());

				aContinuous.setChangeWithData1(status, min, max, data2);
			}
		}

		private class Data2MinMax extends Decided {
			@Override
			protected int getValue(ShortMessage message) {
				return message.getData2();
			}

			@Override
			protected void afterRecording() {
				Continuous aContinuous = continuous.get(continuousTable
						.getEditingRow());

				aContinuous.setChangeWithData2(status, data1, min, max);
			}
		}
	}

	private class TestContext implements Context {

		private Map<String, Float> map = new HashMap<String, Float>();

		private int status;

		private int data1;

		private int data2;

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

		public void clear() {
			map.clear();
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

		public boolean process(Message message, int status, int data1, int data2) {
			try {
				float fStatus = message.processStatus(status, this);
				if (Float.isNaN(fStatus)) {
					return false;
				}
				float fData1 = message.processData1(data1, this);
				if (Float.isNaN(fData1)) {
					return false;
				}
				float fData2 = message.processData2(data2, this);
				if (Float.isNaN(fData2)) {
					return false;
				}
				this.status = Math.round(fStatus);
				this.data1 = Math.round(fData1);
				this.data2 = Math.round(fData2);
			} catch (ProcessingException ex) {
				return false;
			}
			return true;
		}
	}
}