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
 * A slider.
 */
public abstract class Slider extends Responsive {

  private Message message;

  private boolean locking = true;
  
  private int position = 127;
  
  private int threshold = 0;
  
  public boolean isLocking() {
    return locking;
  }

  public void setLocking(boolean locking) {
    this.locking = locking;
          
    fireElementChanged(true);
  }

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

  public void setPosition(int position) {
    this.position = Math.max(0, Math.min(127, position));

    fireElementChanged(false);
  }

  public int getPosition() {
    return position;
  }


  public int getThreshold() {
    return threshold;
  }
    
  public void setThreshold(int granularity) {
    this.threshold = granularity;
    
    fireElementChanged(true);
  }
}