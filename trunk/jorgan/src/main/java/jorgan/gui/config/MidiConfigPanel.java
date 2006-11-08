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

import javax.swing.JCheckBox;

import jorgan.midi.Configuration;
import jorgan.swing.GridBuilder;

/**
 * A panel for the {@link jorgan.midi.Configuration}.
 * 
 * @see jorgan.midi.merge.MergeInput
 * @see jorgan.midi.merge.MidiMerger
 */
public class MidiConfigPanel extends ConfigurationPanel {

	private JCheckBox sendAllNotesOffCheckBox = new JCheckBox();

	/**
	 * Create this panel.
	 */
	public MidiConfigPanel() {
		setName(resources.getString("config.midi.name"));
		setLayout(new GridBagLayout());
		
		GridBuilder builder = new GridBuilder(new double[]{1.0d});

		builder.nextRow();
		
		sendAllNotesOffCheckBox.setText(resources
				.getString("config.midi.sendAllNotesOff"));
		add(sendAllNotesOffCheckBox, builder.nextColumn());
	}

	/**
	 * Read the configuration.
	 */
	public void read() {
		Configuration config = (Configuration) getConfiguration();

		sendAllNotesOffCheckBox.setSelected(config.getSendAllNotesOff());
	}

	/**
	 * Write the configuration.
	 */
	public void write() {
		Configuration config = (Configuration) getConfiguration();

		config.setSendAllNotesOff(sendAllNotesOffCheckBox.isSelected());
	}
}