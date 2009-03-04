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
package jorgan.gui.customize.keyboard;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
import jorgan.midi.mpl.ProcessingException;
import jorgan.swing.BaseAction;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.Group;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;

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

		firstColumn.term(config.get("channel").read(new JLabel()));
		channelSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 15, 1));
		firstColumn.definition(channelSpinner);

		firstColumn.term(config.get("transpose").read(new JLabel()));
		transposeSpinner = new JSpinner(new SpinnerNumberModel(0, -64, 63, 1));
		firstColumn.definition(transposeSpinner);

		Column secondColumn = builder.column();

		secondColumn.term(config.get("from").read(new JLabel()));
		fromSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
		secondColumn.definition(fromSpinner);

		secondColumn.term(config.get("to").read(new JLabel()));
		toSpinner = new JSpinner(new SpinnerNumberModel(127, 0, 127, 1));
		secondColumn.definition(toSpinner);

		secondColumn.definition(new JButton(recordAction));

		read();
	}

	private void read() {
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
		public RecordAction() {
			config.get("record").read(this);
		}

		public void itemStateChanged(ItemEvent e) {
			setEnabled(deviceComboBox.getSelectedItem() != null);
		}

		public void actionPerformed(ActionEvent e) {
		}
	}
}