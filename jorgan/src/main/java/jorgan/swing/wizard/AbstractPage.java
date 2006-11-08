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
package jorgan.swing.wizard;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Basic implementation of a page.
 */
public abstract class AbstractPage implements Page, PropertyChangeListener {

	/**
	 * The containing wizard.
	 */
	private Wizard wizard;

	private boolean ignorePropertyChangeEventsWhileNotifyingWizard = false;

	/**
	 * Set the wizard.
	 * 
	 * @param wizard
	 *            wizard to set
	 */
	public void setWizard(Wizard wizard) {
		this.wizard = wizard;
	}

	/**
	 * Default implementation does nothing.
	 */
	public void enteringFromNext() {
	}

	/**
	 * Default implementation does nothing.
	 */
	public void enteringFromPrevious() {
	}

	/**
	 * Get a description.
	 * 
	 * @return description
	 */
	public String getDescription() {
		return null;
	}

	/**
	 * Default implementation allows leaving to next page.
	 */
	public boolean allowsNext() {
		return true;
	}

	/**
	 * Default implementation allows leaving to previous page.
	 */
	public boolean allowsPrevious() {
		return true;
	}

	/**
	 * Default implementation does nothing.
	 */
	public boolean leavingToNext() {
		return true;
	}

	/**
	 * Default implementation does nothing.
	 */
	public boolean leavingToPrevious() {
		return true;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (!ignorePropertyChangeEventsWhileNotifyingWizard) {
			ignorePropertyChangeEventsWhileNotifyingWizard = true;

			if (wizard != null) {
				wizard.pageChanged(this);
			}

			ignorePropertyChangeEventsWhileNotifyingWizard = false;
		}
	}
}