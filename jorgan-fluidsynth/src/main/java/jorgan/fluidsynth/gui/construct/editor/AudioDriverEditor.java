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
package jorgan.fluidsynth.gui.construct.editor;

import java.beans.PropertyEditorSupport;
import java.util.List;

import jorgan.fluidsynth.Fluidsynth;

public class AudioDriverEditor extends PropertyEditorSupport {

	private static String[] tags = new String[1];

	static {
		try {
			List<String> drivers = Fluidsynth.getAudioDrivers();
			tags = new String[drivers.size() + 1];
			int i = 1;
			for (String driver : drivers) {
				tags[i] = driver;
				i++;
			}
		} catch (Error fluidsynthFailure) {
		}
	}

	@Override
	public String[] getTags() {
		return tags;
	}

	@Override
	public String getAsText() {

		return (String) getValue();
	}

	@Override
	public void setAsText(String string) {

		setValue(string);
	}
}