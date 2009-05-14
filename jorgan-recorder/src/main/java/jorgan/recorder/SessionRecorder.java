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

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.disposition.event.OrganAdapter;
import jorgan.midi.MessageUtils;
import jorgan.play.OrganPlay;
import jorgan.play.event.KeyListener;
import jorgan.recorder.midi.Recorder;
import jorgan.recorder.midi.RecorderListener;
import jorgan.session.OrganSession;
import jorgan.session.SessionListener;

/**
 * A recorder of an {@link OrganSession}.
 */
public class SessionRecorder {

	private Recorder recorder;

	private OrganSession session;

	private Tracker[] trackers = new Tracker[0];

	private EventListener listener = new EventListener();

	/**
	 * Record the given session.
	 */
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
		List<Element> elements = getElements();

		recorder.setTracks(elements.size());

		int track = 0;
		for (Element element : elements) {
			setElement(track, element);
			track++;
		}
	}

	public List<Element> getElements() {
		List<Element> elements = new ArrayList<Element>();

		elements.addAll(session.getOrgan().getElements(Keyboard.class));

		return elements;
	}

	public OrganSession getSession() {
		return session;
	}

	public void stop() {
		recorder.stop();
	}

	public Element getElement(int track) {
		return trackers[track].getElement();
	}

	public void setElement(int track, Element element) {
		recorder.stop();

		if (element == null) {
			trackers[track] = new EmptyTracker(track);
		} else {
			if (element instanceof Keyboard) {
				trackers[track] = new KeyboardTracker(track, (Keyboard) element);
			} else {
				throw new IllegalArgumentException("unsupported "
						+ element.getClass().getName());
			}
		}
	}

	public void dispose() {
		recorder.removeListener(listener);
		session.getPlay().removeKeyListener(listener);
		session.getOrgan().removeOrganListener(listener);

		session = null;
	}

	private class EventListener extends OrganAdapter implements KeyListener,
			RecorderListener, SessionListener {

		public void constructingChanged(boolean constructing) {
			recorder.stop();
		}

		public void destroyed() {
		}

		public void keyPressed(Keyboard keyboard, int pitch, int velocity) {
			if (recorder.isRecording()) {
				for (int track = 0; track < trackers.length; track++) {
					trackers[track].keyPressed(keyboard, pitch, velocity);
				}
			}
		}

		public void keyReleased(Keyboard keyboard, int pitch) {
			if (recorder.isRecording()) {
				for (int track = 0; track < trackers.length; track++) {
					trackers[track].keyReleased(keyboard, pitch);
				}
			}
		}

		public void timeChanged(long millis) {
		}

		public void sequenceChanged() {
			trackers = new Tracker[recorder.getTrackCount()];
			for (int track = 0; track < trackers.length; track++) {
				trackers[track] = new EmptyTracker(track);
			}
		}

		public void played(int track, long millis, MidiMessage message) {
			if (message instanceof ShortMessage) {
				trackers[track].played((ShortMessage) message);
			}
		}

		public void recorded(int track, long millis, MidiMessage message) {
		}

		public void playing() {
			for (Tracker tracker : trackers) {
				tracker.playing();
			}
		}

		public void recording() {
			for (Tracker tracker : trackers) {
				tracker.recording();
			}
		}

		public void stopping() {
			if (recorder.isRecording()) {
				for (Tracker tracker : trackers) {
					tracker.recordStopping();
				}
			} else if (recorder.isPlaying()) {
				for (Tracker tracker : trackers) {
					tracker.playStopping();
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

	/**
	 * A tracker of a {@link Recorder}'s track.
	 */
	private abstract class Tracker {

		public final int track;

		protected Tracker(int track) {
			this.track = track;
		}

		public abstract Element getElement();

		public void playing() {
		}

		public void recording() {
		}

		public void recordStopping() {
		}

		public void playStopping() {
		}

		public void played(ShortMessage message) {
		}

		public void keyReleased(Keyboard keyboard, int pitch) {
		}

		public void keyPressed(Keyboard keyboard, int pitch, int velocity) {
		}
	}

	private class EmptyTracker extends Tracker {

		public EmptyTracker(int track) {
			super(track);
		}

		@Override
		public Element getElement() {
			return null;
		}
	}

	private class KeyboardTracker extends Tracker {

		private Keyboard keyboard;

		public KeyboardTracker(int track, Keyboard keyboard) {
			super(track);

			this.keyboard = keyboard;
		}

		@Override
		public Element getElement() {
			return keyboard;
		}

		@Override
		public void keyPressed(Keyboard keyboard, int pitch, int velocity) {
			recorder.record(track, MessageUtils.newMessage(
					ShortMessage.NOTE_ON, pitch, velocity));
		}

		@Override
		public void keyReleased(Keyboard keyboard, int pitch) {
			recorder.record(track, MessageUtils.newMessage(
					ShortMessage.NOTE_OFF, pitch, 0));
		}

		@Override
		public void played(ShortMessage message) {
			if (message.getStatus() == ShortMessage.NOTE_ON) {
				session.getPlay().pressKey(keyboard, message.getData1(),
						message.getData2());
			} else if (message.getStatus() == ShortMessage.NOTE_OFF) {
				session.getPlay().releaseKey(keyboard, message.getData1());
			}
		}

		/**
		 * {@link OrganPlay#pressKey(Keyboard, int, int)} for all currently
		 * pressed keys.
		 */
		@Override
		public void playing() {
			for (ShortMessage message : getKeyPresses(track)) {
				session.getPlay().pressKey(keyboard, message.getData1(),
						message.getData2());
			}
		}

		/**
		 * {@link Recorder#record(int, MidiMessage) NOTE_OFF for all currently pressed
		 * keys.
		 */
		@Override
		public void recording() {
			for (ShortMessage message : getKeyPresses(track)) {
				recorder.record(track, MessageUtils.newMessage(
						ShortMessage.NOTE_OFF, message.getData1(), 0));
			}
		}

		/**
		 * {@link Recorder#record(int, MidiMessage) NOTE_OFF for all currently pressed
		 * keys.
		 */
		@Override
		public void recordStopping() {
			for (ShortMessage message : getKeyPresses(track)) {
				recorder.record(track, MessageUtils.newMessage(
						ShortMessage.NOTE_OFF, message.getData1(), 0));
			}
		}

		/**
		 * {@link OrganPlay#releaseKey(Keyboard, int, int)} for all currently
		 * pressed keys.
		 */
		@Override
		public void playStopping() {
			for (ShortMessage message : getKeyPresses(track)) {
				session.getPlay().releaseKey(keyboard, message.getData1());
			}
		}
	}
}