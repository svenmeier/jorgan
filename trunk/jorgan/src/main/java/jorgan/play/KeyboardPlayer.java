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

import javax.sound.midi.*;

import jorgan.sound.midi.*;

import jorgan.disposition.*;
import jorgan.disposition.event.*;

/**
 * A player of an keyboard.
 */
public class KeyboardPlayer extends Player {

  /**
   * The currently pressed keys.
   */
  private boolean[] pressedKeys = new boolean[128];

  /**
   * The midiDevice to receive input from.
   */
  private MidiDevice in;
  
  /**
   * The transmitter of the opened midiDevice.
   */
  private Transmitter transmitter;
  
  public KeyboardPlayer(Keyboard keyboard) {
    super(keyboard);
  }

  protected void openImpl() {
    Keyboard keyboard = (Keyboard)getElement();

    String device = keyboard.getDevice();
    if (device != null) {
      PlayerProblem errorDevice = new PlayerProblem(PlayerProblem.ERROR, "device", device);
      try {
        in = DevicePool.getMidiDevice(device, false);
        in.open();

        transmitter = in.getTransmitter();
        transmitter.setReceiver(getOrganPlay().createReceiver(this));

        removeProblem(errorDevice);
      } catch (MidiUnavailableException ex) {
        addProblem(errorDevice);
      }
    }
  }

  protected void closeImpl() {
    if (in != null) {
      transmitter.close();
      in.close();

      transmitter = null;
      in          = null;
    }
    
    for (int p = 0; p < pressedKeys.length; p++) {
      pressedKeys[p] = false;
    }
  }

  public void elementChanged(OrganEvent event) {
    Keyboard keyboard = (Keyboard)getElement();

    PlayerProblem warnDevice = new PlayerProblem(PlayerProblem.WARNING, "device", null); 
    if (keyboard.getDevice() == null && Configuration.instance().getWarnWithoutDevice()) {
      addProblem(warnDevice);
    } else {
      removeProblem(warnDevice);
    }
  }

  protected void input(ShortMessage message) {
    Keyboard keyboard = (Keyboard)getElement();

    if (isNoteMessage(message) && keyboard.getChannel() == message.getChannel()) {

      int command  = message.getCommand();
      int pitch    = message.getData1();
      int velocity = message.getData2();

      Key key = new Key(pitch);
      if ((keyboard.getFrom() == null || keyboard.getFrom().lessEqual   (key)) &&
          (keyboard.getTo()   == null || keyboard.getTo()  .greaterEqual(key))) {

        pitch += keyboard.getTranspose();

        if (command == keyboard.getCommand()) {
          if (velocity > keyboard.getThreshold()) {
            keyDown(pitch, velocity);
          } else {
            keyUp(pitch);
          }
          fireInputAccepted();
        } else {
          if (command == ShortMessage.NOTE_OFF ||
              command == ShortMessage.NOTE_ON && velocity == 0) {
            keyUp(pitch);

            fireInputAccepted();
          }
        }
      }
    }
  }
    
  protected boolean isNoteMessage(ShortMessage message) {
    int status = message.getStatus();

    return (status >= 0x80 && status < 0xb0);
  }

  protected void keyDown(int pitch, int velocity) {
    if (pitch >= 0 && pitch <= 127 && !pressedKeys[pitch]) {
      pressedKeys[pitch] = true;
        
      Keyboard keyboard = (Keyboard)getElement();

      for (int e = 0; e < keyboard.getReferenceCount(); e++) {
        Element element = keyboard.getReference(e).getElement();

        Player player = getOrganPlay().getPlayer(element);
        if (player != null) {
          ((KeyablePlayer)player).keyDown(pitch, velocity);
        }
      }
    }
  }

  protected void keyUp(int pitch) {
    if (pitch >= 0 && pitch <= 127 && pressedKeys[pitch]) {
      pressedKeys[pitch] = false;
          
      Keyboard keyboard = (Keyboard)getElement();

      for (int e = 0; e < keyboard.getReferenceCount(); e++) {
        Element element = keyboard.getReference(e).getElement();

        Player player = getOrganPlay().getPlayer(element);
        if (player != null) {
          ((KeyablePlayer)player).keyUp(pitch);
        }
      }
    }
  }
}