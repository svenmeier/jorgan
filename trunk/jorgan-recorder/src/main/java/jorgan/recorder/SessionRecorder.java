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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
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

		this.recorder = new Recorder();
		initKeyboards();

		session.getOrgan().addOrganListener(listener);
		session.getPlay().addKeyListener(listener);
		recorder.addListener(listener);
	}

	private void initKeyboards() {
		keyboards = new ArrayList<Keyboard>(session.getOrgan().getElements(
				Keyboard.class));

		recorder.setTracks(keyboards.size());
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
		public void elementAdded(Element element) {
			if (element instanceof Keyboard) {
				initKeyboards();
			}
		}

		@Override
		public void elementRemoved(Element element) {
			if (element instanceof Keyboard) {
				initKeyboards();
			}
		}

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

		public void trackCount(int tracks) {
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
			for (int track = 0; track < keyboards.size(); track++) {
				Keyboard keyboard = keyboards.get(track);

				for (ShortMessage message : getKeyPresses(track)) {
					session.getPlay().pressKey(keyboard, message.getData1(),
							message.getData2());
				}
			}
		}

		public void recording() {
			for (int track = 0; track < keyboards.size(); track++) {
				for (ShortMessage message : getKeyPresses(track)) {
					recorder.record(track, createMessage(ShortMessage.NOTE_OFF,
							message.getData1(), 0));
				}
			}
		}

		public void stopping() {
			if (recorder.isRecording()) {
				for (int track = 0; track < keyboards.size(); track++) {
					for (ShortMessage message : getKeyPresses(track)) {
						recorder.record(track, createMessage(
								ShortMessage.NOTE_OFF, message.getData1(), 0));
					}
				}
			} else if (recorder.isPlaying()) {
				for (int track = 0; track < keyboards.size(); track++) {
					Keyboard keyboard = keyboards.get(track);

					for (ShortMessage message : getKeyPresses(track)) {
						session.getPlay().releaseKey(keyboard,
								message.getData1());
					}
				}
			}
		}

		public void stopped() {
		}
	}

	private Collection<ShortMessage> getKeyPresses(int track) {
		Map<Integer, ShortMessage> messages = new HashMap<Integer, ShortMessage>();

		for (MidiEvent event : recorder.messagesForTrackTo(track, recorder
				.getCurrentTick())) {
			if (event.getMessage() instanceof ShortMessage) {
				ShortMessage message = (ShortMessage) event.getMessage();
				if (message.getStatus() == ShortMessage.NOTE_ON) {
					messages.put(message.getData1(), message);
				} else if (message.getStatus() == ShortMessage.NOTE_OFF) {
					messages.remove(message.getData1());
				}
			}
		}

		return messages.values();
	}
}