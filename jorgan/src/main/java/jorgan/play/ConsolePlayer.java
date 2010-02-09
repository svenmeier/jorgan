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

import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.midi.MessageUtils;
import jorgan.problem.Severity;

/**
 * A player of an console.
 */
public class ConsolePlayer<E extends Console> extends Player<E> {

	private Transmitter transmitter;

	private Receiver receiver;

	public ConsolePlayer(E console) {
		super(console);
	}

	@Override
	public void update() {
		super.update();

		Console console = getElement();

		if (console.getOutput() == null) {
			addProblem(Severity.WARNING, "output", "noDevice", console
					.getOutput());
		} else {
			removeProblem(Severity.WARNING, "output");
		}

		if (console.getInput() == null) {
			addProblem(Severity.WARNING, "input", "noDevice", console
					.getInput());
		} else {
			removeProblem(Severity.WARNING, "input");
		}
	}

	@Override
	protected void openImpl() {
		Console console = getElement();

		removeProblem(Severity.ERROR, "input");
		if (console.getInput() != null) {
			try {
				transmitter = getOrganPlay().createTransmitter(
						console.getInput());
				transmitter.setReceiver(new Receiver() {
					public void close() {
					}

					public void send(MidiMessage message, long timeStamp) {
						receive(message);
					}
				});
			} catch (MidiUnavailableException ex) {
				addProblem(Severity.ERROR, "input", "deviceUnavailable",
						console.getInput());
			}
		}

		removeProblem(Severity.ERROR, "output");
		if (console.getOutput() != null) {
			try {
				receiver = getOrganPlay().createReceiver(console.getOutput());
			} catch (MidiUnavailableException ex) {
				addProblem(Severity.ERROR, "output", "deviceUnavailable",
						console.getOutput());
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
	 */
	public void send(MidiMessage message) {
		if (receiver != null) {
			if (getOrganPlay() != null
					&& MessageUtils.isChannelMessage(message)) {
				ShortMessage shortMessage = (ShortMessage) message;
				getOrganPlay().fireSent(shortMessage.getChannel(),
						shortMessage.getCommand(), shortMessage.getData1(),
						shortMessage.getData2());

			}

			receiver.send(message, -1);
		}
	}

	protected void receive(MidiMessage message) {
		if (MessageUtils.isChannelMessage(message)) {
			ShortMessage shortMessage = (ShortMessage) message;
			getOrganPlay().fireReceived(getElement(), null,
					shortMessage.getChannel(), shortMessage.getCommand(),
					shortMessage.getData1(), shortMessage.getData2());
		}

		for (Element element : getElement().getReferenced(Element.class)) {
			Player<? extends Element> player = getOrganPlay()
					.getPlayer(element);
			if (player != null) {
				player.onReceived(message);
			}
		}
	}
}