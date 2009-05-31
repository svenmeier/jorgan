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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import jorgan.util.AbstractIterator;

public class Recorder {

	public static final long SECOND = 1000;

	public static final long MINUTE = 60 * SECOND;

	private static final float DEFAULT_DIVISION = Sequence.PPQ;

	private static final int DEFAULT_RESOLUTION = 50;

	private Sequence sequence;

	private Track[] tracks;

	private long currentTick;

	private State state;

	private List<RecorderListener> listeners = new ArrayList<RecorderListener>();

	public Recorder() {
		this(0);
	}

	public Recorder(int tracks) {
		setTracks(tracks);
	}

	public Recorder(Sequence sequence) {
		setSequence(sequence);
	}

	public void setTracks(int tracks) {
		try {
			setSequence(new Sequence(DEFAULT_DIVISION, DEFAULT_RESOLUTION,
					tracks));
		} catch (InvalidMidiDataException ex) {
			throw new Error(ex);
		}
	}

	public void addTrack() {
		sequence.createTrack();

		setSequence(sequence);
	}

	public void setSequence(Sequence sequence) {
		stop();

		this.sequence = sequence;

		tracks = sequence.getTracks();
		currentTick = 0;

		fireSequenceChanged();
	}

	public void addListener(RecorderListener listener) {
		listeners.add(listener);
	}

	public void removeListener(RecorderListener listener) {
		listeners.remove(listener);
	}

	public int getTrackCount() {
		return tracks.length;
	}

	public long getTime() {
		return tickToMillis(getCurrentTick());
	}

	public long getTotalTime() {
		return tickToMillis(state.getTotalTicks());
	}

	public void setTime(long time) {
		if (time < 0) {
			throw new IllegalArgumentException("negative time");
		}

		stop();

		this.currentTick = Math.min(millisToTick(time), sequence
				.getTickLength());

		fireTimeChanged(tickToMillis(currentTick));
	}

	public void start() {
		stop();

		new Running();
	}

	public boolean isRunning() {
		return state.getClass() == Running.class;
	}

	public boolean isStopped() {
		return state.getClass() == Stopped.class;
	}

	public void stop() {
		if (state != null) {
			if (state instanceof Stopped) {
				// already stopped
				return;
			}

			state.stopping();
		}

		new Stopped();
	}

	/**
	 * Record the given message. <br>
	 * Note that recording is supported when this recorder {@link #isStopped()}
	 * too.
	 * 
	 * @param track
	 *            track to add message to
	 * @param message
	 *            message to add
	 * @throws IllegalArgumentException
	 *             if track or message is invalid
	 */
	public void record(int track, MidiMessage message) {

		if (track >= tracks.length) {
			throw new IllegalArgumentException("invalid track");
		}

		if (SequenceUtils.isEndOfTrack(message)) {
			throw new IllegalArgumentException("endOfTrack is invalid");
		}

		state.record(track, message);
	}

	private void fireTimeChanged(long millis) {
		for (RecorderListener listener : listeners) {
			listener.timeChanged(millis);
		}
	}

	private void fireSequenceChanged() {
		for (RecorderListener listener : new ArrayList<RecorderListener>(
				listeners)) {
			listener.sequenceChanged();
		}
	}

	private void firePlayed(int track, MidiMessage message) {
		for (RecorderListener listener : listeners) {
			listener.played(track, message);
		}
	}

	private void fireEnd(long millis) {
		for (RecorderListener listener : listeners) {
			listener.end(millis);
		}
	}

	private void fireStarting() {
		for (RecorderListener listener : listeners) {
			listener.starting();
		}
	}

	protected void fireStopping() {
		for (RecorderListener listener : listeners) {
			listener.stopping();
		}
	}

	public long millisToTick(long millis) {
		float division = sequence.getDivisionType();
		if (division == Sequence.PPQ) {
			// default tempo is 120 beats per minute -> 2 beats per seconds
			division = 2.0f;
		}

		return Math.round(((double) millis) * division
				* sequence.getResolution() / 1000);
	}

	public long tickToMillis(long tick) {
		float division = sequence.getDivisionType();
		if (division == Sequence.PPQ) {
			// default tempo is 120 beats per minute -> 2 beats per seconds
			division = 2.0f;
		}

		return Math.round(((double) tick) * 1000 / division
				/ sequence.getResolution());
	}

	public long getCurrentTick() {
		return state.currentTick();
	}

	public Iterable<MidiEvent> messagesForTrack(final int track) {
		return messagesForTrack(track, 0, Long.MAX_VALUE);
	}

	public Iterable<MidiEvent> messagesForTrackToCurrent(final int track) {
		return messagesForTrackToTick(track, currentTick);
	}

	public Iterable<MidiEvent> messagesForTrackToTick(final int track,
			final long toTick) {
		return messagesForTrack(track, 0, toTick);
	}

	public Iterable<MidiEvent> messagesForTrackFromCurrent(final int track) {
		return messagesForTrackFromTick(track, currentTick);
	}

	public Iterable<MidiEvent> messagesForTrackFromTick(final int track,
			final long fromTick) {
		return messagesForTrack(track, fromTick, Long.MAX_VALUE);
	}

	private Iterable<MidiEvent> messagesForTrack(final int track,
			final long fromTick, final long toTick) {

		return new AbstractIterator<MidiEvent>() {
			private int index = SequenceUtils.getIndex(tracks[track], fromTick) - 1;

			public boolean hasNext() {
				if (index == tracks[track].size() - 1) {
					return false;
				}

				MidiEvent event = tracks[track].get(index + 1);
				return !SequenceUtils.isEndOfTrack(event.getMessage())
						&& event.getTick() < toTick;
			}

			public MidiEvent next() {
				index++;

				MidiEvent event = tracks[track].get(index);

				return event;
			}

			@Override
			public void remove() {
				tracks[track].remove(tracks[track].get(index));
				index--;
			}
		};
	}

	public void save(OutputStream output) throws IOException {
		MidiSystem.write(sequence, 1, output);
	}

	public void load(InputStream input) throws IOException {
		Sequence sequence;
		try {
			sequence = MidiSystem.getSequence(input);
		} catch (InvalidMidiDataException ex) {
			IOException ioEx = new IOException();
			ioEx.initCause(ex);
			throw ioEx;
		}
		setSequence(sequence);
	}

	private abstract class State {
		public State() {
			state = this;
		}

		public abstract void record(int track, MidiMessage message);

		public abstract long getTotalTicks();

		public abstract long currentTick();

		public void stopping() {
			fireStopping();
		}
	}

	private class Stopped extends State {

		public long currentTick() {
			return currentTick;
		}

		@Override
		public long getTotalTicks() {
			return sequence.getTickLength();
		}

		@Override
		public void record(int track, MidiMessage message) {
			tracks[track].add(new MidiEvent(message, currentTick()));
		}
	}

	private class Running extends State implements Runnable {

		private long initialTick;

		private long startMillis;

		private int[] indices;

		private Thread thread;

		public Running() {
			initialTick = currentTick;

			indices = new int[tracks.length];
			for (int t = 0; t < tracks.length; t++) {
				indices[t] = SequenceUtils.getIndex(tracks[t], initialTick);
			}

			startMillis = System.currentTimeMillis();
			thread = new Thread(this);

			fireStarting();

			thread.start();
		}

		@Override
		public long getTotalTicks() {
			return Math.max(currentTick(), sequence.getTickLength());
		}

		public long currentTick() {
			return initialTick
					+ millisToTick(System.currentTimeMillis() - startMillis);
		}

		public synchronized void run() {
			while (thread != null) {
				long sleepMillis = playEvents();
				if (sleepMillis == -1) {
					thread = null;
					fireEnd(tickToMillis(currentTick()));
					break;
				} else {
					try {
						wait(sleepMillis);
					} catch (InterruptedException interrupted) {
					}
				}
			}

			notifyAll();
		}

		private long playEvents() {
			while (true) {
				int track = -1;
				MidiEvent nextEvent = null;

				for (int t = 0; t < tracks.length; t++) {
					if (indices[t] < tracks[t].size()) {
						MidiEvent event = tracks[t].get(indices[t]);
						if (!SequenceUtils.isEndOfTrack(event.getMessage())) {
							if (nextEvent == null
									|| event.getTick() < nextEvent.getTick()) {
								nextEvent = event;
								track = t;
							}
						}
					}
				}

				if (track == -1
						|| SequenceUtils.isEndOfTrack(nextEvent.getMessage())) {
					return -1;
				}

				long sleepMillis = startMillis
						+ tickToMillis(nextEvent.getTick() - initialTick)
						- System.currentTimeMillis();
				if (sleepMillis <= 0) {
					firePlayed(track, nextEvent.getMessage());

					indices[track]++;
				} else {
					return sleepMillis;
				}
			}
		}

		@Override
		public synchronized void record(int track, MidiMessage message) {
			long tick = currentTick();

			if (thread != null) {
				// first play all events with same time
				playEvents();
			}

			tracks[track].add(new MidiEvent(message, tick));
			indices[track]++;
		}

		@Override
		public synchronized void stopping() {
			currentTick = currentTick();

			if (thread != null) {
				thread.interrupt();
				thread = null;

				try {
					wait();
				} catch (InterruptedException interrupted) {
				}
			}

			super.stopping();

			// step behind last tick
			currentTick = Math.min(currentTick++, sequence.getTickLength());

			SequenceUtils.setTickLength(sequence, currentTick);
		}
	}
}