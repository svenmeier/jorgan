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
package jorgan.fluidsynth.gui.customizer;

import java.util.ArrayList;
import java.util.List;

import jorgan.customizer.gui.Customizer;
import jorgan.customizer.gui.spi.CustomizerProvider;
import jorgan.session.OrganSession;

public class FluidsynthCustomizerProvider implements CustomizerProvider {

	public int getOrder() {
		return 1000;
	}

	public List<Customizer> getCustomizers(OrganSession session) {
		List<Customizer> customizers = new ArrayList<Customizer>();

		if (FluidsynthSoundsCustomizer.customizes(session)) {
			customizers.add(new FluidsynthSoundsCustomizer(session));
		}

		return customizers;
	}
}
