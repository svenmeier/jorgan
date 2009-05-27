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

		fireChanged(tickToMillis(currentTick));
	}

	public void first() {
		stop();

		currentTick = 0;
		fireChanged(0);
	}

	public void last() {
		stop();

		currentTick = sequence.getTickLength();
		fireChanged(tickToMillis(currentTick));
	}

	public void previous() {
		stop();

		currentTick = Math.max(0, millisToTick(tickToMillis(currentTick)
				- SECOND));
	}

	public void next() {
		stop();

		currentTick = Math.min(
				millisToTick(tickToMillis(currentTick) + SECOND), sequence
						.getTickLength());
	}

	public void play() {
		stop();

		new Playing();
	}

	public boolean isPlaying() {
		return state.getClass() == Playing.class;
	}

	public boolean isStopped() {
		return state.getClass() == Stopped.class;
	}

	public void record() {
		stop();

		new Recording();
	}

	public boolean isRecording() {
		return state.getClass() == Recording.class;
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

		long tick = state.currentTick();

		if (track >= tracks.length) {
			throw new IllegalArgumentException("invalid track");
		}

		if (isEndOfTrack(message)) {
			throw new IllegalArgumentException("endOfTrack is invalid");
		}

		if (state instanceof Playing) {
			throw new IllegalStateException("record while playing is invalid");
		}

		tracks[track].add(new MidiEvent(message, tick));

		fireRecorded(track, tickToMillis(tick), message);
	}

	private boolean isEndOfTrack(MidiMessage message) {
		if (message.getStatus() != 0xFF) {
			return false;
		}
		if (message.getLength() != 3) {
			return false;
		}

		byte[] bytes = message.getMessage();
		return bytes[1] == 0x2F && bytes[2] == 0x00;
	}

	protected void fireChanged(long millis) {
		for (RecorderListener listener : listeners) {
			listener.timeChanged(millis);
		}
	}

	protected void fireSequenceChanged() {
		for (RecorderListener listener : new ArrayList<RecorderListener>(
				listeners)) {
			listener.sequenceChanged();
		}
	}

	protected void fireRecorded(int track, long millis, MidiMessage message) {
		for (RecorderListener listener : listeners) {
			listener.recorded(track, millis, message);
		}
	}

	protected void firePlayed(int track, long millis, MidiMessage message) {
		for (RecorderListener listener : listeners) {
			listener.played(track, millis, message);
		}
	}

	protected void firePlaying() {
		for (RecorderListener listener : listeners) {
			listener.playing();
		}
	}

	protected void fireRecording() {
		for (RecorderListener listener : listeners) {
			listener.recording();
		}
	}

	protected void fireStopped() {
		for (RecorderListener listener : listeners) {
			listener.stopped();
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

	public Iterable<MidiEvent> messagesForTrackTo(final int track,
			final long toTick) {
		return messagesForTrack(track, 0, toTick);
	}

	public Iterable<MidiEvent> messagesForTrackFrom(final int track,
			final long fromTick) {
		return messagesForTrack(track, fromTick, Long.MAX_VALUE);
	}

	private Iterable<MidiEvent> messagesForTrack(final int track,
			final long fromTick, final long toTick) {

		return new AbstractIterator<MidiEvent>() {
			private int index = 0;

			public boolean hasNext() {
				if (index == tracks[track].size() - 1) {
					return false;
				}

				MidiEvent event = tracks[track].get(index);
				return !isEndOfTrack(event.getMessage())
						&& event.getTick() < toTick;
			}

			public MidiEvent next() {
				MidiEvent event = tracks[track].get(index);

				index++;

				return event;
			}

			@Override
			public void remove() {
				index--;

				tracks[track].remove(tracks[track].get(index));
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

		public abstract long getTotalTicks();

		public abstract long currentTick();

		public void stopping() {
			fireStopping();
		}
	}

	private class Stopped extends State {
		public Stopped() {
			fireStopped();
		}

		public long currentTick() {
			return currentTick;
		}

		@Override
		public long getTotalTicks() {
			return sequence.getTickLength();
		}
	}

	private class Playing extends State implements Runnable {

		private long initialTick;

		private long startMillis;

		private int[] indices;

		private Thread thread;

		public Playing() {
			initialTick = currentTick;

			indices = new int[tracks.length];
			for (int t = 0; t < tracks.length; t++) {
				indices[t] = SequenceUtils.getIndex(tracks[t], initialTick);
			}

			firePlaying();

			thread = new Thread(this);
			thread.start();
		}

		@Override
		public long getTotalTicks() {
			return sequence.getTickLength();
		}

		public long currentTick() {
			if (thread == null) {
				return currentTick;
			} else {
				return Math.min(
						initialTick
								+ millisToTick(System.currentTimeMillis()
										- startMillis), sequence
								.getTickLength());
			}
		}

		public synchronized void run() {
			startMillis = System.currentTimeMillis();

			while (thread != null) {
				int track = -1;
				long tick = Long.MAX_VALUE;
				MidiEvent event = null;

				for (int t = 0; t < tracks.length; t++) {
					if (indices[t] < tracks[t].size()) {
						event = tracks[t].get(indices[t]);
						if (event.getTick() < tick) {
							tick = event.getTick();
							track = t;
						}
					}
				}

				if (track == -1) {
					currentTick = currentTick();
					thread = null;
					stop();
					break;
				}

				long sleepMillis = startMillis
						+ tickToMillis(tick - initialTick)
						- System.currentTimeMillis();
				if (sleepMillis <= 0) {
					if (!SequenceUtils.isEndOfTrack(event)) {
						firePlayed(track, tickToMillis(tick), event
								.getMessage());
					}

					indices[track]++;
				} else {
					try {
						wait(sleepMillis);
					} catch (InterruptedException interrupted) {
					}
				}
			}

			notifyAll();
		}

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

			// step behind last played tick
			currentTick = Math.min(currentTick++, sequence.getTickLength());
		}
	}

	private class Recording extends State {

		private long initialTick;

		private long startMillis;

		private boolean recording;

		public Recording() {
			initialTick = currentTick;

			for (Track track : tracks) {
				// don't delete meta endOfTrack
				for (int i = track.size() - 2; i >= 0; i--) {
					MidiEvent event = track.get(i);
					if (event.getTick() >= initialTick) {
						track.remove(event);
					}
				}
			}

			fireRecording();

			startMillis = System.currentTimeMillis();
			recording = true;
		}

		@Override
		public long getTotalTicks() {
			return currentTick();
		}

		public long currentTick() {
			if (recording) {
				return initialTick
						+ millisToTick(System.currentTimeMillis()
								- this.startMillis);
			} else {
				return currentTick;
			}
		}

		public void stopping() {
			currentTick = currentTick();

			recording = false;

			super.stopping();

			// step behind last recorded tick
			currentTick++;

			SequenceUtils.setTickLength(sequence, currentTick);
		}
	}
}