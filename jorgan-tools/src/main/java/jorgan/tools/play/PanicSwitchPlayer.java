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
package jorgan.tools.play;

import jorgan.disposition.Keyboard;
import jorgan.play.KeyboardPlayer;
import jorgan.play.SwitchPlayer;
import jorgan.tools.disposition.PanicSwitch;
import jorgan.util.Null;

/**
 * A player for a {@link PanicSwitch}.
 */
public class PanicSwitchPlayer extends SwitchPlayer<PanicSwitch> {

	private boolean engaged = false;

	public PanicSwitchPlayer(PanicSwitch panic) {
		super(panic);
	}

	@Override
	protected void openImpl() {
		engaged = false;

		super.openImpl();
	}

	@Override
	public void update() {
		super.update();

		if (isOpen()) {
			boolean engaged = getElement().isEngaged();
			if (!Null.safeEquals(this.engaged, engaged)) {
				if (!engaged) {
					for (Keyboard keyboard : getElement().getReferenced(
							Keyboard.class)) {
						((KeyboardPlayer) getPlayer(keyboard)).panic();
					}
				}
				this.engaged = engaged;
			}
		}
	}
}
