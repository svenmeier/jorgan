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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import jorgan.disposition.Element;
import jorgan.disposition.Elements;
import jorgan.disposition.Keyboard;
import jorgan.disposition.event.OrganAdapter;
import jorgan.play.event.KeyListener;
import jorgan.recorder.midi.Recorder;
import jorgan.recorder.midi.RecorderListener;
import jorgan.session.OrganSession;
import jorgan.session.SessionListener;

public class SessionRecorder {

	private Recorder recorder;

	private OrganSession session;

	private Keyboard[] track2keyboard = new Keyboard[0];

	private EventListener listener = new EventListener();

	public SessionRecorder(OrganSession session) {
		this.session = session;
		this.recorder = new Recorder();

		session.addListener(listener);
		session.getOrgan().addOrganListener(listener);
		session.getPlay().addKeyListener(listener);
		recorder.addListener(listener);

		reset();
	}

	public void reset() {
		Set<Keyboard> keyboards = getKeyboards();

		recorder.setTracks(keyboards.size());

		int track = 0;
		for (Keyboard keyboard : keyboards) {
			track2keyboard[track] = keyboard;
			track++;
		}
	}

	private Set<Keyboard> getKeyboards() {
		return session.getOrgan()
				.getElements(Keyboard.class);
	}

	public OrganSession getSession() {
		return session;
	}

	public void stop() {
		recorder.stop();
	}

	public String getTitle(int track) {
		if (track < track2keyboard.length) {
			if (track2keyboard[track] != null) {
				return Elements.getDisplayName(track2keyboard[track]);
			}
		}
		return "Track " + track;
	}

	private void checkTracks() {
		Keyboard[] old = this.track2keyboard;

		this.track2keyboard = new Keyboard[recorder.getTrackCount()];
		System.arraycopy(old, 0, this.track2keyboard, 0, Math.min(
				old.length, this.track2keyboard.length));
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
			RecorderListener, SessionListener {

		public void constructingChanged(boolean constructing) {
			recorder.stop();
		}

		public void destroyed() {
		}

		@Override
		public void elementAdded(Element element) {
		}

		@Override
		public void elementRemoved(Element element) {
		}

		@Override
		public void propertyChanged(Element element, String name) {
			// record activation/deactivation
		}

		public void keyPressed(Keyboard keyboard, int pitch, int velocity) {
			if (recorder.isRecording()) {
				MidiMessage message = createMessage(ShortMessage.NOTE_ON,
						pitch, velocity);

				for (int track = 0; track < track2keyboard.length; track++) {
					if (track2keyboard[track] == keyboard) {
						recorder.record(track, message);
					}
				}
			}
		}

		public void keyReleased(Keyboard keyboard, int pitch) {
			if (recorder.isRecording()) {
				MidiMessage message = createMessage(ShortMessage.NOTE_OFF,
						pitch, 0);

				for (int track = 0; track < track2keyboard.length; track++) {
					if (track2keyboard[track] == keyboard) {
						recorder.record(track, message);
					}
				}
			}
		}

		public void timeChanged(long millis) {
		}

		public void tracksChanged(int tracks) {
			checkTracks();
		}

		public void played(int track, long millis, MidiMessage message) {
			Keyboard keyboard = track2keyboard[track];

			if (keyboard == null) {
				return;
			}

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
			for (int track = 0; track < track2keyboard.length; track++) {
				Keyboard keyboard = track2keyboard[track];

				for (ShortMessage message : getKeyPresses(track)) {
					session.getPlay().pressKey(keyboard, message.getData1(),
							message.getData2());
				}
			}
		}

		public void recording() {
			for (int track = 0; track < track2keyboard.length; track++) {
				for (ShortMessage message : getKeyPresses(track)) {
					recorder.record(track, createMessage(ShortMessage.NOTE_OFF,
							message.getData1(), 0));
				}
			}
		}

		public void stopping() {
			if (recorder.isRecording()) {
				for (int track = 0; track < track2keyboard.length; track++) {
					for (ShortMessage message : getKeyPresses(track)) {
						recorder.record(track, createMessage(
								ShortMessage.NOTE_OFF, message.getData1(), 0));
					}
				}
			} else if (recorder.isPlaying()) {
				for (int track = 0; track < track2keyboard.length; track++) {
					Keyboard keyboard = track2keyboard[track];

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

	public Recorder getRecorder() {
		return recorder;
	}
}