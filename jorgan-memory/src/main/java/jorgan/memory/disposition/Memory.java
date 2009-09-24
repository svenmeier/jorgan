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
package jorgan.memory.disposition;

import jorgan.disposition.Combination;
import jorgan.disposition.Element;
import jorgan.disposition.IndexedContinuous;
import jorgan.util.Null;

public class Memory extends IndexedContinuous {

	private int size = 64;

	private String storage;

	public Memory() {
	}

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Combination.class.isAssignableFrom(clazz);
	}

	@Override
	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("size must be greater 0");
		}
		
		if (this.size != size) {
			int oldSize = this.size;

			this.size = size;

			fireChange(new PropertyChange(oldSize, size));
		}
	}

	public String getStorage() {
		return storage;
	}

	public void setStorage(String storage) {
		if (!Null.safeEquals(this.storage, storage)) {
			String oldStore = this.storage;

			this.storage = storage;

			fireChange(new PropertyChange(oldStore, this.storage));
		}
	}
}