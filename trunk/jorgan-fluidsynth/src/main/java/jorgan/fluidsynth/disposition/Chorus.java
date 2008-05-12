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
package jorgan.fluidsynth.disposition;

public class Chorus {
	
	private int nr;

	private double level;

	private double speed;

	private double depth;

	private int type;

	public double getDepth() {
		return depth;
	}

	public double getLevel() {
		return level;
	}

	public int getNr() {
		return nr;
	}

	public double getSpeed() {
		return speed;
	}

	public int getType() {
		return type;
	}

	public void setDepth(double depth) {
		this.depth = depth;
	}

	public void setLevel(double level) {
		this.level = level;
	}

	public void setNr(int nr) {
		this.nr = nr;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	public void setType(int type) {
		this.type = type;
	}
}