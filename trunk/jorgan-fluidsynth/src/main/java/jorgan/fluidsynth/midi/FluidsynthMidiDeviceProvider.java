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
package jorgan.fluidsynth.midi;

import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.midi.spi.MidiDeviceProvider;

import jorgan.fluidsynth.Fluidsynth;

/**
 * Java Wrapper for a Fluidsynth.
 */
public class FluidsynthMidiDeviceProvider extends MidiDeviceProvider {

	private static Map<Info, FluidsynthMidiDevice> devices = new HashMap<Info, FluidsynthMidiDevice>();

	static {
		// TODO remove test
		try {
			Fluidsynth synth1 = addDevice();
			synth1.soundFontLoad("/home/sven/Desktop/Jeux14.SF2");
			
			Fluidsynth synth2 = addDevice();
			synth2.soundFontLoad("/home/sven/Desktop/Jeux14.SF2");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static Fluidsynth addDevice() {
		Info info = new Info("Fluidsynth " + devices.size(),
				"jOrgan", "Fluidsynth of jOrgan", "1.0") {
		};

		Fluidsynth synth = new Fluidsynth();
		
		devices.put(info, new FluidsynthMidiDevice(info, synth));
		
		return synth;
	}

	@Override
	public Info[] getDeviceInfo() {

		Info[] infos = new Info[devices.size()];

		int i = 0;
		for (Info info : devices.keySet()) {
			infos[i++] = info;
		}

		return infos;
	}

	@Override
	public MidiDevice getDevice(MidiDevice.Info info) {
		return devices.get(info);
	}
}
