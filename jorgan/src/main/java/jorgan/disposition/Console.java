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

/**
 * A console.
 */
public class Console extends Input implements Output {

	/**
	 * The output device.
	 */
	private String output;

	/**
	 * The skin.
	 */
	private String skin;

	private String screen;

	protected boolean canReference(Class<? extends Element> clazz) {
		return !Input.class.isAssignableFrom(clazz);
	}

	@Override
	protected Reference createReference(Element element) {
		return new Reference(element);
	}

	public String getOutput() {
		return output;
	}

	public String getSkin() {
		return skin;
	}

	public String getScreen() {
		return screen;
	}

	public void setOutput(String output) {
		this.output = output;

		fireElementChanged(true);
	}

	public void setSkin(String skin) {
		this.skin = skin;

		fireElementChanged(true);
	}

	public void setScreen(String screen) {
		this.screen = screen;

		fireElementChanged(true);
	}

	public void setLocation(Element element, int x, int y) {
		Reference reference = (Reference) getReference(element);

		reference.setX(x);
		reference.setY(y);

		fireChanged("reference", reference, true);
	}

	public int getX(Element element) {
		Reference reference = (Reference) getReference(element);

		return reference.getX();
	}

	public int getY(Element element) {
		if (element == this) {
			return 0;
		} else {
			Reference reference = (Reference) getReference(element);

			return reference.getY();
		}
	}

	/**
	 * A reference of a console to another element.
	 */
	public static class Reference extends jorgan.disposition.Reference {

		private int x;

		private int y;

		public Reference(Element element) {
			super(element);
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public void setX(int i) {
			x = i;
		}

		public void setY(int i) {
			y = i;
		}
	}

	/**
	 * Move to front the reference to the given element.
	 * 
	 * @param element
	 *            element to move to front
	 */
	public void toFront(Element element) {
		jorgan.disposition.Reference reference = getReference(element);
		if (reference == null) {
			throw new IllegalArgumentException("unkown element");
		}

		references.remove(reference);
		references.add(reference);

		fireElementChanged(true);
	}

	/**
	 * Move to back the reference to the given element.
	 * 
	 * @param element
	 *            element to move to back
	 */
	public void toBack(Element element) {
		jorgan.disposition.Reference reference = getReference(element);
		if (reference == null) {
			throw new IllegalArgumentException("unkown element");
		}

		references.remove(reference);
		references.add(0, reference);

		fireElementChanged(true);
	}
}