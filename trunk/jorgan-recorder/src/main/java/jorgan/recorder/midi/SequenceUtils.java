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
	 * Get the index of the first {@link MidiEvent} in the given {@link Track}
	 * at the given tick.
	 * 
	 * @param track
	 * @param tick
	 * @return
	 * @see MidiEvent#getTick()
	 */
	public static int getIndex(Track track, long tick) {
		int index;
		for (index = 0; index < track.size(); index++) {
			MidiEvent event = track.get(index);
			if (event.getTick() >= tick) {
				break;
			}
		}
		return index;
	}

	public static boolean isEndOfTrack(MidiMessage message) {
		if (message instanceof MetaMessage) {
			MetaMessage metaMessage = (MetaMessage) message;
			return metaMessage.getType() == MessageUtils.META_END_OF_TRACK;
		}

		return false;
	}

	/**
	 * Shrink a sequence.
	 * 
	 * @param sequence
	 */
	public static void shrink(Sequence sequence) {
		for (Track track : sequence.getTracks()) {
			long lastTick = 0;
			if (track.size() > 1) {
				lastTick = track.get(track.size() - 2).getTick();
			}
			track.get(track.size() - 1).setTick(lastTick);
		}
	}

}