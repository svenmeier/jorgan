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

import jorgan.midi.log.Configuration;
import jorgan.swing.GridBuilder;

/**
 * A panel for the {@link jorgan.midi.log.Configuration}.
 */
public class MidiLogConfigPanel extends ConfigurationPanel {

	private JLabel maxLabel = new JLabel();

	private JSpinner maxSpinner = new JSpinner(new SpinnerNumberModel(1, 1,
			Integer.MAX_VALUE, 50));

	/**
	 * Create this panel.
	 */
	public MidiLogConfigPanel() {
		setName(resources.getString("config.midi.log.name"));
		setLayout(new GridBagLayout());
		
		GridBuilder builder = new GridBuilder(new double[]{0.0d, 1.0d});
		
		builder.nextRow();

		maxLabel.setText(resources.getString("config.midi.log.max"));
		add(maxLabel, builder.nextColumn());
		add(maxSpinner, builder.nextColumn());
	}

	/**
	 * Read the configuration.
	 */
	public void read() {
		Configuration config = (Configuration) getConfiguration();

		maxSpinner.setValue(new Integer(config.getMax()));
	}

	/**
	 * Write the configuration.
	 */
	public void write() {
		Configuration config = (Configuration) getConfiguration();

		config.setMax(((Integer) maxSpinner.getValue()).intValue());
	}
}