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

import jorgan.disposition.event.OrganListener;
import jorgan.disposition.event.UndoableChange;
import jorgan.util.Null;

/**
 * A console.
 */
public class Console extends Displayable {

	private String skin;

	private String screen;

	protected boolean canReference(Class<? extends Element> clazz) {
		return Displayable.class.isAssignableFrom(clazz)
				&& !Console.class.isAssignableFrom(clazz);
	}

	@Override
	protected boolean validReference(Reference<? extends Element> reference) {
		return reference.getClass() == LocationReference.class;
	}

	@Override
	protected Reference<? extends Element> createReference(Element element) {
		if (element instanceof Displayable) {
			return new LocationReference((Displayable) element);
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
		skin = cleanPath(skin);

		if (!Null.safeEquals(this.skin, skin)) {
			String oldSkin = this.skin;

			this.skin = skin;

			fireChange(new PropertyChange(oldSkin, this.skin));
		}
	}

	public void setScreen(String screen) {
		if ("".equals(screen)) {
			screen = null;
		}

		if (!Null.safeEquals(this.screen, screen)) {
			String oldScreen = this.screen;

			this.screen = screen;

			fireChange(new PropertyChange(oldScreen, this.screen));
		}
	}

	public boolean showFullScreen() {
		return screen != null;
	}

	public void setLocation(final Element element, final int x, final int y) {
		final LocationReference reference = (LocationReference) getReference(element);

		final int oldX = reference.getX();
		final int oldY = reference.getY();

		reference.setX(x);
		reference.setY(y);

		fireChange(new LocationChange(reference, oldX, oldY, x, y));
	}

	public int getX(Element element) {
		if (element == this) {
			return 0;
		} else {
			LocationReference reference = (LocationReference) getReference(element);

			return reference.getX();
		}
	}

	public int getY(Element element) {
		if (element == this) {
			return 0;
		} else {
			LocationReference reference = (LocationReference) getReference(element);

			return reference.getY();
		}
	}

	/**
	 * A reference of a console to another element.
	 */
	public static class LocationReference extends Reference<Displayable> {

		private int x;

		private int y;

		public LocationReference(Displayable element) {
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
		public LocationReference clone(Element element) {
			LocationReference clone = (LocationReference) super.clone(element);

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
	public void toFront(final Element element) {
		final LocationReference reference = (LocationReference) getReference(element);
		if (reference == null) {
			throw new IllegalArgumentException("unkown element");
		}

		moveReference(reference, references.size() - 1);
	}

	/**
	 * Move to back the reference to the given element.
	 * 
	 * @param element
	 *            element to move to back
	 */
	public void toBack(Element element) {
		LocationReference reference = (LocationReference) getReference(element);
		if (reference == null) {
			throw new IllegalArgumentException("unkown element");
		}

		moveReference(reference, 0);
	}

	private class LocationChange implements UndoableChange {

		private LocationReference reference;

		private int oldX;

		private int oldY;

		private int newX;

		private int newY;

		public LocationChange(LocationReference reference, int oldX, int oldY,
				int x, int y) {
			this.reference = reference;

			this.oldX = oldX;
			this.oldY = oldY;

			this.newX = x;
			this.newY = y;
		}

		public void notify(OrganListener listener) {
			listener.indexedPropertyChanged(Console.this, REFERENCE, reference);
		}

		public void undo() {
			setLocation(reference.getElement(), oldX, oldY);
		}

		public void redo() {
			setLocation(reference.getElement(), newX, newY);
		}

		public boolean replaces(UndoableChange change) {
			if (change instanceof LocationChange) {
				LocationChange other = (LocationChange) change;
				if (this.reference == other.reference) {
					this.newX = other.newX;
					this.newY = other.newY;

					return true;
				}
			}

			return false;
		}
	}
}