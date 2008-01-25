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
import jorgan.disposition.Keyboard;
import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.Keyboard.PressKey;
import jorgan.disposition.Keyboard.ReleaseKey;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.DevicePool;
import jorgan.midi.Direction;
import jorgan.midi.mpl.Context;
import jorgan.session.event.Severity;

/**
 * A player of an keyboard.
 */
public class KeyboardPlayer extends Player<Keyboard> {

	private PlayerContext context = new PlayerContext();

	/**
	 * The currently pressed keys.
	 */
	private boolean[] pressed = new boolean[128];

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

	@Override
	protected void openImpl() {
		Keyboard keyboard = getElement();

		removeProblem(Severity.WARNING, "input");

		String input = keyboard.getInput();
		if (input != null) {
			try {
				// Important: assure successfull opening of MIDI device
				// before storing reference in instance variable
				MidiDevice toBeOpened = DevicePool.instance().getMidiDevice(
						input, Direction.IN);
				toBeOpened.open();
				this.in = toBeOpened;

				transmitter = this.in.getTransmitter();
				transmitter.setReceiver(getOrganPlay().createReceiver(this));
			} catch (MidiUnavailableException ex) {
				addProblem(Severity.ERROR, "input", input, "inputUnavailable");
			}
		}
	}

	@Override
	protected void closeImpl() {
		if (in != null) {
			if (transmitter != null) {
				transmitter.close();
				transmitter = null;
			}

			in.close();
			in = null;
		}

		for (int p = 0; p < pressed.length; p++) {
			pressed[p] = false;
		}
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);

		Keyboard keyboard = getElement();

		if (keyboard.getInput() == null && getWarnDevice()) {
			removeProblem(Severity.ERROR, "input");
			addProblem(Severity.WARNING, "input", null, "inputMissing");
		} else if (!isOpen()) {
			removeProblem(Severity.ERROR, "input");
			removeProblem(Severity.WARNING, "input");
		}
	}

	@Override
	protected void input(InputMessage message, Context context) {
		if (message instanceof PressKey) {
			int pitch = Math.round(context.get(PressKey.PITCH));
			if (pitch < 0 || pitch > 127) {
				addProblem(Severity.ERROR, "messages", pitch, "pitchInvalid");
				return;
			}
			int velocity = Math.round(context.get(PressKey.VELOCITY));
			if (velocity < 0 || velocity > 127) {
				addProblem(Severity.ERROR, "messages", pitch, "velocityInvalid");
				return;
			}
			press(pitch, velocity);
		} else if (message instanceof ReleaseKey) {
			int pitch = Math.round(context.get(ReleaseKey.PITCH));
			if (pitch < 0 || pitch > 127) {
				addProblem(Severity.ERROR, "messages", pitch, "pitchInvalid");
				return;
			}
			release(pitch);
		} else {
			super.input(message, context);
		}
	}

	private void press(int pitch, int velocity) {
		Keyboard keyboard = getElement();

		if (!pressed[pitch]) {
			pressed[pitch] = true;

			for (int e = 0; e < keyboard.getReferenceCount(); e++) {
				Element element = keyboard.getReference(e).getElement();

				KeyablePlayer<?> player = (KeyablePlayer<?>) getOrganPlay()
						.getPlayer(element);
				if (player != null) {
					player.keyDown(pitch, velocity);
				}
			}
		}
	}

	private void release(int pitch) {

		Keyboard keyboard = getElement();

		if (pressed[pitch]) {
			pressed[pitch] = false;

			for (int e = 0; e < keyboard.getReferenceCount(); e++) {
				Element element = keyboard.getReference(e).getElement();

				KeyablePlayer<?> player = (KeyablePlayer<?>) getOrganPlay()
						.getPlayer(element);
				if (player != null) {
					player.keyUp(pitch);
				}
			}
		}
	}

	@Override
	public void received(ShortMessage message) {
		input(message, context);
	}
}