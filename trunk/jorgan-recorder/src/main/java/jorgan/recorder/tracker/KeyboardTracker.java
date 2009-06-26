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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.play.OrganPlay;
import jorgan.play.event.KeyListener;
import jorgan.recorder.SessionRecorder;
import jorgan.recorder.midi.Recorder;

public class KeyboardTracker extends AbstractTracker {

	private Keyboard keyboard;

	private EventHandler eventHandler = new EventHandler();

	public KeyboardTracker(SessionRecorder recorder, int track,
			Keyboard keyboard) {
		super(recorder, track);

		this.keyboard = keyboard;

		getPlay().addKeyListener(eventHandler);
	}

	@Override
	public void destroy() {
		getPlay().removeKeyListener(eventHandler);
	}

	@Override
	public Element getElement() {
		return keyboard;
	}

	@Override
	protected boolean owns(MidiEvent event) {
		MidiMessage message = event.getMessage();

		if (message.getStatus() == ShortMessage.NOTE_ON
				|| message.getStatus() == ShortMessage.NOTE_OFF) {
			return true;
		}

		return false;
	}

	public void onPlayed(MidiMessage message) {
		if (message instanceof ShortMessage) {
			ShortMessage shortMessage = (ShortMessage) message;

			if (message.getStatus() == ShortMessage.NOTE_ON) {
				getPlay().pressKey(keyboard, shortMessage.getData1(),
						shortMessage.getData2());
			} else if (message.getStatus() == ShortMessage.NOTE_OFF) {
				getPlay().releaseKey(keyboard, shortMessage.getData1());
			}
		}
	}

	/**
	 * {@link OrganPlay#pressKey(Keyboard, int, int)} for all currently pressed
	 * keys.
	 */
	public void onPlayStarting() {
		super.onPlayStarting();
		
		for (ShortMessage message : getKeyPresses()) {
			getPlay()
					.pressKey(keyboard, message.getData1(), message.getData2());
		}
	}

	/**
	 * {@link Recorder#record(int, MidiMessage) NOTE_OFF for all currently pressed
	 * keys.
	 */
	public void onRecordStarting() {
		super.onRecordStarting();
		
		for (ShortMessage message : getKeyPresses()) {
			record(ShortMessage.NOTE_OFF, message.getData1(), 0);
		}
	}

	/**
	 * {@link Recorder#record(int, MidiMessage) NOTE_OFF for all currently pressed
	 * keys.
	 */
	public void onRecordStopping() {
		super.onRecordStopping();
		
		for (ShortMessage message : getKeyPresses()) {
			record(ShortMessage.NOTE_OFF, message.getData1(), 0);
		}
	}

	/**
	 * {@link OrganPlay#releaseKey(Keyboard, int, int)} for all currently
	 * pressed keys.
	 */
	public void onPlayStopping() {
		super.onPlayStopping();
		
		for (ShortMessage message : getKeyPresses()) {
			getPlay().releaseKey(keyboard, message.getData1());
		}
	}

	private Collection<ShortMessage> getKeyPresses() {
		Map<Integer, ShortMessage> messages = new HashMap<Integer, ShortMessage>();

		for (MidiEvent event : messages()) {
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

	private final class EventHandler implements KeyListener {
		public void keyPressed(Keyboard keyboard, int pitch, int velocity) {
			if (keyboard == KeyboardTracker.this.keyboard) {
				record(ShortMessage.NOTE_ON, pitch, velocity);				
			}
		}

		public void keyReleased(Keyboard keyboard, int pitch) {
			if (keyboard == KeyboardTracker.this.keyboard) {
				record(ShortMessage.NOTE_OFF, pitch, 0);
			}
		}
	}
}