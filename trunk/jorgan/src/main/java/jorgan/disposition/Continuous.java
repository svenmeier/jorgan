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

import java.util.List;

import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.Output.OutputMessage;
import jorgan.midi.mpl.Command;
import jorgan.midi.mpl.Div;
import jorgan.midi.mpl.Equal;
import jorgan.midi.mpl.Get;
import jorgan.midi.mpl.GreaterEqual;
import jorgan.midi.mpl.LessEqual;
import jorgan.midi.mpl.Sub;

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

			fireChange(new PropertyChange(oldLocking, this.locking));
		}
	}

	public void setValue(float value) {
		if (value < 0.0f || value > 1.0f) {
			throw new IllegalArgumentException("value must be between 0 and 1");
		}

		if (this.value != value) {
			float oldValue = this.value;

			this.value = value;

			fireChange(new FastPropertyChange("value", false));

			onValueChanged(oldValue, this.value);

			for (Dependent dependent : getOrgan().getReferrer(this,
					Dependent.class)) {
				dependent.valueChanged(this, value);
			}
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

			fireChange(new PropertyChange(oldThreshold, threshold));
		}
	}

	public Change createChangeWithStatus(int statusMin, int statusMax,
			int data1, int data2) {
		Change change = new Change();

		change.change(newMinMaxGet(statusMin, statusMax).toString(), new Equal(
				data1).toString(), new Equal(data2).toString());

		return change;
	}

	public Change createChangeWithData1(int status, int data1Min, int data1Max,
			int data2) {
		Change change = new Change();

		change.change(new Equal(status).toString(), newMinMaxGet(data1Min,
				data1Max).toString(), new Equal(data2).toString());

		return change;
	}

	public Change createChangeWithData2(int status, int data1, int data2Min,
			int data2Max) {
		Change change = new Change();

		change.change(new Equal(status).toString(),
				new Equal(data1).toString(), newMinMaxGet(data2Min, data2Max)
						.toString());

		return change;
	}

	private Command newMinMaxGet(int min, int max) {
		if (min > max) {
			throw new IllegalArgumentException("min must be smaller than max");
		}

		return new GreaterEqual(min, new LessEqual(max, new Sub(min, new Div(
				max - min, new Get(Change.VALUE)))));
	}

	@Override
	public List<Class<? extends Message>> getMessageClasses() {
		List<Class<? extends Message>> classes = super.getMessageClasses();

		classes.add(Change.class);
		classes.add(Changed.class);

		return classes;
	}

	public static class Change extends InputMessage {

		public static final String VALUE = "value";
	}

	public static class Changed extends OutputMessage {

		public static final String VALUE = "value";
	}

	public static interface Dependent {

		/**
		 * Notification from a referenced {@link Continuous} of a change in
		 * {@link Continuous#getValue()}.
		 */
		public void valueChanged(Continuous element, float value);
	}
}