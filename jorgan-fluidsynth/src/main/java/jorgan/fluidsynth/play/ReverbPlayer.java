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
package jorgan.fluidsynth.play;

import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.fluidsynth.disposition.Reverb;
import jorgan.play.Player;

/**
 * A player for a {@link Reverb}.
 */
public class ReverbPlayer extends Player<Reverb> {

	public ReverbPlayer(Reverb element) {
		super(element);
	}

	public void update() {
		Reverb reverb = getElement();

		for (FluidsynthSound sound : reverb.getOrgan().getReferrer(reverb,
				FluidsynthSound.class)) {
			FluidsynthSoundPlayer player = (FluidsynthSoundPlayer) getPlayer(sound);
			if (player != null) {
				player.configureReverb();
			}
		}
	}
}