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
package jorgan.disposition;

/**
 * A keyable.
 */
public abstract class Keyable extends Registratable {

  public static final int PITCH_DYNAMIC  = 0;
  public static final int PITCH_CONSTANT = 1;
  public static final int PITCH_HIGHEST  = 2;
  public static final int PITCH_LOWEST   = 3;
  
  public static final int DEFAULT_TRANSPOSE = 0;

  public static final int DEFAULT_VELOCITY = 100;

  private int velocity = DEFAULT_VELOCITY;

  private int pitch = PITCH_DYNAMIC;
  
  private int transpose = DEFAULT_TRANSPOSE;

  public int getTranspose() {
    return transpose;
  }

  public int getPitch() {
    return pitch;
  }
  
  public void setPitch(int pitch) {
      if (pitch != PITCH_DYNAMIC && pitch != PITCH_CONSTANT && pitch != PITCH_HIGHEST && pitch != PITCH_LOWEST) {
        throw new IllegalArgumentException("pitch '" + pitch + "'");
      }
      this.pitch = pitch;

      fireElementChanged(true);
  }

  public void setTranspose(int transpose) {
    this.transpose = transpose;

    fireElementChanged(true);
  }

  public int getVelocity() {
    return velocity;
  }

  public void setVelocity(int velocity) {
    if (velocity < 0 || velocity > 127) {
      throw new IllegalArgumentException("velocity '" + velocity + "'");
    }
    this.velocity = velocity;

    fireElementChanged(true);
  }
}