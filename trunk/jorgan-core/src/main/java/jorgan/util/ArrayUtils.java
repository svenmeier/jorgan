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


/**
 * Collection of utility methods for arrays.
 */
public class ArrayUtils {

	@SuppressWarnings("unchecked")
	public static <T> T[] prepend(T t, T[] ts) {
		Object[] prepended = new Object[ts.length + 1];

		System.arraycopy(ts, 0, prepended, 1, ts.length);

		prepended[0] = t;

		return (T[]) prepended;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] apppend(T[] ts, T t) {
		Object[] appended = new Object[ts.length + 1];

		System.arraycopy(ts, 0, appended, 0, ts.length);

		appended[ts.length] = t;

		return (T[]) appended;
	}
}