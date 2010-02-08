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
package jorgan.sysex.play;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

import jorgan.midi.mpl.Context;
import jorgan.play.ConsolePlayer;
import jorgan.problem.Severity;
import jorgan.sysex.disposition.SysexConsole;

/**
 */
public class SysexConsolePlayer extends ConsolePlayer<SysexConsole> {

	private List<SysexMessage> messages = new ArrayList<SysexMessage>();

	public SysexConsolePlayer(SysexConsole console) {
		super(console);
	}

	@Override
	public void update() {
		super.update();

		SysexConsole console = getElement();
		if (console.getMapping() == null) {
			messages.clear();
		} else {
			loadMapping(console.getMapping());
		}
	}

	private void loadMapping(String mapping) {
		messages.clear();

		try {
			Sequence sequence = MidiSystem.getSequence(resolve(mapping));

			Track track = sequence.getTracks()[0];
			for (int e = 0; e < track.size(); e++) {
				MidiEvent event = track.get(e);

				MidiMessage message = event.getMessage();
				if (message instanceof SysexMessage) {
					messages.add((SysexMessage) message);
				}
			}
		} catch (Exception e) {
			addProblem(Severity.ERROR, "mapping", "invalid", mapping);
		}
	}

	@Override
	protected void send(ShortMessage message, Context context) {
		fireSent(message);

		SysexMessage mapped = map(message);
		if (mapped != null) {
			send(mapped);
		}
	}

	@Override
	protected void receive(MidiMessage message) {
		if (message instanceof SysexMessage) {
			SysexMessage sysexMessage = (SysexMessage) message;

			ShortMessage mapped = map(sysexMessage);
			if (mapped != null) {
				super.receive(mapped);
			}
		}
	}

	private ShortMessage map(SysexMessage message) {
		byte[] data = message.getMessage();

		for (int d = 0; d < messages.size(); d++) {
			if (Arrays.equals(messages.get(d).getMessage(), data)) {
				ShortMessage encode = getElement().getEncoding().encode(d);
				if (encode == null) {
					break;
				} else {
					return encode;
				}
			}
		}

		return null;
	}

	private SysexMessage map(ShortMessage message) {
		int d = getElement().getEncoding().decode(message);

		if (d != -1 && d < messages.size()) {
			return messages.get(d);
		}

		return null;
	}
}