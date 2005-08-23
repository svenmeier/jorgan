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

public class CouplerPlayer extends KeyablePlayer {

  public CouplerPlayer(Coupler coupler) {
    super(coupler);
  }

  public void elementChanged(OrganEvent event) {

    Coupler coupler = (Coupler)getElement();
    
    PlayerProblem warnOnMessage  = new PlayerProblem(PlayerProblem.WARNING, "onMessage" , null); 
    if (coupler.getActivateMessage() == null &&
        Configuration.instance().getWarnCouplerWithoutMessage()) {
      addProblem(warnOnMessage);
    } else {
      removeProblem(warnOnMessage);
    }

    PlayerProblem warnOffMessage = new PlayerProblem(PlayerProblem.WARNING, "offMessage", null); 
    if (coupler.getDeactivateMessage() == null &&
        Configuration.instance().getWarnCouplerWithoutMessage()) {
      addProblem(warnOffMessage);
    } else {
      removeProblem(warnOffMessage);
    }

    super.elementChanged(event);
  }
  
  protected void activateKey(int pitch, int velocity) {
    Coupler coupler = (Coupler)getElement();
    if (coupler.getVelocity() != 0) {
      velocity = coupler.getVelocity();
    }
    for (int e = 0; e < coupler.getReferencesCount(); e++) {
      KeyablePlayer keyablePlayer = (KeyablePlayer)getOrganPlay().getPlayer(coupler.getReference(e).getElement());
      
      keyablePlayer.keyDown(pitch, velocity);
    }
  }
    
  protected void deactivateKey(int pitch) {
    Coupler coupler = (Coupler)getElement();
    for (int e = 0; e < coupler.getReferencesCount(); e++) {
      KeyablePlayer keyablePlayer = (KeyablePlayer)getOrganPlay().getPlayer(coupler.getReference(e).getElement());
      
      keyablePlayer.keyUp(pitch);
    }
  }   
}