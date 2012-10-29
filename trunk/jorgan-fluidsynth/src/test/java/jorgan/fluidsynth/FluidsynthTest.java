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
		System.setProperty(Fluidsynth.LIBRARY_PATH,
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

		final Fluidsynth synth = new Fluidsynth("with pulseaudio", 16,
				"pulseaudio");

		synth.soundFontLoad(new File(
				"./src/main/dispositions/fluidsynth-example.SF2"));

		note(synth, 0, 60);
		note(synth, 1, 62);
		note(synth, 2, 64);
		note(synth, 3, 65);
		note(synth, 4, 67);
		note(synth, 5, 69);
		note(synth, 6, 71);
		note(synth, 7, 72);
		note(synth, 8, 74);
		note(synth, 9, 76);
		note(synth, 10, 77);
		note(synth, 11, 79);
		note(synth, 12, 81);
		note(synth, 13, 82);
		note(synth, 14, 84);
		note(synth, 15, 86);

		synth.destroy();
	}

	private void note(Fluidsynth synth, int channel, int note) {
		System.out.println("channel " + channel);
		synth.send(channel, 176, 0, 0);
		synth.send(channel, 192, 0, 0);
		synth.send(channel, 144, note, 127);
		w();
		synth.send(channel, 128, note, 0);
	}

	public synchronized void w() {
		try {
			wait(2000);
		} catch (InterruptedException interrupted) {
		}
	}
}
