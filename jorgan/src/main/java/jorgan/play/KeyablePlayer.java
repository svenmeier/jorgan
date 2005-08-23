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
public abstract class KeyablePlayer extends ActivateablePlayer {

  private static final int ACTIVATE_VELOCITY = 0;
  
  /**
   * The currently pressed keys.
   */
  private int[] pressedKeys = new int[128];
  
  private Action action;
  
  public KeyablePlayer(Keyable keyable) {
    super(keyable);
  }

  protected void openImpl() {
    Keyable keyable = (Keyable)getElement();

    switch (keyable.getAction()) {
      case Keyable.ACTION_STRAIGHT:
        action = new Action();
        break;
      case Keyable.ACTION_INVERSE:
        action = new InverseAction();
        break;
      case Keyable.ACTION_PITCH_CONSTANT:
        action = new ConstantPitchAction();
        break;
      case Keyable.ACTION_PITCH_HIGHEST:
        action = new HighestPitchAction();
        break;
      case Keyable.ACTION_PITCH_LOWEST:
        action = new LowestPitchAction();
        break;
      case Keyable.ACTION_SUSTAIN:
        action = new SustainAction();
        break;
      case Keyable.ACTION_SOSTENUTO:
        action = new SostenutoAction();
        break;
      default:
        throw new Error("unexpected keyable action '" + keyable.getAction() + "'");
    }
  }

  protected void closeImpl() {
    super.closeImpl();
    
    action = null;
      
    for (int p = 0; p < pressedKeys.length; p++) {
      pressedKeys[p] = 0;
    }
  }

  protected void activate() {
  }

  protected abstract void activateKey(int pitch, int velocity);

  protected abstract void deactivateKey(int pitch);

  protected void deactivate() {
  }
  
  public void keyDown(int pitch, int velocity) {

    Keyable keyable = (Keyable)getElement();
    pitch += keyable.getTranspose();

    if (pitch >= 0 && pitch <= 127) {
      if (pressedKeys[pitch] == 0) {
        action.activateKey(pitch, velocity);
      }
      pressedKeys[pitch]++;
    }
  }

  public void keyUp(int pitch) {
    Keyable keyable = (Keyable)getElement();

    pitch += keyable.getTranspose();
    if (pitch >= 0 && pitch <= 127) {
      pressedKeys[pitch]--;
      if (pressedKeys[pitch] == 0) {
        action.deactivateKey(pitch);
      }
    }
  }

  public void elementChanged(OrganEvent event) {   
    if (isOpen()) {
      action.changed();
    }
  }
  
  private class Action {
    protected boolean activated = false;
  
    public void changed() {
      if (shouldActivate()) {
        if (!activated) {
          activated = true;
          activate();
        }
      } else {
        if (activated) {
          deactivate();
          activated = false;
        }
      }  
    }

    protected boolean shouldActivate() {
      return isActive();
    }

    public void activateKey(int pitch, int velocity) {
      if (activated) {
        KeyablePlayer.this.activateKey(pitch, velocity);
      }
    }

    public void deactivateKey(int pitch) {
      if (activated) {
        KeyablePlayer.this.deactivateKey(pitch);
      }
    }

    public void activate() {
      KeyablePlayer.this.activate();

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

      KeyablePlayer.this.deactivate();
    }
  }
  
  private class InverseAction extends Action {
    protected boolean shouldActivate() {
      return !isActive();
    }
  }

  private class HighestPitchAction extends Action {
    public void activateKey(int pitch, int velocity) {
      if (activated) {
        int highest = getHighestPitch();
        if (highest == -1 || pitch > highest) {
          if (highest != -1) {
            KeyablePlayer.this.deactivateKey(highest);
          }              
          KeyablePlayer.this.activateKey(pitch, velocity);
        }
      }
    }

    public void deactivateKey(int pitch) {
      if (activated) {
        int highest = getHighestPitch();
        if (pitch > highest || highest == -1) {
          KeyablePlayer.this.deactivateKey(pitch);
          if (highest != -1) {
            KeyablePlayer.this.activateKey(highest, ACTIVATE_VELOCITY);
          }
        }
      }
    }
    
    public void activate() {
      KeyablePlayer.this.activate();

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

      KeyablePlayer.this.deactivate();
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
  
  private class LowestPitchAction extends Action {
    public void activateKey(int pitch, int velocity) {
      if (activated) {
        int lowest = getLowestPitch();
        if (lowest == -1 || pitch < lowest) {
          if (lowest != -1) {
            KeyablePlayer.this.deactivateKey(lowest);
          }                  
          KeyablePlayer.this.activateKey(pitch, velocity);
        }
      }
    }
    
    public void deactivateKey(int pitch) {
      if (activated) {
        int lowest = getLowestPitch();
        if (pitch < lowest || lowest == -1) {
          KeyablePlayer.this.deactivateKey(pitch);
          if (lowest != -1) {
            KeyablePlayer.this.activateKey(lowest, ACTIVATE_VELOCITY);
          }
        }
      }
    }
    
    public void activate() {
      KeyablePlayer.this.activate();

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

      KeyablePlayer.this.deactivate();
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
  
  private class ConstantPitchAction extends Action {
    public void activateKey(int pitch, int velocity) {
      if (activated) {
        if (!hasPitch()) {
          Keyable keyable = (Keyable)getElement();

          KeyablePlayer.this.activateKey(60 + keyable.getTranspose(), velocity);
        }
      }
    }
      
    public void deactivateKey(int pitch) {
      if (activated) {
        if (!hasPitch()) {
          Keyable keyable = (Keyable)getElement();

          KeyablePlayer.this.deactivateKey(60 + keyable.getTranspose());
        }
      }
    }
      
    public void activate() {
      KeyablePlayer.this.activate();

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

      KeyablePlayer.this.deactivate();
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

  private class SustainAction extends SostenutoAction {

    public void activateKey(int pitch, int velocity) {
      super.activateKey(pitch, velocity);
      
      if (activated) {
        stuckKeys[pitch] = true;
      }
    }
  }

  private class SostenutoAction extends Action {
    protected boolean[] stuckKeys = new boolean[128];

    public void activateKey(int pitch, int velocity) {
      if (!activated || !stuckKeys[pitch]) {
        KeyablePlayer.this.activateKey(pitch, velocity);
      }
    }

    public void deactivateKey(int pitch) {
      if (!activated || !stuckKeys[pitch]) {
        KeyablePlayer.this.deactivateKey(pitch);
      }
    }

    public void activate() {
      for (int k = 0; k < pressedKeys.length; k++) {
        stuckKeys[k] = pressedKeys[k] > 0;
      }
    }

    public void deactivate() {
      for (int k = 0; k < pressedKeys.length; k++) {
        if (stuckKeys[k] && pressedKeys[k] == 0) {
          KeyablePlayer.this.deactivateKey(k);
        }
      }
    }
  }
}