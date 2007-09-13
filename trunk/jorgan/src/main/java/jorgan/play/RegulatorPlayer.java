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

import jorgan.disposition.Regulator;
import jorgan.disposition.event.OrganEvent;

/**
 * A player for a {@link Regulator}.
 */
public class RegulatorPlayer extends ContinuousPlayer<Regulator> {

    private ActivateablePlayer player;

    public RegulatorPlayer(Regulator regulator) {
        super(regulator);
    }

    @Override
	protected void closeImpl() {
        super.closeImpl();

        player = null;
    }

    @Override
	public void elementChanged(OrganEvent event) {
        super.elementChanged(event);

        if (isOpen()) {
            Regulator regulator = getElement();

            if (regulator.getReferenceCount() > 0) {
                int current = (regulator.getValue()
                        * regulator.getReferenceCount() / 128);

                ActivateablePlayer player = (ActivateablePlayer) getOrganPlay()
                        .getPlayer(regulator.getReference(current).getElement());

                if (player != this.player) {
                    if (player != null) {
                        player.activate();
                    }

                    if (this.player != null) {
                        this.player.deactivate();
                    }

                    this.player = player;
                }
            }
        }
    }
}