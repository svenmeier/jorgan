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

import java.util.*;

/**
 * A sound that delays all messages.
 */
public class DelayedSound extends SoundWrapper {

  private static DelayedInvoker invoker = new DelayedInvoker();

  private int delay;
  
  /**
   * Create a delayed sound.
   * 
   * @param sound   sound to wrap
   * @param delay   delay of sound
   */
  public DelayedSound(Sound sound, int delay) {
    super(sound);
    
    this.delay = delay;
  }

  public void noteOff(final int pitch) {
    new DelayedInvocation() {
      public void now() {
        sound.noteOff(pitch);
      }
    };
  }

  public void noteOn(final int pitch, final int velocity) {
    new DelayedInvocation() {
      public void now() {
        sound.noteOn(pitch, velocity);
      }
    };
  }

  public void setCutoff(final int cutoff) {
    new DelayedInvocation() {
      public void now() {
        sound.setCutoff(cutoff);
      }
    };
  }

  public void setModulation(final int amplitude, final int frequency) {
    new DelayedInvocation() {
      public void now() {
        sound.setModulation(amplitude, frequency);
      }
    };
  }

  public void setPan(final int pan) {
    new DelayedInvocation() {
      public void now() {
        sound.setPan(pan);
      }
    };
  }

  public void setBend(final int bend) {
    new DelayedInvocation() {
      public void now() {
        sound.setPitchBend(bend);
      }
    };
  }

  public void setProgram(final int program) {
    new DelayedInvocation() {
      public void now() {
        sound.setProgram(program);
      }
    };
  }

  public void setVolume(final int volume) {
    new DelayedInvocation() {
      public void now() {
        sound.setVolume(volume);
      }
    };
  }

  public void stop() {
    new DelayedInvocation() {
      public void now() {
        sound.stop();
      }
    };
  }

  private abstract class DelayedInvocation implements Comparable {

    private long when;
      
    public DelayedInvocation() {
      when = System.currentTimeMillis() + delay;

      invoker.delay(this); 
    }
    
    public int compareTo(Object object) {
      DelayedInvocation invocation = (DelayedInvocation)object;

      if (this.when < invocation.when) {
        return -1;
      } else if (this.when > invocation.when) {
        return 1;
      }
      return 0;
    }
    
    public abstract void now();
  }
  
  private static class DelayedInvoker implements Runnable {

    private List invocations = new ArrayList();
    
    public DelayedInvoker() {  
      Thread thread = new Thread(this, "DelayedSoundInvoker");
      thread.setDaemon(true);
      thread.start();
    }

    /**
     * Schedule an invocation for delayed invocation.
     * 
     * @param invocation   invocation to schedule
     */
    public synchronized void delay(DelayedInvocation invocation) {
      int index = 0;
      while (index < invocations.size() && ((DelayedInvocation)invocations.get(index)).compareTo(invocation) <= 0) {
        index++;
      }
      invocations.add(index, invocation);
      notify();
    }
    
    public synchronized void run() {
      while (true) {
        try {     
          if (invocations.size() == 0) {
            wait();
          } else {
            DelayedInvocation invocation = (DelayedInvocation)invocations.get(0);

            long timeout = invocation.when - System.currentTimeMillis();
            if (timeout <= 0) {
              invocations.remove(0);
              
              try {
                invocation.now();
              } catch (RuntimeException ex) {
                ex.printStackTrace();
              }
            } else {
              wait(timeout);
            }
          }        
        } catch (InterruptedException ex) {
          throw new Error("unexpected interruption", ex);
        }
      }
    }
  }
}