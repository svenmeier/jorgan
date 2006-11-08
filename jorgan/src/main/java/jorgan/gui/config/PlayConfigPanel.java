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

import jorgan.play.Configuration;
import jorgan.swing.GridBuilder;

/**
 * A panel for the {@link jorgan.play.Configuration}.
 */
public class PlayConfigPanel extends ConfigurationPanel {

	private JCheckBox warnDeviceCheckBox = new JCheckBox();

	private JCheckBox warnMessageCheckBox = new JCheckBox();

	private JCheckBox releaseDevicesCheckBox = new JCheckBox();

	public PlayConfigPanel() {
		setName(resources.getString("config.play.name"));
		setLayout(new GridBagLayout());
		
		GridBuilder builder = new GridBuilder(new double[]{1.0d});

		builder.nextRow();
		
		warnDeviceCheckBox.setText(resources
				.getString("config.play.warn.device"));
		add(warnDeviceCheckBox, builder.nextColumn());

		builder.nextRow();

		warnMessageCheckBox.setText(resources
				.getString("config.play.warn.message"));
		add(warnMessageCheckBox, builder.nextColumn());

		builder.nextRow();

		releaseDevicesCheckBox.setText(resources
				.getString("config.play.releaseDevicesWhenDeactivated"));
		add(releaseDevicesCheckBox, builder.nextColumn());
	}

	/**
	 * Read the configuration.
	 */
	public void read() {
		Configuration config = (Configuration) getConfiguration();

		warnDeviceCheckBox.setSelected(config.getWarnWithoutDevice());
		warnMessageCheckBox.setSelected(config.getWarnWithoutMessage());

		releaseDevicesCheckBox.setSelected(config
				.getReleaseDevicesWhenDeactivated());
	}

	/**
	 * Write the configuration.
	 */
	public void write() {
		Configuration config = (Configuration) getConfiguration();

		config.setWarnWithoutDevice(warnDeviceCheckBox.isSelected());
		config.setWarnWithoutMessage(warnMessageCheckBox.isSelected());

		config.setReleaseDevicesWhenDeactivated(releaseDevicesCheckBox
				.isSelected());
	}
}