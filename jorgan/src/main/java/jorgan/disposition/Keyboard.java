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

import java.util.List;

/**
 * A keyboard.
 */
public class Keyboard extends Element implements Input {

	private String device = null;

	private int transpose = 0;

	public Keyboard() {
		addMessage(new Press().pattern("144, pitch:0-127, velocity:0-127"));
		addMessage(new Release().pattern("128, pitch:0-127, 0-127"));
	}

	@Override
	protected boolean canReference(Class clazz) {
		return Keyable.class.isAssignableFrom(clazz);
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;

		fireElementChanged(true);
	}

	public void setTranspose(int transpose) {
		this.transpose = transpose;

		fireElementChanged(true);
	}

	public int getTranspose() {
		return transpose;
	}

	public List<Class<? extends Matcher>> getMessageClasses() {
		List<Class<? extends Matcher>> names = super.getMessageClasses();

		names.add(Press.class);
		names.add(Release.class);

		return names;
	}

	public static class Press extends InputMessage {

		public transient int pitch;

		public transient int velocity;
	}

	public static class Release extends InputMessage {

		public transient int pitch;
	}
}