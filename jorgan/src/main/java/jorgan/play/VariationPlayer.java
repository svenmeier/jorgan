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

import jorgan.disposition.Variation;
import jorgan.disposition.event.OrganEvent;
import jorgan.play.sound.Sound;
import jorgan.play.sound.SoundWrapper;

/**
 * A player for an variation.
 */
public class VariationPlayer extends ActivateablePlayer implements
        SoundEffectPlayer {

    private List sounds = new ArrayList();

    public VariationPlayer(Variation variation) {
        super(variation);
    }

    protected void closeImpl() {
        super.closeImpl();

        sounds.clear();
    }

    public void elementChanged(OrganEvent event) {
        super.elementChanged(event);

        if (isOpen()) {
            for (int s = 0; s < sounds.size(); s++) {
                VariationSound sound = (VariationSound) sounds.get(s);
                sound.flush();
            }
        }
    }

    public Sound effectSound(Sound sound) {
        return new VariationSound(sound);
    }

    private class VariationSound extends SoundWrapper {
        private int program;

        public VariationSound(Sound sound) {
            super(sound);

            sounds.add(this);
        }

        public void setProgram(int program) {
            this.program = program;

            flush();
        }

        private void flush() {
            Variation variation = (Variation) getElement();

            int program = this.program;
            if (isActive()) {
                program = (program + variation.getProgram()) % 128;
            }

            sound.setProgram(program);

            fireOutputProduced();
        }

        public void stop() {
            super.stop();

            sounds.remove(this);
        }
    }
}