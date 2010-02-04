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

import java.io.File;

import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.Track;

import jorgan.play.GenericSoundPlayer;
import jorgan.problem.Severity;
import jorgan.sysex.disposition.SysexSound;

/**
 * A player for a {@link SysexSound}.
 */
public class SysexSoundPlayer extends GenericSoundPlayer<SysexSound> {

	public SysexSoundPlayer(SysexSound sound) {
		super(sound);
	}

	@Override
	protected void openImpl() {
		super.openImpl();

		String open = getElement().getOpen();
		if (open != null) {
			send("open", open);
		}
	}

	protected void closeImpl() {
		String close = getElement().getClose();
		if (close != null) {
			send("close", close);
		}

		super.closeImpl();
	}

	private void send(String property, String value) {
		Receiver receiver = getReceiver();
		if (receiver != null) {
			try {
				File file = resolve(value);

				Sequence sequence = MidiSystem.getSequence(file);

				Track track = sequence.getTracks()[0];
				for (int e = 0; e < track.size(); e++) {
					MidiEvent event = track.get(e);

					MidiMessage message = event.getMessage();
					if (message instanceof SysexMessage) {
						receiver.send(message, -1);
					}
				}
			} catch (Exception e) {
				addProblem(Severity.ERROR, property, "invalid", value);
			}
		}
	}
}