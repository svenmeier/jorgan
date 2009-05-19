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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.midi.MessageUtils;
import jorgan.play.OrganPlay;
import jorgan.recorder.midi.Recorder;

public class KeyboardTracker extends Tracker {

	private Keyboard keyboard;

	public KeyboardTracker(SessionRecorder recorder, int track,
			Keyboard keyboard) {
		super(recorder, track);

		this.keyboard = keyboard;
	}

	@Override
	public Element getElement() {
		return keyboard;
	}

	@Override
	public void keyPressed(Keyboard keyboard, int pitch, int velocity) {
		getRecorder().record(getTrack(),
				MessageUtils.newMessage(ShortMessage.NOTE_ON, pitch, velocity));
	}

	@Override
	public void keyReleased(Keyboard keyboard, int pitch) {
		getRecorder().record(getTrack(),
				MessageUtils.newMessage(ShortMessage.NOTE_OFF, pitch, 0));
	}

	@Override
	public void played(ShortMessage message) {
		if (message.getStatus() == ShortMessage.NOTE_ON) {
			getPlay()
					.pressKey(keyboard, message.getData1(), message.getData2());
		} else if (message.getStatus() == ShortMessage.NOTE_OFF) {
			getPlay().releaseKey(keyboard, message.getData1());
		}
	}

	/**
	 * {@link OrganPlay#pressKey(Keyboard, int, int)} for all currently pressed
	 * keys.
	 */
	@Override
	public void playing() {
		for (ShortMessage message : getKeyPresses()) {
			getPlay()
					.pressKey(keyboard, message.getData1(), message.getData2());
		}
	}

	/**
	 * {@link Recorder#record(int, MidiMessage) NOTE_OFF for all currently pressed
	 * keys.
	 */
	@Override
	public void recording() {
		for (ShortMessage message : getKeyPresses()) {
			getRecorder().record(
					getTrack(),
					MessageUtils.newMessage(ShortMessage.NOTE_OFF, message
							.getData1(), 0));
		}
	}

	/**
	 * {@link Recorder#record(int, MidiMessage) NOTE_OFF for all currently pressed
	 * keys.
	 */
	@Override
	public void recordStopping() {
		for (ShortMessage message : getKeyPresses()) {
			getRecorder().record(
					getTrack(),
					MessageUtils.newMessage(ShortMessage.NOTE_OFF, message
							.getData1(), 0));
		}
	}

	/**
	 * {@link OrganPlay#releaseKey(Keyboard, int, int)} for all currently
	 * pressed keys.
	 */
	@Override
	public void playStopping() {
		for (ShortMessage message : getKeyPresses()) {
			getPlay().releaseKey(keyboard, message.getData1());
		}
	}

	private Collection<ShortMessage> getKeyPresses() {
		Map<Integer, ShortMessage> messages = new HashMap<Integer, ShortMessage>();

		int track = getTrack();
		
		for (MidiEvent event : getRecorder().messagesForTrackTo(track,
				getRecorder().getCurrentTick())) {
			if (event.getMessage() instanceof ShortMessage) {
				ShortMessage message = (ShortMessage) event.getMessage();
				if (message.getStatus() == ShortMessage.NOTE_ON) {
					messages.put(message.getData1(), message);
				} else if (message.getStatus() == ShortMessage.NOTE_OFF) {
					messages.remove(message.getData1());
				}
			}
		}

		return messages.values();
	}
}