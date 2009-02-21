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

import java.util.Set;

import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.Output.OutputMessage;
import jorgan.midi.mpl.Equal;

/**
 * A continuous element.
 */
public class Continuous extends Displayable {

	private boolean locking = true;

	private float threshold = 0.0f;

	private float value = 0.0f;

	public boolean isLocking() {
		return locking;
	}

	public void setLocking(boolean locking) {
		if (this.locking != locking) {
			boolean oldLocking = this.locking;

			this.locking = locking;

			fireChange(new UndoablePropertyChange(oldLocking, this.locking));
		}
	}

	public void setValue(float value) {
		if (value < 0.0f || value > 1.0f) {
			throw new IllegalArgumentException("value must be between 0 and 1");
		}

		if (this.value != value) {
			float oldValue = this.value;

			this.value = value;

			fireChange(new PropertyChange());

			onValueChanged(oldValue, this.value);
		}
	}

	protected void onValueChanged(float oldValue, float newValue) {
	}

	public float getValue() {
		return value;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		if (this.threshold != threshold) {
			float oldThreshold = this.threshold;

			this.threshold = threshold;

			fireChange(new UndoablePropertyChange(oldThreshold, threshold));
		}
	}

	public void setChange(int status, int data1, int data2) {
		removeMessages(Change.class);

		addMessage(new Change().change(new Equal(status).toString(), new Equal(
				data1).toString(), new Equal(data2).toString()));
	}

	@Override
	public Set<Class<? extends Message>> getMessageClasses() {
		Set<Class<? extends Message>> names = super.getMessageClasses();

		names.add(Change.class);
		names.add(Changed.class);

		return names;
	}

	public static class Change extends InputMessage {

		public static final String VALUE = "value";
	}

	public static class Changed extends OutputMessage {

		public static final String VALUE = "value";
	}
}