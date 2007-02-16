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

import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Tremulant;
import jorgan.disposition.event.OrganEvent;
import jorgan.play.sound.Sound;
import jorgan.play.sound.SoundWrapper;

/**
 * A player for a tremulant.
 */
public class TremulantPlayer extends ActivateablePlayer implements
        SoundEffectPlayer {

    private List<TremulantSound> sounds = new ArrayList<TremulantSound>();

    public TremulantPlayer(Tremulant tremulant) {
        super(tremulant);
    }

    protected void closeImpl() {
        super.closeImpl();

        sounds.clear();
    }

    public void elementChanged(OrganEvent event) {
        super.elementChanged(event);

        if (isOpen()) {
            for (int s = 0; s < sounds.size(); s++) {
                TremulantSound sound = sounds.get(s);
                sound.flush();
            }
        }
    }

    public Sound effectSound(Sound sound) {
        TremulantSound tremulantSound = new TremulantSound(sound);

        tremulantSound.flush();

        return tremulantSound;
    }

    private class TremulantSound extends SoundWrapper {
        public TremulantSound(Sound sound) {
            super(sound);

            sounds.add(this);
        }

        private void flush() {
            Tremulant tremulant = (Tremulant) getElement();

            if (isActive()) {
                sound.setModulation(tremulant.getAmplitude(), tremulant
                        .getFrequency());
            } else {
                sound.setModulation(0, 0);
            }
            fireOutputProduced();
        }

        public void stop() {
            super.stop();

            sounds.remove(this);
        }
    }
}
