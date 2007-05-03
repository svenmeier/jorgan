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
import java.text.MessageFormat;

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
import bias.swing.Category;
import bias.swing.PropertyModel;

import com.sun.imageio.plugins.common.I18N;

/**
 * {@link OrganShell} category.
 */
public class OrganShellCategory extends JOrganCategory {

	private static I18N i18n = I18N.get(OrganShellCategory.class);

	private PropertyModel encoding = getModel(OrganShell.class, "encoding");

	private PropertyModel useDefaultEncoding = getModel(OrganShell.class,
			"useDefaultEncoding");

	private JRadioButton encodingDefaultRadioButton = new JRadioButton();

	private JRadioButton encodingOtherRadioButton = new JRadioButton();

	private ButtonGroup buttonGroup = new ButtonGroup();

	private JComboBox encodingComboBox = new JComboBox();

	protected JComponent createComponent() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 1.0d });

		builder.nextRow(1.0d);

		panel.add(createEncodingPanel(), builder.nextColumn()
				.gridWidthRemainder().fillHorizontal());

		builder.nextRow();
		return panel;
	}

	protected String createName() {
		return i18n.getString("name");
	}

	private JPanel createEncodingPanel() {

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(),
				i18n.getString("encodingPanel/title")));

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();

		String defaultEncoding = System.getProperty("file.encoding");
		encodingDefaultRadioButton.setText(MessageFormat.format(i18n
				.getString("encodingDefaultRadioButton/text"),
				new Object[] { defaultEncoding }));
		buttonGroup.add(encodingDefaultRadioButton);
		panel.add(encodingDefaultRadioButton, builder.nextColumn()
				.gridWidthRemainder());

		builder.nextRow();

		encodingOtherRadioButton.setText(i18n
				.getString("encodingOtherRadioButton/text"));
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