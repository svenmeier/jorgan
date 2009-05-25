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

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;

import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.midi.MessageUtils;
import jorgan.play.OrganPlay;
import jorgan.recorder.SessionRecorder;
import jorgan.recorder.Tracker;

public abstract class AbstractTracker implements Tracker {

	private SessionRecorder recorder;

	private int track;

	protected AbstractTracker(SessionRecorder recorder, int track) {
		this.recorder = recorder;

		this.track = track;
	}

	public void destroy() {
		recorder = null;
	}

	public int getTrack() {
		return track;
	}

	public Organ getOrgan() {
		return recorder.getSession().getOrgan();
	}

	public OrganPlay getPlay() {
		return recorder.getSession().getPlay();
	}

	public abstract Element getElement();

	public void playing() {
	}

	public void recording() {
	}

	public void recordStopping() {
	}

	public void playStopping() {
	}

	public void played(MidiMessage message) {
	}

	protected Iterable<MidiEvent> messages() {
		return recorder.getRecorder().messagesForTrackTo(getTrack(),
				recorder.getRecorder().getCurrentTick());
	}

	protected void record(int status, int data1, int data2) {
		record(MessageUtils.newMessage(status, data1, data2));
	}

	protected void record(MidiMessage message) {
		if (recorder.getRecorder().isRecording()) {
			recorder.getRecorder().record(getTrack(), message);
		}
	}
}