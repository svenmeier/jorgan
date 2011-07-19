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

public abstract class IndexedContinuous extends Continuous {

	protected int getIndex(float value) {
		if (getSize() == 0) {
			return 0;
		}

		return (int) ((getSize() - 1) * value);
	}

	public int getIndex() {
		return getIndex(getValue());
	}

	public void setIndex(int index) {
		if (index < 0 || index > getSize() - 1) {
			throw new IllegalArgumentException("invalid index " + index);
		}

		if (getSize() == 0) {
			setValue(0.0f);
		} else {
			setValue(((float) index) / (getSize() - 1));
		}
	}

	@Override
	protected void onValueChanged(float oldValue, float newValue) {
		int oldIndex = getIndex(oldValue);
		int newIndex = getIndex(newValue);

		if (oldIndex != newIndex) {
			onIndexChanged(oldIndex, newIndex);
		}
	}

	protected void onIndexChanged(int oldIndex, int newIndex) {
		fireChange(new FastPropertyChange("index", true));
	}

	public abstract int getSize();

	public void increment(int delta) {
		if (getSize() == 0) {
			return;
		}

		int index = getIndex() + delta;
		if (index < 0) {
			index = 0;
		}
		if (index > getSize() - 1) {
			index = getSize() - 1;
		}

		setIndex(index);
	}
}