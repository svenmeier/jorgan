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
 * A swell.
 */
public class Swell extends Active implements SoundEffect {

  private Message message;
  private int volume   = 64;
  private int cutoff   = 64;
  private int position = 127;

  public Message getMessage() {
    return message;
  }

  public void setMessage(Message message) {
    if (message != null && !message.hasWildcard()) {
      message = new Message(message.getStatus(), message.getData1(), -1);
    }
    this.message = message;

    fireElementChanged(true);
  }

  public void setVolume(int volume) {
    if (volume < 0 || volume > 127) {
      throw new IllegalArgumentException("volume '" + volume + "'");
    }

    this.volume = volume;

    fireElementChanged(true);
  }

  public int getVolume() {
    return volume;
  }

  public void setCutoff(int cutoff) {
    if (cutoff < 0 || cutoff > 127) {
      throw new IllegalArgumentException("cutoff '" + cutoff + "'");
    }

    this.cutoff = cutoff;

    fireElementChanged(true);
  }

  public int getCutoff() {
    return cutoff;
  }

  public void setPosition(int position) {
    // a swell may be frequently updated so
    // change position only if needed
    if (this.position != position) {
      this.position = Math.max(0, Math.min(127, position));

      fireElementChanged(false);
    }
  }

  public int getPosition() {
    return position;
  }
}