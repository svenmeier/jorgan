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

import java.util.ArrayList;
import java.util.List;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import jorgan.util.AbstractIterator;

public class Sequencer {

	public static final long SECOND = 1000;

	public static final long MINUTE = 60 * SECOND;

	private SequencerListener listener;

	private Sequence sequence;

	private Track[] tracks;

	private long currentTick;

	private State state;

	public Sequencer(Sequence sequence) {
		this(sequence, new SequencerListener() {
			@Override
			public void onStarting() {
			}

			@Override
			public void onEvent(int track, MidiMessage message) {
			}

			@Override
			public void onStopping() {
			}

			@Override
			public void onLast() {
			}
		});
	}

	public Sequencer(Sequence sequence, SequencerListener listener) {
		this.sequence = sequence;
		this.listener = listener;

		tracks = sequence.getTracks();
		currentTick = 0;

		new Stopped();
	}

	public Sequence getSequence() {
		return sequence;
	}

	public int getTrackCount() {
		return tracks.length;
	}

	public long getTime() {
		return tickToMillis(getCurrentTick());
	}

	public long getTotalTime() {
		return tickToMillis(getTotalTicks());
	}

	public void setTime(long time) {
		if (time < 0) {
			throw new IllegalArgumentException("negative time");
		}

		setTick(millisToTick(time));
	}

	private void setTick(long tick) {
		synchronized (sequence) {
			stop();

			this.currentTick = Math.min(tick, getTotalTicks() + 1);
		}
	}

	private long getCurrentTick() {
		return state.currentTick();
	}

	private long getTotalTicks() {
		return state.totalTicks();
	}

	public void first() {
		setTick(0);
	}

	public void last() {
		setTick(sequence.getTickLength() + 1);
	}

	public boolean isLast() {
		return currentTick == sequence.getTickLength() + 1;
	}

	public void start() {
		synchronized (sequence) {
			stop();

			new Running();
		}
	}

	public boolean isRunning() {
		return state.getClass() == Running.class;
	}

	public boolean isStopped() {
		return state.getClass() == Stopped.class;
	}

	public void stop() {
		synchronized (sequence) {
			if (state instanceof Stopped) {
				// already stopped
				return;
			}

			state.stopping();

			new Stopped();
		}
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

		synchronized (sequence) {
			state.record(track, message);
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

	public Iterable<MidiEvent> eventsAtTick(final int track, final long tick) {
		return events(track, tick, tick + 1);
	}

	public Iterable<MidiEvent> eventsToCurrent(final int track) {
		return eventsToTick(track, currentTick);
	}

	public Iterable<MidiEvent> eventsToTick(final int track, final long toTick) {
		return events(track, 0, toTick);
	}

	public Iterable<MidiEvent> eventsFromCurrent(final int track) {
		return eventsFromTick(track, currentTick);
	}

	public Iterable<MidiEvent> eventsFromTick(final int track,
			final long fromTick) {
		return events(track, fromTick, Long.MAX_VALUE);
	}

	private Iterable<MidiEvent> events(final int track, final long fromTick,
			final long toTick) {

		synchronized (sequence) {
			return new AbstractIterator<MidiEvent>() {
				private int index = SequenceUtils.getIndex(tracks[track],
						fromTick) - 1;

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
	}

	private abstract class State {
		public State() {
			state = this;
		}

		public abstract void record(int track, MidiMessage message);

		public abstract long currentTick();

		public abstract long totalTicks();

		public void stopping() {
			listener.onStopping();
		}
	}

	private class Stopped extends State {

		public long currentTick() {
			return currentTick;
		}

		@Override
		public long totalTicks() {
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
			thread = new Thread(this, "Sequencer");

			listener.onStarting();

			thread.start();
		}

		public long currentTick() {
			return initialTick
					+ millisToTick(System.currentTimeMillis() - startMillis);
		}

		@Override
		public long totalTicks() {
			return Math.max(sequence.getTickLength(), currentTick());
		}

		public void run() {
			while (true) {
				past();

				synchronized (sequence) {
					if (thread != Thread.currentThread()) {
						break;
					}

					MidiEvent event = nextEvent();
					if (event == null) {
						listener.onLast();
						break;
					} else {
						try {
							long sleepMillis = startMillis
									+ tickToMillis(event.getTick()
											- initialTick)
									- System.currentTimeMillis();
							if (sleepMillis > 0) {
								sequence.wait(sleepMillis);
							}
						} catch (InterruptedException interrupted) {
						}
					}
				}
			}
		}

		private void past() {
			synchronized (sequence) {
				currentTick = currentTick();
			}

			for (int track = 0; track < tracks.length; track++) {
				past(track);
			}
		}

		private void past(int track) {
			List<MidiEvent> events = new ArrayList<MidiEvent>();

			synchronized (sequence) {
				while (indices[track] < tracks[track].size()) {
					MidiEvent event = tracks[track].get(indices[track]);

					if (SequenceUtils.isEndOfTrack(event.getMessage())) {
						break;
					}

					if (event.getTick() > currentTick) {
						break;
					}

					events.add(event);
					indices[track]++;
				}
			}

			notifyEvents(track, events);
		}

		private MidiEvent nextEvent() {
			MidiEvent nextEvent = null;

			for (int t = 0; t < tracks.length; t++) {
				if (indices[t] < tracks[t].size()) {
					MidiEvent event = tracks[t].get(indices[t]);
					if (!SequenceUtils.isEndOfTrack(event.getMessage())) {
						if (nextEvent == null
								|| event.getTick() < nextEvent.getTick()) {
							nextEvent = event;
						}
					}
				}
			}

			return nextEvent;
		}

		@Override
		public void record(int track, MidiMessage message) {
			past();

			tracks[track].add(new MidiEvent(message, currentTick));
			indices[track]++;
		}

		@Override
		public void stopping() {
			past();

			// stop possible waiting thread (could be current)
			thread.interrupt();
			thread = null;

			super.stopping();

			SequenceUtils.shrink(sequence);

			// step behind last tick
			currentTick = Math.min(currentTick, sequence.getTickLength()) + 1;
		}
	}

	private void notifyEvents(int track, List<MidiEvent> events) {
		for (MidiEvent event : events) {
			listener.onEvent(track, event.getMessage());
		}
	}
}