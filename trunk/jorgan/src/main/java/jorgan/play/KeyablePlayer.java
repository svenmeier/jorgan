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
package jorgan.play;

import jorgan.disposition.*;
import jorgan.disposition.event.*;

/**
 * A player for a keyable.
 */
public abstract class KeyablePlayer extends RegistratablePlayer {

  private static final int ACTIVATE_VELOCITY = 0;
  
  /**
   * The currently pressed keys.
   */
  private int[] pressedKeys = new int[128];
  
  private boolean activated = false;
  
  private Pitcher pitcher;
  
  public KeyablePlayer(Keyable keyable) {
    super(keyable);
  }

  protected void openImpl() {
    Keyable keyable = (Keyable)getElement();

    switch (keyable.getPitch()) {
      case Keyable.PITCH_DYNAMIC:
        pitcher = new DynamicPitcher();
        break;
      case Keyable.PITCH_CONSTANT:
          pitcher = new ConstantPitcher();
          break;
      case Keyable.PITCH_HIGHEST:
          pitcher = new HighestPitcher();
          break;
      case Keyable.PITCH_LOWEST:
          pitcher = new LowestPitcher();
          break;
      default:
          pitcher = new Pitcher();
    }
  }

  protected void closeImpl() {

    activated = false;
    pitcher = null;
      
    for (int p = 0; p < pressedKeys.length; p++) {
      pressedKeys[p] = 0;
    }
  }

  protected void activate() {
    synchronized (getLock()) {
      pitcher.activate();
    }
  }

  protected abstract void activateKey(int pitch, int velocity);

  protected void deactivate() {
    synchronized (getLock()) {
      pitcher.deactivate();
    }
  }
  
  protected abstract void deactivateKey(int pitch);

  public void keyDown(int pitch, int velocity) {

    Keyable keyable = (Keyable)getElement();
    pitch += keyable.getTranspose();

    if (pitch >= 0 && pitch <= 127) {
      synchronized (getLock()) {
        if (pressedKeys[pitch] == 0) {
          if (activated) {
            pitcher.activateKey(pitch, velocity);
          }
        }
        pressedKeys[pitch]++;
      }
    }
  }

  public void keyUp(int pitch) {
    Keyable keyable = (Keyable)getElement();

    pitch += keyable.getTranspose();
    if (pitch >= 0 && pitch <= 127) {
      synchronized (getLock()) {
        pressedKeys[pitch]--;
        if (pressedKeys[pitch] == 0) {
          if (activated) {
            pitcher.deactivateKey(pitch);
          }
        }
      }
    }
  }

  public void elementChanged(OrganEvent event) {
    Keyable keyable = (Keyable)getElement();
    
    if (isOpen()) {
      if (keyable.isOn() ^ keyable.isInverse()) {
        if (!activated) {
          activate();

          activated = true;
        }
      } else {
        if (activated) {
          deactivate();

          activated = false;
        }
      }      
    }
  }
  
  private class Pitcher {
    public void activateKey(int pitch, int velocity) { }
    public void deactivateKey(int pitch) { }
    public void activate() { }
    public void deactivate() { }
  }
  
  private class DynamicPitcher extends Pitcher {
    public void activateKey(int pitch, int velocity) {
      KeyablePlayer.this.activateKey(pitch, velocity);
    }

    public void deactivateKey(int pitch) {
      KeyablePlayer.this.deactivateKey(pitch);
    }

    public void activate() {
      for (int p = 0; p < pressedKeys.length; p++) {
        if (pressedKeys[p] > 0) {
          KeyablePlayer.this.activateKey(p, ACTIVATE_VELOCITY);
        }
      }
    }

    public void deactivate() {
      for (int p = 0; p < pressedKeys.length; p++) {
        if (pressedKeys[p] > 0) {
          KeyablePlayer.this.deactivateKey(p);
        }
      }
    }
  }
  
  private class HighestPitcher extends Pitcher {
    public void activateKey(int pitch, int velocity) {
      int highest = getHighestPitch();
      if (highest == -1 || pitch > highest) {
        if (highest != -1) {
          KeyablePlayer.this.deactivateKey(highest);
        }              
        KeyablePlayer.this.activateKey(pitch, velocity);
      }
    }

    public void deactivateKey(int pitch) {
      int highest = getHighestPitch();
      if (pitch > highest || highest == -1) {
        KeyablePlayer.this.deactivateKey(pitch);
        if (highest != -1) {
          KeyablePlayer.this.activateKey(highest, ACTIVATE_VELOCITY);
        }
      }
    }
    
    public void activate() {
      int highest = getHighestPitch();
      if (highest != -1) {
        KeyablePlayer.this.activateKey(highest, ACTIVATE_VELOCITY);
      }
    }
    
    public void deactivate() {
      int highest = getHighestPitch();
      if (highest != -1) {
        KeyablePlayer.this.deactivateKey(highest);
      }
    }
    
    private int getHighestPitch() {
      for (int p = pressedKeys.length - 1; p >= 0; p--) {
        if (pressedKeys[p] > 0) {
          return p;
        }
      }
      return -1;
    }      
  }
  
  private class LowestPitcher extends Pitcher {
    public void activateKey(int pitch, int velocity) {
      int lowest = getLowestPitch();
      if (lowest == -1 || pitch < lowest) {
        if (lowest != -1) {
          KeyablePlayer.this.deactivateKey(lowest);
        }                  
        KeyablePlayer.this.activateKey(pitch, velocity);
      }
    }
    
    public void deactivateKey(int pitch) {
      int lowest = getLowestPitch();
      if (pitch < lowest || lowest == -1) {
        KeyablePlayer.this.deactivateKey(pitch);
        if (lowest != -1) {
          KeyablePlayer.this.activateKey(lowest, ACTIVATE_VELOCITY);
        }
      }
    }
    
    public void activate() {
      int lowest = getLowestPitch();
      if (lowest != -1) {
        KeyablePlayer.this.activateKey(lowest, ACTIVATE_VELOCITY);
      }
    }
    
    public void deactivate() {
      int lowest = getLowestPitch();
      if (lowest != -1) {
        KeyablePlayer.this.deactivateKey(lowest);
      }
    }
    
    private int getLowestPitch() {
      for (int p = 0; p < pressedKeys.length; p++) {
        if (pressedKeys[p] > 0) {
          return p;
        }
      }
      return -1;
    }
  }
  
  private class ConstantPitcher extends Pitcher {
    public void activateKey(int pitch, int velocity) {
      if (!hasPitch()) {
        Keyable keyable = (Keyable)getElement();

        KeyablePlayer.this.activateKey(60 + keyable.getTranspose(), velocity);
      }
    }
      
    public void deactivateKey(int pitch) {
      if (!hasPitch()) {
        Keyable keyable = (Keyable)getElement();

        KeyablePlayer.this.deactivateKey(60 + keyable.getTranspose());
      }
    }
      
    public void activate() {
      if (hasPitch()) {
        Keyable keyable = (Keyable)getElement();

        KeyablePlayer.this.activateKey(60 + keyable.getTranspose(), ACTIVATE_VELOCITY);
      }
    }
      
    public void deactivate() {
      if (hasPitch()) {
        Keyable keyable = (Keyable)getElement();

        KeyablePlayer.this.deactivateKey(60 + keyable.getTranspose());
      }
    }
    
    private boolean hasPitch() {
      for (int p = 0; p < pressedKeys.length; p++) {
        if (pressedKeys[p] > 0) {
          return true;
        }
      }
      return false;
    }       
  }  
}