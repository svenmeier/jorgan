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
 * A player for a Counter.
 */
public class CounterPlayer extends Player {

  private static Problem warningMessage = new Problem(Problem.WARNING, "message");
  private static Problem warningNextMessage = new Problem(Problem.WARNING, "nextMessage");
  private static Problem warningPreviousMessage = new Problem(Problem.WARNING, "previousMessage");
  
  public CounterPlayer(Counter counter) {
    super(counter);
  }

  public void messageReceived(ShortMessage shortMessage) {
    Counter counter = (Counter)getElement();

    Message message = counter.getMessage();
    if (message != null  &&
        message.match(BugFix.getStatus(shortMessage), shortMessage.getData1(), shortMessage.getData2())) {
        
      int current = message.wildcard(shortMessage.getData1(), shortMessage.getData2());
      if (current != -1) {
        fireInputAccepted();

        counter.setCurrent(current);
      }
    }

    Message nextMessage = counter.getNextMessage();
    if (nextMessage != null &&
        nextMessage.match(BugFix.getStatus(shortMessage), shortMessage.getData1(), shortMessage.getData2())) {
        
      fireInputAccepted();
      
      counter.next();
    }
    
    Message previousMessage = counter.getPreviousMessage();
    if (previousMessage != null &&
        previousMessage.match(BugFix.getStatus(shortMessage), shortMessage.getData1(), shortMessage.getData2())) {

      fireInputAccepted();

      counter.previous();
    }
  }

  
  public void elementChanged(OrganEvent event) {
    Counter counter = (Counter)getElement();
    
    if (counter.getMessage() == null &&
        Configuration.instance().getWarnWithoutMessage()) {
      addProblem(warningMessage.value(null));
    } else {
      removeProblem(warningMessage);
    }       

    if (counter.getNextMessage() == null &&
        Configuration.instance().getWarnWithoutMessage()) {
      addProblem(warningNextMessage.value(null));
    } else {
      removeProblem(warningNextMessage);
    }
    
    if (counter.getNextMessage() == null &&
        Configuration.instance().getWarnWithoutMessage()) {
      addProblem(warningPreviousMessage.value(null));
    } else {
      removeProblem(warningPreviousMessage);
    }
  }
}