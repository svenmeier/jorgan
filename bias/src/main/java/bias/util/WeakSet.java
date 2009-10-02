/**
 * Bias - POJO Configuration.
 * Copyright (C) 2007 Sven Meier
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package bias.util;

import java.lang.ref.ReferenceQueue;
import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A set implementation keeping weak references to its entries.
 * 
 * @param <E>
 *            the element type
 */
public class WeakSet<E> extends AbstractSet<E> implements Set<E> {

	/** The reference queue used to get object removal notifications. */
	protected final ReferenceQueue<E> queue = new ReferenceQueue<E>();

	protected final Set<WeakObject<E>> set = new HashSet<WeakObject<E>>();

	/**
	 * Maintain the elements in the set. Removes objects from the set that have
	 * been reclaimed due to GC.
	 */
	protected final void maintain() {
		Object weak;
		while ((weak = queue.poll()) != null) {
			set.remove(weak);
		}
	}

	@Override
	public int size() {
		maintain();

		return set.size();
	}

	private static Object UNKOWN = new Object();

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {

			/** The set's iterator */
			Iterator<WeakObject<E>> iter = set.iterator();

			/** The next available object. */
			Object next = UNKOWN;

			public boolean hasNext() {
				if (next == UNKOWN) {
					while (iter.hasNext()) {
						WeakObject weak = iter.next();
						Object obj = null;
						if (weak != null && (obj = weak.get()) == null) {
							// object has been reclaimed by the GC
							continue;
						}

						next = obj;
						return true;
					}
				}

				return false;
			}

			@SuppressWarnings("unchecked")
			public E next() {
				if ((next == UNKOWN) && !hasNext()) {
					throw new NoSuchElementException();
				}

				E e = (E) next;
				next = UNKOWN;

				return e;
			}

			public void remove() {
				iter.remove();
			}
		};
	}

	@Override
	public boolean add(final E obj) {
		maintain();

		return set.add(WeakObject.create(obj, queue));
	}

	@Override
	public boolean isEmpty() {
		maintain();

		return set.isEmpty();
	}

	@Override
	public boolean contains(final Object obj) {
		maintain();

		return set.contains(WeakObject.create(obj));
	}

	@Override
	public boolean remove(final Object obj) {
		maintain();

		return set.remove(WeakObject.create(obj));
	}

	@Override
	public void clear() {
		set.clear();
	}
}