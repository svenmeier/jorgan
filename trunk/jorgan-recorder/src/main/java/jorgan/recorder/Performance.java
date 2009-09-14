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

import java.io.File;
import java.io.IOException;
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
import jorgan.play.OrganPlay;
import jorgan.problem.ElementProblems;
import jorgan.problem.Problem;
import jorgan.problem.Severity;
import jorgan.recorder.disposition.Recorder;
import jorgan.recorder.io.MidiStream;
import jorgan.recorder.midi.MessageRecorder;
import jorgan.recorder.spi.TrackerRegistry;
import jorgan.recorder.tracker.EmptyTracker;
import jorgan.session.OrganSession;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * A performance of an {@link OrganSession}.
 */
public abstract class Performance {

	private static Configuration config = Configuration.getRoot().get(
			Performance.class);

	private static final float DEFAULT_DIVISION = Sequence.PPQ;

	private static final int DEFAULT_RESOLUTION = 50;

	public static final int STATE_STOP = 0;

	public static final int STATE_PLAY = 1;

	public static final int STATE_RECORD = 2;

	private List<PerformanceListener> listeners = new ArrayList<PerformanceListener>();

	private MessageRecorder messageRecorder;

	private Recorder recorder;

	private OrganPlay play;

	private ElementProblems problems;

	private List<Tracker> trackers = new ArrayList<Tracker>();

	private EventListener listener = new EventListener();

	private int state = STATE_STOP;

	private boolean changed = false;

	/**
	 * Record the given session.
	 */
	public Performance(OrganPlay play, ElementProblems problems) {
		this.play = play;
		this.play.getOrgan().addOrganListener(listener);

		this.problems = problems;

		load();
	}

	public void addListener(PerformanceListener listener) {
		listeners.add(listener);
	}

	public void removeListener(PerformanceListener listener) {
		listeners.remove(listener);
	}

	public OrganPlay getPlay() {
		return play;
	}

	protected String createMessage(String key, Object... args) {
		MessageBuilder builder = new MessageBuilder();

		return config.get(key).read(builder).build(args);
	}

	protected abstract File resolve(String performance);

	public void load() {
		stop();

		initSequence(null);

		recorder = play.getOrgan().getElement(Recorder.class);
		if (recorder != null) {
			problems.removeProblem(new Problem(Severity.ERROR, recorder,
					"performance", null));

			String performance = recorder.getPerformance();
			if (performance != null) {
				try {
					File file = resolve(performance);

					Sequence sequence;
					if (file.exists()) {
						sequence = new MidiStream().read(file);
					} else {
						sequence = createSequence();
					}

					initSequence(sequence);
				} catch (Exception ex) {
					problems.addProblem(new Problem(Severity.ERROR, recorder,
							"performance", createMessage("load", performance)));
				}
			}
		}

		changed = false;
		
		fireChanged();
	}

	public void save() throws IOException {
		stop();

		writeTrackers();

		String performance = recorder.getPerformance();
		Sequence sequence = this.messageRecorder.getSequence();

		new MidiStream().write(sequence, resolve(performance));
		
		changed = false;
	}

	public File getFile() {
		if (recorder != null) {
			String performance = recorder.getPerformance();
			if (performance != null) {
				return resolve(performance);
			}
		}
		return null;
	}

	public void setFile(File file) {
		if (recorder != null) {
			if (file == null) {
				recorder.setPerformance(null);
			} else {
				recorder.setPerformance(file.getPath());
			}
		}
	}

	/**
	 * Is performance functionality enabled for the current disposition.
	 */
	public boolean isEnabled() {
		return recorder != null;
	}

	public boolean isLoaded() {
		return messageRecorder != null;
	}

	public boolean isChanged() {
		return changed ;
	}
	
	private void initSequence(Sequence sequence) {
		if (this.messageRecorder != null) {
			for (Tracker tracker : trackers) {
				tracker.destroy();
			}
			trackers.clear();

			this.messageRecorder = null;
		}

		if (sequence != null) {
			messageRecorder = new InternalRecorder(sequence);

			trackers = new ArrayList<Tracker>();
			for (int track = 0; track < messageRecorder.getTrackCount(); track++) {
				Tracker tracker = readTracker(track);
				if (tracker == null) {
					tracker = new EmptyTracker(this, track);
				}
				trackers.add(tracker);
			}
		}
	}

	private Sequence createSequence() {
		try {
			return new Sequence(DEFAULT_DIVISION, DEFAULT_RESOLUTION, 1);
		} catch (InvalidMidiDataException ex) {
			throw new Error(ex);
		}
	}

	public void stop() {
		if (state != STATE_STOP) {
			messageRecorder.stop();

			state = STATE_STOP;

			fireStateChanged();
		}
	}

	public void play() {
		if (state != STATE_PLAY) {
			state = STATE_PLAY;

			messageRecorder.stop();

			if (messageRecorder.isLast()) {
				messageRecorder.first();
			}

			messageRecorder.start();

			fireStateChanged();
		}
	}

	public void record() {
		state = STATE_RECORD;

		messageRecorder.start();

		changed = true;
		
		fireStateChanged();
	}

	public int getState() {
		return state;
	}

	public void first() {
		stop();

		messageRecorder.first();
	}

	public void last() {
		stop();

		messageRecorder.last();
	}

	public void record(int track, MidiMessage message) {
		if (state == STATE_RECORD && getTracker(track).isRecordEnabled()) {
			messageRecorder.record(track, message);
		}
	}

	public long millisToTick(long millis) {
		return messageRecorder.millisToTick(millis);
	}

	public Iterable<MidiEvent> eventsFromTick(int track, long tick) {
		return messageRecorder.eventsFromTick(track, tick);
	}

	public long tickToMillis(long tick) {
		return messageRecorder.tickToMillis(tick);
	}

	public Iterable<MidiEvent> eventsToCurrent(int track) {
		return messageRecorder.eventsToCurrent(track);
	}

	public Iterable<MidiEvent> eventsFromCurrent(int track) {
		return messageRecorder.eventsFromCurrent(track);
	}

	public Element getElement(int track) {
		return trackers.get(track).getElement();
	}

	public List<Element> getTrackableElements() {
		List<Element> elements = new ArrayList<Element>();

		for (Element element : play.getOrgan().getElements(Element.class)) {
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

		messageRecorder.setTime(time);

		fireTimeChanged(getTime());
	}

	public long getTime() {
		if (messageRecorder == null) {
			return 0;
		} else {
			return messageRecorder.getTime();
		}
	}

	public long getTotalTime() {
		if (messageRecorder == null) {
			return 0;
		} else {
			return messageRecorder.getTotalTime();
		}
	}

	public void setElement(int track, Element element) {
		stop();

		trackers.get(track).destroy();

		Tracker tracker;
		if (element == null) {
			tracker = new EmptyTracker(this, track);
		} else {
			tracker = TrackerRegistry.createTracker(this, track, element);
			if (tracker == null) {
				throw new IllegalArgumentException("unsupported "
						+ element.getClass().getName());
			}
		}

		trackers.set(track, tracker);

		changed = true;
		
		fireChanged();
	}

	public int getTrackerCount() {
		return trackers.size();
	}

	public Tracker getTracker(int track) {
		return trackers.get(track);
	}

	public void removeTrack(int track) {
		
		writeTrackers();
		
		Sequence sequence = messageRecorder.getSequence();
		sequence.deleteTrack(sequence.getTracks()[track]);
		if (sequence.getTracks().length == 0) {
			sequence.createTrack();
		}
		
		initSequence(sequence);
		
		changed = true;
		
		fireChanged();
	}
	
	public void addTrack() {
		
		writeTrackers();
		
		Sequence sequence = messageRecorder.getSequence();
		sequence.createTrack();
		
		initSequence(sequence);
		
		changed = true;
		
		fireChanged();
	}
	
	private void fireTimeChanged(long millis) {
		for (PerformanceListener listener : listeners) {
			listener.timeChanged(millis);
		}
	}

	private void fireChanged() {
		// work on copy since change might remove listeners
		for (PerformanceListener listener : new ArrayList<PerformanceListener>(
				listeners)) {
			listener.changed();
		}
	}

	private void fireStateChanged() {
		for (PerformanceListener listener : listeners) {
			listener.stateChanged(state);
		}
	}

	public void dispose() {
		play.getOrgan().removeOrganListener(listener);
		play = null;
	}

	private class InternalRecorder extends MessageRecorder {

		public InternalRecorder(Sequence sequence) {
			super(sequence);
		}

		@Override
		protected void onPlayed(int track, MidiMessage message) {
			Tracker tracker = trackers.get(track);
			if (tracker.isPlayEnabled()) {
				tracker.onPlayed(message);
			}
		}

		@Override
		protected void onLast() {
			if (getState() == STATE_PLAY) {
				Performance.this.stop();
			}
		}

		@Override
		protected void onStarting() {
			for (Tracker tracker : trackers) {
				if (state == STATE_RECORD) {
					if (tracker.isRecordEnabled()) {
						tracker.onRecordStarting();
					} else if (tracker.isPlayEnabled()) {
						tracker.onPlayStarting();
					}
				} else if (state == STATE_PLAY) {
					if (tracker.isPlayEnabled()) {
						tracker.onPlayStarting();
					}
				}
			}
		}

		@Override
		protected void onStopping() {
			for (Tracker tracker : trackers) {
				if (state == STATE_RECORD) {
					if (tracker.isRecordEnabled()) {
						tracker.onRecordStopping();
					} else if (tracker.isPlayEnabled()) {
						tracker.onPlayStopping();
					}
				} else if (state == STATE_PLAY) {
					if (tracker.isPlayEnabled()) {
						tracker.onPlayStopping();
					}
				}
			}
		}
	}

	private class EventListener extends OrganAdapter {

		@Override
		public void propertyChanged(Element element, String name) {
			if (element == recorder && "performance".equals(name)) {
				load();
			}
		}

		@Override
		public void elementAdded(Element element) {
			if (recorder == null && element instanceof Recorder) {
				load();
			}
		}

		@Override
		public void elementRemoved(Element element) {
			if (element == recorder) {
				load();
			} else {
				for (Tracker tracker : trackers) {
					if (tracker.getElement() == element) {
						setElement(tracker.getTrack(), null);
						break;
					}
				}
			}
		}
	}

	private Tracker readTracker(int track) {
		String id = null;
		boolean playEnabled = false;
		boolean recordEnabled = false;
		for (MidiEvent event : messageRecorder.eventsAtTick(track, 0)) {
			if (event.getMessage() instanceof MetaMessage) {
				MetaMessage message = (MetaMessage) event.getMessage();
				if (message.getType() == MessageUtils.META_TRACK_NAME) {
					id = MessageUtils.getText(message);
				} else if (message.getType() == MessageUtils.META_CUE_POINT) {
					if ("play".equals(MessageUtils.getText(message))) {
						playEnabled = true;
					} else if ("record".equals(MessageUtils.getText(message))) {
						recordEnabled = true;
					}
				}
			}
		}

		if (id != null) {
			Element element;
			try {
				element = play.getOrgan().getElement(Long.valueOf(id));
			} catch (Exception e) {
				return null;
			}

			Tracker tracker = TrackerRegistry.createTracker(Performance.this,
					track, element);
			if (tracker != null) {
				tracker.setPlayEnabled(playEnabled);
				tracker.setRecordEnabled(recordEnabled);
				return tracker;
			}
		}

		return null;
	}

	private void writeTrackers() {
		for (Tracker tracker : trackers) {
			if (tracker.getElement() != null) {
				writeTracker(tracker);
			}
		}
	}

	private void writeTracker(Tracker tracker) {
		Iterator<MidiEvent> iterator = messageRecorder.eventsAtTick(
				tracker.getTrack(), 0).iterator();
		while (iterator.hasNext()) {
			MidiEvent event = iterator.next();

			if (event.getMessage() instanceof MetaMessage) {
				MetaMessage message = (MetaMessage) event.getMessage();
				if (message.getType() == MessageUtils.META_TRACK_NAME
						|| message.getType() == MessageUtils.META_CUE_POINT) {
					iterator.remove();
				}
			}
		}

		if (tracker.getElement() != null) {
			messageRecorder.setTime(0);

			messageRecorder.record(tracker.getTrack(), MessageUtils
					.newMetaMessage(MessageUtils.META_TRACK_NAME, ""
							+ tracker.getElement().getId()));

			if (tracker.isPlayEnabled()) {
				messageRecorder.record(tracker.getTrack(), MessageUtils
						.newMetaMessage(MessageUtils.META_CUE_POINT, "play"));
			}

			if (tracker.isRecordEnabled()) {
				messageRecorder.record(tracker.getTrack(), MessageUtils
						.newMetaMessage(MessageUtils.META_CUE_POINT, "record"));
			}
		}
	}
}