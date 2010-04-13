/*
 * jOrgan - Java Virtual Pipe Organ
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

import javax.swing.AbstractButton;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import jorgan.cli.CLI;
import jorgan.cli.Interpreter;
import jorgan.swing.button.ButtonGroup;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.Category;
import bias.util.MessageBuilder;
import bias.util.Property;

/**
 * {@link CLI} category.
 */
public class CLICategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			CLICategory.class);

	private Model encoding = getModel(new Property(CLI.class, "encoding"));

	private Model useDefaultEncoding = getModel(new Property(CLI.class,
			"useDefaultEncoding"));

	private JRadioButton encodingDefaultRadioButton = new JRadioButton();

	private JRadioButton encodingOtherRadioButton = new JRadioButton();

	private JComboBox encodingComboBox = new JComboBox();

	public CLICategory() {
		config.read(this);
	}

	@Override
	protected JComponent createComponent() {
		JPanel panel = new JPanel();

		DefinitionBuilder builder = new DefinitionBuilder(panel);
		Column column = builder.column();

		ButtonGroup encodingGroup = new ButtonGroup() {
			@Override
			protected void onSelected(AbstractButton button) {
				encodingComboBox.setEnabled(button == encodingOtherRadioButton);
			}
		};
		column.term(config.get("encoding").read(new JLabel()));

		String message = config.get("encodingDefault").read(
				new MessageBuilder())
				.build(System.getProperty("file.encoding"));
		encodingDefaultRadioButton.setText(message);
		encodingGroup.add(encodingDefaultRadioButton);
		column.definition(encodingDefaultRadioButton);

		config.get("encodingOther").read(encodingOtherRadioButton);
		encodingGroup.add(encodingOtherRadioButton);
		column.definition(encodingOtherRadioButton);

		encodingComboBox.setEditable(true);
		encodingComboBox.setModel(new DefaultComboBoxModel(Interpreter
				.getEncodings()));
		column.definition(encodingComboBox);

		return panel;
	}

	@Override
	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	@Override
	protected void read() {
		encodingDefaultRadioButton.setSelected((Boolean) useDefaultEncoding
				.getValue());
		encodingComboBox.setEnabled(encodingOtherRadioButton.isSelected());
		encodingComboBox.setSelectedItem(encoding.getValue());
	}

	@Override
	protected void write() {
		useDefaultEncoding.setValue(encodingDefaultRadioButton.isSelected());
		encoding.setValue(encodingComboBox.getSelectedItem());
	}
}