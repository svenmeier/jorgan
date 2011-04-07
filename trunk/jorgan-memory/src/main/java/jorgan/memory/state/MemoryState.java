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
import jorgan.disposition.Reference;
import jorgan.memory.disposition.Memory;

/**
 * The state of a {@link Memory}.
 */
public class MemoryState {

	private List<String> titles = new ArrayList<String>();

	private List<CombinationState> combinations = new ArrayList<CombinationState>();

	public MemoryState() {
	}

	private void ensureIndex(List<?> list, int index) {
		while (list.size() <= index) {
			list.add(null);
		}
	}

	private CombinationState getState(Combination combination) {
		for (CombinationState state : combinations) {
			if (state.isFor(combination)) {
				return state;
			}
		}

		CombinationState state = new CombinationState(combination);
		combinations.add(state);

		return state;
	}

	public String getTitle(int index) {
		ensureIndex(titles, index);

		String title = titles.get(index);
		if (title == null) {
			title = "";
		}

		return title;
	}

	public void setTitle(int index, String title) {
		ensureIndex(titles, index);

		titles.set(index, title);
	}

	public void clear(int index) {
		setTitle(index, null);

		for (CombinationState state : combinations) {
			state.clear(index);
		}
	}

	public void swap(int index1, int index2) {
		String title1 = getTitle(index1);
		setTitle(index1, getTitle(index2));
		setTitle(index2, title1);

		for (CombinationState state : combinations) {
			state.swap(index1, index2);
		}
	}

	/**
	 * Write the state into all combinations.
	 */
	public void write(Memory memory, int index) {
		for (Combination combination : memory.getReferenced(Combination.class)) {
			CombinationState state = getState(combination);

			state.write(combination, index);
		}
	}

	/**
	 * Read the state from all combinations.
	 */
	public void read(Memory memory, int index) {
		for (Combination combination : memory.getReferenced(Combination.class)) {
			CombinationState state = getState(combination);
			state.read(combination, index);
		}
	}

	/**
	 * Read the state from the given combinations.
	 */
	public void read(Combination combination, Reference<?> reference, int index) {
		CombinationState state = getState(combination);
		state.read(reference, index);
	}
}