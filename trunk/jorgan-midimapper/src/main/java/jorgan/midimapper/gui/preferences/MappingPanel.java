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
package jorgan.midimapper.gui.preferences;

import java.awt.Component;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midimapper.mapping.Mapping;
import jorgan.swing.ComboBoxUtils;
import jorgan.swing.StandardDialog;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;

/**
 */
public class MappingPanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			MappingPanel.class);

	private JTextField nameTextField;

	private JComboBox deviceComboBox;

	private Mapping mapping;

	public MappingPanel(Mapping mapping) {
		config.read(this);

		this.mapping = mapping;

		DefinitionBuilder builder = new DefinitionBuilder(this);

		Column column = builder.column();

		column.term(config.get("name").read(new JLabel()));

		nameTextField = new JTextField();
		column.definition(nameTextField).fillHorizontal();

		column.term(config.get("device").read(new JLabel()));

		deviceComboBox = new JComboBox(ComboBoxUtils.withNull(DevicePool
				.instance().getMidiDeviceNames(Direction.IN)));
		deviceComboBox.setEditable(false);

		column.definition(deviceComboBox).fillHorizontal();

		read();
	}

	private void read() {
		nameTextField.setText(mapping.getName());
		deviceComboBox.setSelectedItem(mapping.getDevice());
	}

	private void write() {
		mapping.setName(nameTextField.getText());
		mapping.setDevice((String) deviceComboBox.getSelectedItem());
	}

	public static boolean showInDialog(Component owner, Mapping mapping) {
		StandardDialog dialog = StandardDialog.create(owner);

		dialog.addOKAction();
		dialog.addCancelAction();

		MappingPanel mappingPanel = new MappingPanel(mapping);
		dialog.setBody(mappingPanel);
		dialog.autoPosition();

		dialog.setVisible(true);

		if (dialog.wasCancelled()) {
			return false;
		} else {
			mappingPanel.write();
			return true;
		}
	}
}