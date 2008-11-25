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
 * Can this element be engaged.
 */
public class Engageable extends Displayable {

	/**
	 * Is this element engaged from referencing {@link Engaging}.
	 * 
	 * @return <code>true</code> if engaged
	 * 
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
	 * {@link Engaging#engages(Engageable))}.
	 * 
	 * @param engaged
	 */
	public final void engagingChanged(boolean engaged) {

		if (isEngagedChange(engaged)) {
			fireChange(new SimplePropertyChange());

			onEngaged(engaged);
		}
	}

	protected final boolean isEngagedChange(boolean engaged) {
		int engagedCount = getEngagedCount();

		if (engaged) {
			if (engagedCount == 1) {
				// first engaged
				return true;
			}
		} else {
			if (engagedCount == 0) {
				// last disengaged
				return true;
			}
		}

		return false;
	}

	protected int getEngagedCount() {
		int count = 0;
		for (Engaging engaging : getOrgan().getReferrer(this, Engaging.class)) {
			if (engaging.engages(this)) {
				count++;
			}
		}
		return count;
	}
}