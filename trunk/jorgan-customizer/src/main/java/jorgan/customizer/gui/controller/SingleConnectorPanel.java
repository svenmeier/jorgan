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
package jorgan.customizer.gui.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import jorgan.customizer.builder.ContinuousBuilder;
import jorgan.customizer.builder.TupleBuilder;
import jorgan.disposition.Connector;
import jorgan.disposition.Continuous;
import jorgan.disposition.Message;
import jorgan.disposition.Continuous.Change;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.MessageRecorder;
import jorgan.midi.MessageUtils;
import jorgan.swing.BaseAction;
import jorgan.swing.ComboBoxUtils;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * A panel for a single {@link Continuous} element on a {@link Connector}.
 */
public class SingleConnectorPanel extends AbstractConnectorPanel {

	private static Configuration config = Configuration.getRoot().get(
			SingleConnectorPanel.class);

	private JComboBox deviceComboBox;

	private RecordAction recordAction = new RecordAction();

	private Connector connector;

	private Continuous continuous;

	private Message message;

	public SingleConnectorPanel(Connector connector, Continuous continuous) {
		this.connector = connector;
		this.continuous = continuous;

		DefinitionBuilder builder = new DefinitionBuilder(this);

		Column column = builder.column();

		column.term(config.get("device").read(new JLabel()));
		deviceComboBox = new JComboBox();
		deviceComboBox.setEditable(false);
		deviceComboBox.addItemListener(recordAction);
		column.definition(deviceComboBox).fillHorizontal();

		column.definition(new JButton(recordAction));

		read();
	}

	private void read() {
		this.deviceComboBox.setModel(ComboBoxUtils
				.createModelWithNull(DevicePool.instance().getMidiDeviceNames(
						Direction.IN)));
		this.deviceComboBox.setSelectedItem(connector.getInput());
	}

	public void apply() {
		connector.setInput((String) deviceComboBox.getSelectedItem());

		if (message != null) {
			continuous.removeMessages(Change.class);
			continuous.addMessage(message);
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

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				final TupleBuilder builder = new ContinuousBuilder();

				MessageRecorder recorder = new MessageRecorder(
						(String) deviceComboBox.getSelectedItem()) {
					@Override
					public boolean messageRecorded(MidiMessage message) {
						builder.analyse(MessageUtils.getDatas(message));

						return true;
					}
				};

				int result = messageBox.show(SingleConnectorPanel.this);

				recorder.close();

				if (result == MessageBox.OPTION_OK) {
					message = new Continuous.Change().change(builder.decide());
				}
			} catch (MidiUnavailableException cannotRecord) {
			}
		}
	}
}