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
import java.util.Iterator;
import java.util.List;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.midi.MessageUtils;
import jorgan.recorder.midi.Recorder;
import jorgan.recorder.midi.RecorderListener;
import jorgan.recorder.spi.TrackerRegistry;
import jorgan.recorder.tracker.AbstractTracker;
import jorgan.session.OrganSession;
import jorgan.session.SessionListener;

/**
 * A recorder of an {@link OrganSession}.
 */
public class SessionRecorder {

	public static final int STATE_STOP = 0;

	public static final int STATE_PLAY = 1;

	public static final int STATE_RECORD = 2;

	private List<SessionRecorderListener> listeners = new ArrayList<SessionRecorderListener>();

	private Recorder recorder;

	private OrganSession session;

	private Tracker[] trackers = new Tracker[0];

	private EventListener listener = new EventListener();

	private int state = STATE_STOP;

	/**
	 * Record the given session.
	 */
	public SessionRecorder(OrganSession session) {
		this.session = session;
		this.recorder = new Recorder();

		session.addListener(listener);
		session.getOrgan().addOrganListener(listener);
		recorder.addListener(listener);

		reset();
	}

	public void addListener(SessionRecorderListener listener) {
		listeners.add(listener);
	}

	public void reset() {
		int track;

		track = 0;
		List<Tracker> trackers = new ArrayList<Tracker>();
		for (Element element : session.getOrgan().getElements()) {
			Tracker tracker = TrackerRegistry.createTracker(this, track,
					element);
			if (tracker != null) {
				trackers.add(tracker);
				track++;
			}
		}

		recorder.setTracks(trackers.size());

		track = 0;
		for (Tracker tracker : trackers) {
			setTracker(track, tracker);
			track++;
		}
	}

	public OrganSession getSession() {
		return session;
	}

	public void stop() {
		if (state != STATE_STOP) {
			recorder.stop();

			state = STATE_STOP;

			fireStateChanged();
		}
	}

	public void play() {
		state = STATE_PLAY;

		recorder.start();

		fireStateChanged();
	}

	public void record() {
		state = STATE_RECORD;

		recorder.start();

		fireStateChanged();
	}

	public int getState() {
		return state;
	}

	public void first() {
		stop();

		recorder.setTime(0);
	}

	public void last() {
		stop();

		recorder.setTime(recorder.getTotalTime());
	}

	public Element getElement(int track) {
		return trackers[track].getElement();
	}

	public List<Element> getTrackableElements() {
		List<Element> elements = new ArrayList<Element>();

		for (Element element : session.getOrgan().getElements()) {
			Tracker tracker = TrackerRegistry.createTracker(this, 0, element);
			if (tracker != null) {
				tracker.destroy();

				elements.add(element);
			}
		}

		return elements;
	}

	public void setElement(int track, Element element) {
		Tracker tracker;

		if (element == null) {
			tracker = new EmptyTracker(track);
		} else {
			tracker = TrackerRegistry.createTracker(this, track, element);
			if (tracker == null) {
				throw new IllegalArgumentException("unsupported "
						+ element.getClass().getName());
			}
		}

		setTracker(track, tracker);
	}

	public void setTracker(int track, Tracker tracker) {
		recorder.stop();

		if (tracker.getElement() == null) {
			setTrackName(track, null);
		} else {
			setTrackName(track, tracker.getElement().getName());
		}

		trackers[track].destroy();

		trackers[track] = tracker;

		fireTrackerChanged(track);
	}

	public Tracker getTracker(int track) {
		return trackers[track];
	}

	private void fireTimeChanged(long millis) {
		for (SessionRecorderListener listener : listeners) {
			listener.timeChanged(millis);
		}
	}

	private void fireTrackerChanged(int track) {
		for (SessionRecorderListener listener : listeners) {
			listener.trackerChanged(track);
		}
	}

	private void fireStateChanged() {
		for (SessionRecorderListener listener : listeners) {
			listener.stateChanged(state);
		}
	}

	public void dispose() {
		recorder.removeListener(listener);
		session.getOrgan().removeOrganListener(listener);

		session = null;
	}

	private class EventListener extends OrganAdapter implements
			RecorderListener, SessionListener {

		public void constructingChanged(boolean constructing) {
			recorder.stop();
		}

		public void destroyed() {
		}

		public void timeChanged(long millis) {
			stop();
			
			fireTimeChanged(millis);
		}

		public void sequenceChanged() {
			for (int track = 0; track < trackers.length; track++) {
				trackers[track].destroy();
			}

			trackers = new Tracker[recorder.getTrackCount()];

			for (int track = 0; track < trackers.length; track++) {
				trackers[track] = createTracker(track);
			}
		}

		public void played(int track, MidiMessage message) {
		}

		public void recorded(int track, MidiMessage message) {
		}

		public void end(long millis) {
			if (getState() == STATE_PLAY) {
				stop();
			}
		}

		public void starting() {
		}

		public void stopping() {
		}

		public void stopped() {
		}
	}

	public Recorder getRecorder() {
		return recorder;
	}

	private Tracker createTracker(int track) {
		Tracker tracker;

		String name = getTrackName(track);
		if (name != null) {
			Element element = session.getOrgan().getElement(name);
			if (element != null) {
				tracker = TrackerRegistry.createTracker(SessionRecorder.this,
						track, element);
				if (tracker != null) {
					return tracker;
				}
			}
		}

		return new EmptyTracker(track);
	}

	/**
	 * Set the name of the given track.
	 * 
	 * @param track
	 * @param name
	 */
	private void setTrackName(int track, String name) {
		Iterator<MidiEvent> iterator = recorder.events(track)
				.iterator();
		while (iterator.hasNext()) {
			MidiEvent event = iterator.next();

			if (event.getMessage() instanceof MetaMessage) {
				MetaMessage message = (MetaMessage) event.getMessage();
				if (message.getType() == MessageUtils.META_TRACK_NAME) {
					iterator.remove();
				}
			}
		}

		if (name != null) {
			recorder.setTime(0);
			recorder.record(track, MessageUtils.newMetaMessage(
					MessageUtils.META_TRACK_NAME, name));
		}
	}

	/**
	 * Get the name of the given track.
	 * 
	 * @param track
	 * @return name
	 */
	private String getTrackName(int track) {
		for (MidiEvent event : recorder.events(track)) {
			if (event.getMessage() instanceof MetaMessage) {
				MetaMessage message = (MetaMessage) event.getMessage();
				if (message.getType() == MessageUtils.META_TRACK_NAME) {
					return MessageUtils.getText(message);
				}
			}
		}

		return null;
	}

	private class EmptyTracker extends AbstractTracker {

		public EmptyTracker(int track) {
			super(SessionRecorder.this, track);
		}

		@Override
		public Element getElement() {
			return null;
		}
		
		@Override
		protected boolean owns(MidiEvent event) {
			return false;
		}
	}
}