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

import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Combination;
import jorgan.disposition.Continuous;
import jorgan.disposition.Reference;
import jorgan.disposition.Switch;

/**
 * The state of a {@link Combination}.
 */
public class CombinationState {

	private long id;

	private List<ReferenceState<?>> references = new ArrayList<ReferenceState<?>>();

	public CombinationState(Combination combination) {
		this.id = combination.getId();
	}

	public boolean isFor(Combination combination) {
		return this.id == combination.getId();
	}

	public void clear(int index) {
		for (ReferenceState<?> state : references) {
			state.clear(index);
		}
	}

	private ReferenceState<?> getState(Reference<?> reference) {
		for (ReferenceState<?> state : references) {
			if (state.isFor(reference.getElement())) {
				return state;
			}
		}

		ReferenceState<?> state;
		if (reference.getElement() instanceof Switch) {
			state = new SwitchReferenceState((Switch) reference.getElement());
		} else if (reference.getElement() instanceof Continuous) {
			state = new ContinuousReferenceState((Continuous) reference
					.getElement());
		} else {
			throw new Error();
		}
		references.add(state);

		return state;
	}

	public void read(Combination combination, int index) {
		for (Reference<?> reference : combination.getReferences()) {
			ReferenceState<?> state = getState(reference);
			state.read(reference, index);
		}
	}

	public void write(Combination combination, int index) {
		for (Reference<?> reference : combination.getReferences()) {
			ReferenceState<?> state = getState(reference);
			state.write(reference, index);
		}
	}
}