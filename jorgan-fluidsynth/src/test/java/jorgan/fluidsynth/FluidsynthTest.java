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

import javax.sound.midi.MidiEvent;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import jorgan.midi.MessageUtils;
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

		Recording recording = new Recording() {
			@Override
			public synchronized void w() {
				try {
					wait(2000);
				} catch (InterruptedException interrupted) {
				}
			}

			@Override
			public void on(int cc, int data1, int data2) {
				synth.send(0, cc, data1, data2);
			}
		};

		script(recording);

		synth.destroy();
	}

	public void testSequence() throws Exception {
		final Sequence sequence = new Sequence(Sequence.PPQ, 1);

		Recording recording = new Recording() {
			private Track track = sequence.createTrack();

			private int tick = 0;

			@Override
			public void w() {
				tick += 4;
			}

			@Override
			public void on(int cc, int data1, int data2) {
				track.add(new MidiEvent(MessageUtils.newMessage(cc, data1,
						data2), tick));
			}
		};

		script(recording);

		// MidiSystem.write(sequence, 1, new File("test.mid"));
	}

	private void script(Recording recording) throws Exception {
		int CONTROLLER = 11;

		recording.on(176, 121, 0);
		recording.on(192, 0, 0);
		recording.on(176, CONTROLLER, 60);
		recording.w();
		recording.on(144, 60, 127);
		recording.w();
		recording.on(176, CONTROLLER, 60);
		recording.w();
		recording.on(128, 60, 0);
		recording.w();
	}

	private interface Recording {
		public void w();

		public void on(int cc, int d1, int d2);
	}
}
