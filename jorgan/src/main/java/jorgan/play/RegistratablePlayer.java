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

/**
 * An abstract base class for players that control registratable elements.
 */
public abstract class RegistratablePlayer extends Player {

  public RegistratablePlayer(Registratable registratable) {
    super(registratable);
  }

  public void messageReceived(ShortMessage message) {
    Registratable registratable = (Registratable)getElement();

    if (registratable.isOn()) {
      Message offMessage = registratable.getOffMessage();
      if (offMessage != null &&
          offMessage.match(BugFix.getStatus(message), message.getData1(), message.getData2())) {
        registratable.setOn(false);
      }
    } else {
      Message onMessage = registratable.getOnMessage();
      if (onMessage != null &&
          onMessage.match(BugFix.getStatus(message), message.getData1(), message.getData2())) {
        registratable.setOn(true);
      }
    }
  }
}
