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
package jorgan.fluidsynth.gui.construct.info;

import jorgan.fluidsynth.disposition.Chorus;
import jorgan.fluidsynth.gui.construct.editor.ChorusEditor;
import jorgan.gui.construct.info.ContinuousBeanInfo;

/**
 * BeanInfo for {@link Chorus}.
 */
public class ChorusBeanInfo extends ContinuousBeanInfo {

	@Override
	protected void registerProperties() {
		super.registerProperties();

		add("parameter", Chorus.class, ChorusEditor.class);
	}
}
