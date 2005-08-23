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
 * A player for a swell.
 */
public class SliderPlayer extends Player {

  public SliderPlayer(Slider slider) {
    super(slider);
  }

  public void messageReceived(ShortMessage shortMessage) {
    Slider slider = (Slider)getElement();

    Message message = slider.getMessage();
    if (message != null &&
        message.match(BugFix.getStatus(shortMessage), shortMessage.getData1(), shortMessage.getData2())) {

      int position;
      if (message.getData1() == -1) {
        position = shortMessage.getData1();
      } else if (message.getData2() == -1) {
        position = shortMessage.getData2();
      } else {
        return;
      }
      if (Math.abs(slider.getPosition() - position) > slider.getThreshold()) {
        slider.setPosition(position);
      }
    }
  }

  public void elementChanged(OrganEvent event) {
    Slider slider = (Slider)getElement();
    
    PlayerProblem problem = new PlayerProblem(PlayerProblem.WARNING, "message", null); 
    if (slider.getMessage() == null &&
        Configuration.instance().getWarnCrescendoWithoutMessage()) {
      addProblem(problem);
    } else {
      removeProblem(problem);
    }
  }
}