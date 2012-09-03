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
package jorgan.customizer.gui.console;

import java.awt.BorderLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.disposition.Console;
import jorgan.disposition.Elements;
import jorgan.gui.FullScreen;
import jorgan.swing.combobox.BaseComboBoxModel;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.Group;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;

/**
 * A panel for a {@link console}.
 */
public class ConsolePanel extends JPanel {

	private static Configuration config = Configuration.getRoot().get(
			ConsolePanel.class);

	private Console console;

	private JComboBox screenComboBox;

	private JSpinner zoomSpinner;

	public ConsolePanel(Console console) {
		this.console = console;

		setLayout(new BorderLayout());

		add(new Group(new JLabel(Elements.getDisplayName(console))),
				BorderLayout.NORTH);

		JPanel definitions = new JPanel();
		add(definitions, BorderLayout.CENTER);

		DefinitionBuilder builder = new DefinitionBuilder(definitions);

		Column column = builder.column();

		column.term(config.get("screen").read(new JLabel()));
		screenComboBox = new JComboBox();
		screenComboBox.setEditable(false);
		column.definition(screenComboBox).fillHorizontal();

		column.term(config.get("zoom").read(new JLabel()));
		zoomSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.5, 5.0, 0.1));
		zoomSpinner.setEditor(new JSpinner.NumberEditor(zoomSpinner, "0.00"));
		column.definition(zoomSpinner);

		read();
	}

	private void read() {
		this.screenComboBox.setModel(new BaseComboBoxModel<String>(true,
				FullScreen.getIDs()));
		this.screenComboBox.setSelectedItem(console.getScreen());

		this.zoomSpinner.setValue(new Double(console.getZoom()));
	}

	public void apply() {
		console.setScreen((String) this.screenComboBox.getSelectedItem());
		console.setZoom(((Number) this.zoomSpinner.getValue()).floatValue());
	}
}