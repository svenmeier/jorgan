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

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;

import jorgan.swing.StandardDialog;
import jorgan.util.I18N;

/**
 * A dialog showing a wizard.
 */
public class WizardDialog extends StandardDialog {

	private static I18N i18n = I18N.get(WizardDialog.class);
	
	private Action previousAction = new PreviousAction();

	private Action nextAction = new NextAction();

	private Action finishAction = new FinishAction();

	private WizardListener listener = new InternalWizardListener();

	private Wizard wizard;

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner of this dialog
	 */
	public WizardDialog(JDialog owner) {
		super(owner);

		init();
	}

	/**
	 * Constructor.
	 * 
	 * @param owner
	 *            the owner of this dialog
	 */
	public WizardDialog(JFrame owner) {
		super(owner);

		init();
	}

	private void init() {

		setTitle(i18n.getString("title"));

		addAction(previousAction);
		addAction(nextAction);
		addAction(finishAction, true);
		addCancelAction();

		setWizard(new BasicWizard());
	}

	/**
	 * Set the wizard.
	 * 
	 * @param wizard
	 *            the wizard
	 */
	public void setWizard(Wizard wizard) {
		if (this.wizard != null) {
			this.wizard.removeWizardListener(listener);

			setBody(null);
		}

		this.wizard = wizard;

		if (this.wizard != null) {
			this.wizard.addWizardListener(listener);

			listener.wizardChanged();
		}
	}

	private class PreviousAction extends AbstractAction {

		private PreviousAction() {
			putValue(Action.NAME, i18n.getString("previousAction.name"));
		}

		public void actionPerformed(ActionEvent ev) {
			wizard.previous();
		}
	}

	private class NextAction extends AbstractAction {

		private NextAction() {
			putValue(Action.NAME, i18n.getString("nextAction.name"));
		}

		public void actionPerformed(ActionEvent ev) {
			wizard.next();
		}
	}

	private class FinishAction extends AbstractAction {

		private FinishAction() {
			putValue(Action.NAME, i18n.getString("finishAction.name"));
		}

		public void actionPerformed(ActionEvent ev) {
			wizard.finish();
		}
	}

	private class InternalWizardListener implements WizardListener {
		public void wizardChanged() {
			Page current = wizard.getCurrentPage();
			if (current == null) {
				setBody(null);
				previousAction.setEnabled(false);
				nextAction.setEnabled(false);
				finishAction.setEnabled(false);
			} else {
				JComponent component = current.getComponent();
				if (component == null) {
					setBody(null);
					setDescription(null);
				} else {
					if (!component.equals(getBody())) {
						setBody(component);
					}
					setDescription(current.getDescription());
				}
				previousAction.setEnabled(wizard.hasPrevious()
						&& wizard.getCurrentPage().allowsPrevious());
				nextAction.setEnabled(wizard.hasNext()
						&& wizard.getCurrentPage().allowsNext());
				finishAction.setEnabled(wizard.allowsFinish());
			}
		}

		public void wizardCancelled() {
			onCancel();
		}

		public void wizardFinished() {
			onOK();
		}
	}

	/**
	 * Create a dialog suitable for the given owner.
	 * 
	 * @param owner
	 *            owner
	 * @return dialog
	 */
	public static WizardDialog create(Component owner) {
		Window window = getWindow(owner);

		if (window instanceof JFrame) {
			return new WizardDialog((JFrame) window);
		} else if (window instanceof JDialog) {
			return new WizardDialog((JDialog) window);
		} else {
			throw new Error("unable to get window ancestor");
		}
	}
}