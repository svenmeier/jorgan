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

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.customizer.builder.NotesBuilder;
import jorgan.disposition.Elements;
import jorgan.disposition.Keyboard;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.MessageRecorder;
import jorgan.midi.mpl.ProcessingException;
import jorgan.swing.BaseAction;
import jorgan.swing.combobox.BaseComboBoxModel;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.Group;
import jorgan.swing.layout.DefinitionBuilder.Column;
import jorgan.swing.spinner.SpinnerUtils;
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

		add(new Group(new JLabel(Elements.getDescriptionName(keyboard))),
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
		channelSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 16, 1));
		SpinnerUtils.setColumns(channelSpinner, 3);
		secondColumn.definition(channelSpinner);

		secondColumn.term(config.get("from").read(new JLabel()));
		fromSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
		SpinnerUtils.setColumns(fromSpinner, 3);
		secondColumn.definition(fromSpinner);

		secondColumn.term(config.get("to").read(new JLabel()));
		toSpinner = new JSpinner(new SpinnerNumberModel(127, 0, 127, 1));
		SpinnerUtils.setColumns(toSpinner, 3);
		secondColumn.definition(toSpinner);

		init();
	}

	private void init() {
		deviceComboBox.setModel(new BaseComboBoxModel<String>(true, DevicePool
				.instance().getMidiDeviceNames(Direction.IN)));
		deviceComboBox.setSelectedItem(keyboard.getInput());

		try {
			channelSpinner.setValue(keyboard.getChannel() + 1);
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

		if (channelSpinner.isEnabled()) {
			keyboard.setChannel(((Integer) channelSpinner.getValue()) - 1);
		}

		if (fromSpinner.isEnabled()) {
			keyboard.setPitch((Integer) fromSpinner.getValue(),
					(Integer) toSpinner.getValue(), (Integer) transposeSpinner
							.getValue());
		}
	}

	private class RecordAction extends BaseAction implements ItemListener {

		private MessageBox messageBox = new MessageBox(
				MessageBox.OPTIONS_OK_CANCEL);

		public RecordAction() {
			config.get("record").read(this);

			config.get("record/message").read(messageBox);

			setEnabled(false);
		}

		public void itemStateChanged(ItemEvent e) {
			setEnabled(deviceComboBox.getSelectedItem() != null);
		}

		public void actionPerformed(ActionEvent e) {

			final NotesBuilder builder = new NotesBuilder();

			try {
				MessageRecorder recorder = new MessageRecorder(
						(String) deviceComboBox.getSelectedItem()) {
					@Override
					public boolean messageRecorded(MidiMessage message) {
						byte[] datas = message.getMessage();

						builder.analyse(datas);
						return true;
					}
				};

				int result = messageBox.show(KeyboardPanel.this);

				recorder.close();

				if (result == MessageBox.OPTION_OK
						&& builder.getChannel() != -1) {
					channelSpinner.setValue(builder.getChannel() + 1);
					fromSpinner.setValue(builder.getFrom());
					toSpinner.setValue(builder.getTo());
				}
			} catch (MidiUnavailableException cannotRecord) {
			}
		}
	}
}