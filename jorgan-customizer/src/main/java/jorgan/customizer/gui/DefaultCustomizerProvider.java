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
package jorgan.customizer.gui;

import java.util.ArrayList;
import java.util.List;

import jorgan.customizer.gui.console.ConsoleCustomizer;
import jorgan.customizer.gui.keyboard.KeyboardsCustomizer;
import jorgan.customizer.gui.sound.GenericSoundsCustomizer;
import jorgan.customizer.gui.spi.CustomizerProvider;
import jorgan.disposition.Console;
import jorgan.session.OrganSession;

/**
 * Default provider of customizers.
 */
public class DefaultCustomizerProvider implements CustomizerProvider {

	public int getOrder() {
		return 0;
	}

	public List<Customizer> getCustomizers(OrganSession session) {
		List<Customizer> customizers = new ArrayList<Customizer>();

		if (KeyboardsCustomizer.customizes(session)) {
			customizers.add(new KeyboardsCustomizer(session));
		}
		if (GenericSoundsCustomizer.customizes(session)) {
			customizers.add(new GenericSoundsCustomizer(session));
		}
		for (Console console : session.getOrgan().getElements(Console.class)) {
			if (ConsoleCustomizer.customizes(session, console)) {
				customizers.add(new ConsoleCustomizer(session, console));
			}
		}

		return customizers;
	}
}