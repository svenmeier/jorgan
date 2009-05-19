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

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.disposition.event.OrganAdapter;
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
		int track = 0;
		
		List<Tracker> trackers = new ArrayList<Tracker>();
		for (Element element : session.getOrgan().getElements()) {
			Tracker tracker = createTracker(track, element);
			if (tracker != null) {
				trackers.add(tracker);
				track++;
			}
		}

		recorder.setTracks(trackers.size());

		for (Tracker tracker : trackers) {
			this.trackers[tracker.getTrack()] = tracker;
		}
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

	public List<Element> getTrackableElements() {
		List<Element> elements = new ArrayList<Element>();
		
		for (Element element : session.getOrgan().getElements()) {
			if (createTracker(0, element) != null) {
				elements.add(element);
			}
		}
		
		return elements;
	}
	
	public void setElement(int track, Element element) {
		recorder.stop();

		if (element == null) {
			trackers[track] = new EmptyTracker(track);
		} else {
			Tracker tracker = createTracker(track, element);
			if (tracker == null) {
				throw new IllegalArgumentException("unsupported "
						+ element.getClass().getName());
			}
			trackers[track] = tracker;
		}
	}

	private Tracker createTracker(int track, Element element) {
		if (element instanceof Keyboard) {
			return new KeyboardTracker(this, track, (Keyboard) element);
		} else if (element instanceof Console) {
			return new ConsoleTracker(this, track, (Console) element);
		} else {
			return null;
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

	public Recorder getRecorder() {
		return recorder;
	}

	private class EmptyTracker extends Tracker {

		public EmptyTracker(int track) {
			super(SessionRecorder.this, track);
		}

		@Override
		public Element getElement() {
			return null;
		}
	}
}