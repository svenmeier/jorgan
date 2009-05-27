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
package jorgan.recorder.midi;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

import jorgan.midi.MessageUtils;

public class SequenceUtils {

	/**
	 * Set the tick length of all {@link Track}s of the given sequence to the
	 * given minimal tick.
	 * 
	 * @param sequence
	 * @param tick
	 * @see Sequence#getTickLength()
	 */
	public static void setTickLength(Sequence sequence, long tick) {
		for (Track track : sequence.getTracks()) {
			MidiEvent endOfTrack = track.get(track.size() - 1);
			if (endOfTrack.getTick() < tick) {
				endOfTrack.setTick(tick);
			}
		}
	}

	/**
	 * Get the index of the first {@link MidiEvent} in the given {@link Track}
	 * at the given tick.
	 * 
	 * @param track
	 * @param tick
	 * @return
	 * @see MidiEvent#getTick()
	 */
	public static int getIndex(Track track, long tick) {
		// don't step over endOfTrack thus (size - 1)
		for (int i = 0; i < track.size() - 1; i++) {
			MidiEvent event = track.get(i);
			if (event.getTick() >= tick) {
				return i;
			}
		}
		return 0;
	}

	public static boolean isEndOfTrack(MidiEvent event) {
		MidiMessage message = event.getMessage();
		if (message instanceof MetaMessage) {
			MetaMessage metaMessage = (MetaMessage) message;
			return metaMessage.getType() == MessageUtils.META_END_OF_TRACK;
		}

		return false;
	}
}