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
package jorgan.disposition;


/**
 * A keyer initiates a key press when {@link Switch#isEngaged()}.
 */
public class Keyer extends Activator {

	private int pitch = 64;

	private int velocity = 100;

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Keyable.class.isAssignableFrom(clazz);
	}

	public void setPitch(int pitch) {
		if (this.pitch != pitch) {
			int oldPitch = this.pitch;
			
			this.pitch = pitch;

			fireChange(new PropertyChange(oldPitch, this.pitch));
		}
	}

	public int getPitch() {
		return pitch;
	}

	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}

	public int getVelocity() {
		return velocity;
	}
}