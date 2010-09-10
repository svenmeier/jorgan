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
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midimapper.MidiMapperProvider;
import jorgan.midimapper.mapping.Mapper;
import jorgan.midimapper.mapping.Mapping;
import jorgan.swing.ComboBoxUtils;
import jorgan.swing.StandardDialog;
import jorgan.swing.button.ButtonGroup;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.FlowBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import jorgan.swing.layout.FlowBuilder.Flow;
import bias.Configuration;

/**
 */
public class MappingPanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			MappingPanel.class);

	private Mapping mapping;

	private JTextField nameTextField;

	private JRadioButton inRadioButton;

	private JRadioButton outRadioButton;

	private JComboBox deviceComboBox;

	private JPanel mapperPanels;

	public MappingPanel(Mapping mapping) {
		config.read(this);

		this.mapping = mapping;

		DefinitionBuilder builder = new DefinitionBuilder(this);

		Column column = builder.column();

		column.term(config.get("name").read(new JLabel()));

		nameTextField = new JTextField();
		column.definition(nameTextField).fillHorizontal();

		column.term(config.get("device").read(new JLabel()));

		ButtonGroup directionGroup = new ButtonGroup() {
			@Override
			protected void onSelected(AbstractButton button) {
				initDevices(inRadioButton.isSelected() ? Direction.IN
						: Direction.OUT);
			}
		};
		inRadioButton = config.get("directionIn").read(new JRadioButton());
		directionGroup.add(inRadioButton);
		column.definition(inRadioButton).fillHorizontal();
		outRadioButton = config.get("directionOut").read(new JRadioButton());
		directionGroup.add(outRadioButton);
		column.definition(outRadioButton).fillHorizontal();

		deviceComboBox = new JComboBox();
		deviceComboBox.setEditable(false);

		column.definition(deviceComboBox).fillHorizontal();

		mapperPanels = new JPanel();
		JScrollPane scrollPane = new JScrollPane(mapperPanels,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(320, 160));
		column.box(scrollPane).growVertical();

		read();
	}

	private void read() {
		nameTextField.setText(mapping.getName());

		inRadioButton.setSelected(mapping.getDirection() == Direction.IN);
		outRadioButton.setSelected(mapping.getDirection() == Direction.OUT);

		initDevices(mapping.getDirection());
		deviceComboBox.setSelectedItem(mapping.getDevice());

		mapperPanels.removeAll();
		Flow flow = new FlowBuilder(mapperPanels, FlowBuilder.TOP).flow();
		for (Mapper mapper : mapping.getMappers()) {
			flow.add(new MapperPanel(mapper));
		}
	}

	private void initDevices(Direction direction) {
		Object selected = deviceComboBox.getSelectedItem();

		MidiMapperProvider provider = new MidiMapperProvider();

		List<String> devices = new ArrayList<String>();
		for (String device : DevicePool.instance()
				.getMidiDeviceNames(direction)) {
			if (!provider.isMapper(device)) {
				devices.add(device);
			}
		}
		deviceComboBox.setModel(ComboBoxUtils.createModelWithNull(devices));

		deviceComboBox.setSelectedItem(selected);
	}

	private void write() {
		mapping.setName(nameTextField.getText());
		mapping.setDirection(inRadioButton.isSelected() ? Direction.IN
				: Direction.OUT);
		mapping.setDevice((String) deviceComboBox.getSelectedItem());
	}

	public static void showInDialog(Component owner, Mapping mapping) {
		StandardDialog dialog = StandardDialog.create(owner);

		dialog.addOKAction();

		MappingPanel mappingPanel = new MappingPanel(mapping);
		dialog.setBody(mappingPanel);
		dialog.autoPosition();

		dialog.setVisible(true);

		mappingPanel.write();
	}
}