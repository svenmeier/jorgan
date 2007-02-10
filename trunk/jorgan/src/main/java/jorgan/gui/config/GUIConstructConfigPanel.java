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
package jorgan.gui.config;

import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import jorgan.gui.construct.Configuration;
import jorgan.swing.GridBuilder;
import jorgan.swing.color.ColorSelector;
import jorgan.util.I18N;

/**
 * A panel for the {@link jorgan.gui.construct.Configuration}.
 */
public class GUIConstructConfigPanel extends ConfigurationPanel {

	private static I18N i18n = I18N.get(GUIConstructConfigPanel.class);

	private JLabel gridLabel = new JLabel();

	private JSpinner gridSpinner = new JSpinner(new SpinnerNumberModel(1, 1,
			256, 1));

	private JLabel colorLabel = new JLabel();

	private ColorSelector colorSelector = new ColorSelector();

	/**
	 * Constructor.
	 */
	public GUIConstructConfigPanel() {
		setName(i18n.getString("name"));
		setLayout(new GridBagLayout());

		GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

		builder.nextRow();

		gridLabel.setText(i18n.getString("gridLabel.text"));
		add(gridLabel, builder.nextColumn());
		add(gridSpinner, builder.nextColumn());

		builder.nextRow();

		colorLabel.setText(i18n.getString("colorLabel.text"));
		add(colorLabel, builder.nextColumn());
		add(colorSelector, builder.nextColumn());
	}

	public void read() {
		Configuration config = (Configuration) getConfiguration();

		gridSpinner.setValue(new Integer(config.getGrid()));

		colorSelector.setSelectedColor(config.getColor());
	}

	/**
	 * Write the configuration.
	 */
	public void write() {
		Configuration config = (Configuration) getConfiguration();

		config.setGrid(((Integer) gridSpinner.getValue()).intValue());

		config.setColor(colorSelector.getSelectedColor());
	}
}