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

import javax.swing.JComponent;

/**
 * Basic implementation of a page.
 */
public abstract class AbstractPage implements Page {

	/**
	 * The containing wizard.
	 */
	private Wizard wizard;
	
	private JComponent component;

	/**
	 * Set the wizard.
	 * 
	 * @param wizard
	 *            wizard to set
	 */
	public void setWizard(Wizard wizard) {
		this.wizard = wizard;
	}

	private PropertyChangeListener changeListener = new PropertyChangeListener() {

		private boolean ignorePropertyChangeEventsWhileNotifyingWizard = false;

		public void propertyChange(PropertyChangeEvent evt) {
			if (!ignorePropertyChangeEventsWhileNotifyingWizard) {
				ignorePropertyChangeEventsWhileNotifyingWizard = true;

				if (wizard != null) {
					changing();
				}

				ignorePropertyChangeEventsWhileNotifyingWizard = false;
			}
		}
	};
 
	public final JComponent getComponent() {
		JComponent component = getComponentImpl();
		
		if (component != this.component) {
			if (this.component != null) {
				this.component.removePropertyChangeListener(changeListener);
			}
			
			this.component = component;
			
			if (this.component != null) {
				this.component.addPropertyChangeListener(changeListener);
			}
		}
		
		return this.component;
	}
	
	protected abstract JComponent getComponentImpl();
	
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

	protected void changing() {
		wizard.pageChanged(AbstractPage.this);
	}
}