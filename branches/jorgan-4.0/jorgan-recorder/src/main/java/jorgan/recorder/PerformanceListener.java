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
package jorgan.recorder;

/**
 * A listener of a {@link Performance}.
 */
public interface PerformanceListener {

	/**
	 * @see Performance#setSpeed(float speed)
	 */
	public void speedChanged(float speed);

	/**
	 * @see Performance#setTime(long time)
	 */
	public void timeChanged(long millis);

	/**
	 * @see Performance#play()
	 * @see Performance#record()
	 * @see Performance#stop()
	 */
	public void stateChanged(int state);

	public void changed();
}