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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
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
import jorgan.swing.button.ButtonGroup;
import jorgan.swing.list.ListUtils;
import swingx.dnd.ObjectTransferable;
import swingx.docking.DockedPanel;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * Panel shows the {@link Reference}s of {@link Element}s.
 */
public class ReferencesPanel extends DockedPanel implements OrganAware {

	private static Configuration config = Configuration.getRoot().get(
			ReferencesPanel.class);

	/**
	 * The edited organ.
	 */
	private OrganSession session;

	private Element element;

	/**
	 * The listener to selection changes.
	 */
	private SelectionHandler selectionHandler = new SelectionHandler();

	private AddAction addAction = new AddAction();

	private RemoveAction removeAction = new RemoveAction();

	private JList list = new JList();

	private JToggleButton referencesToButton = new JToggleButton();

	private JToggleButton referencedFromButton = new JToggleButton();

	private JToggleButton sortByNameButton = new JToggleButton();

	private JToggleButton sortByTypeButton = new JToggleButton();

	private OrganListener organListener = new OrganObserver();

	private ReferencesToModel referencesToModel = new ReferencesToModel();

	private ReferencedFromModel referencedFromModel = new ReferencedFromModel();

	/**
	 * Create a tree panel.
	 */
	public ReferencesPanel() {

		addTool(addAction);
		addTool(removeAction);

		addToolSeparator();

		ButtonGroup toFromGroup = new ButtonGroup() {
			@Override
			protected void onSelected(AbstractButton button) {
				updateReferences();
			}
		};

		config.get("referencesTo").read(referencesToButton);
		toFromGroup.add(referencesToButton);
		addTool(referencesToButton);

		config.get("referencedFrom").read(referencedFromButton);
		toFromGroup.add(referencedFromButton);
		addTool(referencedFromButton);

		addToolSeparator();

		ButtonGroup sortGroup = new ButtonGroup(true) {
			@Override
			protected void onSelected(AbstractButton button) {
				updateReferences();
			}
		};

		config.get("sortByName").read(sortByNameButton);
		sortGroup.add(sortByNameButton);
		addTool(sortByNameButton);

		config.get("sortByType").read(sortByTypeButton);
		sortGroup.add(sortByTypeButton);
		addTool(sortByTypeButton);

		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setModel(referencesToModel);
		list.setCellRenderer(new ReferenceListCellRenderer() {
			@Override
			protected OrganSession getOrgan() {
				return session;
			}
		});
		list.addListSelectionListener(removeAction);
		ListUtils.addActionListener(list, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Element element = (Element) list.getSelectedValue();

				session.getSelectionModel().setSelectedElement(element);
			}
		});
		list.setTransferHandler(new TransferHandler() {
			@Override
			public void exportToClipboard(JComponent comp, Clipboard clip,
					int action) throws IllegalStateException {
				int[] indices = list.getSelectedIndices();
				if (indices.length > 0) {
					clip.setContents(new ObjectTransferable(getModel().get(
							indices, true)), null);
				}
			}

			@Override
			public boolean importData(JComponent comp, Transferable t) {
				try {
					getModel().add(ObjectTransferable.getObject(t));

					return true;
				} catch (Exception noImport) {
					return false;
				}
			}
		});

		setScrollableBody(list, true, false);

		updateReferences();
	}

	/**
	 * Set the organ to be edited.
	 * 
	 * @param session
	 *            session to be edited
	 */
	public void setOrgan(OrganSession session) {
		if (this.session != null) {
			this.session.removeOrganListener(organListener);
			this.session.removeSelectionListener(selectionHandler);
		}

		this.session = session;

		if (this.session != null) {
			this.session.addOrganListener(organListener);
			this.session.addSelectionListener(selectionHandler);
		}

		updateReferences();
	}

	private void updateReferences() {
		element = null;

		list.setModel(new DefaultListModel());
		list.setVisible(false);

		if (session != null
				&& session.getSelectionModel().getSelectionCount() == 1) {

			element = session.getSelectionModel().getSelectedElement();

			if (referencesToButton.isSelected()) {
				referencesToModel.update();
				list.setModel(referencesToModel);
			} else {
				referencedFromModel.update();
				list.setModel(referencedFromModel);
			}
			list.setVisible(true);
		}

		addAction.update();
	}

	private ReferencesModel getModel() {
		return (ReferencesModel) list.getModel();
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
	private class OrganObserver implements OrganListener {
		public void added(OrganEvent event) {
			getModel().onChange(event);
		}

		public void removed(OrganEvent event) {
			getModel().onChange(event);
		}

		public void changed(final OrganEvent event) {
			getModel().onChange(event);
		}
	}

	private abstract class ReferencesModel extends AbstractListModel {
		public abstract void add(Object object);

		public abstract void onChange(OrganEvent event);

		public abstract Object get(int[] indices, boolean cut);
	}

	private class ReferencesToModel extends ReferencesModel {

		private List<Reference> references = new ArrayList<Reference>();

		public int getSize() {
			return references.size();
		}

		public Object getElementAt(int index) {
			return references.get(index).getElement();
		}

		@Override
		public Object get(int[] indices, boolean cut) {
			Reference[] subReferences = new Reference[indices.length];
			for (int r = 0; r < subReferences.length; r++) {
				subReferences[r] = references.get(indices[r]);
			}

			if (cut) {
				for (Reference reference : subReferences) {
					element.removeReference(reference);
				}
			}

			return subReferences;
		}

		@Override
		public void add(Object object) {
			Reference[] subReferences = (Reference[]) object;
			for (Reference reference : subReferences) {
				if (element.canReference(reference.getElement())) {
					element.addReference(reference.clone());
				}
			}
		}

		@Override
		public void onChange(OrganEvent event) {
			if (event.getElement() == element && event.getReference() != null) {
				updateReferences();
			}
		}

		public void update() {
			references.clear();

			for (Reference reference : element.getReferences()) {
				references.add(reference);
			}

			if (sortByNameButton.isSelected()) {
				Collections.sort(references, new ReferenceComparator(true));
			} else if (sortByTypeButton.isSelected()) {
				Collections.sort(references, new ReferenceComparator(false));
			}
		}
	}

	/**
	 * TODO would be nice to use a tree for this.
	 */
	private class ReferencedFromModel extends ReferencesModel {

		private List<BackReference> backReferences = new ArrayList<BackReference>();

		public int getSize() {
			return backReferences.size();
		}

		public Object getElementAt(int index) {
			return backReferences.get(index).element;
		}

		@Override
		public Object get(int[] indices, boolean cut) {
			BackReference[] subBackReferences = new BackReference[indices.length];
			for (int r = 0; r < subBackReferences.length; r++) {
				subBackReferences[r] = backReferences.get(indices[r]);
			}

			if (cut) {
				for (BackReference backReference : subBackReferences) {
					backReference.getElement().removeReference(
							backReference.getReference());
				}
			}

			return subBackReferences;
		}

		@Override
		public void add(Object object) {
			BackReference[] subBackReferences = (BackReference[]) object;
			for (BackReference subBackReference : subBackReferences) {
				if (subBackReference.getElement().canReference(element)) {
					subBackReference.getElement().addReference(
							subBackReference.getReference().clone(element));
				}
			}
		}

		@Override
		public void onChange(OrganEvent event) {
			if (event.getReference() != null) {
				if (event.getElement().references(element)) {
					updateReferences();
					return;
				}
				
				for (BackReference backReference : backReferences) {
					if (backReference.getElement() == event.getElement()) {
						updateReferences();
						return;
					}
				}
			}
		}

		public void update() {
			backReferences.clear();

			for (Element aReferrer : element.getOrgan().getReferrer(element)) {
				for (Reference<? extends Element> reference : aReferrer
						.getReferences(element)) {
					backReferences.add(new BackReference(aReferrer, reference));
				}
			}

			if (sortByNameButton.isSelected()) {
				Collections.sort(backReferences, new BackReferenceComparator(
						true));
			} else if (sortByTypeButton.isSelected()) {
				Collections.sort(backReferences, new BackReferenceComparator(
						false));
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

			getModel().get(list.getSelectedIndices(), true);
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

	private class BackReferenceComparator implements Comparator<BackReference> {
		private ElementComparator comparator;

		public BackReferenceComparator(boolean sort) {
			this.comparator = new ElementComparator(sort);
		}

		public int compare(BackReference reference1, BackReference reference2) {

			return comparator.compare(reference1.getElement(), reference2
					.getElement());
		}
	}

	public static class BackReference {

		private Element element;

		private Reference reference;

		public BackReference(Element element, Reference reference) {
			this.element = element;
			this.reference = reference;
		}

		public Element getElement() {
			return element;
		}

		public Reference getReference() {
			return reference;
		}
	}
}