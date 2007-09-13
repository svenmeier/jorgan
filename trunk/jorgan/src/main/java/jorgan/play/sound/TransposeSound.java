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
 * A transposing sound.
 */
public class TransposeSound extends SoundWrapper {

  private int transpose;
  
  /**
   * Create a wrapper for the given sound.
   * 
   * @param sound   sound to wrap
   */
  public TransposeSound(Sound sound, int transpose) {
    super(sound);
    
    this.transpose = transpose;
  }
  
  @Override
public void noteOff(int pitch) {
    pitch += transpose;
    if (pitch >= 0 && pitch <= 127) {
      sound.noteOff(pitch);
    }
  }

  @Override
public void noteOn(int pitch, int velocity) {
    pitch += transpose;
    if (pitch >= 0 && pitch <= 127) {
      sound.noteOn(pitch, velocity);
    }
  }
}