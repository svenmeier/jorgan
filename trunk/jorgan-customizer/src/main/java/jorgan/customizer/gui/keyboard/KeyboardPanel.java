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
package jorgan.customizer.gui.keyboard;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.disposition.Elements;
import jorgan.disposition.Keyboard;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.ShortMessageRecorder;
import jorgan.midi.mpl.ProcessingException;
import jorgan.swing.BaseAction;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.Group;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * A panel for a {@link Keyboard}.
 */
public class KeyboardPanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			KeyboardPanel.class);

	private Keyboard keyboard;

	private JComboBox deviceComboBox;

	private JSpinner channelSpinner;

	private JSpinner fromSpinner;

	private JSpinner toSpinner;

	private JSpinner transposeSpinner;

	private RecordAction recordAction = new RecordAction();

	public KeyboardPanel(Keyboard keyboard) {
		this.keyboard = keyboard;

		setLayout(new BorderLayout());

		add(new Group(new JLabel(Elements.getDisplayName(keyboard))),
				BorderLayout.NORTH);

		JPanel definitions = new JPanel();
		add(definitions, BorderLayout.CENTER);

		DefinitionBuilder builder = new DefinitionBuilder(definitions);

		Column firstColumn = builder.column();

		firstColumn.term(config.get("device").read(new JLabel()));
		deviceComboBox = new JComboBox();
		deviceComboBox.setEditable(false);
		deviceComboBox.addItemListener(recordAction);
		firstColumn.definition(deviceComboBox).fillHorizontal();

		firstColumn.term(config.get("transpose").read(new JLabel()));
		transposeSpinner = new JSpinner(new SpinnerNumberModel(0, -64, 63, 1));
		firstColumn.definition(transposeSpinner);

		firstColumn.definition(new JButton(recordAction));

		Column secondColumn = builder.column();

		secondColumn.term(config.get("channel").read(new JLabel()));
		channelSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 15, 1));
		secondColumn.definition(channelSpinner);

		secondColumn.term(config.get("from").read(new JLabel()));
		fromSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
		secondColumn.definition(fromSpinner);

		secondColumn.term(config.get("to").read(new JLabel()));
		toSpinner = new JSpinner(new SpinnerNumberModel(127, 0, 127, 1));
		secondColumn.definition(toSpinner);

		init();
	}

	private void init() {
		String[] deviceNames = DevicePool.instance().getMidiDeviceNames(
				Direction.IN);
		String[] items = new String[1 + deviceNames.length];
		System.arraycopy(deviceNames, 0, items, 1, deviceNames.length);
		deviceComboBox.setModel(new DefaultComboBoxModel(items));
		deviceComboBox.setSelectedItem(keyboard.getInput());

		try {
			channelSpinner.setValue(keyboard.getChannel());
		} catch (ProcessingException e) {
			channelSpinner.setEnabled(false);
		}

		try {
			fromSpinner.setValue(keyboard.getFrom());
			toSpinner.setValue(keyboard.getTo());
			transposeSpinner.setValue(keyboard.getTranspose());
		} catch (ProcessingException e) {
			fromSpinner.setEnabled(false);
			toSpinner.setEnabled(false);
			transposeSpinner.setEnabled(false);
		}
	}

	public void apply() {
		keyboard.setInput((String) deviceComboBox.getSelectedItem());

		try {
			if (channelSpinner.isEnabled()) {
				keyboard.setChannel((Integer) channelSpinner.getValue());
			}
		} catch (ProcessingException ignore) {
		}

		try {
			if (fromSpinner.isEnabled()) {
				keyboard.setPitch((Integer) fromSpinner.getValue(),
						(Integer) toSpinner.getValue(),
						(Integer) transposeSpinner.getValue());
			}
		} catch (ProcessingException ignore) {
		}
	}

	private class RecordAction extends BaseAction implements ItemListener {

		private MessageBox messageBox = new MessageBox(
				MessageBox.OPTIONS_OK_CANCEL);

		private int channel;

		private int from;

		private int to;

		public RecordAction() {
			config.get("record").read(this);

			config.get("record/message").read(messageBox);
			
			setEnabled(false);
		}

		public void itemStateChanged(ItemEvent e) {
			setEnabled(deviceComboBox.getSelectedItem() != null);
		}

		public void actionPerformed(ActionEvent e) {
			channel = -1;
			from = Integer.MAX_VALUE;
			to = 0;

			try {
				ShortMessageRecorder recorder = new ShortMessageRecorder(
						(String) deviceComboBox.getSelectedItem()) {
					@Override
					public boolean messageRecorded(ShortMessage message) {
						if (isValid(message.getCommand())) {
							if (channel == -1) {
								channel = message.getChannel();
							}

							if (message.getChannel() == channel) {
								from = Math.min(from, message.getData1());
								to = Math.max(to, message.getData1());
							}
						}

						return true;
					}
				};

				int result = messageBox.show(KeyboardPanel.this);

				recorder.close();

				if (result == MessageBox.OPTION_OK && channel != -1) {
					channelSpinner.setValue(channel);
					fromSpinner.setValue(from);
					toSpinner.setValue(to);
				}
			} catch (MidiUnavailableException cannotRecord) {
			}
		}

		private boolean isValid(int command) {
			return command == ShortMessage.NOTE_ON
					|| command == ShortMessage.NOTE_OFF
					|| command == ShortMessage.POLY_PRESSURE;
		}
	}
}