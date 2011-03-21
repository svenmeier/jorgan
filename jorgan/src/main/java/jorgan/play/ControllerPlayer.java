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

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

import jorgan.disposition.Controller;
import jorgan.disposition.Element;
import jorgan.midi.MessageUtils;
import jorgan.problem.Severity;

/**
 * A player of an {@link Controller}.
 */
public class ControllerPlayer<E extends Controller> extends Player<E> {

	private Transmitter transmitter;

	private Receiver receiver;

	public ControllerPlayer(E console) {
		super(console);
	}

	@Override
	public void update() {
		super.update();

		Controller controller = getElement();

		if (controller.getOutput() == null) {
			addProblem(Severity.WARNING, "output", "noDevice", controller
					.getOutput());
		} else {
			removeProblem(Severity.WARNING, "output");
		}

		if (controller.getInput() == null) {
			addProblem(Severity.WARNING, "input", "noDevice", controller
					.getInput());
		} else {
			removeProblem(Severity.WARNING, "input");
		}
	}

	@Override
	protected void openImpl() {
		Controller controller = getElement();

		removeProblem(Severity.ERROR, "input");
		if (controller.getInput() != null) {
			try {
				transmitter = getOrganPlay().createTransmitter(
						controller.getInput());
				transmitter.setReceiver(new Receiver() {
					public void close() {
					}

					public void send(MidiMessage message, long timeStamp) {
						receive(message);
					}
				});
			} catch (MidiUnavailableException ex) {
				addProblem(Severity.ERROR, "input", "deviceUnavailable",
						controller.getInput());
			}
		}

		removeProblem(Severity.ERROR, "output");
		if (controller.getOutput() != null) {
			try {
				receiver = getOrganPlay()
						.createReceiver(controller.getOutput());
			} catch (MidiUnavailableException ex) {
				addProblem(Severity.ERROR, "output", "deviceUnavailable",
						controller.getOutput());
			}
		}
	}

	@Override
	protected void closeImpl() {
		if (transmitter != null) {
			transmitter.close();
			transmitter = null;
		}

		if (receiver != null) {
			receiver.close();
			receiver = null;
		}
	}

	/**
	 * Send a message - may be called by all players handling referenced
	 * elements.
	 * 
	 * @throws InvalidMidiDataException
	 */
	public void send(byte[] datas) throws InvalidMidiDataException {
		if (receiver != null) {
			MidiMessage midiMessage = MessageUtils.createMessage(datas);

			fireSent(midiMessage);

			receiver.send(midiMessage, -1);
		}
	}

	protected void receive(MidiMessage midiMessage) {
		fireReceived(midiMessage);

		byte[] datas = MessageUtils.getDatas(midiMessage);

		for (Element element : getElement().getReferenced(Element.class)) {
			Player<?> player = getPlayer(element);
			if (player != null) {
				player.onReceived(datas);
			}
		}
	}
}