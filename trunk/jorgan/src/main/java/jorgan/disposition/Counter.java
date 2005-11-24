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

public abstract class Counter extends Responsive {

  private int current = 0;

  private Message previousMessage;
  private Message nextMessage;
  private Message message;
  
  public int getCurrent() {
    return current;
  }

  public void setCurrent(int current) {
    if (current < 0 || current > 127) {
      throw new IllegalArgumentException("current must be between 0 and 127"); 
    }
    this.current = current;
      
    fireElementChanged(false);
  }
  
  public Message getNextMessage() {
    return nextMessage;
  }

  public void setNextMessage(Message message) {
    this.nextMessage = message;

    fireElementChanged(true);
  }
  
  public Message getPreviousMessage() {
    return previousMessage;
  }

  public void setPreviousMessage(Message message) {
    this.previousMessage = message;

    fireElementChanged(true);
  }
  
  public Message getMessage() {
    return message;
  }

  public void setMessage(Message message) {
    this.message = message;

    fireElementChanged(true);
  }    
  
  public void next() {
    change(+1);
  }
    
  public void previous() {
    change(-1);
  }

  protected void change(int delta) {
    setCurrent((128 + current + delta) % 128);
  }    
}