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

import javax.sound.midi.ShortMessage;

import junit.framework.TestCase;

/**
 * Test for {@link Fluidsynth}.
 */
public class FluidsynthTest extends TestCase {

	static {
		System.setProperty(Fluidsynth.JORGAN_FLUIDSYNTH_LIBRARY_PATH,
				"./target/native");
	}

	public void test() throws Exception {
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

		Fluidsynth synth = new Fluidsynth();

		synth.soundFontLoad(new File(
				"./src/main/dispositions/fluidsynth-example.SF2"));

		synth.setTuning(0, 0, "Pietro Aaron", new double[] { 0.0d, -48.0d,
				-14.0d, +20.0d, -28.0d, +6.0d, -42.0d, -8.0d, -56.0d, -22.0d,
				+12.0d, -36.0d });

		// select tuningBank
		rpn(synth, 0, 4, 0, 0);
		// select tuningProgram
		rpn(synth, 0, 3, 0, 0);

		synth.send(0, ShortMessage.PROGRAM_CHANGE, 0, 0);

		play(synth, 60);
		play(synth, 62);
		play(synth, 64);
		play(synth, 65);
		play(synth, 67);
		play(synth, 69);
		play(synth, 71);
		play(synth, 72);

		synchronized (this) {
			wait(1000);
		}

		synth.destroy();
	}

	private void rpn(Fluidsynth synth, int msb, int lsb, int msbValue,
			int lsbValue) {
		synth.send(0, ShortMessage.CONTROL_CHANGE, 101, msb);
		synth.send(0, ShortMessage.CONTROL_CHANGE, 100, lsb);
		synth.send(0, ShortMessage.CONTROL_CHANGE, 6, msbValue);
		synth.send(0, ShortMessage.CONTROL_CHANGE, 38, lsbValue);
	}

	private void play(Fluidsynth synth, int pitch) throws Exception {
		synth.send(0, ShortMessage.NOTE_ON, pitch, 100);

		synchronized (this) {
			wait(1000);
		}

		synth.send(0, ShortMessage.NOTE_OFF, pitch, 0);
	}
}
