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
package jorgan.disposition;

import java.util.Set;

import jorgan.disposition.Input.InputMessage;

/**
 * A keyboard.
 */
public class Keyboard extends Element implements Input.Referenceable {

	public Keyboard() {
		// note on, pitch, velocity
		addMessage(new PressKey().change("equal 144", "get pitch",
				"greater 0 | get velocity"));
		// note on, pitch, -
		addMessage(new ReleaseKey().change("equal 144", "get pitch", "equal 0"));
		// note off, pitch, -
		addMessage(new ReleaseKey().change("equal 128", "get pitch", ""));
	}

	protected boolean canReference(Class<? extends Element> clazz) {
		return Keyable.class.isAssignableFrom(clazz);
	}

	@Override
	public Set<Class<? extends Message>> getMessageClasses() {
		Set<Class<? extends Message>> names = super.getMessageClasses();

		names.add(PressKey.class);
		names.add(ReleaseKey.class);

		return names;
	}

	public static class PressKey extends InputMessage {

		public static final String PITCH = "pitch";

		public static final String VELOCITY = "velocity";
	}

	public static class ReleaseKey extends InputMessage {

		public static final String PITCH = "pitch";
	}
}