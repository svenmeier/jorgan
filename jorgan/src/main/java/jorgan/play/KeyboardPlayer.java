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
package jorgan.play;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import jorgan.disposition.Element;
import jorgan.disposition.Key;
import jorgan.disposition.Keyboard;
import jorgan.disposition.event.OrganEvent;
import jorgan.sound.midi.DevicePool;

/**
 * A player of an keyboard.
 */
public class KeyboardPlayer extends Player<Keyboard> {

	private static final Problem warningDevice = new Problem(Problem.WARNING,
			"device");

	private static final Problem errorDevice = new Problem(Problem.ERROR,
			"device");

	/**
	 * The currently pressed keys.
	 */
	private boolean[] pressedKeys = new boolean[128];

	/**
	 * The midiDevice to receive input from.
	 */
	private MidiDevice in;

	/**
	 * The transmitter of the opened midiDevice.
	 */
	private Transmitter transmitter;

	/**
	 * Create player for the given keyboard.
	 * 
	 * @param keyboard
	 *            keyboard to play
	 */
	public KeyboardPlayer(Keyboard keyboard) {
		super(keyboard);
	}

	protected void openImpl() {
		Keyboard keyboard = getElement();

		removeProblem(errorDevice);

		String device = keyboard.getDevice();
		if (device != null) {
			try {
				// Important: assure successfull opening of MIDI device
				// before storing reference in instance variable
				MidiDevice toBeOpened = DevicePool.getMidiDevice(device, false);
				toBeOpened.open();
				this.in = toBeOpened;

				transmitter = this.in.getTransmitter();
				transmitter.setReceiver(getOrganPlay().createReceiver(this));
			} catch (MidiUnavailableException ex) {
				addProblem(errorDevice.value(device));
			}
		}
	}

	protected void closeImpl() {
		if (in != null) {
			if (transmitter != null) {
				transmitter.close();
				transmitter = null;
			}

			in.close();
			in = null;
		}

		for (int p = 0; p < pressedKeys.length; p++) {
			pressedKeys[p] = false;
		}
	}

	public void elementChanged(OrganEvent event) {
		Keyboard keyboard = getElement();

		if (keyboard.getDevice() == null && getWarnDevice()) {
			removeProblem(errorDevice);
			addProblem(warningDevice.value(null));
		} else {
			removeProblem(warningDevice);
		}
	}

	protected void input(ShortMessage message) {
		Keyboard keyboard = getElement();

		if (isNoteMessage(message)
				&& keyboard.getChannel() == message.getChannel()) {

			int command = message.getCommand();
			int pitch = message.getData1();
			int velocity = message.getData2();

			Key key = new Key(pitch);
			if ((keyboard.getFrom() == null || keyboard.getFrom()
					.lessEqual(key))
					&& (keyboard.getTo() == null || keyboard.getTo()
							.greaterEqual(key))) {

				pitch += keyboard.getTranspose();

				if (command == keyboard.getCommand()) {
					if (velocity > keyboard.getThreshold()) {
						keyDown(pitch, velocity);
					} else {
						keyUp(pitch);
					}
					fireInputAccepted();
				} else {
					if (command == ShortMessage.NOTE_OFF
							|| command == ShortMessage.NOTE_ON && velocity == 0) {
						keyUp(pitch);

						fireInputAccepted();
					}
				}
			}
		}
	}

	protected boolean isNoteMessage(ShortMessage message) {
		int status = message.getStatus();

		return (status >= 0x80 && status < 0xb0);
	}

	protected void keyDown(int pitch, int velocity) {
		if (pitch >= 0 && pitch <= 127 && !pressedKeys[pitch]) {
			pressedKeys[pitch] = true;

			Keyboard keyboard = getElement();

			for (int e = 0; e < keyboard.getReferenceCount(); e++) {
				Element element = keyboard.getReference(e).getElement();

				Player player = getOrganPlay().getPlayer(element);
				if (player != null) {
					((KeyablePlayer) player).keyDown(pitch, velocity);
				}
			}
		}
	}

	protected void keyUp(int pitch) {
		if (pitch >= 0 && pitch <= 127 && pressedKeys[pitch]) {
			pressedKeys[pitch] = false;

			Keyboard keyboard = getElement();

			for (int e = 0; e < keyboard.getReferenceCount(); e++) {
				Element element = keyboard.getReference(e).getElement();

				Player player = getOrganPlay().getPlayer(element);
				if (player != null) {
					((KeyablePlayer) player).keyUp(pitch);
				}
			}
		}
	}
}