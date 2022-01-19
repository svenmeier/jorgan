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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.midi.MessageUtils;
import jorgan.play.KeyboardPlayer;
import jorgan.play.OrganPlay;
import jorgan.play.OrganPlay.Playing;
import jorgan.play.event.KeyListener;
import jorgan.recorder.Performance;
import jorgan.recorder.midi.Sequencer;

public class KeyboardTracker extends AbstractTracker {

	private Keyboard keyboard;

	private EventHandler eventHandler = new EventHandler();

	private OrganPlay play;

	public KeyboardTracker(int track, Keyboard keyboard) {
		super(track);

		this.keyboard = keyboard;
	}

	public void attach(Performance performance) {
		super.attach(performance);

		this.play = performance.getPlay();
		play.addKeyListener(eventHandler);
	}

	@Override
	public void detach() {
		play.removeKeyListener(eventHandler);
		play = null;

		super.detach();
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

			if (isKeyRelease(shortMessage)) {
				releaseKey(keyboard, shortMessage.getData1());
			} else if (isKeyPress(shortMessage)) {
				pressKey(keyboard, shortMessage.getData1(),
						shortMessage.getData2());
			}
		}
	}

	private boolean isKeyRelease(ShortMessage message) {
		return message.getStatus() == ShortMessage.NOTE_OFF
				|| (message.getStatus() == ShortMessage.NOTE_ON
						&& message.getData2() == 0);
	}

	private boolean isKeyPress(ShortMessage message) {
		return message.getStatus() == ShortMessage.NOTE_ON
				&& message.getData2() > 0;
	}

	/**
	 * {@link OrganPlay#pressKey(Keyboard, int, int)} for all currently pressed
	 * keys.
	 */
	public void onPlayStarting() {
		super.onPlayStarting();

		for (ShortMessage message : getKeyPresses()) {
			pressKey(keyboard, message.getData1(), message.getData2());
		}
	}

	/**
	 * {@link Sequencer#record(int, MidiMessage) NOTE_OFF for all currently
	 * pressed keys.
	 */
	public void onRecordStarting() {
		super.onRecordStarting();

		for (ShortMessage message : getKeyPresses()) {
			record(createKeyRelease(message.getData1()));
		}
	}

	/**
	 * {@link Sequencer#record(int, MidiMessage) NOTE_OFF for all currently
	 * pressed keys.
	 */
	public void onRecordStopping() {
		super.onRecordStopping();

		for (ShortMessage message : getKeyPresses()) {
			record(createKeyRelease(message.getData1()));
		}
	}

	private ShortMessage createKeyRelease(int pitch) {
		try {
			return MessageUtils.createMessage(ShortMessage.NOTE_OFF, pitch, 0);
		} catch (InvalidMidiDataException ex) {
			throw new RuntimeException(ex);
		}
	}

	private ShortMessage createKeyPress(int pitch, int velocity) {
		try {
			return MessageUtils.createMessage(ShortMessage.NOTE_ON, pitch,
					velocity);
		} catch (InvalidMidiDataException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * {@link OrganPlay#releaseKey(Keyboard, int, int)} for all currently
	 * pressed keys.
	 */
	public void onPlayStopping() {
		super.onPlayStopping();

		for (ShortMessage message : getKeyPresses()) {
			releaseKey(keyboard, message.getData1());
		}
	}

	private Collection<ShortMessage> getKeyPresses() {
		Map<Integer, ShortMessage> messages = new HashMap<Integer, ShortMessage>();

		for (MidiEvent event : messages()) {
			if (event.getMessage() instanceof ShortMessage) {
				ShortMessage message = (ShortMessage) event.getMessage();
				if (isKeyRelease(message)) {
					messages.remove(message.getData1());
				} else if (isKeyPress(message)) {
					messages.put(message.getData1(), message);
				}
			}
		}

		return messages.values();
	}

	private final class EventHandler implements KeyListener {
		public void keyPressed(Keyboard keyboard, int pitch, int velocity) {
			if (keyboard == KeyboardTracker.this.keyboard) {
				record(createKeyPress(pitch, velocity));
			}
		}

		public void keyReleased(Keyboard keyboard, int pitch) {
			if (keyboard == KeyboardTracker.this.keyboard) {
				record(createKeyRelease(pitch));
			}
		}
	}

	private void pressKey(Keyboard keyboard, final int pitch,
			final int velocity) {
		play.play(keyboard, new Playing<KeyboardPlayer>() {
			@Override
			public void play(KeyboardPlayer player) {
				player.press(pitch, velocity);
			}
		});
	}

	private void releaseKey(Keyboard keyboard, final int pitch) {
		play.play(keyboard, new Playing<KeyboardPlayer>() {
			@Override
			public void play(KeyboardPlayer player) {
				player.release(pitch);
			}
		});
	}
}