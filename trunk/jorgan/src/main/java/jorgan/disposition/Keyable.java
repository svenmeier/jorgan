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
 * A keyable.
 * 
 * TODO convert actions into elements ?
 */
public abstract class Keyable extends Switch {

	public static final int VELOCITY_UNMODIFIED = 0;

	public static final int ACTION_STRAIGHT = 0;

	public static final int ACTION_PITCH_CONSTANT = 1;

	public static final int ACTION_PITCH_HIGHEST = 2;

	public static final int ACTION_PITCH_LOWEST = 3;

	public static final int ACTION_SUSTAIN = 4;

	public static final int ACTION_SOSTENUTO = 5;

	public static final int ACTION_INVERSE = 6;

	private int action = ACTION_STRAIGHT;

	private int from = 0;

	private int to = 127;

	private int transpose = 0;

	private int velocity = 0;

	public int getVelocity() {
		return velocity;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public int getTranspose() {
		return transpose;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		if (this.action != action) {
			int oldAction = this.action;

			if (!isValidAction(action)) {
				throw new IllegalArgumentException("action '" + action + "'");
			}
			this.action = action;

			fireChange(new PropertyChange(oldAction, this.action));
		}
	}

	protected abstract boolean isValidAction(int action);

	public void setVelocity(int velocity) {
		if (velocity < VELOCITY_UNMODIFIED || velocity > 127) {
			throw new IllegalArgumentException();
		}

		if (this.velocity != velocity) {
			int oldVelocity = this.velocity;

			this.velocity = velocity;

			fireChange(new PropertyChange(oldVelocity, this.velocity));
		}
	}

	public void setFrom(int from) {
		if (from < 0 || from > 127) {
			throw new IllegalArgumentException();
		}

		if (this.from != from) {
			int oldFrom = this.from;

			this.from = from;

			fireChange(new PropertyChange(oldFrom, this.from));
		}
	}

	public void setTo(int to) {
		if (to < 0 || to > 127) {
			throw new IllegalArgumentException();
		}

		if (this.to != to) {
			int oldTo = this.to;

			this.to = to;

			fireChange(new PropertyChange(oldTo, this.to));
		}
	}

	public void setTranspose(int transpose) {
		if (this.transpose != transpose) {
			int oldTranspose = this.transpose;

			this.transpose = transpose;

			fireChange(new PropertyChange(oldTranspose, this.transpose));
		}
	}
}