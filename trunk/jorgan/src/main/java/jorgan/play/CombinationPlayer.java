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
 * A player for a combination.
 */
public class CombinationPlayer extends Player {

  private static final Problem warningRecallMessage = new Problem(Problem.WARNING, "recallMessage"); 
  private static final Problem warningCaptureMessage = new Problem(Problem.WARNING, "captureMessage");
  
  private boolean armed = false;

  public CombinationPlayer(Combination combination) {
    super(combination);
  }

  public void messageReceived(ShortMessage message) {
    Combination combination = (Combination)getElement();

    Message captureMessage = combination.getCaptureMessage();
    if (captureMessage != null                   &&
        captureMessage.match(BugFix.getStatus(message), message.getData1(), message.getData2())) {
        
      fireInputAccepted();
      
      if (combination.isCaptureWithRecall()) {
        armed = true;
        return;
      } else {
        combination.capture();
      }
    }
    Message recallMessage = combination.getRecallMessage();
    if (recallMessage != null                   &&
        recallMessage.match(BugFix.getStatus(message), message.getData1(), message.getData2())) {

      fireInputAccepted();

      if (combination.isCaptureWithRecall() && armed) {
        combination.capture();
      } else {
        combination.recall();
      }
    }

    armed = false;
  }

  public void elementChanged(OrganEvent event) {

    Combination combination = (Combination)getElement();
    
    if (combination.getRecallMessage() == null &&
        Configuration.instance().getWarnWithoutMessage()) {
      addProblem(warningRecallMessage.value(null));
    } else {
      removeProblem(warningRecallMessage);
    }
    
    if (combination.getCaptureMessage() == null &&
        Configuration.instance().getWarnWithoutMessage()) {
      addProblem(warningCaptureMessage.value(null));
    } else {
      removeProblem(warningCaptureMessage);
    }
  }

  protected void closeImpl() {
    armed = false;
  }
}
