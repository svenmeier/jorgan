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

import java.util.Arrays;

public class Memory extends IndexedContinuous {

	private String[] titles = new String[10];

	public Memory() {
		Arrays.fill(titles, "");
	}

	@Override
	protected boolean canReference(Class<? extends Element> clazz) {
		return Combination.class.isAssignableFrom(clazz);
	}

	public String getTitle() {
		return titles[getIndex()];
	}

	public void setTitle(String index) {
		titles[getIndex()] = index;
		
		fireChanged(false);
	}

	public void setSize(int size) {
		if (size != titles.length) {
			String[] strings = new String[size];
			Arrays.fill(strings, 0, strings.length, "");
			System.arraycopy(titles, 0, strings, 0, Math.min(strings.length,
					titles.length));
			titles = strings;

			fireChanged(true);
		}
	}

	public int getSize() {
		return titles.length;
	}

	public String getTitle(int index) {
		if (index < 0 || index > titles.length) {
			throw new IllegalArgumentException("index has to be between 0 and "
					+ titles.length);
		}
		return titles[index];
	}

	public void setTitle(int index, String title) {
		if (index < 0 || index > titles.length) {
			throw new IllegalArgumentException("index has to be between 0 and "
					+ titles.length);
		}
		if (title == null) {
			throw new IllegalArgumentException("title must not be null");
		}
		titles[index] = title;

		fireChanged(false);
	}

	public void clear(int index) {
		setTitle(index, "");

		for (Combination combination : getReferenced(Combination.class)) {
			combination.clear(index);
		}
	}

	public void swap(int index1, int index2) {
		String title1 = getTitle(index1);
		String title2 = getTitle(index2);

		setTitle(index1, title2);
		setTitle(index2, title1);

		for (Combination combination : getReferenced(Combination.class)) {
			combination.swap(index1, index2);
		}

		fireChanged(false);
	}

	public void copy(int index1, int index2) {
		String title = getTitle(index1);

		setTitle(index2, title);

		for (Combination combination : getReferenced(Combination.class)) {
			combination.copy(index1, index2);
		}
		
		fireChanged(false);
	}
}