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

import javax.sound.midi.ShortMessage;

import jorgan.sound.midi.BugFix;

import jorgan.disposition.*;
import jorgan.disposition.event.*;

/**
 * A player for a piston.
 */
public class PistonPlayer extends Player {

  private boolean armed = false;

  public PistonPlayer(Piston piston) {
    super(piston);
  }

  public void messageReceived(ShortMessage message) {
    Piston piston = (Piston)getElement();

    Message setMessage = piston.getSetMessage();
    if (setMessage != null                   &&
        setMessage.match(BugFix.getStatus(message), message.getData1(), message.getData2())) {
      if (piston.isSetWithGet()) {
        armed = true;
        return;
      } else {
        piston.set();
      }
    }
    Message getMessage = piston.getGetMessage();
    if (getMessage != null                   &&
        getMessage.match(BugFix.getStatus(message), message.getData1(), message.getData2())) {
      if (piston.isSetWithGet() && armed) {
        piston.set();
      } else {
        piston.get();
      }
    }

    armed = false;
  }

  public void set() {
    Piston piston = (Piston)getElement();

    piston.set();
  }

  public void get() {
    Piston piston = (Piston)getElement();

    piston.get();
  }

  public void elementChanged(OrganEvent event) {

    Piston piston = (Piston)getElement();
    
    PlayerProblem warnGetMessage = new PlayerProblem(PlayerProblem.WARNING, "getMessage", null); 
    if (piston.getGetMessage() == null &&
        Configuration.instance().getWarnPistonWithoutMessage()) {
      addProblem(warnGetMessage);
    } else {
      removeProblem(warnGetMessage);
    }
    
    PlayerProblem warnSetMessage = new PlayerProblem(PlayerProblem.WARNING, "setMessage", null); 
    if (piston.getSetMessage() == null &&
        Configuration.instance().getWarnPistonWithoutMessage()) {
      addProblem(warnSetMessage);
    } else {
      removeProblem(warnSetMessage);
    }
  }

  protected void closeImpl() {
    armed = false;
  }
}
