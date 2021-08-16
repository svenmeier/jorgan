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

/**
 * A continuous element.
 */
public class Continuous extends Displayable {

	public static final int DURATION_INFINITE = -1;

	public static final int DURATION_NONE = 0;

	private int duration = DURATION_INFINITE;

	private float threshold = 0.05f;

	private float value = 0.0f;

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		if (duration < DURATION_INFINITE) {
			duration = DURATION_INFINITE;
		}

		if (this.duration != duration) {
			int oldDuration = this.duration;

			this.duration = duration;

			fireChange(new PropertyChange(oldDuration, this.duration));
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

			for (Observer observer : getOrgan().getReferrer(this, Observer.class)) {
				observer.changed(this);
			}
		}
	}

	public void change(float value) {
		setValue(value);

		if (duration == DURATION_NONE) {
			setValue(0f);
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

	@Override
	public List<Class<? extends Message>> getMessageClasses() {
		List<Class<? extends Message>> classes = super.getMessageClasses();

		classes.add(InputMessage.class);
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
}