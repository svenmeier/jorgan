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
 * A player of an console.
 */
public class ConsolePlayer extends Player {

  /**
   * The midiDevice to receive input from.
   */
  private MidiDevice in;
  
  /**
   * The transmitter of the opened midiDevice.
   */
  private Transmitter transmitter;
  
  public ConsolePlayer(Console console) {
    super(console);
  }

  protected void openImpl() {
    Console console = (Console)getElement();

    String device = console.getDevice();
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
    if (transmitter != null) {
      transmitter.close();
      in.close();

      transmitter = null;
      in          = null;
    }
  }

  public void elementChanged(OrganEvent event) {
    Console console = (Console)getElement();

    PlayerProblem warnDevice = new PlayerProblem(PlayerProblem.WARNING, "device", null); 
    if (console.getDevice() == null && Configuration.instance().getWarnWithoutDevice()) {
      addProblem(warnDevice);
    } else {
      removeProblem(warnDevice);
    }
  }

  protected void input(ShortMessage message) {
    Console console = (Console)getElement();

    for (int r = 0; r < console.getReferenceCount(); r++) {
      Reference reference = console.getReference(r);
      
      Player player = getOrganPlay().getPlayer(reference.getElement());
      if (player != null) {
        player.messageReceived(message);
      }
    }
  }     
}