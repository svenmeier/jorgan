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
import jorgan.disposition.Output.OutputMessage;

/**
 * Base class for an switchable elements.
 * 
 * @see #setActive(boolean)
 * @see #isActive()
 */
public class Switch extends Momentary implements Engageable {

	private boolean active = false;

	private boolean locking = true;

	public void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;

			fireChanged(false);
			engagedChanged();
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

		fireChanged(true);
	}

	/**
	 * Is this element engaged, either explicitely through
	 * {@link #setActive(boolean)} or from a referencing {@link Activating}.
	 * 
	 * @return <code>true</code> if engaged
	 * 
	 * @see #setActive(boolean)
	 * @see Activating#activates(Element)
	 */
	public boolean isEngaged() {
		boolean engaged = false;

		if (active) {
			engaged = true;
		} else {
			if (getOrgan() != null) {
				for (Activating activating : getOrgan().getReferrer(this,
						Activating.class)) {
					if (activating.activates(this)) {
						engaged = true;
						break;
					}
				}
			}
		}

		return engaged;
	}

	/**
	 * Hook method on change of {@link #isEngaged()}.
	 */
	protected void engagedChanged() {
	}
	
	/**
	 * If a referring {@link Activating} changes, {@link #isEngaged()} might
	 * change too.
	 * 
	 * @see #isEngaged()
	 */
	@Override
	public void referrerChanged(Element element) {
		if (element instanceof Activating) {
			fireChanged(false);
			engagedChanged();
		}
	}

	@Override
	public Set<Class<? extends Message>> getMessageClasses() {
		Set<Class<? extends Message>> names = super.getMessageClasses();

		names.add(Activate.class);
		names.add(Deactivate.class);
		names.add(Toggle.class);
		names.add(Activated.class);
		names.add(Deactivated.class);

		return names;
	}

	public static class Activate extends InputMessage {
	}

	public static class Deactivate extends InputMessage {
	}

	public static class Toggle extends InputMessage {
	}

	public static class Activated extends OutputMessage {
	}

	public static class Deactivated extends OutputMessage {
	}
}