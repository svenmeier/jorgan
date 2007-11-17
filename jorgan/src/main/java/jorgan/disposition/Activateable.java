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
 * Abstract base class for an activateable elements.
 */
public abstract class Activateable extends Momentary {

	private boolean active = false;

	private boolean locking = true;

	public void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;

			fireElementChanged(false);
		}
	}

	public boolean isActive() {
		return active;
	}

	public boolean isLocking() {
		return locking;
	}

	public void setLocking(boolean locking) {
		this.locking = locking;

		fireElementChanged(true);
	}

	/**
	 * Is this element activated, either explicitely through
	 * {@link #setActive(boolean)} or from a referencing {@link Activating}.
	 * 
	 * @return <code>true</code> if activated
	 * 
	 * @see Activating#activates(Activateable)
	 */
	public boolean isActivated() {
		if (active) {
			return true;
		}

		for (Activating activating : getReferrer(Activating.class)) {
			if (activating.activates(this)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * If a referring {@link Activating} changes, this element changes too.
	 * 
	 * @see #isActivated()
	 */
	@Override
	public void referrerChanged(Element element) {
		if (element instanceof Activating) {
			fireElementChanged(false);
		}
	}

	public List<Class<? extends Matcher>> getMessageClasses() {
		List<Class<? extends Matcher>> names = super.getMessageClasses();

		names.add(Activate.class);
		names.add(Dectivate.class);

		return names;
	}

	public static class Activate extends Matcher {
	}

	public static class Dectivate extends Matcher {
	}
}