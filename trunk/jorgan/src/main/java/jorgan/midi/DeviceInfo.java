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
package jorgan.midi;

import javax.sound.midi.MidiDevice;

/**
 * Info of a device.
 */
public class DeviceInfo extends MidiDevice.Info {

	/**
	 * Constructs a device info object.
	 * 
	 * @param name
	 *            the name of the device
	 * @param vendor
	 *            the name of the company who provides the device
	 * @param description
	 *            a description of the device
	 * @param version
	 *            version information for the device
	 */
	public DeviceInfo(String name, String vendor, String description,
			String version) {
		super(name, vendor, description, version);
	}
}
