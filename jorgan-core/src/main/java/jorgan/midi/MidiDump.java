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
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiDevice.Info;

/**
 */
public class MidiDump {
	public static void main(String[] args) {
		for (Info info : MidiSystem.getMidiDeviceInfo()) {
			System.out.println(info.getName());
			System.out.println("  " + info.getDescription());
			System.out.println("  " + info.getVendor());
			System.out.println("  " + info.getVersion());

			try {
				MidiDevice device = MidiSystem.getMidiDevice(info);

				System.out.println("  Receivers #" + device.getMaxReceivers());
				System.out.println("  Transmitters #"
						+ device.getMaxTransmitters());
			} catch (MidiUnavailableException ex) {
				System.out.println("  not available");
			}
		}
	}
}