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

import jorgan.disposition.Swell;
import jorgan.disposition.event.OrganEvent;
import jorgan.play.sound.Sound;
import jorgan.play.sound.SoundWrapper;

/**
 * A player for a swell.
 */
public class SwellPlayer extends ContinuousPlayer<Swell> implements SoundEffectPlayer {

    private List<SwellSound> sounds = new ArrayList<SwellSound>();

    public SwellPlayer(Swell swell) {
        super(swell);
    }

    protected void closeImpl() {
        sounds.clear();
    }

    public void elementChanged(OrganEvent event) {
        super.elementChanged(event);

        if (isOpen()) {
            for (int s = 0; s < sounds.size(); s++) {
                SwellSound sound = sounds.get(s);
                sound.flush();
            }
        }
    }

    public Sound effectSound(Sound sound) {

        return new SwellSound(sound);
    }

    private class SwellSound extends SoundWrapper {

        private int volume = 127;

        private int oldVolume = -1;

        private int oldCutoff = -1;

        public SwellSound(Sound sound) {
            super(sound);

            sounds.add(this);
        }

        public void setVolume(int volume) {
            this.volume = volume;

            flush();
        }

        public void stop() {
            super.stop();

            sounds.remove(this);
        }

        private void flush() {
            Swell swell = getElement();

            boolean output = false;

            int newVolume = getValue(swell.getVolume(), swell.getValue())
                    * volume / 127;
            if (newVolume != oldVolume) {
                sound.setVolume(newVolume);
                oldVolume = newVolume;

                output = true;
            }

            // Change cutoff only if lower than max value, so user can choose to
            // not use
            // this feature in case of soundfonts with preset cutoff values.
            if (swell.getCutoff() < 127) {
                int newCutoff = getValue(swell.getCutoff(), swell.getValue());
                if (newCutoff != oldCutoff) {
                    sound.setCutoff(newCutoff);
                    oldCutoff = newCutoff;

                    output = true;
                }
            }

            if (output) {
                fireOutputProduced();
            }
        }

        private int getValue(int base, int position) {
            return base + ((127 - base) * position / 127);
        }
    }
}