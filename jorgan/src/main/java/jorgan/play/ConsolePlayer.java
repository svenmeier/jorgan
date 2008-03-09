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
import jorgan.disposition.event.OrganEvent;
import jorgan.midi.MessageUtils;
import jorgan.midi.mpl.Context;
import jorgan.session.event.Severity;

/**
 * A player of an console.
 */
public class ConsolePlayer extends Player<Console> {

	private Transmitter transmitter;

	private Receiver receiver;

	public ConsolePlayer(Console console) {
		super(console);
	}

	@Override
	public void elementChanged(OrganEvent event) {
		super.elementChanged(event);
		
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
				transmitter.setReceiver(new ReceiverImpl());
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

	@Override
	protected void send(ShortMessage message, Context context) {

		if (receiver != null) {
			receiver.send(message, -1);
		
			if (getOrganPlay() != null) {
				getOrganPlay().fireOutputProduced();
			}
		}
	}

	/**
	 * The receiver of messages - notifies referenced elements of a received message.
	 */
	private class ReceiverImpl implements Receiver {
		public void close() {
		}

		public void send(MidiMessage message, long timeStamp) {
			if (MessageUtils.isShortMessage(message)) {
				for (Element element : getElement()
						.getReferenced(Element.class)) {
					Player<? extends Element> player = getOrganPlay()
							.getPlayer(element);
					if (player != null) {
						player.received((ShortMessage) message);
					}
				}
			}
		}
	}
}