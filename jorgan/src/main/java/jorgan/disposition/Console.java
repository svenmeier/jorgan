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

import jorgan.util.Null;

/**
 * A console.
 */
public class Console extends Displayable implements Input, Output {

	private String skin;

	private String screen;
	
	private String input;

	private String output;

	public void setInput(String input) {
		if (!Null.safeEquals(this.input, input)) {
			this.input = input;
			
			fireChanged(true);
		}
	}
	
	public String getInput() {
		return input;
	}
	
	public void setOutput(String output) {
		if (!Null.safeEquals(this.output, output)) {
			this.output = output;
			
			fireChanged(true);
		}
	}
	
	public String getOutput() {
		return output;
	}
		
	protected boolean canReference(Class<? extends Element> clazz) {
		return Displayable.class.isAssignableFrom(clazz) && !(Console.class == clazz);
	}

	@Override
	protected boolean validReference(
			jorgan.disposition.Reference<? extends Element> reference) {
		return reference.getClass() == Reference.class;
	}

	@Override
	protected jorgan.disposition.Reference<? extends Element> createReference(Element element) {
		if (element instanceof Displayable) {
			return new Reference((Displayable)element);
		} else {
			return super.createReference(element);
		}
	}

	public String getSkin() {
		return skin;
	}

	public String getScreen() {
		return screen;
	}

	public void setSkin(String skin) {
		if (!Null.safeEquals(this.skin, skin)) {
			this.skin = skin;

			fireChanged(true);
		}
	}

	public void setScreen(String screen) {
		if (!Null.safeEquals(this.screen, screen)) {
			this.screen = screen;

			fireChanged(true);
		}
	}

	public void setLocation(Element element, int x, int y) {
		Reference reference = (Reference) getReference(element);

		reference.setX(x);
		reference.setY(y);

		fireChanged(reference, true);
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
	public static class Reference extends jorgan.disposition.Reference<Displayable> {

		private int x;

		private int y;

		public Reference(Displayable element) {
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

		@Override
		public Reference clone(Element element) {
			Reference clone = (Reference) super.clone(element);

			clone.x += 32;
			clone.y += 32;

			return clone;
		}
	}

	/**
	 * Move to front the reference to the given element.
	 * 
	 * @param element
	 *            element to move to front
	 */
	public void toFront(Element element) {
		Reference reference = (Reference) getReference(element);
		if (reference == null) {
			throw new IllegalArgumentException("unkown element");
		}

		references.remove(reference);
		references.add(reference);

		fireChanged(true);
	}

	/**
	 * Move to back the reference to the given element.
	 * 
	 * @param element
	 *            element to move to back
	 */
	public void toBack(Element element) {
		Reference reference = (Reference) getReference(element);
		if (reference == null) {
			throw new IllegalArgumentException("unkown element");
		}

		references.remove(reference);
		references.add(0, reference);

		fireChanged(true);
	}
}