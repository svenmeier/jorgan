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
package jorgan.customizer.gui.console;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;

import jorgan.disposition.Console;
import jorgan.disposition.Continuous;
import jorgan.disposition.Elements;
import jorgan.disposition.Message;
import jorgan.disposition.Switch;
import jorgan.disposition.Continuous.Change;
import jorgan.disposition.Switch.Activate;
import jorgan.disposition.Switch.Deactivate;
import jorgan.disposition.Switch.Toggle;
import jorgan.gui.FullScreen;
import jorgan.gui.OrganPanel;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.ShortMessageRecorder;
import jorgan.midi.mpl.Context;
import jorgan.swing.BaseAction;
import jorgan.swing.ComboBoxUtils;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import jorgan.swing.table.ActionCellEditor;
import jorgan.swing.table.BaseTableModel;
import jorgan.swing.table.TableUtils;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * A panel for a {@link console}.
 */
public class ConsolePanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			ConsolePanel.class);

	private static final Icon inputIcon = new ImageIcon(OrganPanel.class
			.getResource("img/input.gif"));

	private Console console;

	private JComboBox screenComboBox;

	private JSpinner zoomSpinner;

	private JComboBox deviceComboBox;

	private SwitchesModel switchesModel = new SwitchesModel();

	private List<SwitchRow> switchRows = new ArrayList<SwitchRow>();

	private ContinuousModel continuousModel = new ContinuousModel();

	private List<ContinuousRow> continuousRows = new ArrayList<ContinuousRow>();

	private ActivateAction activateAction = new ActivateAction();

	private DeactivateAction deactivateAction = new DeactivateAction();

	private ToggleAction toggleAction = new ToggleAction();

	private ChangeAction changeAction = new ChangeAction();

	private JTable switchesTable;

	private JTable continuousTable;

	private ShortMessageRecorder recorder;

	private TestContext context = new TestContext();

	private Set<Message> received = new HashSet<Message>();

	public ConsolePanel(Console console) {
		this.console = console;

		DefinitionBuilder builder = new DefinitionBuilder(this);

		Column column = builder.column();

		column.term(config.get("screen").read(new JLabel()));
		screenComboBox = new JComboBox();
		screenComboBox.setEditable(false);
		column.definition(screenComboBox).fillHorizontal();

		column.term(config.get("zoom").read(new JLabel()));
		zoomSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.5, 5.0, 0.1));
		zoomSpinner.setEditor(new JSpinner.NumberEditor(zoomSpinner, "0.00"));
		column.definition(zoomSpinner);

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
		scrollPane.setPreferredSize(new Dimension(128, 32));
		column.box(scrollPane).growVertical();

		switchesTable = new JTable();
		config.get("switchesTable").read(switchesModel);
		switchesTable.setModel(switchesModel);
		TableUtils.pleasantLookAndFeel(switchesTable);
		switchesTable.getColumnModel().getColumn(1).setCellRenderer(
				new HighlightCellRenderer());
		switchesTable.getColumnModel().getColumn(1).setCellEditor(
				new ActionCellEditor(activateAction));
		switchesTable.getColumnModel().getColumn(2).setCellRenderer(
				new HighlightCellRenderer());
		switchesTable.getColumnModel().getColumn(2).setCellEditor(
				new ActionCellEditor(deactivateAction));
		switchesTable.getColumnModel().getColumn(3).setCellRenderer(
				new HighlightCellRenderer());
		switchesTable.getColumnModel().getColumn(3).setCellEditor(
				new ActionCellEditor(toggleAction));
		scrollPane.setViewportView(switchesTable);
	}

	private void initContinuous(Column column) {
		column.group(config.get("continuous").read(new JLabel()));

		JScrollPane scrollPane = new JScrollPane(
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(128, 32));
		column.box(scrollPane).growVertical();

		continuousTable = new JTable();
		config.get("continuousTable").read(continuousModel);
		continuousTable.setModel(continuousModel);
		continuousTable.getColumnModel().getColumn(1).setCellRenderer(
				new HighlightCellRenderer());
		continuousTable.getColumnModel().getColumn(1).setCellEditor(
				new ActionCellEditor(changeAction));
		TableUtils.pleasantLookAndFeel(continuousTable);
		scrollPane.setViewportView(continuousTable);
	}

	private void read() {
		this.screenComboBox.setModel(ComboBoxUtils
				.createModelWithNull(FullScreen.getIDs()));
		this.screenComboBox.setSelectedItem(console.getScreen());

		this.zoomSpinner.setValue(new Double(console.getZoom()));

		this.deviceComboBox.setModel(ComboBoxUtils
				.createModelWithNull(DevicePool.instance().getMidiDeviceNames(
						Direction.IN)));
		this.deviceComboBox.setSelectedItem(console.getInput());

		switchRows = new ArrayList<SwitchRow>();
		for (Switch aSwitch : console.getReferenced(Switch.class)) {
			switchRows.add(new SwitchRow(aSwitch));
		}

		continuousRows = new ArrayList<ContinuousRow>();
		for (Continuous aContinuous : console.getReferenced(Continuous.class)) {
			continuousRows.add(new ContinuousRow(aContinuous));
		}
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
					public boolean messageRecorded(final MidiMessage message) {
						if (isShowing()) {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									received(message);
								}
							});
						}
						return true;
					}
				};
			} catch (MidiUnavailableException notAvailable) {
			}
		}
	}

	private void received(MidiMessage message) {
		received.clear();

		for (SwitchRow switchRow : switchRows) {
			switchRow.received(message);
		}
		switchesTable.repaint();

		for (ContinuousRow continuousRow : continuousRows) {
			continuousRow.received(message);
		}
		continuousTable.repaint();
	}

	public void apply() {
		console.setScreen((String) this.screenComboBox.getSelectedItem());
		console.setInput(getDeviceName());

		console.setZoom(((Number) this.zoomSpinner.getValue()).floatValue());

		for (SwitchRow switchRow : switchRows) {
			switchRow.apply();
		}

		for (ContinuousRow continuousRow : continuousRows) {
			continuousRow.apply();
		}
	}

	private String getDeviceName() {
		return (String) deviceComboBox.getSelectedItem();
	}

	public class SwitchesModel extends BaseTableModel<SwitchRow> {

		public int getColumnCount() {
			return 4;
		}

		public int getRowCount() {
			return switchRows.size();
		}

		@Override
		protected SwitchRow getRow(int rowIndex) {
			return switchRows.get(rowIndex);
		}

		@Override
		protected boolean isEditable(SwitchRow row, int columnIndex) {
			return getDeviceName() != null && (columnIndex >= 1);
		}

		@Override
		protected Object getValue(SwitchRow switchRow, int columnIndex) {

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

	public class ContinuousModel extends BaseTableModel<ContinuousRow> {

		public int getColumnCount() {
			return 2;
		}

		public int getRowCount() {
			return continuousRows.size();
		}

		@Override
		protected ContinuousRow getRow(int rowIndex) {
			return continuousRows.get(rowIndex);
		}

		@Override
		protected boolean isEditable(ContinuousRow row, int columnIndex) {
			return getDeviceName() != null && (columnIndex >= 1);
		}

		@Override
		protected Object getValue(ContinuousRow continuousRow, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return continuousRow.getDisplayName();
			case 1:
				return continuousRow.change;
			}

			throw new Error();
		}
	}

	private abstract class RecordAction extends BaseAction {

		private MessageBox messageBox = new MessageBox(
				MessageBox.OPTIONS_OK_CANCEL);

		protected RecordAction(String name) {
			config.get(name).read(this);

			config.get(name + "/message").read(messageBox);
		}

		public void actionPerformed(ActionEvent e) {
			try {
				beforeRecording();

				ShortMessageRecorder recorder = new ShortMessageRecorder(
						(String) deviceComboBox.getSelectedItem()) {
					@Override
					public boolean messageRecorded(MidiMessage message) {
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

				int result = messageBox.show(ConsolePanel.this);

				recorder.close();

				if (result == MessageBox.OPTION_OK) {
					afterRecording();
				}
			} catch (MidiUnavailableException cannotRecord) {
			}
		}

		protected void beforeRecording() {
		}

		protected abstract boolean recorded(MidiMessage message);

		protected void afterRecording() {
		}
	}

	private class ActivateAction extends RecordAction {

		public ActivateAction() {
			super("activate");
		}

		@Override
		protected boolean recorded(MidiMessage message) {
			SwitchRow switchRow = switchRows.get(switchesTable.getEditingRow());

			switchRow.newActivate(message.getMessage());

			return false;
		}

		@Override
		protected void afterRecording() {
			SwitchRow switchRow = switchRows.get(switchesTable.getEditingRow());

			switchRow.clearActivate();
		}
	}

	private class DeactivateAction extends RecordAction {

		public DeactivateAction() {
			super("deactivate");
		}

		@Override
		protected boolean recorded(MidiMessage message) {
			SwitchRow switchRow = switchRows.get(switchesTable.getEditingRow());

			switchRow.newDeactivate(message.getMessage());

			return false;
		}

		@Override
		protected void afterRecording() {
			SwitchRow switchRow = switchRows.get(switchesTable.getEditingRow());

			switchRow.clearDeactivate();
		}
	}

	private class ToggleAction extends RecordAction {

		public ToggleAction() {
			super("toggle");
		}

		@Override
		protected boolean recorded(MidiMessage message) {
			SwitchRow switchRow = switchRows.get(switchesTable.getEditingRow());

			switchRow.newToggle(message.getMessage());

			return false;
		}

		@Override
		protected void afterRecording() {
			SwitchRow switchRow = switchRows.get(switchesTable.getEditingRow());

			switchRow.clearToggle();
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
		protected boolean recorded(MidiMessage message) {

			state.recorded(message);

			return true;
		}

		@Override
		protected void afterRecording() {
			state.afterRecording();
		}

		private abstract class State {
			protected abstract void recorded(MidiMessage message);

			protected abstract void afterRecording();
		}

		private class Undecided extends State {

			@Override
			protected void recorded(MidiMessage message) {
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
				ContinuousRow continuousRow = continuousRows
						.get(continuousTable.getEditingRow());

				continuousRow.clearChange();
			}
		}

		private abstract class Decided extends State {
			@Override
			protected void recorded(MidiMessage message) {
				int value = getValue(message);

				min = Math.min(min, value);
				max = Math.max(max, value);
			}

			protected abstract int getValue(MidiMessage message);
		}

		private class StatusMinMax extends Decided {
			@Override
			protected int getValue(MidiMessage message) {
				return message.getStatus();
			}

			@Override
			protected void afterRecording() {
				ContinuousRow continuousRow = continuousRows
						.get(continuousTable.getEditingRow());

				continuousRow.newChangeWithStatus(min, max, data1, data2);
			}
		}

		private class Data1MinMax extends Decided {
			@Override
			protected int getValue(MidiMessage message) {
				return message.getData1();
			}

			@Override
			protected void afterRecording() {
				ContinuousRow continuousRow = continuousRows
						.get(continuousTable.getEditingRow());

				continuousRow.newChangeWithData1(status, min, max, data2);
			}
		}

		private class Data2MinMax extends Decided {
			@Override
			protected int getValue(MidiMessage message) {
				return message.getData2();
			}

			@Override
			protected void afterRecording() {
				ContinuousRow continuousRow = continuousRows
						.get(continuousTable.getEditingRow());

				continuousRow.newChangeWithData2(status, data1, min, max);
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
				datas[d] = (byte) rounded;
			}
			return true;
		}
	}

	private class SwitchRow {

		private boolean changed = false;

		private Switch aSwitch;

		private Activate activate;

		private Deactivate deactivate;

		private Toggle toggle;

		public SwitchRow(Switch aSwitch) {
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

		public void newActivate(byte[] datas) {
			changed = true;

			activate = aSwitch.createActivate(datas);
		}

		public void newDeactivate(byte[] datas) {
			changed = true;

			deactivate = aSwitch.createDeactivate(datas);
		}

		public void newToggle(byte[] datas) {
			changed = true;

			toggle = aSwitch.createToggle(datas);
		}

		public void clearActivate() {
			changed = true;

			activate = null;
		}

		public void clearDeactivate() {
			changed = true;

			deactivate = null;
		}

		public void clearToggle() {
			changed = true;

			toggle = null;
		}

		public void received(MidiMessage message) {
			if (activate != null
					&& context.process(activate, message.getMessage())) {
				received.add(activate);
			}

			if (deactivate != null
					&& context.process(deactivate, message.getMessage())) {
				received.add(deactivate);
			}
		}

		public void apply() {
			if (changed) {
				aSwitch.removeMessages(Activate.class);
				if (activate != null) {
					aSwitch.addMessage(activate);
				}

				aSwitch.removeMessages(Deactivate.class);
				if (deactivate != null) {
					aSwitch.addMessage(deactivate);
				}

				aSwitch.removeMessages(Toggle.class);
				if (toggle != null) {
					aSwitch.addMessage(toggle);
				}
			}
		}
	}

	private class ContinuousRow {

		private boolean changed = false;

		private Continuous aContinuous;

		private Change change;

		public ContinuousRow(Continuous aContinuous) {
			this.aContinuous = aContinuous;

			List<Change> changes = aContinuous.getMessages(Change.class);
			if (!changes.isEmpty()) {
				change = changes.get(0);
			}
		}

		public String getDisplayName() {
			return Elements.getDisplayName(aContinuous);
		}

		public void newChangeWithStatus(int min, int max, int data1, int data2) {
			changed = true;

			change = aContinuous.createChangeWithStatus(min, max, data1, data2);
		}

		public void newChangeWithData1(int status, int min, int max, int data2) {
			changed = true;

			change = aContinuous.createChangeWithData1(status, min, max, data2);
		}

		public void newChangeWithData2(int status, int data1, int min, int max) {
			changed = true;

			change = aContinuous.createChangeWithData2(status, data1, min, max);
		}

		public void clearChange() {
			changed = true;

			change = null;
		}

		public void received(MidiMessage message) {
			if (change != null && context.process(change, message.getMessage())) {
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

	private class HighlightCellRenderer extends DefaultTableCellRenderer {

		private Color defaultBackground;

		public HighlightCellRenderer() {
			this.defaultBackground = getBackground();
			setHorizontalAlignment(CENTER);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {

			setBackground(defaultBackground);

			super.getTableCellRendererComponent(table, null, isSelected,
					hasFocus, row, column);

			if (value != null && received.contains(value)) {
				setBackground(Color.yellow);
			} else {
			}

			if (value != null) {
				setIcon(inputIcon);
			} else {
				setIcon(null);
			}

			return this;
		}
	}
}