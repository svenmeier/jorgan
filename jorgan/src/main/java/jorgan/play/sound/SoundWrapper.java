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
package jorgan.play.sound;

/**
 * A wrapper for a sound.
 */
public class SoundWrapper implements Sound {

  /**
   * The wrapped sound.
   */
  protected Sound sound;

  /**
   * Create a wrapper for the given sound.
   * 
   * @param sound   sound to wrap
   */
  public SoundWrapper(Sound sound) {
    if (sound == null) {
      throw new IllegalArgumentException("wrapped sound must not be null");
    }
    this.sound = sound;
  }

  public void stop() {
    sound.stop();
  }

  public void setProgram(int program) {
    sound.setProgram(program);
  }

  public void setPan(int pan) {
    sound.setPan(pan);
  }

  public void setPitchBend(int bend) {
    sound.setPitchBend(bend);
  }

  public void setVolume(int volume) {
    sound.setVolume(volume);
  }

  public void setCutoff(int cutoff) {
    sound.setCutoff(cutoff);
  }

  public void setModulation(int amplitude, int frequency) {
    sound.setModulation(amplitude, frequency);
  }

  public void noteOff(int pitch) {
    sound.noteOff(pitch);
  }

  public void noteOn(int pitch, int velocity) {
    sound.noteOn(pitch, velocity);
  }
}