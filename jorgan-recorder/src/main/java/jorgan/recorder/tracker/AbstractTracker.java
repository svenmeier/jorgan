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
package jorgan.recorder.tracker;

import java.util.Iterator;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;

import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.midi.MessageUtils;
import jorgan.play.OrganPlay;
import jorgan.recorder.SessionRecorder;
import jorgan.recorder.Tracker;
import jorgan.recorder.midi.RecorderListener;

public abstract class AbstractTracker implements Tracker {

	private SessionRecorder recorder;

	private int track;

	private boolean plays = true;

	private boolean records = true;

	private EventListener listener = new EventListener();

	protected AbstractTracker(SessionRecorder recorder, int track) {
		this.recorder = recorder;
		recorder.getRecorder().addListener(listener);

		this.track = track;
	}

	public void destroy() {
		recorder.getRecorder().removeListener(listener);

		recorder = null;
	}

	public int getTrack() {
		return track;
	}

	public void setRecords(boolean records) {
		this.records = records;
	}

	public boolean records() {
		return records;
	}

	public void setPlays(boolean plays) {
		this.plays = plays;
	}

	public boolean plays() {
		return plays;
	}

	public Organ getOrgan() {
		return recorder.getSession().getOrgan();
	}

	public OrganPlay getPlay() {
		return recorder.getSession().getPlay();
	}

	public abstract Element getElement();

	protected void onPlayStarting() {
	}

	protected void onRecordStarting() {
	}

	protected void onPlayStopping() {
	}

	protected void onRecordStopping() {
	}

	protected void onPlayed(MidiMessage message) {
	}

	protected Iterable<MidiEvent> messages() {
		return recorder.getRecorder().messagesForTrackToCurrent(getTrack());
	}

	protected void record(int status, int data1, int data2) {
		record(MessageUtils.newMessage(status, data1, data2));
	}

	protected void record(MidiMessage message) {
		if (recorder.getState() == SessionRecorder.STATE_RECORD && records()) {
			recorder.getRecorder().record(getTrack(), message);
		}
	}

	private class EventListener implements RecorderListener {

		public void timeChanged(long millis) {
		}

		public void sequenceChanged() {
		}

		public void played(int track, MidiMessage message) {
			if (track == getTrack() && plays()) {
				onPlayed(message);
			}
		}

		public void recorded(int track, MidiMessage message) {
		}

		public void end(long millis) {
		}

		public void starting() {
			if (track == getTrack()) {
				if (recorder.getState() == SessionRecorder.STATE_RECORD) {
					if (records()) {
						Iterator<MidiEvent> iterator = recorder.getRecorder()
								.messagesForTrackFromCurrent(getTrack())
								.iterator();
						while (iterator.hasNext()) {
							iterator.next();
							iterator.remove();
						}

						onRecordStarting();
					} else if (plays()) {
						onPlayStarting();
					}
				} else if (recorder.getState() == SessionRecorder.STATE_PLAY) {
					if (plays()) {
						onPlayStarting();
					}
				}
			}
		}

		public void stopping() {
			if (track == getTrack()) {
				if (recorder.getState() == SessionRecorder.STATE_RECORD) {
					if (records()) {
						onRecordStopping();
					} else if (plays()) {
						onPlayStopping();
					}
				} else if (recorder.getState() == SessionRecorder.STATE_PLAY) {
					if (plays()) {
						onPlayStopping();
					}
				}
			}
		}

		public void stopped() {
		}
	}
}