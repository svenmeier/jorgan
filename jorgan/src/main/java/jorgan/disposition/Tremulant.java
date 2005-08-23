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
 * A tremulant.
 */
public class Tremulant extends Activateable implements SoundEffect {

  private int frequency = 64;
  private int amplitude = 64;

  public void setFrequency(int frequency) {
    if (frequency < 0 || frequency > 127) {
      throw new IllegalArgumentException("frequency '" + frequency + "'");
    }

    this.frequency = frequency;

    fireElementChanged(true);
  }

  public int getFrequency() {
    return frequency;
  }

  public void setAmplitude(int amplitude) {
    if (amplitude < 0 || amplitude > 127) {
      throw new IllegalArgumentException("amplitude '" + amplitude + "'");
    }

    this.amplitude = amplitude;

    fireElementChanged(true);
  }

  public int getAmplitude() {
    return amplitude;
  }
}