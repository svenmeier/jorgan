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
package jorgan.gui.config;

import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.MessageFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.TitledBorder;

import jorgan.shell.Configuration;
import jorgan.shell.Interpreter;
import jorgan.swing.GridBuilder;

/**
 * A panel for the {@link jorgan.shell.Configuration}.
 */
public class ShellConfigPanel extends ConfigurationPanel {

	private JPanel encodingPanel = new JPanel();

	private JRadioButton encodingDefaultRadioButton = new JRadioButton();

	private JRadioButton encodingOtherRadioButton = new JRadioButton();

	private ButtonGroup buttonGroup = new ButtonGroup();

	private JComboBox encodingComboBox = new JComboBox();

	/**
	 * Constructor.
	 */
	public ShellConfigPanel() {
		setName(resources.getString("config.shell.name"));
		setLayout(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 1.0d });

		builder.nextRow(1.0d);

		encodingPanel.setLayout(new GridBagLayout());
		encodingPanel.setBorder(new TitledBorder(BorderFactory
				.createEtchedBorder(), resources
				.getString("config.shell.encoding")));
		add(encodingPanel, builder.nextColumn().gridWidthRemainder()
				.fillHorizontal());

		GridBuilder encodingBuilder = new GridBuilder(
				new double[] { 0.0d, 1.0d });

		encodingBuilder.nextRow();

		String defaultEncoding = System.getProperty("file.encoding");
		encodingDefaultRadioButton.setText(MessageFormat.format(resources
				.getString("config.shell.encodingDefault"),
				new Object[] { defaultEncoding }));
		buttonGroup.add(encodingDefaultRadioButton);
		encodingPanel.add(encodingDefaultRadioButton, encodingBuilder
				.nextColumn().gridWidthRemainder());

		encodingBuilder.nextRow();

		encodingOtherRadioButton.setText(resources
				.getString("config.shell.encodingOther"));
		buttonGroup.add(encodingOtherRadioButton);
		encodingPanel.add(encodingOtherRadioButton, encodingBuilder
				.nextColumn());

		encodingComboBox.setEditable(true);
		encodingComboBox.setModel(new DefaultComboBoxModel(Interpreter
				.getEncodings()));
		encodingOtherRadioButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				encodingComboBox.setEnabled(encodingOtherRadioButton
						.isSelected());
			}
		});
		encodingPanel.add(encodingComboBox, encodingBuilder.nextColumn());
	}

	public void read() {
		Configuration config = (Configuration) getConfiguration();

		encodingDefaultRadioButton.setSelected(config.getUseDefaultEncoding());
		encodingOtherRadioButton.setSelected(!config.getUseDefaultEncoding());
		encodingComboBox.setEnabled(!config.getUseDefaultEncoding());
		encodingComboBox.setSelectedItem(config.getEncoding());
	}

	/**
	 * Write the configuration.
	 */
	public void write() {
		Configuration config = (Configuration) getConfiguration();

		config.setUseDefaultEncoding(encodingDefaultRadioButton.isSelected());
		config.setEncoding((String) encodingComboBox.getSelectedItem());
	}
}