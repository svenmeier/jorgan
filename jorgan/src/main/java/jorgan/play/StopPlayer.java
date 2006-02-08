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

import jorgan.disposition.Element;
import jorgan.disposition.SoundEffect;
import jorgan.disposition.SoundSource;
import jorgan.disposition.Stop;
import jorgan.play.sound.SilentSound;
import jorgan.play.sound.Sound;

/**
 * A player for a stop.
 */
public class StopPlayer extends KeyablePlayer {

    private static Problem warningProgram = new Problem(Problem.WARNING,
            "program");

    private Sound sound;

    public StopPlayer(Stop stop) {
        super(stop);
    }

    protected void closeImpl() {

        super.closeImpl();

        sound = null;

        removeProblem(warningProgram);
    }

    protected void activated() {

        Stop stop = (Stop) getElement();

        boolean silentSound = false;

        for (int r = 0; r < stop.getReferenceCount(); r++) {
            Element element = stop.getReference(r).getElement();

            if (element instanceof SoundSource) {
                SoundSourcePlayer soundSourcePlayer = (SoundSourcePlayer) getOrganPlay()
                        .getPlayer(element);

                sound = soundSourcePlayer.createSound(stop.getProgram());
                if (sound != null) {
                    break;
                }
            }
        }

        if (sound == null) {
            sound = new SilentSound();

            silentSound = true;
        }

        for (int r = 0; r < stop.getReferenceCount(); r++) {
            Element element = stop.getReference(r).getElement();

            if (element instanceof SoundEffect) {
                SoundEffectPlayer soundEffectPlayer = (SoundEffectPlayer) getOrganPlay()
                        .getPlayer(element);

                sound = soundEffectPlayer.effectSound(sound);
            }
        }

        sound.setProgram(stop.getProgram());
        sound.setVolume(stop.getVolume());
        if (stop.getPan() != 64) {
            sound.setPan(stop.getPan());
        }
        if (stop.getBend() != 64) {
            sound.setPitchBend(stop.getBend());
        }

        fireOutputProduced();

        if (silentSound) {
            addProblem(warningProgram.value(new Integer(stop.getProgram())));
        } else {
            removeProblem(warningProgram);
        }

        super.activated();
    }

    protected void activateKey(int pitch, int velocity) {
        if (sound != null) {
            Stop stop = (Stop) getElement();
            if (stop.getVelocity() != 0) {
                velocity = stop.getVelocity();
            }

            sound.noteOn(pitch, velocity);

            fireOutputProduced();
        }
    }

    protected void deactivated() {
        super.deactivated();

        sound.stop();
        sound = null;

        fireOutputProduced();

        removeProblem(warningProgram);
    }

    protected void deactivateKey(int pitch) {
        if (sound != null) {
            sound.noteOff(pitch);

            fireOutputProduced();
        }
    }
}
