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
 * A sound.
 */
public interface Sound {

  /**
   * Stop the sound.
   */
  public void stop();

  /**
   * Set the program of the sound.
   * 
   * @param program   program
   */
  public void setProgram(int program);
  
  /**
   * Set the pan of the sound.
   * 
   * @param pan      pan to set
   */
  public void setPan(int pan);

  /**
   * Set the pitch bend of the sound.
   * 
   * @param bend        bend to set
   */
  public void setPitchBend(int bend);

  /**
   * Set the volume of the sound.
   * 
   * @param volume      volume to set
   */
  public void setVolume(int volume);
  
  /**
   * Set the cutoff (a.k.a. brightness) of the sound.
   * 
   * @param cutoff      cutoff to set
   */
  public void setCutoff(int cutoff);

  /**
   * Set the modulation of the sound.
   * 
   * @param amplitude   the amplitude of the modulation
   * @param frequency   the frequency of the modulation
   */
  public void setModulation(int amplitude, int frequency);

  /**
   * Turn a note off.
   * 
   * @param pitch   pitch of note
   */
  public void noteOff(int pitch);

  /**
   * Turn a note on.
   * 
   * @param pitch    pitch of note
   * @param velocity velocity of note
   */ 
  public void noteOn(int pitch, int velocity);
}