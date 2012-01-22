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
package jorgan.util;

import java.util.Comparator;

/**
 * A chain of {@link Comparator}s.
 */
public class ComparatorChain<T> implements Comparator<T> {

	private Comparator<T>[] comparators;

	public ComparatorChain(Comparator<T>... comparators) {
		this.comparators = comparators;
	}

	public int compare(T t1, T t2) {
		for (Comparator<T> comparator : comparators) {
			int result = comparator.compare(t1, t2);
			if (result != 0) {
				return result;
			}
		}

		return 0;
	}

	@SuppressWarnings("unchecked")
	public static <T> ComparatorChain<T> of(Comparator<T> comparator1,
			Comparator<T> comparator2) {
		return new ComparatorChain<T>(comparator1, comparator2);
	}

	@SuppressWarnings("unchecked")
	public static <T> ComparatorChain<T> of(Comparator<T> comparator1,
			Comparator<T> comparator2, Comparator<T> comparator3) {
		return new ComparatorChain<T>(comparator1, comparator2, comparator3);
	}
}