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
package jorgan.gui.console;

import jorgan.disposition.Rank;
import jorgan.skin.ButtonLayer;

/**
 * A view for a rank.
 */
public class RankView extends View<Rank> {

	public static final String BINDING_ENGAGED = "engaged";

	/**
	 * Constructor.
	 * 
	 * @param rank
	 *            the rank to view
	 */
	public RankView(Rank rank) {
		super(rank);
	}

	@Override
	protected void initBindings() {
		super.initBindings();

		setBinding(BINDING_ENGAGED, new ButtonLayer.Binding() {
			public boolean isPressable() {
				return false;
			}

			public boolean isPressed() {
				return getElement().isEngaged();
			}

			public void pressed() {
			}

			public void released() {
			};
		});
	}
}