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
package jorgan.recorder;

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.disposition.event.OrganAdapter;
import jorgan.play.event.KeyListener;
import jorgan.recorder.midi.Recorder;
import jorgan.recorder.midi.RecorderListener;
import jorgan.session.OrganSession;

public class SessionRecorder {

	private Recorder recorder;

	private OrganSession session;

	private List<Keyboard> keyboards;

	private EventListener listener = new EventListener();

	public SessionRecorder(OrganSession session) {
		this.session = session;

		this.keyboards = new ArrayList<Keyboard>(session.getOrgan()
				.getElements(Keyboard.class));

		this.recorder = new Recorder(keyboards.size());

		session.getOrgan().addOrganListener(listener);
		session.getPlay().addKeyListener(listener);
		recorder.addListener(listener);
	}

	public Recorder getRecorder() {
		return recorder;
	}

	public void dispose() {
		recorder.removeListener(listener);
		session.getPlay().removeKeyListener(listener);
		session.getOrgan().removeOrganListener(listener);

		session = null;
	}

	private ShortMessage createMessage(int status, int data1, int data2) {
		ShortMessage message = new ShortMessage();
		try {
			message.setMessage(status, data1, data2);
		} catch (InvalidMidiDataException ex) {
			throw new IllegalArgumentException(ex);
		}
		return message;
	}

	private class EventListener extends OrganAdapter implements KeyListener,
			RecorderListener {

		@Override
		public void propertyChanged(Element element, String name) {
			// record activation/deactivation
		}

		public void keyPressed(Keyboard keyboard, int pitch, int velocity) {
			if (recorder.isRecording()) {
				int track = keyboards.indexOf(keyboard);

				MidiMessage message = createMessage(ShortMessage.NOTE_ON,
						pitch, velocity);

				recorder.record(track, message);
			}
		}

		public void keyReleased(Keyboard keyboard, int pitch) {
			if (recorder.isRecording()) {
				int track = keyboards.indexOf(keyboard);

				MidiMessage message = createMessage(ShortMessage.NOTE_OFF,
						pitch, 0);

				recorder.record(track, message);
			}
		}

		public void played(int track, long millis, MidiMessage message) {
			// TODO handle invalid track
			Keyboard keyboard = keyboards.get(track);

			if (message instanceof ShortMessage) {
				ShortMessage shortMessage = (ShortMessage) message;
				if (shortMessage.getStatus() == ShortMessage.NOTE_ON) {
					session.getPlay().pressKey(keyboard,
							shortMessage.getData1(), shortMessage.getData2());
				} else if (shortMessage.getStatus() == ShortMessage.NOTE_OFF) {
					session.getPlay().releaseKey(keyboard,
							shortMessage.getData1());
				}
			}
		}

		public void recorded(int track, long millis, MidiMessage message) {
		}

		public void playing() {
		}

		public void recording() {
		}

		public void stopped() {
		}
	}
}