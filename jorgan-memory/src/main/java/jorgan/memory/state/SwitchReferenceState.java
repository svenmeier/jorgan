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
package jorgan.memory.state;

import jorgan.disposition.Reference;
import jorgan.disposition.Switch;
import jorgan.disposition.Combination.SwitchReference;

/**
 * The state of a {@link SwitchReference}.
 */
public class SwitchReferenceState extends ReferenceState<Switch> {

	private boolean[] actives = new boolean[0];

	public SwitchReferenceState(Switch element) {
		super(element);
	}

	protected void ensureIndex(int index) {
		if (actives.length <= index) {
			boolean[] temp = new boolean[index + 1];

			System.arraycopy(actives, 0, temp, 0, actives.length);

			actives = temp;
		}
	}

	@Override
	public void clear(int index) {
		ensureIndex(index);
		
		actives[index] = false;
	}
	
	@Override
	public void swap(int index1, int index2) {
		ensureIndex(Math.max(index1, index2));

		boolean active1 = actives[index1];
		actives[index1] = actives[index2];
		actives[index2] = active1;
	}
	
	@Override
	public  void read(Reference<?> reference, int index) {
		ensureIndex(index);
		
		SwitchReference temp = (SwitchReference) reference;

		actives[index] = temp.isActive();
	}

	@Override
	public void write(Reference<?> reference, int index) {
		ensureIndex(index);

		SwitchReference temp = (SwitchReference) reference;

		temp.setActive(actives[index]);
	}
}