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
import jorgan.recorder.midi.Sequencer;
import jorgan.recorder.midi.SequencerListener;
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

	private Sequencer sequencer;

	private Recorder recorder;

	private OrganPlay play;

	private ElementProblems problems;

	private List<Tracker> trackers = new ArrayList<Tracker>();

	private EventListener listener = new EventListener();

	private int state = STATE_STOP;

	private boolean modified = false;

	private ElementEncoder encoder;

	/**
	 * Record the given session.
	 */
	public Performance(OrganPlay play, ElementProblems problems) {
		this.play = play;
		this.play.getOrgan().addOrganListener(listener);

		this.problems = problems;

		this.encoder = new ElementEncoder(play.getOrgan());

		load();
	}

	public void addListener(PerformanceListener listener) {
		listeners.add(listener);
	}

	public void removeListener(PerformanceListener listener) {
		if (!listeners.remove(listener)) {
			throw new IllegalArgumentException("unknown listener");
		}
	}

	public OrganPlay getPlay() {
		return play;
	}

	protected String createMessage(String key, Object... args) {
		MessageBuilder builder = new MessageBuilder();

		return config.get(key).read(builder).build(args);
	}

	protected abstract File resolve(String performance);

	protected abstract String deresolve(File file);

	public void load() {
		stop();

		initSequencer(null);

		recorder = play.getOrgan().getElement(Recorder.class);
		if (recorder != null) {
			problems.removeProblem(new Problem(Severity.ERROR, recorder,
					"performance", null));

			String performance = recorder.getPerformance();
			if (performance != null) {
				try {
					File file = resolve(performance);

					Sequence sequence = new MidiStream().read(file);

					initSequencer(sequence);
				} catch (Exception ex) {
					problems.addProblem(new Problem(Severity.ERROR, recorder,
							"performance", createMessage("load", performance)));
				}
			}
		}

		modified = false;

		fireChanged();
	}

	public void save() throws IOException {
		stop();

		writeTrackers();

		String performance = recorder.getPerformance();
		Sequence sequence = this.sequencer.getSequence();

		new MidiStream().write(sequence, resolve(performance));

		modified = false;
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

	/**
	 * Set the file to be used as performance. Creates an empty performance if
	 * the file doesn't exist.
	 */
	public void setFile(File file) {
		if (recorder != null) {
			if (file == null) {
				recorder.setPerformance(null);
			} else {
				if (!file.exists()) {
					try {
						new MidiStream().write(createSequence(), file);
					} catch (IOException ex) {
						// load will show problem
					}
				}

				recorder.setPerformance(deresolve(file));
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
		return sequencer != null;
	}

	public boolean isModified() {
		return modified;
	}

	protected void markModified() {
		modified = true;
	}

	private void initSequencer(Sequence sequence) {
		if (this.sequencer != null) {
			for (Tracker tracker : trackers) {
				tracker.detach();
			}
			trackers.clear();

			this.sequencer = null;
		}

		if (sequence != null) {
			sequencer = new Sequencer(sequence, listener);

			trackers = new ArrayList<Tracker>();
			for (int track = 0; track < sequencer.getTrackCount(); track++) {
				Tracker tracker = readTracker(track);
				if (tracker == null) {
					tracker = new EmptyTracker(track);
				}
				tracker.attach(this);
				trackers.add(tracker);
			}
		}
	}

	private Sequence createSequence() {

		List<Tracker> trackers = new ArrayList<Tracker>();
		for (Element element : this.recorder.getReferenced(Element.class)) {
			Tracker tracker = TrackerRegistry.createTracker(trackers.size(),
					element);
			if (tracker != null) {
				trackers.add(tracker);
			}
		}

		Sequence sequence;
		try {
			if (trackers.size() == 0) {
				sequence = new Sequence(DEFAULT_DIVISION, DEFAULT_RESOLUTION, 1);
			} else {
				sequence = new Sequence(DEFAULT_DIVISION, DEFAULT_RESOLUTION,
						trackers.size());
				Sequencer sequencer = new Sequencer(sequence, null);
				for (Tracker tracker : trackers) {
					writeTracker(sequencer, tracker);
				}
			}
		} catch (InvalidMidiDataException ex) {
			throw new Error(ex);
		}

		return sequence;
	}

	public void stop() {
		if (sequencer == null) {
			return;
		}

		if (state != STATE_STOP) {
			sequencer.stop();

			state = STATE_STOP;

			fireStateChanged();
		}
	}

	public void play() {
		if (sequencer == null) {
			return;
		}

		if (state != STATE_PLAY) {
			state = STATE_PLAY;

			sequencer.stop();

			if (sequencer.isLast()) {
				sequencer.first();
			}

			sequencer.start();

			fireStateChanged();
		}
	}

	public void record() {
		if (sequencer == null) {
			return;
		}

		state = STATE_RECORD;

		sequencer.start();

		markModified();

		fireStateChanged();
	}

	public int getState() {
		return state;
	}

	public void first() {
		stop();

		sequencer.first();
	}

	public void last() {
		stop();

		sequencer.last();
	}

	public void record(int track, MidiMessage message) {
		if (state == STATE_RECORD && getTracker(track).isRecordEnabled()) {
			sequencer.record(track, message);
		}
	}

	public long millisToTick(long millis) {
		return sequencer.millisToTick(millis);
	}

	public Iterable<MidiEvent> eventsFromTick(int track, long tick) {
		return sequencer.eventsFromTick(track, tick);
	}

	public long tickToMillis(long tick) {
		return sequencer.tickToMillis(tick);
	}

	public Iterable<MidiEvent> eventsToCurrent(int track) {
		return sequencer.eventsToCurrent(track);
	}

	public Iterable<MidiEvent> eventsFromCurrent(int track) {
		return sequencer.eventsFromCurrent(track);
	}

	public Element getElement(int track) {
		return trackers.get(track).getElement();
	}

	public List<Element> getTrackableElements() {
		List<Element> elements = new ArrayList<Element>();

		for (Element element : play.getOrgan().getElements(Element.class)) {
			Tracker tracker = TrackerRegistry.createTracker(0, element);
			if (tracker != null) {
				elements.add(element);
			}
		}

		return elements;
	}

	public void setTime(long time) {
		stop();

		sequencer.setTime(time);

		fireTimeChanged(getTime());
	}

	public long getTime() {
		if (sequencer == null) {
			return 0;
		} else {
			return sequencer.getTime();
		}
	}

	public long getTotalTime() {
		if (sequencer == null) {
			return 0;
		} else {
			return sequencer.getTotalTime();
		}
	}

	public void setElement(int track, Element element) {
		stop();

		trackers.get(track).detach();

		Tracker tracker;
		if (element == null) {
			tracker = new EmptyTracker(track);
		} else {
			tracker = TrackerRegistry.createTracker(track, element);
			if (tracker == null) {
				throw new IllegalArgumentException("unsupported "
						+ element.getClass().getName());
			}
		}
		tracker.attach(this);
		trackers.set(track, tracker);

		markModified();

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

		Sequence sequence = sequencer.getSequence();
		sequence.deleteTrack(sequence.getTracks()[track]);
		if (sequence.getTracks().length == 0) {
			sequence.createTrack();
		}

		initSequencer(sequence);

		markModified();

		fireChanged();
	}

	public void addTrack() {

		writeTrackers();

		Sequence sequence = sequencer.getSequence();
		sequence.createTrack();

		initSequencer(sequence);

		markModified();

		fireChanged();
	}

	public void autoTracks() {
	}

	private void fireTimeChanged(long millis) {
		for (PerformanceListener listener : listeners) {
			listener.timeChanged(millis);
		}
	}

	private void fireSpeedChanged(float speed) {
		for (PerformanceListener listener : listeners) {
			listener.speedChanged(speed);
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
		stop();

		play.getOrgan().removeOrganListener(listener);
		play = null;
	}

	private class EventListener extends OrganAdapter implements
			SequencerListener {

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

		@Override
		public void onStarting() {
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
		public void onEvent(int track, MidiMessage message) {
			Tracker tracker = trackers.get(track);
			if (tracker.isPlayEnabled()) {
				tracker.onPlayed(message);
			}
		}

		@Override
		public void onLast() {
			if (getState() == STATE_PLAY) {
				Performance.this.stop();
			}
		}

		@Override
		public void onStopping() {
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

	private Tracker readTracker(int track) {
		Element element = null;
		boolean playEnabled = false;
		boolean recordEnabled = false;

		for (MidiEvent event : sequencer.eventsAtTick(track, 0)) {
			if (event.getMessage() instanceof MetaMessage) {
				MetaMessage message = (MetaMessage) event.getMessage();
				if (message.getType() == MessageUtils.META_TRACK_NAME) {
					try {
						element = encoder.decode(MessageUtils.getText(message));
					} catch (IllegalArgumentException invalidMessage) {
					}
				} else if (message.getType() == MessageUtils.META_CUE_POINT) {
					if ("play".equals(MessageUtils.getText(message))) {
						playEnabled = true;
					} else if ("record".equals(MessageUtils.getText(message))) {
						recordEnabled = true;
					}
				}
			}
		}

		Tracker tracker = TrackerRegistry.createTracker(track, element);
		if (tracker != null) {
			tracker.setPlayEnabled(playEnabled);
			tracker.setRecordEnabled(recordEnabled);
			return tracker;
		}

		return null;
	}

	private void writeTrackers() {
		for (Tracker tracker : trackers) {
			if (tracker.getElement() != null) {
				writeTracker(sequencer, tracker);
			}
		}
	}

	private void writeTracker(Sequencer sequencer, Tracker tracker) {
		Iterator<MidiEvent> iterator = sequencer.eventsAtTick(
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
			sequencer.setTime(0);

			sequencer.record(tracker.getTrack(), MessageUtils
					.createMetaMessage(MessageUtils.META_TRACK_NAME,
							encoder.encode(tracker.getElement())));

			if (tracker.isPlayEnabled()) {
				sequencer
						.record(tracker.getTrack(), MessageUtils
								.createMetaMessage(MessageUtils.META_CUE_POINT,
										"play"));
			}

			if (tracker.isRecordEnabled()) {
				sequencer.record(tracker.getTrack(), MessageUtils
						.createMetaMessage(MessageUtils.META_CUE_POINT,
								"record"));
			}
		}
	}

	public ElementEncoder getEncoder() {
		return encoder;
	}

	public void setSpeed(float speed) {
		sequencer.setSpeed(speed);

		fireSpeedChanged(getSpeed());
	}

	public float getSpeed() {
		return sequencer.getSpeed();
	}
}