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
import jorgan.memory.disposition.Memory;
import jorgan.memory.state.CombinationState;

/**
 * The state of a {@link Memory}.
 */
public class MemoryState {

	private List<String> titles = new ArrayList<String>();

	private List<CombinationState> states = new ArrayList<CombinationState>();

	public MemoryState() {
	}

	private void ensureIndex(List<?> list, int index) {
		while (list.size() <= index) {
			list.add(null);
		}
	}

	private CombinationState getState(Combination combination) {
		for (CombinationState store : states) {
			if (store.isFor(combination)) {
				return store;
			}
		}

		CombinationState state = new CombinationState(combination);
		states.add(state);

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
		
		for (CombinationState state : states) {
			state.clear(index);
		}
	}

	public void read(Memory memory, int index) {
		for (Combination combination : memory.getReferenced(Combination.class)) {
			CombinationState state = getState(combination);
			if (state == null) {
				state = new CombinationState(combination);
				states.add(state);
			}

			state.read(combination, index);
		}
	}

	public void write(Memory memory, int index) {
		for (Combination combination : memory.getReferenced(Combination.class)) {
			CombinationState state = getState(combination);
			if (state == null) {
				state = new CombinationState(combination);
				states.add(state);
			}

			state.write(combination, index);
		}
	}
}