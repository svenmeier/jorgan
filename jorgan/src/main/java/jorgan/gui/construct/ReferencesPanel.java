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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Element;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.OrganSession;
import jorgan.gui.event.ElementSelectionEvent;
import jorgan.gui.event.ElementSelectionListener;
import jorgan.swing.list.ListUtils;
import spin.Spin;
import swingx.docking.DockedPanel;

/**
 * Panel shows the references of elements.
 */
public class ReferencesPanel extends DockedPanel {

    protected static final ResourceBundle resources = ResourceBundle
            .getBundle("jorgan.gui.resources");

    private static final Icon sortNameIcon = new ImageIcon(ElementsPanel.class
            .getResource("/jorgan/gui/img/sortName.gif"));

    private static final Icon sortTypeIcon = new ImageIcon(ElementsPanel.class
            .getResource("/jorgan/gui/img/sortType.gif"));

    private static final Icon referencesToIcon = new ImageIcon(
            ElementsPanel.class.getResource("/jorgan/gui/img/referencesTo.gif"));

    private static final Icon referencedFromIcon = new ImageIcon(
            ElementsPanel.class
                    .getResource("/jorgan/gui/img/referencedFrom.gif"));

    private static final Icon referenceIcon = new ImageIcon(ElementsPanel.class
            .getResource("/jorgan/gui/img/reference.gif"));

    /**
     * The edited organ.
     */
    private OrganSession session;

    private List rows = new ArrayList();

    /**
     * The listener to selection changes.
     */
    private SelectionHandler selectionHandler = new SelectionHandler();

    private AddAction addAction = new AddAction();

    private RemoveAction removeAction = new RemoveAction();

    private JList list = new JList();

    private JToggleButton referencesToButton = new JToggleButton(
            referencesToIcon);

    private JToggleButton referencedFromButton = new JToggleButton(
            referencedFromIcon);

    private JToggleButton sortNameButton = new JToggleButton(sortNameIcon);

    private JToggleButton sortTypeButton = new JToggleButton(sortTypeIcon);

    private ReferencesModel referencesModel = new ReferencesModel();

    /**
     * Create a tree panel.
     */
    public ReferencesPanel() {

        addTool(addAction);
        addTool(removeAction);

        addToolSeparator();

        sortNameButton.setSelected(true);
        sortNameButton.setToolTipText(resources.getString("sort.name"));
        sortNameButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (sortNameButton.isSelected()) {
                    sortTypeButton.setSelected(false);
                }
                updateReferences();
            }
        });
        addTool(sortNameButton);

        sortTypeButton.setToolTipText(resources.getString("sort.type"));
        sortTypeButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (sortTypeButton.isSelected()) {
                    sortNameButton.setSelected(false);
                }
                updateReferences();
            }
        });
        addTool(sortTypeButton);

        addToolSeparator();

        ButtonGroup toFromGroup = new ButtonGroup();
        referencesToButton.getModel().setGroup(toFromGroup);
        referencesToButton.setSelected(true);
        referencesToButton.getModel().addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                updateReferences();
            }
        });
        addTool(referencesToButton);

        referencedFromButton.getModel().setGroup(toFromGroup);
        addTool(referencedFromButton);

        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setModel(referencesModel);
        list.setCellRenderer(new ReferenceListCellRenderer());
        list.addListSelectionListener(selectionHandler);
        ListUtils.addActionListener(list, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Row row = (Row) rows.get(list.getSelectedIndex());

                if (getShowReferencesTo()) {
                    session.getSelectionModel().setSelectedElement(
                            row.reference.getElement());
                } else {
                    session.getSelectionModel().setSelectedElement(row.element);
                }
            }
        });

        setScrollableBody(list, true, false);
    }

    /**
     * Set the organ to be edited.
     * 
     * @param organ
     *            organ to be edited
     */
    public void setOrgan(OrganSession session) {
        if (this.session != null) {
            this.session.getOrgan().removeOrganListener(
                    (OrganListener) Spin.over(referencesModel));
            this.session.getSelectionModel().removeSelectionListener(
                    selectionHandler);
        }

        this.session = session;

        if (this.session != null) {
            this.session.getOrgan().addOrganListener(
                    (OrganListener) Spin.over(referencesModel));
            this.session.getSelectionModel().addSelectionListener(
                    selectionHandler);
        }

        updateReferences();
    }

    private void updateReferences() {

        int size = rows.size();
        rows.clear();
        referencesModel.fireRemoved(size);

        if (session != null && session.getSelectionModel().getSelectionCount() == 1) {
            Element element = session.getSelectionModel().getSelectedElement();

            if (getShowReferencesTo()) {
                for (int r = 0; r < element.getReferenceCount(); r++) {
                    rows.add(new Row(element, element.getReference(r)));
                }
            } else {
                Iterator iterator = element.getReferrer().iterator();
                while (iterator.hasNext()) {
                    Element referrer = (Element) iterator.next();

                    List references = referrer.getReferences(element);
                    for (int r = 0; r < references.size(); r++) {
                        rows.add(new Row(referrer, (Reference) references
                                .get(r)));
                    }
                }
            }
        }

        if (sortNameButton.isSelected()) {
            Collections.sort(rows, new RowComparator(
                    new ElementComparator(true)));
        } else if (sortTypeButton.isSelected()) {
            Collections.sort(rows, new RowComparator(new ElementComparator(
                    false)));
        }
        referencesModel.fireAdded(rows.size());

        addAction.update();
    }

    public void setShowReferencesTo(boolean showReferencesTo) {
        if (showReferencesTo != referencesToButton.isSelected()) {
            if (showReferencesTo) {
                referencesToButton.setSelected(true);
            } else {
                referencedFromButton.setSelected(true);
            }
        }
    }

    public boolean getShowReferencesTo() {
        return referencesToButton.isSelected();
    }

    /**
     * The handler of selections.
     */
    private class SelectionHandler implements ElementSelectionListener,
            ListSelectionListener {

        public void selectionChanged(ElementSelectionEvent ev) {
            updateReferences();
        }

        public void valueChanged(ListSelectionEvent e) {
            removeAction.update();
        }
    }

    /**
     * Note that <em>Spin</em> ensures that the methods of this listeners are
     * called on the EDT, although a change in the organ might be triggered by a
     * change on a MIDI thread.
     */
    private class ReferencesModel extends AbstractListModel implements
            OrganListener {

        public int getSize() {
            if (rows == null) {
                return 0;
            } else {
                return rows.size();
            }
        }

        public Object getElementAt(int index) {
            return rows.get(index);
        }

        public void elementAdded(OrganEvent event) {
        }

        public void elementRemoved(OrganEvent event) {
        }

        public void elementChanged(final OrganEvent event) {
            int index = rows.indexOf(event.getElement());
            if (index != -1) {
                fireContentsChanged(this, index, index);
            }
        }

        public void fireRemoved(int count) {
            if (count > 0) {
                fireIntervalRemoved(this, 0, count - 1);
            }
        }

        public void fireAdded(int count) {
            if (count > 0) {
                fireIntervalAdded(this, 0, count - 1);
            }
        }

        public void referenceChanged(OrganEvent event) {
        }

        public void referenceAdded(OrganEvent event) {
            updateReferences();
        }

        public void referenceRemoved(OrganEvent event) {
            updateReferences();
        }
    }

    public class ReferenceListCellRenderer extends DefaultListCellRenderer {

        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {

            Row row = (Row)value;
            
            Element element;
            if (getShowReferencesTo()) {
                element = row.reference.getElement();
            } else {
                element = row.element;
            }
            
            String name = ElementUtils.getElementAndTypeName(element,
                    sortNameButton.isSelected());

            super.getListCellRendererComponent(list, name, index, isSelected,
                    cellHasFocus);

            setIcon(referenceIcon);

            return this;
        }
    }

    private class AddAction extends AbstractAction {

        public AddAction() {
            putValue(Action.NAME, resources
                    .getString("construct.action.reference.add.name"));
            putValue(Action.SHORT_DESCRIPTION, resources
                    .getString("construct.action.reference.add.description"));
            putValue(Action.SMALL_ICON, new ImageIcon(ElementsPanel.class
                    .getResource("/jorgan/gui/img/add.gif")));

            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ev) {
            CreateReferencesWizard.showInDialog((JFrame) SwingUtilities
                    .getWindowAncestor(ReferencesPanel.this), session
                    .getOrgan(), session.getSelectionModel().getSelectedElement());
        }

        public void update() {
            setEnabled(session != null && session.getSelectionModel().getSelectionCount() == 1);
        }
    }

    private class RemoveAction extends AbstractAction {

        public RemoveAction() {
            putValue(Action.NAME, resources
                    .getString("construct.action.reference.remove.name"));
            putValue(Action.SHORT_DESCRIPTION, resources
                    .getString("construct.action.reference.remove.description"));
            putValue(Action.SMALL_ICON, new ImageIcon(ElementsPanel.class
                    .getResource("/jorgan/gui/img/remove.gif")));

            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ev) {
            int[] indices = list.getSelectedIndices();
            if (indices != null) {
                for (int i = indices.length - 1; i >= 0; i--) {
                    Row row = (Row) rows.get(indices[i]);

                    if (getShowReferencesTo()) {
                        row.element.removeReference(row.reference);
                    } else {
                        row.element.removeReference(row.reference);
                    }
                }
            }
        }

        public void update() {
            setEnabled(list.getSelectedIndex() != -1);
        }
    }

    private class Row {
        public Row(Element element, Reference reference) {
            this.element = element;
            this.reference = reference;
        }

        public Element element;

        public Reference reference;
    }

    private class RowComparator implements Comparator {
        private ElementComparator comparator;

        public RowComparator(ElementComparator comparator) {
            this.comparator = comparator;
        }

        public int compare(Object o1, Object o2) {
            Row row1 = (Row) o1;
            Row row2 = (Row) o2;

            if (getShowReferencesTo()) {
                return comparator.compare(row1.reference.getElement(),
                        row2.reference.getElement());
            } else {
                return comparator.compare(row1.element, row2.element);
            }
        }
    }
}