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
package jorgan.disposition.spi;

import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Activator;
import jorgan.disposition.Captor;
import jorgan.disposition.Combination;
import jorgan.disposition.Console;
import jorgan.disposition.Continuous;
import jorgan.disposition.ContinuousFilter;
import jorgan.disposition.Coupler;
import jorgan.disposition.Element;
import jorgan.disposition.GenericSound;
import jorgan.disposition.Incrementer;
import jorgan.disposition.Keyboard;
import jorgan.disposition.Keyer;
import jorgan.disposition.Label;
import jorgan.disposition.Memory;
import jorgan.disposition.Rank;
import jorgan.disposition.Regulator;
import jorgan.disposition.Stop;
import jorgan.disposition.Switch;
import jorgan.disposition.SwitchFilter;

/**
 * The default provider of {@link Element}s.
 */
public class DefaultElementProvider implements ElementProvider {

	private static List<Class<? extends Element>> elementClasses;

	static {
		List<Class<? extends Element>> classes = new ArrayList<Class<? extends Element>>();
		classes.add(Console.class);
		classes.add(Label.class);
		classes.add(Keyboard.class);
		classes.add(Coupler.class);
		classes.add(Stop.class);
		classes.add(Rank.class);
		classes.add(Switch.class);
		classes.add(SwitchFilter.class);
		classes.add(Continuous.class);
		classes.add(ContinuousFilter.class);
		classes.add(Keyer.class);
		classes.add(Activator.class);
		classes.add(Regulator.class);
		classes.add(Combination.class);
		classes.add(Captor.class);
		classes.add(Incrementer.class);
		classes.add(Memory.class);
		classes.add(GenericSound.class);

		elementClasses = classes;
	}

	public List<Class<? extends Element>> getElementClasses() {
		return new ArrayList<Class<? extends Element>>(elementClasses);
	}
}
