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
public class Switch extends Engageable {

	private boolean active = false;

	private boolean locking = true;

	private Shortcut shortcut;

	public Shortcut getShortcut() {
		return shortcut;
	}

	public void setShortcut(Shortcut shortcut) {
		this.shortcut = shortcut;

		fireChanged(true);
	}

	public void toggle() {
		setActive(!isActive());
	}

	public void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;

			fireChanged(false);

			onActivated(active);

			updateEngaged(active);

			for (Activating activating : getOrgan().getReferrer(this,
					Activating.class)) {
				activating.switchChanged(this, active);
			}
		}
	}

	/**
	 * Hook method on change of {@link #isActive()}.
	 */
	protected void onActivated(boolean active) {
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

	protected int getEngagedCount() {
		int count = super.getEngagedCount();
		if (isActive()) {
			count++;
		}
		return count;
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