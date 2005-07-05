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

import java.util.*;
import javax.sound.midi.ShortMessage;

import jorgan.sound.midi.BugFix;

import jorgan.disposition.*;
import jorgan.disposition.event.*;
import jorgan.play.sound.*;

/**
 * A player for a swell.
 */
public class SwellPlayer extends Player implements SoundEffectPlayer {

  private List sounds = new ArrayList();

  public SwellPlayer(Swell swell) {
    super(swell);
  }

  public void messageReceived(ShortMessage shortMessage) {
    Swell swell = (Swell)getElement();

    Message message = swell.getMessage();
    if (message != null &&
        message.match(BugFix.getStatus(shortMessage), shortMessage.getData1(), shortMessage.getData2())) {

      if (message.getData1() == -1) {
        swell.setPosition(shortMessage.getData1());
      } else if (message.getData2() == -1) {
        swell.setPosition(shortMessage.getData2());
      } 
    }
  }

  public void elementChanged(OrganEvent event) {
    Swell swell = (Swell)getElement();
    
    PlayerProblem problem = new PlayerProblem(PlayerProblem.WARNING, "message", null); 
    if (swell.getMessage() == null &&
        Configuration.instance().getWarnSwellWithoutMessage()) {
      addProblem(problem);
    } else {
      removeProblem(problem);
    }

    if (isOpen()) {
      for (int s = 0; s < sounds.size(); s++) {
        SwellSound sound = (SwellSound)sounds.get(s);
        sound.flush();
      }
    }
  }

  public Sound effectSound(Sound sound) {

    return new SwellSound(sound);
  }

  private class SwellSound extends SoundWrapper {
    
    private int volume = 127;
    
    public SwellSound(Sound sound) {
      super(sound);

      sounds.add(this);
    }

    public void setVolume(int volume) {
      this.volume = volume;
      
      flush();
    }

    public void stop() {
      super.stop();

      sounds.remove(this);
    }
    
    private void flush() {
      Swell swell = (Swell)getElement();

      sound.setVolume((swell.getVolume() + (swell.getPosition() * (127 - swell.getVolume()) / 127)) * volume / 127);

      // Change cutoff only if lower than max value, so user can choose to not use
      // this feature in case of soundfonts with preset cutoff values.
      if (swell.getCutoff() < 127) {
        sound.setCutoff(swell.getCutoff() + (swell.getPosition() * (127 - swell.getCutoff()) / 127));
      } 

      fireOutputProduced();
    }
  }
}
