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
package jorgan.gui.construct;

import java.util.*;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;

import javax.swing.*;

import jorgan.swing.wizard.*;

import jorgan.disposition.*;

/**
 * A wizard for creating of references.
 */
public class CreateReferencesWizard extends BasicWizard {

    /**
     * The resource bundle.
     */
    protected static ResourceBundle resources = ResourceBundle
            .getBundle("jorgan.gui.resources");

    private Organ organ;

    private Element element;

    private List referencesTo = new ArrayList();

    private List referencedFrom = new ArrayList();

    /**
     * Create a new wizard.
     */
    public CreateReferencesWizard(Organ organ, Element element) {
        this.organ = organ;
        this.element = element;

        addPage(new ReferencesToPage());
        addPage(new ReferencedByPage());
    }

    /**
     * Allows finish only if elements for new references are selected.
     * 
     * @return <code>true</code> if stops are selected
     */
    public boolean allowsFinish() {
        return referencesTo.size() > 0 || referencedFrom.size() > 0;
    }

    /**
     * Finish.
     */
    protected boolean finishImpl() {

        for (int t = 0; t < referencesTo.size(); t++) {
            Element referenced = (Element) referencesTo.get(t);
            element.reference(referenced);
        }

        for (int f = 0; f < referencedFrom.size(); f++) {
            Element referrer = (Element) referencedFrom.get(f);
            referrer.reference(element);
        }

        return true;
    }

    /**
     * Page for selecting elements to reference to.
     */
    private class ReferencesToPage extends AbstractPage {

        private ElementsSelectionPanel elementsSelectionPanel = new ElementsSelectionPanel();

        public ReferencesToPage() {

            elementsSelectionPanel.addPropertyChangeListener(this);

            elementsSelectionPanel.setElements(organ
                    .getReferenceToCandidates(element));
        }

        public String getDescription() {
            return resources
                    .getString("construct.create.referencesTo.description");
        }

        public JComponent getComponent() {
            return elementsSelectionPanel;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            referencesTo = elementsSelectionPanel.getSelectedElements();

            super.propertyChange(evt);
        }
    }

    /**
     * Page for selecting elements to be referenced from.
     */
    private class ReferencedByPage extends AbstractPage {

        private ElementsSelectionPanel elementsSelectionPanel = new ElementsSelectionPanel();

        public ReferencedByPage() {

            elementsSelectionPanel.addPropertyChangeListener(this);

            elementsSelectionPanel.setElements(organ
                    .getReferencedFromCandidates(element));
        }

        public String getDescription() {
            return resources
                    .getString("construct.create.referencedFrom.description");
        }

        public JComponent getComponent() {
            return elementsSelectionPanel;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            referencedFrom = elementsSelectionPanel.getSelectedElements();

            super.propertyChange(evt);
        }
    }

    /**
     * Show an reference creation wizard in a dialog.
     * 
     * @param owner
     *            owner of dialog
     * @param element
     *            element to add created references to
     */
    public static void showInDialog(Frame owner, Organ organ, Element element) {

        WizardDialog dialog = new WizardDialog(owner);

        dialog.setTitle(resources
                .getString("construct.create.references.title"));

        dialog.setWizard(new CreateReferencesWizard(organ, element));

        dialog.start();

        dialog.dispose();
    }
}