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
 * A source of sounds.
 */
public class SoundSource extends Element {

  private String  device;
  private String  type;
  private int     bank  = 0;
  private String  samples;
  private int     delay = 0; 

  public String getDevice() {
    return device;
  }

  public void setDevice(String device) {
    this.device = device;

    fireElementChanged(true);
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;

    fireElementChanged(true);
  }

  public int getDelay() {
    return delay;
  }

  public void setDelay(int delay) {
    if (delay < 0) {
      throw new IllegalArgumentException("delay '" + delay + "'");
    }
    this.delay = delay;

    fireElementChanged(true);
  }

  public int getBank() {
    return bank;
  } 

  public void setBank(int bank) {
  	if (bank < 0 || bank > 127) {
  		throw new IllegalArgumentException("bank '" + bank + "'");
  	}
    this.bank = bank;

    fireElementChanged(true);
  }
  
  public String getSamples() {
  	return samples;
  }
  
  public void setSamples(String samples) {
  	this.samples = samples;
  }
}