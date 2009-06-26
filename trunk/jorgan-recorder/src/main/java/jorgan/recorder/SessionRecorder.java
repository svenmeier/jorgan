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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.midi.MessageUtils;
import jorgan.recorder.midi.Recorder;
import jorgan.recorder.spi.TrackerRegistry;
import jorgan.recorder.tracker.AbstractTracker;
import jorgan.session.OrganSession;
import jorgan.session.SessionListener;

/**
 * A recorder of an {@link OrganSession}.
 */
public class SessionRecorder {

	private static final float DEFAULT_DIVISION = Sequence.PPQ;

	private static final int DEFAULT_RESOLUTION = 50;

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
		session.addListener(listener);
		session.getOrgan().addOrganListener(listener);

		this.recorder = new InternalRecorder();

		reset();
	}

	public void addListener(SessionRecorderListener listener) {
		listeners.add(listener);
	}

	public void reset() {
		stop();

		for (int track = 0; track < this.trackers.length; track++) {
			this.trackers[track].destroy();
		}
		this.trackers = null;

		List<Tracker> trackers = new ArrayList<Tracker>();
		for (Element element : session.getOrgan().getElements(Element.class)) {
			if ("".equals(element.getName())) {
				continue;
			}
			
			Tracker tracker = TrackerRegistry.createTracker(this, trackers
					.size(), element);
			if (tracker != null) {
				trackers.add(tracker);
			}
		}
		if (trackers.isEmpty()) {
			trackers.add(new EmptyTracker(0));
		}

		recorder.setSequence(createSequence(trackers.size()));

		this.trackers = new Tracker[trackers.size()];
		for (int track = 0; track < this.trackers.length; track++) {
			setTracker(track, trackers.get(track));
		}

		fireTrackersChanged();
	}

	public void setSequence(Sequence sequence) {
		stop();

		for (int track = 0; track < trackers.length; track++) {
			trackers[track].destroy();
		}
		trackers = null;

		recorder.setSequence(sequence);

		trackers = new Tracker[recorder.getTrackCount()];
		for (int track = 0; track < trackers.length; track++) {
			setTracker(track, createTracker(track));
		}

		fireTrackersChanged();
	}

	public Sequence getSequence() {
		return recorder.getSequence();
	}

	private static Sequence createSequence(int tracks) {
		try {
			return new Sequence(DEFAULT_DIVISION, DEFAULT_RESOLUTION, tracks);
		} catch (InvalidMidiDataException ex) {
			throw new Error(ex);
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
		if (state != STATE_PLAY) {
			state = STATE_PLAY;

			recorder.start();

			fireStateChanged();
		}
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

		recorder.first();
	}

	public void last() {
		stop();

		recorder.last();
	}

	public Element getElement(int track) {
		return trackers[track].getElement();
	}

	public List<Element> getTrackableElements() {
		List<Element> elements = new ArrayList<Element>();

		for (Element element : session.getOrgan().getElements(Element.class)) {
			Tracker tracker = TrackerRegistry.createTracker(this, 0, element);
			if (tracker != null) {
				tracker.destroy();

				elements.add(element);
			}
		}

		return elements;
	}

	public void setTime(long time) {
		stop();

		recorder.setTime(time);

		fireTimeChanged(getTime());
	}

	public long getTime() {
		return recorder.getTime();
	}

	public long getTotalTime() {
		return recorder.getTotalTime();
	}

	public void setElement(int track, Element element) {
		stop();

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

		fireTrackersChanged();
	}

	private void setTracker(int track, Tracker tracker) {
		if (tracker.getElement() == null) {
			setTrackName(track, null);
		} else {
			setTrackName(track, tracker.getElement().getName());
		}

		if (trackers[track] != null) {
			trackers[track].destroy();
		}

		trackers[track] = tracker;
	}

	public int getTrackerCount() {
		return trackers.length;
	}

	public Tracker getTracker(int track) {
		return trackers[track];
	}

	private void fireTimeChanged(long millis) {
		for (SessionRecorderListener listener : listeners) {
			listener.timeChanged(millis);
		}
	}

	private void fireTrackersChanged() {
		for (SessionRecorderListener listener : listeners) {
			listener.trackersChanged();
		}
	}

	private void fireStateChanged() {
		for (SessionRecorderListener listener : listeners) {
			listener.stateChanged(state);
		}
	}

	public void dispose() {
		session.removeListener(listener);
		session.getOrgan().removeOrganListener(listener);

		session = null;
	}

	private class InternalRecorder extends Recorder {

		private InternalRecorder() {
			super(createSequence(0));
		}

		@Override
		protected void onPlayed(int track, MidiMessage message) {
			Tracker tracker = trackers[track];
			if (tracker.plays()) {
				tracker.onPlayed(message);
			}
		}

		@Override
		protected void onLast() {
			if (getState() == STATE_PLAY) {
				SessionRecorder.this.stop();
			}
		}

		@Override
		protected void onStarting() {
			for (Tracker tracker : trackers) {
				if (state == STATE_RECORD) {
					if (tracker.records()) {
						tracker.onRecordStarting();
					} else if (tracker.plays()) {
						tracker.onPlayStarting();
					}
				} else if (state == STATE_PLAY) {
					if (tracker.plays()) {
						tracker.onPlayStarting();
					}
				}
			}
		}

		@Override
		protected void onStopping() {
			for (Tracker tracker : trackers) {
				if (state == STATE_RECORD) {
					if (tracker.records()) {
						tracker.onRecordStopping();
					} else if (tracker.plays()) {
						tracker.onPlayStopping();
					}
				} else if (state == STATE_PLAY) {
					if (tracker.plays()) {
						tracker.onPlayStopping();
					}
				}
			}
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
		Iterator<MidiEvent> iterator = recorder.events(track).iterator();
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
	
	private class EventListener extends OrganAdapter implements SessionListener {

		public void constructingChanged(boolean constructing) {
			stop();
		}

		public void destroyed() {
		}
		
		@Override
		public void propertyChanged(Element element, String name) {
			if ("name".equals(name)) {
				for (Tracker tracker : trackers) {
					if (tracker.getElement() == element) {
						if ("".equals(element.getName())) {
							element = null;
						}
						setElement(tracker.getTrack(), element);
						break;
					}
				}
			}
		}
		
		@Override
		public void elementRemoved(Element element) {
			for (Tracker tracker : trackers) {
				if (tracker.getElement() == element) {
					setElement(tracker.getTrack(), null);
					break;
				}
			}
		}
	}
}