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


public class Tuning {

	private String name = "";

	private double[] derivations;

	public String getName() {
		return name;
	}

	public double[] getDerivations() {
		return derivations;
	}

	public double getDerivation(int index) {
		if (index < 0 || index > 11) {
			throw new IllegalArgumentException("index must be from 0 to 11");
		}

		return this.derivations[index];
	}

	protected Tuning change(String name, double[] derivations) {
		if (name == null) {
			name = "";
		}
		this.name = name;

		if (derivations == null) {
			throw new IllegalArgumentException("must not be null");
		}
		if (derivations.length != 12) {
			throw new IllegalArgumentException("must have length 12");
		}
		this.derivations = derivations;

		return this;
	}
}