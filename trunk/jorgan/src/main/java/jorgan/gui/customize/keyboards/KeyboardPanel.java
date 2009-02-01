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
package jorgan.gui.customize.keyboards;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.disposition.Elements;
import jorgan.disposition.Keyboard;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.swing.layout.DefinitionBuilder;
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

	public KeyboardPanel(Keyboard keyboard) {
		this.keyboard = keyboard;

		DefinitionBuilder builder = new DefinitionBuilder(this);

		Column column = builder.column();

		column.group(new JLabel(Elements.getDisplayName(keyboard)));

		column.term(config.get("device").read(new JLabel()));
		deviceComboBox = new JComboBox(DevicePool.instance().getMidiDeviceNames(Direction.IN));
		deviceComboBox.setEditable(false);
		column.definition(deviceComboBox).fillHorizontal();

		column.term(config.get("channel").read(new JLabel()));
		channelSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 16, 1));
		column.definition(channelSpinner);

		column.term(config.get("from").read(new JLabel()));
		fromSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 127, 1));
		column.definition(fromSpinner);

		column.term(config.get("to").read(new JLabel()));
		toSpinner = new JSpinner(new SpinnerNumberModel(127, 0, 127, 1));
		column.definition(toSpinner);

		column.term(config.get("transpose").read(new JLabel()));
		transposeSpinner = new JSpinner(new SpinnerNumberModel(0, -64, 63, 1));
		column.definition(transposeSpinner);

	}
}