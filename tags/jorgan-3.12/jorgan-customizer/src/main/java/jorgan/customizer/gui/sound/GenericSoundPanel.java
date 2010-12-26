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
package jorgan.customizer.gui.sound;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jorgan.disposition.Elements;
import jorgan.disposition.GenericSound;
import jorgan.disposition.Keyboard;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.swing.ComboBoxUtils;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;

/**
 * A panel for a {@link Keyboard}.
 */
public class GenericSoundPanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			GenericSoundPanel.class);

	private GenericSound sound;

	private JComboBox deviceComboBox;

	public GenericSoundPanel(GenericSound sound) {
		this.sound = sound;

		DefinitionBuilder builder = new DefinitionBuilder(this);

		Column column = builder.column();

		column.group(new JLabel(Elements.getDisplayName(sound)));

		column.term(config.get("device").read(new JLabel()));
		deviceComboBox = new JComboBox();
		deviceComboBox.setEditable(false);
		column.definition(deviceComboBox).fillHorizontal();

		read();
	}

	private void read() {
		this.deviceComboBox.setModel(ComboBoxUtils
				.createModelWithNull(DevicePool.instance().getMidiDeviceNames(
						Direction.OUT)));
		this.deviceComboBox.setSelectedItem(sound.getOutput());
	}

	public void apply() {
		sound.setOutput((String) deviceComboBox.getSelectedItem());
	}
}