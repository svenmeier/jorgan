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

import jorgan.disposition.Element;
import jorgan.disposition.Message;
import jorgan.disposition.SoundEffect;
import jorgan.disposition.SoundSource;
import jorgan.disposition.Stop;
import jorgan.disposition.event.OrganEvent;
import jorgan.play.sound.SilentSound;
import jorgan.play.sound.Sound;

/**
 * A player for a stop.
 */
public class StopPlayer extends KeyablePlayer {

  private Sound sound;
    
  public StopPlayer(Stop stop) {
    super(stop);
  }
  
  protected void closeImpl() {

    super.closeImpl();

    Stop stop = (Stop)getElement();

    sound = null;
    
    removeProblem(programWarning(stop.getProgram()));
  }
  
  protected void activate() {

    Stop stop = (Stop)getElement();

    boolean silentSound = false;
    
    for (int r = 0; r < stop.getReferencesCount(); r++) {
      Element element = stop.getReference(r).getElement();
            
      if (element instanceof SoundSource) {
        SoundSourcePlayer soundSourcePlayer = (SoundSourcePlayer)getOrganPlay().getPlayer(element);
              
        sound = soundSourcePlayer.createSound(stop.getProgram());
        if (sound != null) {
          break;
        }
      }
    }
      
    if (sound == null) {
      sound = new SilentSound();
        
      silentSound = true;
    }
      
    for (int r = 0; r < stop.getReferencesCount(); r++) {
      Element element = stop.getReference(r).getElement();
          
      if (element instanceof SoundEffect) {
        SoundEffectPlayer soundEffectPlayer = (SoundEffectPlayer)getOrganPlay().getPlayer(element);
         
        sound = soundEffectPlayer.effectSound(sound);
      }
    }

    sound.setProgram(stop.getProgram());
    sound.setVolume(stop.getVolume());
    if (stop.getPan() != 64) {
      sound.setPan(stop.getPan());
    }
    if (stop.getBend() != 64) {
      sound.setPitchBend(stop.getBend());
    }

    fireOutputProduced();

    if (silentSound) {
      addProblem(programWarning(stop.getProgram()));
    } else {
      removeProblem(programWarning(stop.getProgram()));
    }

    super.activate();    
  }

  protected void activateKey(int pitch, int velocity) {
    if (sound != null) {
      Stop stop = (Stop)getElement();
      if (stop.getVelocity() != 0) {
          velocity = stop.getVelocity();
        }
        
      sound.noteOn(pitch, velocity);        

      fireOutputProduced();
    }
  }
  
  protected void deactivate() {
    super.deactivate();

    Stop stop = (Stop)getElement();

    sound.stop();
    sound = null;

    fireOutputProduced();
    
    removeProblem(programWarning(stop.getProgram()));
  }
  
  private PlayerProblem programWarning(int program) {
      
    return new PlayerProblem(PlayerProblem.WARNING, "program", new Integer(program));
  }

  private PlayerProblem onMessageWarning(Message message) {
      
    return new PlayerProblem(PlayerProblem.WARNING, "onMessage", message);
  }

  private PlayerProblem offMessageWarning(Message message) {
      
    return new PlayerProblem(PlayerProblem.WARNING, "offMessage", message);
  }

  protected void deactivateKey(int pitch) {
    if (sound != null) {
      sound.noteOff(pitch);
      
      fireOutputProduced();
    }
  }
  
  public void elementChanged(OrganEvent event) {
    Stop stop = (Stop)getElement();
      
    if (stop.getOnMessage() == null &&
        Configuration.instance().getWarnStopWithoutMessage()) {
      addProblem(onMessageWarning(stop.getOnMessage()));
    } else {
      removeProblem(onMessageWarning(stop.getOnMessage()));
    }

    if (stop.getOffMessage() == null &&
        Configuration.instance().getWarnStopWithoutMessage()) {
      addProblem(offMessageWarning(stop.getOffMessage()));
    } else {
      removeProblem(offMessageWarning(stop.getOffMessage()));
    }

    super.elementChanged(event);
  }
}
