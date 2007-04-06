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
package jorgan.midi.merge;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.spi.MidiDeviceProvider;

import jorgan.sound.midi.DeviceInfo;
import jorgan.sound.midi.merge.MidiMerger;

/**
 * The provider of <code>MidiMerger</code> devices.
 * 
 * @see jorgan.sound.midi.merge.MidiMerger
 */
public class MidiMergerProvider extends MidiDeviceProvider {

	/**
	 * The device info for this providers device.
	 */
	public static final DeviceInfo INFO = new DeviceInfo("jOrgan Midi Merger",
			"jOrgan", "Midi-Merger of jOrgan", "1.0");

	/**
	 * The device.
	 */
	private static MidiMerger midiMerger;

	public MidiDevice.Info[] getDeviceInfo() {

		return new MidiDevice.Info[] { INFO };
	}

	public MidiDevice getDevice(MidiDevice.Info info) {
		if (MidiMergerProvider.INFO == info) {
			if (midiMerger == null) {
				midiMerger = new MidiMerger(info);
			}
			
			return midiMerger;
		}

		return null;
	}
}
