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

import java.util.Map;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.disposition.Keyboard.Press;
import jorgan.disposition.Keyboard.Release;
import jorgan.disposition.Message.InputMessage;
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.DevicePool;

/**
 * A player of an keyboard.
 */
public class KeyboardPlayer extends Player<Keyboard> {

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

		removeProblem(new Warning("input"));

		String device = keyboard.getInput();
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
				addProblem(new Error("input", device));
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
			removeProblem(new Error("input"));
			addProblem(new Warning("input"));
		} else {
			removeProblem(new Warning("input"));
		}
	}

	@Override
	protected void input(InputMessage message, Map<String, Float> values) {
		if (message instanceof Press) {
			press(Math.round(values.get(Press.PITCH)), Math.round(values
					.get(Press.VELOCITY)));
		} else if (message instanceof Release) {
			release(Math.round(values.get(Release.PITCH)));
		} else {
			super.input(message, values);
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
		input(message);
	}
}