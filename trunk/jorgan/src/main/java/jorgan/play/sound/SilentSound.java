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
 * A silent sound that does nothing.
 */
public class SilentSound implements Sound {

  public void noteOff(int pitch) {
  }

  public void noteOn(int pitch, int velocity) {
  }

  public void setCutoff(int cutoff) {
  }

  public void setModulation(int amplitude, int frequency) {
  }

  public void setPan(int pan) {
  }

  public void setPitchBend(int bend) {
  }

  public void setBank(int bank) {
  }

  public void setProgram(int program) {
  }
  
  public void setVolume(int volume) {
  }

  public void stop() {
  }
}