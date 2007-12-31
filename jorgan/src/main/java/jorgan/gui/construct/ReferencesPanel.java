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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.JList;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Element;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.OrganAware;
import jorgan.gui.OrganSession;
import jorgan.gui.ReferenceListCellRenderer;
import jorgan.gui.event.ElementSelectionEvent;
import jorgan.gui.event.ElementSelectionListener;
import jorgan.swing.BaseAction;
import jorgan.swing.list.ListUtils;
import swingx.docking.DockedPanel;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * Panel shows the references of elements.
 */
public class ReferencesPanel extends DockedPanel implements OrganAware {

	private static Configuration config = Configuration.getRoot().get(
			ReferencesPanel.class);

	/**
	 * The edited organ.
	 */
	private OrganSession session;

	private Element element;

	private List<Reference> references = new ArrayList<Reference>();

	/**
	 * The listener to selection changes.
	 */
	private SelectionHandler selectionHandler = new SelectionHandler();

	private AddAction addAction = new AddAction();

	private RemoveAction removeAction = new RemoveAction();

	private JList list = new JList();

	private JToggleButton sortByNameButton = new JToggleButton();

	private JToggleButton sortByTypeButton = new JToggleButton();

	private ReferencesModel referencesModel = new ReferencesModel();

	/**
	 * Create a tree panel.
	 */
	public ReferencesPanel() {

		addTool(addAction);
		addTool(removeAction);

		addToolSeparator();

		ButtonGroup sortGroup = new ButtonGroup();
		config.get("sortByName").read(sortByNameButton);
		sortByNameButton.getModel().setGroup(sortGroup);
		sortByNameButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updateReferences();
			}
		});
		addTool(sortByNameButton);

		config.get("sortByType").read(sortByTypeButton);
		sortByTypeButton.getModel().setGroup(sortGroup);
		sortByTypeButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updateReferences();
			}
		});
		addTool(sortByTypeButton);

		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setModel(referencesModel);
		list.setCellRenderer(new ReferenceListCellRenderer() {
			@Override
			protected OrganSession getOrgan() {
				return session;
			}
		});
		list.addListSelectionListener(removeAction);
		ListUtils.addActionListener(list, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Element element = references.get(list.getSelectedIndex())
						.getElement();

				session.getSelectionModel().setSelectedElement(element);
			}
		});

		setScrollableBody(list, true, false);
	}

	/**
	 * Set the organ to be edited.
	 * 
	 * @param session
	 *            session to be edited
	 */
	public void setOrgan(OrganSession session) {
		if (this.session != null) {
			this.session.removeOrganListener(referencesModel);
			this.session.removeSelectionListener(selectionHandler);
		}

		this.session = session;

		if (this.session != null) {
			this.session.addOrganListener(referencesModel);
			this.session.addSelectionListener(selectionHandler);
		}

		updateReferences();
	}

	private void updateReferences() {
		element = null;
		references.clear();
		referencesModel.update();
		list.setVisible(false);

		if (session != null
				&& session.getSelectionModel().getSelectionCount() == 1) {

			element = session.getSelectionModel().getSelectedElement();

			for (Reference reference : element.getReferences()) {
				references.add(reference);
			}

			if (sortByNameButton.isSelected()) {
				Collections.sort(references, new ReferenceComparator(true));
			} else if (sortByTypeButton.isSelected()) {
				Collections.sort(references, new ReferenceComparator(false));
			}
			referencesModel.update();
			list.setVisible(true);
		}

		addAction.update();
	}

	/**
	 * The handler of selections.
	 */
	private class SelectionHandler implements ElementSelectionListener {

		public void selectionChanged(ElementSelectionEvent ev) {
			updateReferences();
		}
	}

	/**
	 * Note that <em>Spin</em> ensures that the methods of this listeners are
	 * called on the EDT, although a change in the organ might be triggered by a
	 * change on a MIDI thread.
	 */
	private class ReferencesModel extends AbstractListModel implements
			OrganListener {

		private int size = 0;

		private void update() {
			if (references.size() > size) {
				fireIntervalAdded(this, size, references.size() - 1);
			} else if (references.size() < size) {
				fireIntervalRemoved(this, references.size(), size - 1);
			} else {
				fireContentsChanged(this, 0, references.size());
			}
			size = references.size();
		}

		public int getSize() {
			return references.size();
		}

		public Object getElementAt(int index) {
			return references.get(index).getElement();
		}

		public void added(OrganEvent event) {
			if (event.getReference() != null && event.getElement() == element) {
				updateReferences();

				list.setSelectedIndex(references.indexOf(event.getReference()));
			}
		}

		public void removed(OrganEvent event) {
			if (event.getReference() != null && event.getElement() == element) {
				updateReferences();
			}
		}

		public void changed(final OrganEvent event) {
			if (event.getReference() != null && event.getElement() == element) {
				updateReferences();
			}
		}
	}

	private class AddAction extends BaseAction {

		private AddAction() {
			config.get("add").read(this);

			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			CreateReferencesWizard.showInDialog(ReferencesPanel.this, session
					.getOrgan(), element);
		}

		public void update() {
			setEnabled(element != null);
		}
	}

	private class RemoveAction extends BaseAction implements
			ListSelectionListener {

		private RemoveAction() {
			config.get("remove").read(this);

			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			if (config.get("remove/confirm").read(
					new MessageBox(MessageBox.OPTIONS_OK_CANCEL)).show(
					ReferencesPanel.this) != MessageBox.OPTION_OK) {
				return;
			}
			
			int[] indices = list.getSelectedIndices();
			if (indices != null) {
				for (int i = indices.length - 1; i >= 0; i--) {
					Reference reference = references.get(indices[i]);

					ReferencesPanel.this.element.removeReference(reference);
				}
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			setEnabled(list.getSelectedIndex() != -1);
		}
	}

	private class ReferenceComparator implements Comparator<Reference> {
		private ElementComparator comparator;

		public ReferenceComparator(boolean sort) {
			this.comparator = new ElementComparator(sort);
		}

		public int compare(Reference reference1, Reference reference2) {

			return comparator.compare(reference1.getElement(), reference2
					.getElement());
		}
	}
}