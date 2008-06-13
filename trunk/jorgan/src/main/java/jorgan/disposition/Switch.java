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
public class Switch extends Element implements Engageable, Console.Referenceable {

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

	/**
	 * Is this element engaged, either explicitely through
	 * {@link #setActive(boolean)} or from a referencing {@link Engaging}.
	 * 
	 * @return <code>true</code> if engaged
	 * 
	 * @see #setActive(boolean)
	 * @see Engaging#engages(Element)
	 */
	public final boolean isEngaged() {
		return getEngagedCount() > 0;
	}

	/**
	 * Hook method on change of {@link #isEngaged()}.
	 */
	protected void onEngaged(boolean engaged) {
	}
	
	/**
	 * Notification from a referencing {@link Engaging} of a change in
	 * {@link Engaging#engages(Switch)}.
	 * 
	 * @param engaged
	 */
	public final void engagingChanged(boolean engaged) {

		if (updateEngaged(engaged)) {
			fireChanged(false);
		}
	}
	
	private boolean updateEngaged(boolean engaged) {
		int engagedCount = getEngagedCount();
		
		if (engaged) {
			if (engagedCount == 1) {
				// first engaged
				onEngaged(true);
				return true;
			}
		} else {
			if (engagedCount == 0) {
				// last disengaged
				onEngaged(false);
				return true;
			}
		}
		
		return false;
	}
	
	private int getEngagedCount() {
		int count = 0;
		for (Engaging activating : getOrgan().getReferrer(this, Engaging.class)) {
			if (activating.engages(this)) {
				count++;
			}
		}
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