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

import java.util.Arrays;

public class Tuning implements Cloneable {

	public static final int COUNT = 12;

	private String name = "";

	private double[] derivations = new double[COUNT];

	public String getName() {
		return name;
	}

	public double[] getDerivations() {
		return Arrays.copyOf(derivations, COUNT);
	}

	public double getDerivation(int index) {
		if (index < 0 || index >= COUNT) {
			throw new IllegalArgumentException("invalid index " + index);
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
		if (derivations.length != COUNT) {
			throw new IllegalArgumentException("must have length 12");
		}
		this.derivations = derivations;

		return this;
	}

	/**
	 * Always cloneable.
	 */
	@Override
	public Tuning clone() {
		try {
			return (Tuning) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new Error(ex);
		}
	}
}