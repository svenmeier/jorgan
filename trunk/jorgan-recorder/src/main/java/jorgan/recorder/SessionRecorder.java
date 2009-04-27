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

import javax.sound.midi.MidiMessage;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.play.event.PlayListener;
import jorgan.recorder.midi.Recorder;
import jorgan.recorder.midi.RecorderListener;
import jorgan.session.OrganSession;

public class SessionRecorder {

	private Recorder recorder;

	private OrganSession session;

	public SessionRecorder(OrganSession session, Recorder recorder) {
		this.recorder = recorder;
		recorder.addListener(new RecorderListener() {

			public void played(int track, long millis, MidiMessage message) {
				// 
			}

			public void recorded(int track, long millis, MidiMessage message) {
			}

			public void playing() {
			}

			public void recording() {
			}

			public void stopped() {
			}
		});

		this.session = session;
		this.session.getOrgan().addOrganListener(new OrganAdapter() {
			@Override
			public void propertyChanged(Element element, String name) {
				// record activation/deactivation
			}
		});
		this.session.getPlay().addPlayerListener(new PlayListener() {
			public void received(int channel, int command, int data1, int data2) {
			}

			public void sent(int channel, int command, int data1, int data2) {
			}
		});
	}
}