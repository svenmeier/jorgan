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
package jorgan.fluidsynth;

import java.io.File;
import java.util.List;

import javax.sound.midi.ShortMessage;

import junit.framework.TestCase;

/**
 * Test for {@link Fluidsynth}.
 */
public class FluidsynthTest extends TestCase {

	static {
		System.setProperty(Fluidsynth.JORGAN_FLUIDSYNTH_LIBRARY_PATH, "./target/native");
	}
	
	public void test() throws Exception {
		List<String> drivers = Fluidsynth.getAudioDrivers();
		for (String driver : drivers) {
			System.out.println(driver);
			for (String device : Fluidsynth.getAudioDevices(driver)) {
				System.out.println("  " + device);
			}
		}
		
		Fluidsynth synth = new Fluidsynth("test", 16, "alsa");
		
		synth.soundFontLoad(new File("./src/main/dispositions/fluidsynth-example.SF2"));
		synth.send(0, ShortMessage.PROGRAM_CHANGE, 0, 0);
		synth.send(0, ShortMessage.NOTE_ON, 64, 100);
		
		synchronized (this) {
			wait(1000);
		}
		
		synth.send(0, ShortMessage.NOTE_OFF, 64, 0);

		synchronized (this) {
			wait(1000);
		}
		
		synth.destroy();
	}
}
