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
package jorgan.recorder.midi;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import jorgan.recorder.midi.Recorder;
import junit.framework.TestCase;

public class RecorderTest extends TestCase {

	public void test() throws Exception {
		Recorder recorder = new Recorder(1);
		recorder.addListener(new RecorderAdapter() {
			
			public void played(int track, long millis, MidiMessage message) {
				trace(track, millis, message);
			}

			public void recorded(int track, long millis, MidiMessage message) {
				trace(track, millis, message);
			}

			public void playing() {
				System.out.println("Playing");
			}

			public void recording() {
				System.out.println("Recording");
			}

			public void stopped() {
				System.out.println("Stopped");
			}
		});

		recorder.record();

		keyPressed(recorder, 60, 100);

		Thread.sleep(1000);

		keyReleased(recorder, 60);
		keyPressed(recorder, 62, 100);

		recorder.stop();

		Thread.sleep(2000);
		
		recorder.record();

		Thread.sleep(1000);

		keyReleased(recorder, 62);
		keyPressed(recorder, 64, 100);

		Thread.sleep(1000);

		keyReleased(recorder, 64);

		Thread.sleep(2000);

		recorder.stop();

		OutputStream output = new FileOutputStream("recorder.mid");
		try {
			recorder.save(output);
		} finally {
			output.close();
		}

		InputStream input = new FileInputStream("recorder.mid");
		try {
			recorder.load(input);
		} finally {
			input.close();
		}

		recorder.play();
		
		Thread.sleep(recorder.getTotalTime() + 1000);

		recorder.play();
		
		Thread.sleep(2000);

		recorder.first();
		
		recorder.play();

		Thread.sleep(2500);
		
		recorder.stop();
	}

	private void keyPressed(Recorder recorder, int pitch, int velocity) {
		ShortMessage message = new ShortMessage();
		try {
			message.setMessage(144, pitch, velocity);
		} catch (InvalidMidiDataException ex) {
			throw new IllegalArgumentException(ex);
		}
		recorder.record(0, message);
	}

	private void keyReleased(Recorder recorder, int pitch) {
		ShortMessage message = new ShortMessage();
		try {
			message.setMessage(128, pitch, 0);
		} catch (InvalidMidiDataException ex) {
			throw new IllegalArgumentException(ex);
		}
		recorder.record(0, message);
	}

	private void trace(int track, long millis, MidiMessage message) {
		System.out.print("[" + track + "] ");
		System.out.print(millis + " :");
		
		byte[] bytes = message.getMessage();
		for (int b = 0; b < message.getLength(); b++) {
			System.out.print(' ');
			System.out.print(bytes[b] & 0xFF);
		}
		System.out.println();
	}
}