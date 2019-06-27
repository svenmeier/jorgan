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
package jorgan.tools.disposition;

import jorgan.disposition.Element;
import jorgan.disposition.Rank;
import jorgan.disposition.Switch;

public class RepeatSwitch extends Switch {

	private int velocity = 100;

	public RepeatSwitch() {
		setDuration(DURATION_NONE);
	}

	public int getVelocity() {
		return velocity;
	}

	public void setVelocity(int velocity) {
		if (velocity < 0 || velocity > 127) {
			throw new IllegalArgumentException();
		}

		if (this.velocity != velocity) {
			int oldVelocity = this.velocity;

			this.velocity = velocity;

			fireChange(new PropertyChange(oldVelocity, this.velocity));
		}
	}

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Rank.class.isAssignableFrom(clazz);
	}
}