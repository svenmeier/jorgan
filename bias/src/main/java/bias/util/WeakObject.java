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
 */package bias.util;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Convenience class to wrap an {@link Object} into a {@link WeakReference}.
 * 
 * @param <T>
 *            type of object
 */
public final class WeakObject<T> extends WeakReference<T> {

	/** The hash code of the nested object */
	private final int hashCode;

	/**
	 * Constructor.
	 * 
	 * @param object
	 *            object to reference
	 */
	public WeakObject(final T object) {
		this(object, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param object
	 *            object to reference
	 * @param queue
	 *            reference queue
	 */
	public WeakObject(final T object, final ReferenceQueue<T> queue) {
		super(object, queue);
		hashCode = object.hashCode();
	}

	/**
	 * Check the equality of an object with this.
	 * 
	 * @param object
	 *            object to test equality with.
	 * @return true if object is equal.
	 */
	@Override
	public boolean equals(final Object object) {
		if (object == this)
			return true;

		if (object != null && object.getClass() == getClass()) {
			WeakObject<?> soft = (WeakObject) object;

			Object a = this.get();
			Object b = soft.get();
			if (a == null || b == null)
				return false;
			if (a == b)
				return true;

			return a.equals(b);
		}

		return false;
	}

	/**
	 * Return the hash code of the nested object.
	 * 
	 * @return The hash code of the nested object.
	 */
	@Override
	public int hashCode() {
		return hashCode;
	}

	/**
	 * Create a WeakObject for the given object.
	 * 
	 * @param <T>
	 *            the type of object
	 * 
	 * @param object
	 *            object to reference
	 * @return WeakObject or <code>null</code> if object is null
	 */
	public static <T> WeakObject<T> create(final T object) {
		if (object == null)
			return null;
		else
			return new WeakObject<T>(object);
	}

	/**
	 * Create a WeakObject for the given object.
	 * 
	 * @param <T>
	 *            the type of object
	 * 
	 * @param object
	 *            Object to reference.
	 * @param queue
	 *            Reference queue.
	 * @return WeakObject or <code>null</code> if object is null
	 */
	public static <T> WeakObject<T> create(final T object,
			final ReferenceQueue<T> queue) {
		if (object == null)
			return null;
		else
			return new WeakObject<T>(object, queue);
	}
}
