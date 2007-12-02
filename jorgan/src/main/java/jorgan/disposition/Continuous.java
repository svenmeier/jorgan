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

import jorgan.disposition.Message.InputMessage;
import jorgan.disposition.Message.OutputMessage;

/**
 * A continuous element.
 */
public abstract class Continuous extends Element implements Displayable {

	private boolean locking = true;

	private float threshold = 0.0f;

	private float value = 0.0f;

	public boolean isLocking() {
		return locking;
	}

	public void setLocking(boolean locking) {
		this.locking = locking;

		fireElementChanged(true);
	}

	public void setValue(float value) {
		if (value < 0.0f || value > 1.0f) {
			throw new IllegalArgumentException("value must be between 0 and 1");
		}

		if (this.value != value) {
			this.value = value;

			fireElementChanged(false);
		}
	}

	public float getValue() {
		return value;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;

		fireElementChanged(true);
	}

	public List<Class<? extends Message>> getMessageClasses() {
		List<Class<? extends Message>> names = super.getMessageClasses();

		names.add(Change.class);
		names.add(Changed.class);

		return names;
	}

	public static class Change extends InputMessage {

		public static final String VALUE = "value";
		
		@Override
		protected int getOrder() {
			return 0;
		}
	}

	public static class Changed extends OutputMessage {

		public static final String VALUE = "value";
		
		@Override
		protected int getOrder() {
			return 10;
		}
	}
}