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
package jorgan.gui.customize.spi;

import java.util.ArrayList;
import java.util.List;

import jorgan.disposition.Console;
import jorgan.disposition.GenericSound;
import jorgan.disposition.Keyboard;
import jorgan.gui.customize.Customizer;
import jorgan.gui.customize.consoles.ConsoleCustomizer;
import jorgan.gui.customize.genericSounds.GenericSoundsCustomizer;
import jorgan.gui.customize.keyboards.KeyboardsCustomizer;
import jorgan.session.OrganSession;

/**
 * Default provider of customizers.
 */
public class DefaultCustomizerProvider implements CustomizerProvider {

	public List<Customizer> getCustomizers(OrganSession session) {
		List<Customizer> customizers = new ArrayList<Customizer>();

		if (!session.getOrgan().getElements(Keyboard.class).isEmpty()) {
			customizers.add(new KeyboardsCustomizer(session));
		}
		if (!session.getOrgan().getElements(GenericSound.class).isEmpty()) {
			customizers.add(new GenericSoundsCustomizer(session));
		}
		for (Console console : session.getOrgan().getElements(Console.class)) {
			if (console.getReferenceCount() > 0) {
				customizers.add(new ConsoleCustomizer(session, console));
			}
		}

		return customizers;
	}
}