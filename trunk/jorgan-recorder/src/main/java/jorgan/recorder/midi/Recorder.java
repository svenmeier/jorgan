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

	private static final float DEFAULT_DIVISION = Sequence.PPQ;

	private static final int DEFAULT_RESOLUTION = 50;

	private Sequence sequence;

	private Track[] tracks;

	private long time;

	private State state;

	private List<RecorderListener> listeners = new ArrayList<RecorderListener>();

	public Recorder() {
		this(0);
	}

	public Recorder(int tracks) {
		setTracks(0);
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

	public void setSequence(Sequence sequence) {
		stop();

		this.sequence = sequence;

		tracks = sequence.getTracks();
		time = 0;
	}

	public void addListener(RecorderListener listener) {
		listeners.add(listener);
	}

	public void removeListener(RecorderListener listener) {
		listeners.remove(listener);
	}

	public long getTime() {
		// state might have new time
		time = state.getTime();

		return time;
	}

	public void setTime(long time) {
		stop();

		time = Math.max(0, time);
		time = Math.min(time, getTotalTime());

		this.time = time;
	}

	public long getTotalTime() {
		return tickToMillis(sequence.getTickLength());
	}

	public void first() {
		stop();

		time = 0;
	}

	public void last() {
		stop();

		time = getTotalTime();
	}

	public void previous() {
		stop();

		time = Math.max(0, time - 1000);
	}

	public void next() {
		stop();

		time = Math.min(time + 1000, getTotalTime());
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

			// give state last opportunity to update time
			getTime();
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

		if (isEndOfTrack(message)) {
			throw new IllegalArgumentException("endOfTrack is invalid");
		}

		if (state instanceof Playing) {
			throw new IllegalStateException("record while playing is invalid");
		}

		long tick = millisToTick(getTime());

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

	private long millisToTick(long millis) {
		float division = sequence.getDivisionType();
		if (division == Sequence.PPQ) {
			// default tempo is 120 beats per minute -> 2 beats per seconds
			division = 2.0f;
		}

		return Math.round(((double) millis) * division
				* sequence.getResolution() / 1000);
	}

	private long tickToMillis(long tick) {
		float division = sequence.getDivisionType();
		if (division == Sequence.PPQ) {
			// default tempo is 120 beats per minute -> 2 beats per seconds
			division = 2.0f;
		}

		return Math.round(((double) tick) * 1000 / division
				/ sequence.getResolution());
	}

	public Iterable<MidiMessage> iterator(final int track) {
		return new AbstractIterator<MidiMessage>() {
			private int index = 0;

			public boolean hasNext() {
				if (index == tracks[track].size() - 1) {
					return false;
				}

				MidiEvent event = tracks[track].get(index);
				return tickToMillis(event.getTick()) <= getTime();
			}

			public MidiMessage next() {
				MidiEvent event = tracks[track].get(index);

				index++;

				return event.getMessage();
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

		public abstract long getTime();

		public void stopping() {
			fireStopping();
		}
	}

	private class Stopped extends State {
		public Stopped() {
			fireStopped();
		}

		public long getTime() {
			return time;
		}
	}

	private class Playing extends State implements Runnable {

		private long initialTime;

		private long startMillis;

		private int[] indices;

		private Thread thread;

		public Playing() {
			initialTime = time;

			indices = new int[tracks.length];
			for (int t = 0; t < tracks.length; t++) {
				Track track = tracks[t];
				// don't step over meta endOfTrack
				for (int e = 0; e < track.size() - 1; e++) {
					MidiEvent event = track.get(e);
					if (tickToMillis(event.getTick()) >= initialTime) {
						break;
					}
					indices[t]++;
				}
			}

			firePlaying();

			thread = new Thread(this);
			thread.start();
		}

		public long getTime() {
			if (thread == null) {
				return Recorder.this.time;
			} else {
				return initialTime + (System.currentTimeMillis() - startMillis);
			}
		}

		public synchronized void run() {
			startMillis = System.currentTimeMillis();

			while (thread != null) {
				int track = -1;
				long time = Long.MAX_VALUE;
				MidiEvent event = null;

				for (int t = 0; t < tracks.length; t++) {
					if (indices[t] < tracks[t].size()) {
						MidiEvent candidate = tracks[t].get(indices[t]);
						long candidateTime = tickToMillis(candidate.getTick());
						if (candidateTime < time) {
							track = t;
							time = candidateTime;
							if (indices[track] < tracks[track].size() - 1) {
								event = candidate;
							}
						}
					}
				}

				if (track == -1) {
					thread = null;
					stop();
					break;
				}

				long sleepMillis = startMillis + (time - initialTime)
						- System.currentTimeMillis();
				if (sleepMillis <= 0) {
					// don't play endOfTrack
					if (event != null) {
						firePlayed(track, time, event.getMessage());
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
			if (thread != null) {
				thread.interrupt();
				thread = null;

				try {
					wait();
				} catch (InterruptedException interrupted) {
				}
			}

			super.stopping();
		}
	}

	private class Recording extends State {

		private long initialTime;

		private long startMillis;

		public Recording() {
			initialTime = time;

			for (Track track : tracks) {
				// don't delete meta endOfTrack
				for (int i = track.size() - 2; i >= 0; i--) {
					MidiEvent event = track.get(i);
					if (tickToMillis(event.getTick()) > initialTime) {
						track.remove(event);
					}
				}
			}

			fireRecording();

			startMillis = System.currentTimeMillis();
		}

		public void stopping() {
			long tick = millisToTick(Recorder.this.getTime());

			super.stopping();

			for (Track track : tracks) {
				track.get(track.size() - 1).setTick(tick);
			}
		}

		public long getTime() {
			return initialTime
					+ (System.currentTimeMillis() - this.startMillis);
		}
	}
}