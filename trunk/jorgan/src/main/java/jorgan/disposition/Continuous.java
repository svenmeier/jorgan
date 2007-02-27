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

/**
 * A continuous element.
 */
public abstract class Continuous extends Element {

	private Message message;

	private boolean locking = true;

	private boolean reverse = false;

	private int value = 0;

	private int threshold = 0;

	public boolean isLocking() {
		return locking;
	}

	public void setLocking(boolean locking) {
		this.locking = locking;

		fireElementChanged(true);
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		if (message != null && !message.hasWildcard()) {
			message = new Message(message.getStatus(), message.getData1(), -1);
		}
		this.message = message;

		fireElementChanged(true);
	}

	public void setValue(int position) {
		if (position < 0 || position > 127) {
			throw new IllegalArgumentException(
					"position must be between 0 and 127");
		}

		this.value = position;

		fireElementChanged(false);
	}

	public int getValue() {
		return value;
	}

	public int getThreshold() {
		return threshold;
	}

	public void setThreshold(int granularity) {
		this.threshold = granularity;

		fireElementChanged(true);
	}

	public boolean isReverse() {
		return reverse;
	}

	public void setReverse(boolean reverse) {
		this.reverse = reverse;

		fireElementChanged(true);
	}

	public void increment(int delta) {
		int position = getValue();
		if (delta > 0) {
			position += delta;
			position -= (position % delta);
		} else {
			position += delta - 1;
			if (position >= 0) {
				position -= delta + (position % delta);
			}
		}

		setValue(limitIncrement(position));
	}

	protected int limitIncrement(int position) {
		return Math.max(0, Math.min(127, position));
	}
}