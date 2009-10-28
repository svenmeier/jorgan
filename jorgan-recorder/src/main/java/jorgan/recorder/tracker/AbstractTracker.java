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
import jorgan.play.OrganPlay;
import jorgan.recorder.Performance;
import jorgan.recorder.Tracker;

public abstract class AbstractTracker implements Tracker {

	private Performance performance;

	private int track;

	private boolean playEnabled = false;

	private boolean recordEnabled = false;

	protected AbstractTracker(Performance performance, int track) {
		this.performance = performance;

		this.track = track;
	}

	public void destroy() {
		performance = null;
	}

	public int getTrack() {
		return track;
	}

	public void setRecordEnabled(boolean records) {
		this.recordEnabled = records;
	}

	public boolean isRecordEnabled() {
		return recordEnabled;
	}

	public void setPlayEnabled(boolean plays) {
		this.playEnabled = plays;
	}

	public boolean isPlayEnabled() {
		return playEnabled;
	}

	public abstract Element getElement();

	public Organ getOrgan() {
		return performance.getPlay().getOrgan();
	}

	public OrganPlay getPlay() {
		return performance.getPlay();
	}

	public void onPlayStarting() {
	}

	public void onRecordStarting() {
		removeFollowingEvents();
	}

	public void onPlayStopping() {
	}

	public void onRecordStopping() {
	}

	public void onPlayed(MidiMessage message) {
	}

	protected Iterable<MidiEvent> messages() {
		return performance.eventsToCurrent(getTrack());
	}

	protected void record(MidiMessage message) {
		performance.record(track, message);
	}

	private void removeFollowingEvents() {
		Iterator<MidiEvent> iterator = performance
				.eventsFromCurrent(getTrack()).iterator();
		while (iterator.hasNext()) {
			if (owns(iterator.next())) {
				iterator.remove();
			}
		}
	}

	protected abstract boolean owns(MidiEvent event);

	public Element decode(String string) throws IllegalArgumentException {
		return performance.decode(string);
	}

	public String encode(Element element) {
		return performance.encode(element);
	}

}