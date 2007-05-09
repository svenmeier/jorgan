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

import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import jorgan.shell.Interpreter;
import jorgan.shell.OrganShell;
import jorgan.swing.GridBuilder;
import bias.Configuration;
import bias.swing.Category;
import bias.util.MessageBuilder;
import bias.util.Property;

/**
 * {@link OrganShell} category.
 */
public class OrganShellCategory extends JOrganCategory {

	private static Configuration config = Configuration.getRoot().get(
			OrganShellCategory.class);

	private Model encoding = getModel("jorgan/shell/OrganShell", new Property(
			OrganShell.class, "encoding"));

	private Model useDefaultEncoding = getModel("jorgan/shell/OrganShell",
			new Property(OrganShell.class, "useDefaultEncoding"));

	private JRadioButton encodingDefaultRadioButton = new JRadioButton();

	private JRadioButton encodingOtherRadioButton = new JRadioButton();

	private ButtonGroup buttonGroup = new ButtonGroup();

	private JComboBox encodingComboBox = new JComboBox();

	public OrganShellCategory() {
		config.read(this);
	}

	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 1.0d });

		builder.nextRow(1.0d);

		panel.add(createEncodingPanel(), builder.nextColumn()
				.gridWidthRemainder().fillHorizontal());

		builder.nextRow();
		return panel;
	}

	private JPanel createEncodingPanel() {

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(config.get("encodingPanel").read(
				new TitledBorder(BorderFactory.createEtchedBorder())));

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();

		String message = config.get("encodingDefault").read(
				new MessageBuilder())
				.build(System.getProperty("file.encoding"));
		encodingDefaultRadioButton.setText(message);
		buttonGroup.add(encodingDefaultRadioButton);
		panel.add(encodingDefaultRadioButton, builder.nextColumn()
				.gridWidthRemainder());

		builder.nextRow();

		config.get("encodingOtherRadioButton").read(encodingOtherRadioButton);
		buttonGroup.add(encodingOtherRadioButton);
		encodingOtherRadioButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				encodingComboBox.setEnabled(encodingOtherRadioButton
						.isSelected());
			}
		});
		panel.add(encodingOtherRadioButton, builder.nextColumn());

		encodingComboBox.setEditable(true);
		encodingComboBox.setModel(new DefaultComboBoxModel(Interpreter
				.getEncodings()));
		panel.add(encodingComboBox, builder.nextColumn());

		return panel;
	}

	public Class<? extends Category> getParentCategory() {
		return AppCategory.class;
	}

	protected void read() {
		encodingDefaultRadioButton.setSelected((Boolean) useDefaultEncoding
				.getValue());
		encodingComboBox.setEnabled(encodingOtherRadioButton.isSelected());
		encodingComboBox.setSelectedItem(encoding.getValue());
	}

	protected void write() {
		useDefaultEncoding.setValue(encodingDefaultRadioButton.isSelected());
		encoding.setValue(encodingComboBox.getSelectedItem());
	}
}