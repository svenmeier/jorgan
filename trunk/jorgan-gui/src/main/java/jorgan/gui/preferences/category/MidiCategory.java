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
package jorgan.gui.preferences.category;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jorgan.midi.DevicePool;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.Category;
import bias.util.Property;

/**
 * Midi category.
 */
public class MidiCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			MidiCategory.class);

	private Model<Boolean> cacheDevices = getModel(new Property(
			DevicePool.class, "cache"));

	private Model<Boolean> enumerateDevices = getModel(new Property(
			DevicePool.class, "enumerate"));

	private JCheckBox cacheDevicesCheckBox = new JCheckBox();

	private JCheckBox enumerateDevicesCheckBox = new JCheckBox();

	public MidiCategory() {
		config.read(this);
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);

		Column column = builder.column();

		column.group(config.get("devices").read(new JLabel()));

		column.definition(config.get("cacheDevices").read(cacheDevicesCheckBox));

		column.definition(config.get("enumerateDevices").read(
				enumerateDevicesCheckBox));

		return panel;
	}

	@Override
	protected void read() {
		cacheDevicesCheckBox.setSelected(cacheDevices.getValue());
		enumerateDevicesCheckBox.setSelected(enumerateDevices.getValue());
	}

	@Override
	protected void write() {
		cacheDevices.setValue(cacheDevicesCheckBox.isSelected());

		enumerateDevices.setValue(enumerateDevicesCheckBox.isSelected());
	}
}