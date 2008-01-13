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
import jorgan.disposition.Keyer;
import jorgan.disposition.event.OrganEvent;

/**
 * A player of a keyer.
 */
public class KeyerPlayer extends SwitchPlayer<Keyer> {

    private boolean keying = false;

    public KeyerPlayer(Keyer keyer) {
        super(keyer);
    }

    @Override
	protected void closeImpl() {
        super.closeImpl();

        keying = false;
    }

    @Override
	public void elementChanged(OrganEvent event) {
        super.elementChanged(event);

        if (isOpen()) {
            Keyer keyer = getElement();

            if (keyer.isEngaged()) {
                if (!keying) {
                    for (int e = 0; e < keyer.getReferenceCount(); e++) {
                        Element element = keyer.getReference(e).getElement();

                        Player player = getOrganPlay().getPlayer(element);
                        if (player != null) {
                            ((KeyablePlayer) player).keyDown(keyer.getPitch(),
                                    keyer.getVelocity());
                        }
                    }
                    keying = true;
                }
            } else {
                if (keying) {
                    for (int e = 0; e < keyer.getReferenceCount(); e++) {
                        Element element = keyer.getReference(e).getElement();

                        Player player = getOrganPlay().getPlayer(element);
                        if (player != null) {
                            ((KeyablePlayer) player).keyUp(keyer.getPitch());
                        }
                    }
                    keying = false;
                }
            }
        }
    }
}