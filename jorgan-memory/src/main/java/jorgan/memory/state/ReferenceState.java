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

import jorgan.disposition.Element;
import jorgan.disposition.Reference;

public abstract class ReferenceState<T extends Element> {

	private long id;

	public ReferenceState(T element) {
		this.id = element.getId();
	}

	protected abstract void ensureIndex(int index);

	public abstract void read(Reference<?> reference, int index);

	public abstract void write(Reference<?> reference, int index);

	public boolean isFor(Element element) {
		return this.id == element.getId();
	}

	public abstract void clear(int index);

	public abstract void swap(int index1, int index2);
}