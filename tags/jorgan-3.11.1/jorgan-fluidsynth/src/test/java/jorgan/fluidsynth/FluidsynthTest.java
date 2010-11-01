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
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

/**
 * Test for {@link Fluidsynth}.
 */
public class FluidsynthTest extends TestCase {

	static {
		System.setProperty(Fluidsynth.JORGAN_FLUIDSYNTH_LIBRARY_PATH,
				"./target/native");
	}

	public void testLive() throws Exception {
		List<String> drivers = Fluidsynth.getAudioDrivers();
		for (String driver : drivers) {
			System.out.println(driver);
			for (String device : Fluidsynth.getAudioDevices(driver)) {
				System.out.println("  " + device);
			}
		}

		try {
			new Fluidsynth("unkown driver", 16, "foo_bar");
			fail();
		} catch (IOException expected) {
		}

		final Fluidsynth synth = new Fluidsynth();

		synth.soundFontLoad(new File(
				"./src/main/dispositions/fluidsynth-example.SF2"));

		int CONTROLLER = 11;

		synth.send(0, 176, 121, 0);
		synth.send(0, 192, 0, 0);
		synth.send(0, 176, CONTROLLER, 60);
		w();
		synth.send(0, 144, 60, 127);
		w();
		synth.send(0, 176, CONTROLLER, 60);
		w();
		synth.send(0, 128, 60, 0);
		w();

		synth.destroy();
	}

	public synchronized void w() {
		try {
			wait(2000);
		} catch (InterruptedException interrupted) {
		}
	}
}
