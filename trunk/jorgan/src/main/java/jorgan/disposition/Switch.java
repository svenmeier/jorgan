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

import jorgan.disposition.Input.InputMessage;
import jorgan.disposition.Output.OutputMessage;
import jorgan.midi.mpl.Command;
import jorgan.util.Null;

/**
 * Base class for an switchable elements.
 * 
 * @see #setActive(boolean)
 * @see #isActive()
 */
public class Switch extends Engageable {

	public static final int DURATION_INFINITE = -1;

	public static final int DURATION_NONE = 0;

	private boolean active = false;

	private int duration = DURATION_INFINITE;

	private Shortcut shortcut;

	/**
	 * Sweep from other {@link Switch}es if both are configured to
	 * {@link #DURATION_INFINITE}.
	 */
	@Override
	public void sweep(Displayable displayable) {
		if (displayable instanceof Switch) {
			Switch other = (Switch) displayable;

			if (other.getDuration() == DURATION_INFINITE
					&& this.getDuration() == DURATION_INFINITE) {
				setActive(other.isActive());
			}
		}
	}

	public Shortcut getShortcut() {
		return shortcut;
	}

	public void setShortcut(Shortcut shortcut) {
		if (!Null.safeEquals(this.shortcut, shortcut)) {
			Shortcut oldShortcut = this.shortcut;

			this.shortcut = shortcut;

			fireChange(new PropertyChange(oldShortcut, this.shortcut));
		}
	}

	public void toggle() {
		setActive(!isActive());
	}

	public void initiate() {
		setActive(true);
		setActive(false);
	}

	public void activate(boolean active) {
		if (active) {
			setActive(true);
			if (duration == 0) {
				setActive(false);
			}
		} else {
			setActive(false);
		}
	}

	public void setActive(boolean active) {
		if (this.active != active) {
			this.active = active;

			fireChange(new FastPropertyChange("active", false));

			onActivated(active);

			engagingChanged(active);

			for (Observer observer : getOrgan().getReferrer(this,
					Observer.class)) {
				observer.changed(this);
			}
		}
	}

	protected void onActivated(boolean active) {
	}

	public boolean isActive() {
		return active;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		if (duration < -1) {
			duration = -1;
		}

		if (this.duration != duration) {
			int oldDuration = this.duration;

			this.duration = duration;

			fireChange(new PropertyChange(oldDuration, this.duration));
		}
	}

	protected int getEngagedCount() {
		int count = super.getEngagedCount();
		if (isActive()) {
			count++;
		}
		return count;
	}

	public Activate createActivate(Command[] commands) {
		Activate activate = new Activate();

		activate.change(commands);

		return activate;
	}

	public Deactivate createDeactivate(Command[] commands) {
		Deactivate deactivate = new Deactivate();

		deactivate.change(commands);

		return deactivate;
	}

	public Toggle createToggle(Command[] commands) {
		Toggle toggle = new Toggle();

		toggle.change(commands);

		return toggle;
	}

	@Override
	public List<Class<? extends Message>> getMessageClasses() {
		List<Class<? extends Message>> classes = super.getMessageClasses();

		classes.add(Activate.class);
		classes.add(Deactivate.class);
		classes.add(Toggle.class);
		classes.add(Initiate.class);
		classes.add(Activated.class);
		classes.add(Deactivated.class);

		return classes;
	}

	public static class Activate extends InputMessage {
	}

	public static class Deactivate extends InputMessage {
	}

	public static class Toggle extends InputMessage {
	}

	public static class Initiate extends InputMessage {
	}

	public static class Activated extends OutputMessage {
	}

	public static class Deactivated extends OutputMessage {
	}
}