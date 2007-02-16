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

import java.util.ArrayList;

/**
 * A wizard implementation.
 */
public class BasicWizard implements Wizard {

	private ArrayList<WizardListener> listeners = new ArrayList<WizardListener>();

	private ArrayList<Page> pages = new ArrayList<Page>();

	protected Page current = null;

	/**
	 * Add a page.
	 * 
	 * @param page
	 *            page to add
	 */
	public void addPage(Page page) {
		addPage(pages.size(), page);
	}

	/**
	 * Add a page to the given index.
	 * 
	 * @param index
	 *            index
	 * @param page
	 *            page to add
	 */
	public void addPage(int index, Page page) {
		pages.add(index, page);
		page.setWizard(this);

		if (current == null) {
			setCurrentPage(page);
		}

		fireWizardChanged();
	}

	/**
	 * Remove a page.
	 * 
	 * @param page
	 *            page to remove
	 */
	public void removePage(Page page) {
		if (current == page) {
			setCurrentPage(pages.get(0));
		}

		pages.remove(page);
		page.setWizard(null);

		fireWizardChanged();
	}

	public boolean hasPrevious() {
		return pages.indexOf(current) > 0;
	}

	public boolean hasNext() {
		return pages.indexOf(current) < pages.size() - 1;
	}

	public Page getCurrentPage() {
		return current;
	}

	public void next() {
		if (current.leavingToNext()) {
			int index = pages.indexOf(current);

			Page page = pages.get(index + 1);

			page.enteringFromPrevious();

			setCurrentPage(page);
		}
	}

	public void previous() {
		if (current.leavingToPrevious()) {
			int index = pages.indexOf(current);

			Page page = pages.get(index - 1);

			page.enteringFromNext();

			setCurrentPage(page);
		}
	}

	public boolean allowsFinish() {
		return true;
	}

	public final void finish() {
		if (current.leavingToNext() && finishImpl()) {

			fireWizardFinished();
		}
	}

	protected boolean finishImpl() {
		return true;
	}

	/**
	 * Cancel this wizard.
	 */
	public void cancel() {
		cancelImpl();

		fireWizardCancelled();
	}

	protected void cancelImpl() {
	}

	public void pageChanged(Page page) {
		fireWizardChanged();
	}

	protected void setCurrentPage(Page page) {
		current = page;

		fireWizardChanged();
	}

	public void addWizardListener(WizardListener listener) {
		listeners.add(listener);
	}

	public void removeWizardListener(WizardListener listener) {
		listeners.remove(listener);
	}

	private void fireWizardChanged() {
		for (int l = 0; l < listeners.size(); l++) {
			WizardListener listener = listeners.get(l);

			listener.wizardChanged();
		}
	}

	private void fireWizardFinished() {
		for (int l = 0; l < listeners.size(); l++) {
			WizardListener listener = listeners.get(l);

			listener.wizardFinished();
		}
	}

	private void fireWizardCancelled() {
		for (int l = 0; l < listeners.size(); l++) {
			WizardListener listener = listeners.get(l);

			listener.wizardCancelled();
		}
	}
}