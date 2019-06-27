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

import jorgan.fluidsynth.disposition.Chorus;
import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.play.ContinuousPlayer;

/**
 * A player for a {@link FluidsynthSound}.
 */
public class ChorusPlayer extends ContinuousPlayer<Chorus> {

	public ChorusPlayer(Chorus element) {
		super(element);
	}

	public void update() {
		Chorus chorus = getElement();

		for (FluidsynthSound sound : chorus.getOrgan().getReferrer(chorus,
				FluidsynthSound.class)) {
			FluidsynthSoundPlayer player = (FluidsynthSoundPlayer) getPlayer(sound);
			if (player != null) {
				player.configureChorus();
			}
		}
	}
}