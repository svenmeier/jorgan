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
package jorgan.gui.dock;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
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
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Element;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.ElementListCellRenderer;
import jorgan.gui.construct.CreateReferencesWizard;
import jorgan.gui.construct.ElementComparator;
import jorgan.session.OrganSession;
import jorgan.session.event.ElementSelectionEvent;
import jorgan.session.event.ElementSelectionListener;
import jorgan.swing.BaseAction;
import jorgan.swing.button.ButtonGroup;
import jorgan.swing.list.ListUtils;
import swingx.dnd.ObjectTransferable;
import swingx.docking.Docked;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * Panel shows the {@link Reference}s of {@link Element}s.
 */
public class ReferencesDockable extends OrganDockable {

	private static Configuration config = Configuration.getRoot().get(
			ReferencesDockable.class);

	/**
	 * The edited organ.
	 */
	private OrganSession session;

	private Element element;

	private List<ReferrerReference> references = new ArrayList<ReferrerReference>();

	private EventHandler eventHandler = new EventHandler();

	private AddAction addAction = new AddAction();

	private RemoveAction removeAction = new RemoveAction();

	private JList list = new JList();

	private JToggleButton referencesToButton = new JToggleButton();

	private JToggleButton referencedFromButton = new JToggleButton();

	private JToggleButton sortByNameButton = new JToggleButton();

	private JToggleButton sortByTypeButton = new JToggleButton();

	private ReferencesToModel referencesToModel = new ReferencesToModel();

	private ReferencedFromModel referencedFromModel = new ReferencedFromModel();

	/**
	 * Create a tree panel.
	 */
	public ReferencesDockable() {
		config.read(this);

		ButtonGroup toFromGroup = new ButtonGroup() {
			@Override
			protected void onSelected(AbstractButton button) {
				updateReferences();
			}
		};

		config.get("referencesTo").read(referencesToButton);
		toFromGroup.add(referencesToButton);

		config.get("referencedFrom").read(referencedFromButton);
		toFromGroup.add(referencedFromButton);

		ButtonGroup sortGroup = new ButtonGroup(true) {
			@Override
			protected void onSelected(AbstractButton button) {
				updateReferences();
			}
		};

		config.get("sortByName").read(sortByNameButton);
		sortGroup.add(sortByNameButton);

		config.get("sortByType").read(sortByTypeButton);
		sortGroup.add(sortByTypeButton);

		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.setModel(referencesToModel);
		list.setCellRenderer(new ElementListCellRenderer() {
			@Override
			protected OrganSession getOrgan() {
				return session;
			}
		});
		list.addListSelectionListener(removeAction);
		ListUtils.addActionListener(list, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Element element = (Element) list.getSelectedValue();

				session.getElementSelection().setSelectedElement(element);
			}
		});
		list.setTransferHandler(new TransferHandler() {
			@Override
			public void exportToClipboard(JComponent comp, Clipboard clip,
					int action) throws IllegalStateException {
				int[] indices = list.getSelectedIndices();
				if (indices.length > 0) {
					ReferrerReference[] subReferences = new ReferrerReference[indices.length];
					for (int r = 0; r < subReferences.length; r++) {
						subReferences[r] = references.get(indices[r]);
					}

					if (action == DnDConstants.ACTION_MOVE) {
						for (ReferrerReference reference : subReferences) {
							reference.getReferrer().removeReference(
									reference.getReference());
						}
					}

					clip.setContents(new ObjectTransferable(subReferences),
							null);
				}
			}

			@Override
			public boolean importData(JComponent comp, Transferable t) {
				if (element != null) {
					try {
						for (ReferrerReference reference : (ReferrerReference[]) ObjectTransferable
								.getObject(t)) {
							try {
								getReferencesModel().add(reference);
							} catch (Exception invalidReference) {
							}
						}
						return true;
					} catch (Exception noReferrerReferences) {
					}
				}
				return false;
			}
		});

		setContent(new JScrollPane(list));

		updateReferences();
	}

	@Override
	public boolean forPlay() {
		return false;
	}

	@Override
	public void docked(Docked docked) {
		super.docked(docked);

		docked.addTool(addAction);
		docked.addTool(removeAction);
		docked.addToolSeparator();
		docked.addTool(referencesToButton);
		docked.addTool(referencedFromButton);
		docked.addToolSeparator();
		docked.addTool(sortByNameButton);
		docked.addTool(sortByTypeButton);
	}

	/**
	 * Set the organ to be edited.
	 * 
	 * @param session
	 *            session to be edited
	 */
	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.removeOrganListener(eventHandler);
			this.session.removeSelectionListener(eventHandler);
		}

		this.session = session;

		if (this.session != null) {
			this.session.addOrganListener(eventHandler);
			this.session.addSelectionListener(eventHandler);
		}

		updateReferences();
	}

	private void updateReferences() {
		element = null;
		references.clear();

		list.setModel(new DefaultListModel());
		list.setVisible(false);

		if (session != null
				&& session.getElementSelection().getSelectionCount() == 1) {

			element = session.getElementSelection().getSelectedElement();

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

	private ReferencesModel getReferencesModel() {
		return (ReferencesModel) list.getModel();
	}

	/**
	 * Note that <em>Spin</em> ensures that the methods of this listeners are
	 * called on the EDT, although a change in the organ might be triggered by a
	 * change on a MIDI thread.
	 */
	private class EventHandler implements ElementSelectionListener,
			OrganListener {

		public void selectionChanged(ElementSelectionEvent ev) {
			updateReferences();
		}

		public void added(OrganEvent event) {
			if (element != null && event.getReference() != null
					&& getReferencesModel().onReferenceChange(event)) {
				updateReferences();

				for (int r = 0; r < references.size(); r++) {
					ReferrerReference reference = references.get(r);
					if (reference.getReference() == event.getReference()) {
						list.setSelectedIndex(r);
					}
				}
			}
		}

		public void removed(OrganEvent event) {
			if (element != null && event.getReference() != null
					&& getReferencesModel().onReferenceChange(event)) {
				updateReferences();
			}
		}

		public void changed(OrganEvent event) {
			if (element != null && event.getReference() != null
					&& getReferencesModel().onReferenceChange(event)) {
				updateReferences();
			}
		}
	}

	private abstract class ReferencesModel extends AbstractListModel implements
			Comparator<ReferrerReference> {

		public int getSize() {
			return references.size();
		}

		public Object getElementAt(int index) {
			return getElement(references.get(index));
		}

		public abstract void add(ReferrerReference references);

		public abstract boolean onReferenceChange(OrganEvent event);

		public void update() {
			references = getReferences();

			Collections.sort(references, this);
		}

		public int compare(ReferrerReference reference1,
				ReferrerReference reference2) {
			Element element1 = getElement(reference1);
			Element element2 = getElement(reference2);

			if (sortByNameButton.isSelected()) {
				return ElementComparator.compareByName(element1, element2);
			} else if (sortByTypeButton.isSelected()) {
				return ElementComparator.compareByType(element1, element2);
			} else {
				return 0;
			}
		}

		protected abstract List<ReferrerReference> getReferences();

		protected abstract Element getElement(ReferrerReference reference);
	}

	private class ReferencesToModel extends ReferencesModel {

		@Override
		public void add(ReferrerReference reference) {
			element.addReference(reference.getReference().clone());
		}

		@Override
		public boolean onReferenceChange(OrganEvent event) {
			return event.getElement() == element;
		}

		@Override
		protected List<ReferrerReference> getReferences() {
			List<ReferrerReference> references = new ArrayList<ReferrerReference>();

			for (Reference<? extends Element> reference : element
					.getReferences()) {
				references.add(new ReferrerReference(element, reference));
			}

			return references;
		}

		@Override
		protected Element getElement(ReferrerReference reference) {
			return reference.getReference().getElement();
		}
	}

	private class ReferencedFromModel extends ReferencesModel {

		@Override
		public void add(ReferrerReference reference) {
			reference.getReferrer().addReference(
					reference.getReference().clone(element));
		}

		@Override
		public boolean onReferenceChange(OrganEvent event) {
			return event.getReference().getElement() == element;
		}

		@Override
		protected List<ReferrerReference> getReferences() {
			List<ReferrerReference> references = new ArrayList<ReferrerReference>();

			for (Element aReferrer : element.getOrgan().getReferrer(element)) {
				for (Reference<? extends Element> reference : aReferrer
						.getReferences(element)) {
					references.add(new ReferrerReference(aReferrer, reference));
				}
			}

			return references;
		}

		@Override
		protected Element getElement(ReferrerReference reference) {
			return reference.getReferrer();
		}
	}

	private class AddAction extends BaseAction {

		private AddAction() {
			config.get("add").read(this);

			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			CreateReferencesWizard.showInDialog(list, session.getOrgan(),
					element);
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
					new MessageBox(MessageBox.OPTIONS_OK_CANCEL)).show(list) != MessageBox.OPTION_OK) {
				return;
			}

			int[] indices = list.getSelectedIndices();
			ReferrerReference[] subReferences = new ReferrerReference[indices.length];
			for (int r = 0; r < subReferences.length; r++) {
				subReferences[r] = references.get(indices[r]);
			}

			for (ReferrerReference reference : subReferences) {
				reference.getReferrer().removeReference(
						reference.getReference());
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			setEnabled(list.getSelectedIndex() != -1);
		}
	}

	public static class ReferrerReference {

		private Element referrer;

		private Reference<? extends Element> reference;

		public ReferrerReference(Element referrer,
				Reference<? extends Element> reference) {
			this.referrer = referrer;
			this.reference = reference;
		}

		public Element getReferrer() {
			return referrer;
		}

		public Reference<? extends Element> getReference() {
			return reference;
		}
	}
}