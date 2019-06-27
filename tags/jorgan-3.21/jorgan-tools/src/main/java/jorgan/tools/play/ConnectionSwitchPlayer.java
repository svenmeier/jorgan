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

import jorgan.disposition.Element;
import jorgan.disposition.Input.InputMessage;
import jorgan.midi.mpl.Context;
import jorgan.play.Player;
import jorgan.play.SwitchPlayer;
import jorgan.tools.disposition.ConnectionSwitch;

/**
 * A player for a {@link ConnectionSwitch}.
 */
public class ConnectionSwitchPlayer extends SwitchPlayer<ConnectionSwitch> {

	private transient boolean onInput;

	public ConnectionSwitchPlayer(ConnectionSwitch element) {
		super(element);
	}

	@Override
	public void onReceived(byte[] datas) {
		onInput = false;

		super.onReceived(datas);

		if (onInput == false && getElement().isEngaged()) {
			for (Element element : getElement().getReferenced(Element.class)) {
				Player<?> player = getPlayer(element);

				if (player != null) {
					player.onReceived(datas);
				}
			}
		}
	}

	@Override
	protected void onInput(InputMessage message, Context context) {
		super.onInput(message, context);

		onInput = true;
	}
}
