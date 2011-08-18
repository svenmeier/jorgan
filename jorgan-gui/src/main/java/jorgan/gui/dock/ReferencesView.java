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
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Element;
import jorgan.disposition.ElementNameComparator;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.ElementListCellRenderer;
import jorgan.gui.construct.CreateReferencesWizard;
import jorgan.gui.construct.ElementTypeComparator;
import jorgan.gui.selection.ElementSelection;
import jorgan.gui.selection.SelectionListener;
import jorgan.gui.undo.Compound;
import jorgan.gui.undo.UndoManager;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.button.ButtonGroup;
import jorgan.swing.list.ListUtils;
import jorgan.util.ComparatorChain;
import spin.Spin;
import swingx.dnd.ObjectTransferable;
import swingx.docking.Docked;
import bias.Configuration;

/**
 * Panel shows the {@link Reference}s of {@link Element}s.
 */
public class ReferencesView extends AbstractView {

	private static Configuration config = Configuration.getRoot().get(
			ReferencesView.class);

	/**
	 * The edited organ.
	 */
	private OrganSession session;

	private ObjectTransferable transferable;

	private List<Item> items = new ArrayList<Item>();

	private EventHandler eventHandler = new EventHandler();

	private AddAction addAction = new AddAction();

	private RemoveAction removeAction = new RemoveAction();

	private JList list = new JList();

	private JToggleButton referencesToButton = new JToggleButton();

	private JToggleButton referencedFromButton = new JToggleButton();

	private JToggleButton sortByNameButton = new JToggleButton();

	private JToggleButton sortByTypeButton = new JToggleButton();

	/**
	 * Create a tree panel.
	 */
	public ReferencesView() {
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
		list.setCellRenderer(new ElementListCellRenderer() {
			@Override
			protected OrganSession getOrgan() {
				return session;
			}
		});
		list.addListSelectionListener(removeAction);
		ListUtils.addActionListener(list, 2, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Element element = (Element) list.getSelectedValue();

				session.lookup(ElementSelection.class).setSelectedElement(
						element);
			}
		});
		list.setDragEnabled(true);
		list.setDropMode(DropMode.INSERT);
		list.setTransferHandler(new TransferHandler() {
			@Override
			public int getSourceActions(JComponent c) {
				return COPY | MOVE;
			}

			@Override
			public boolean canImport(TransferSupport support) {

				if (sortByNameButton.isSelected()
						|| sortByTypeButton.isSelected()) {
					return false;
				}

				Item[] items;
				try {
					items = (Item[]) ObjectTransferable.getObject(support
							.getTransferable());
				} catch (Exception ex) {
					return false;
				}

				ReferencesModel model = getReferencesModel();

				return model.canAdd(items);
			}

			@Override
			protected Transferable createTransferable(JComponent c) {
				int[] rows = list.getSelectedIndices();

				Item[] subItems = new Item[rows.length];
				for (int t = 0; t < subItems.length; t++) {
					subItems[t] = items.get(rows[t]);
				}

				return new ObjectTransferable(subItems);
			}

			@Override
			protected void exportDone(JComponent source, Transferable t,
					int action) {
				try {
					if (action == MOVE) {
						Item[] subItems = (Item[]) ObjectTransferable
								.getObject(t);
						for (Item item : subItems) {
							item.getReferrer().removeReference(
									item.getReference());
						}
					}
				} catch (Exception noExport) {
				}
			}

			@Override
			public void exportToClipboard(JComponent comp, Clipboard clip,
					int action) throws IllegalStateException {
				int[] indices = list.getSelectedIndices();
				if (indices.length > 0) {
					Item[] subItems = new Item[indices.length];
					for (int r = 0; r < subItems.length; r++) {
						subItems[r] = items.get(indices[r]);
					}

					if (action == DnDConstants.ACTION_MOVE) {
						for (Item item : subItems) {
							item.getReferrer().removeReference(
									item.getReference());
						}
					}

					transferable = new ObjectTransferable(subItems);

					clip.setContents(transferable, null);
				}
			}

			@Override
			public boolean importData(final TransferSupport support) {
				try {
					final Item[] items = (Item[]) ObjectTransferable
							.getObject(support.getTransferable());

					session.lookup(UndoManager.class).compound(new Compound() {
						@Override
						public void run() {
							int index = ListUtils.importIndex(list, support);

							ReferencesModel model = getReferencesModel();
							for (Item item : items) {
								try {
									model.add(item, index);
								} catch (Exception invalidReference) {
								}
							}
						}
					});
					return true;
				} catch (Exception noItem) {
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
	protected void addTools(Docked docked) {
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
			this.session.getOrgan().removeOrganListener(
					(OrganListener) Spin.over(eventHandler));
			this.session.lookup(ElementSelection.class).removeListener(
					eventHandler);
		}

		this.session = session;

		if (this.session != null) {
			this.session.getOrgan().addOrganListener(
					(OrganListener) Spin.over(eventHandler));
			this.session.lookup(ElementSelection.class).addListener(
					eventHandler);
		}

		if (transferable != null) {
			transferable.clear();
			transferable = null;
		}

		updateReferences();
	}

	private void updateReferences() {
		items.clear();

		list.setModel(new DefaultListModel());
		list.setVisible(false);

		if (session != null
				&& session.lookup(ElementSelection.class).getSelectionCount() == 1) {

			Element element = session.lookup(ElementSelection.class)
					.getSelectedElement();

			if (referencesToButton.isSelected()) {
				list.setModel(new ReferencesToModel(element));
			} else {
				list.setModel(new ReferencedFromModel(element));
			}
			list.setVisible(true);

			Object location = session.lookup(ElementSelection.class)
					.getLocation();
			if (location instanceof Reference<?>) {
				Reference<?> reference = (Reference<?>) location;

				for (int r = 0; r < items.size(); r++) {
					Item rr = items.get(r);
					if (rr.getReference() == reference) {
						list.setSelectedIndex(r);
					}
				}
			}
		}

		addAction.update();
	}

	private ReferencesModel getReferencesModel() {
		return (ReferencesModel) list.getModel();
	}

	private Element getElement() {
		if (list.getModel() instanceof ReferencesModel) {
			return ((ReferencesModel) list.getModel()).getElement();
		}
		return null;
	}

	/**
	 * Note that <em>Spin</em> ensures that the methods of this listeners are
	 * called on the EDT, although a change in the organ might be triggered by a
	 * change on a MIDI thread.
	 */
	private class EventHandler extends OrganAdapter implements
			SelectionListener, OrganListener {

		public void selectionChanged() {
			updateReferences();
		}

		@Override
		public void indexedPropertyAdded(Element element, String name,
				Object value) {
			if (Element.REFERENCE.equals(name)) {
				Reference<?> reference = (Reference<?>) value;

				if (getElement() != null
						&& getReferencesModel().onReferenceChange(element,
								reference)) {
					updateReferences();
				}
			}
		}

		@Override
		public void indexedPropertyRemoved(Element element, String name,
				Object value) {
			if (Element.REFERENCE.equals(name)) {
				Reference<?> reference = (Reference<?>) value;

				if (getElement() != null
						&& getReferencesModel().onReferenceChange(element,
								reference)) {
					updateReferences();
				}
			}
		}

		@Override
		public void indexedPropertyChanged(Element element, String name,
				Object value) {
			if (Element.REFERENCE.equals(name)) {
				Reference<?> reference = (Reference<?>) value;
				if (getElement() != null
						&& getReferencesModel().onReferenceChange(element,
								reference)) {
					updateReferences();
				}
			}
		}
	}

	private abstract class ReferencesModel extends AbstractListModel implements
			Comparator<Item> {

		private Element element;

		private Comparator<Element> nameType = ComparatorChain.of(
				new ElementNameComparator(), new ElementTypeComparator());
		private Comparator<Element> typeName = ComparatorChain.of(
				new ElementTypeComparator(), new ElementNameComparator());

		protected ReferencesModel(Element element) {
			this.element = element;

			items = createItems();

			Collections.sort(items, this);
		}

		public boolean canAdd(Item[] items) {
			return false;
		}

		public Element getElement() {
			return element;
		}

		protected abstract List<Item> createItems();

		public int compare(Item item1, Item item2) {
			Element element1 = getElement(item1);
			Element element2 = getElement(item2);

			if (sortByNameButton.isSelected()) {
				return nameType.compare(element1, element2);
			} else if (sortByTypeButton.isSelected()) {
				return typeName.compare(element1, element2);
			} else {
				return 0;
			}
		}

		public int getSize() {
			return items.size();
		}

		public Object getElementAt(int index) {
			return getElement(items.get(index));
		}

		public abstract void add(Item item, int index);

		public abstract boolean onReferenceChange(Element element,
				Reference<?> reference);

		protected abstract Element getElement(Item item);
	}

	private class ReferencesToModel extends ReferencesModel {

		protected ReferencesToModel(Element element) {
			super(element);
		}

		public boolean canAdd(Item[] items) {
			for (Item item : items) {
				if (!getElement()
						.canReference(item.getReference().getElement())) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void add(Item item, int index) {
			getElement().addReference(item.getReference().clone(), index);
		}

		@Override
		public boolean onReferenceChange(Element element, Reference<?> reference) {
			return element == this.getElement();
		}

		@Override
		protected List<Item> createItems() {
			List<Item> items = new ArrayList<Item>();

			for (Reference<? extends Element> reference : getElement()
					.getReferences()) {
				items.add(new Item(getElement(), reference));
			}

			return items;
		}

		@Override
		protected Element getElement(Item item) {
			return item.getReference().getElement();
		}
	}

	private class ReferencedFromModel extends ReferencesModel {

		protected ReferencedFromModel(Element element) {
			super(element);
		}

		@Override
		public void add(Item item, int index) {
			item.getReferrer().addReference(
					item.getReference().clone(getElement()));
		}

		@Override
		public boolean onReferenceChange(Element element, Reference<?> item) {
			return item.getElement() == this.getElement();
		}

		@Override
		protected List<Item> createItems() {
			List<Item> items = new ArrayList<Item>();

			for (Element referrer : getElement().getOrgan().getReferrer(
					getElement())) {
				for (Reference<? extends Element> reference : referrer
						.getReferences(getElement())) {
					items.add(new Item(referrer, reference));
				}
			}

			return items;
		}

		@Override
		protected Element getElement(Item item) {
			return item.getReferrer();
		}
	}

	private class AddAction extends BaseAction {

		private AddAction() {
			config.get("add").read(this);

			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			CreateReferencesWizard.showInDialog(list, session, getElement());
		}

		public void update() {
			setEnabled(getElement() != null);
		}
	}

	private class RemoveAction extends BaseAction implements
			ListSelectionListener, Compound {

		private RemoveAction() {
			config.get("remove").read(this);

			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			session.lookup(UndoManager.class).compound(this);
		}

		public void run() {
			int[] indices = list.getSelectedIndices();
			Item[] subReferences = new Item[indices.length];
			for (int r = 0; r < subReferences.length; r++) {
				subReferences[r] = items.get(indices[r]);
			}

			for (Item reference : subReferences) {
				reference.getReferrer().removeReference(
						reference.getReference());
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			setEnabled(list.getSelectedIndex() != -1);
		}
	}

	public static class Item {

		private Element referrer;

		private Reference<? extends Element> reference;

		public Item(Element referrer, Reference<? extends Element> reference) {
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