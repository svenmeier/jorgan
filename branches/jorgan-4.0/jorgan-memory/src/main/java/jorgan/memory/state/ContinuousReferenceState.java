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

import jorgan.disposition.Continuous;
import jorgan.disposition.Reference;
import jorgan.disposition.Combination.ContinuousReference;

/**
 * The state of a {@link ContinuousReference}.
 */
public class ContinuousReferenceState extends ReferenceState<Continuous> {

	private float[] values = new float[0];

	public ContinuousReferenceState(Continuous element) {
		super(element);
	}

	protected void ensureIndex(int index) {
		if (values.length <= index) {
			float[] temp = new float[index + 1];

			System.arraycopy(values, 0, temp, 0, values.length);

			values = temp;
		}
	}

	@Override
	public void clear(int index) {
		ensureIndex(index);

		values[index] = 0.0f;
	}

	@Override
	public void swap(int index1, int index2) {
		ensureIndex(Math.max(index1, index2));

		float value1 = values[index1];
		values[index1] = values[index2];
		values[index2] = value1;
	}

	@Override
	public void read(Reference<?> reference, int index) {
		ensureIndex(index);

		ContinuousReference temp = (ContinuousReference) reference;

		values[index] = temp.getValue();
	}

	@Override
	public void write(Reference<?> reference, int index) {
		ensureIndex(index);

		ContinuousReference temp = (ContinuousReference) reference;

		temp.setValue(values[index]);
	}

	@Override
	public Object get(int index) {
		ensureIndex(index);

		return values[index];
	}
}