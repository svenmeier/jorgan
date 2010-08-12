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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.ElementListCellRenderer;
import jorgan.gui.construct.CreateElementWizard;
import jorgan.gui.construct.ElementNameComparator;
import jorgan.gui.construct.ElementTypeComparator;
import jorgan.gui.selection.ElementSelection;
import jorgan.gui.selection.SelectionListener;
import jorgan.gui.undo.Compound;
import jorgan.gui.undo.UndoManager;
import jorgan.problem.ElementProblems;
import jorgan.problem.Problem;
import jorgan.problem.ProblemListener;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import jorgan.swing.button.ButtonGroup;
import jorgan.util.ComparatorChain;
import jorgan.util.Generics;
import spin.Spin;
import swingx.dnd.ObjectTransferable;
import swingx.docking.Docked;
import bias.Configuration;

/**
 * Panel shows all elements.
 */
public class ElementsDockable extends OrganDockable {

	private static Configuration config = Configuration.getRoot().get(
			ElementsDockable.class);

	/**
	 * The edited organ.
	 */
	private OrganSession session;

	private ObjectTransferable transferable;

	/**
	 * The handler of selection changes.
	 */
	private SelectionHandler selectionHandler = new SelectionHandler();

	private JList list = new JList();

	private JToggleButton sortByNameButton = new JToggleButton();

	private JToggleButton sortByTypeButton = new JToggleButton();

	private List<Element> elements = new ArrayList<Element>();

	private AddAction addAction = new AddAction();

	/**
	 * Create a tree panel.
	 */
	public ElementsDockable() {

		config.read(this);

		list.setModel(new ElementsModel());
		list.setCellRenderer(new ElementListCellRenderer() {
			@Override
			protected OrganSession getOrgan() {
				return session;
			}
		});
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.addListSelectionListener(selectionHandler);
		list.setDragEnabled(true);
		list.setTransferHandler(new TransferHandler() {

			@Override
			public int getSourceActions(JComponent c) {
				return DnDConstants.ACTION_LINK | DnDConstants.ACTION_COPY
						| DnDConstants.ACTION_MOVE;
			}

			@Override
			protected Transferable createTransferable(JComponent c) {
				return new ObjectTransferable(list.getSelectedValues());
			}

			@Override
			public void exportToClipboard(JComponent comp, Clipboard clip,
					int action) throws IllegalStateException {
				int[] indices = list.getSelectedIndices();
				if (indices.length > 0) {
					Element[] subElements = new Element[indices.length];
					for (int e = 0; e < subElements.length; e++) {
						subElements[e] = elements.get(indices[e]);
					}

					for (Element element : subElements) {
						if (action == DnDConstants.ACTION_MOVE) {
							session.getOrgan().removeElement(element);
						}
					}

					transferable = new ObjectTransferable(subElements);
					clip.setContents(transferable, null);
				}
			}

			@Override
			public boolean importData(JComponent comp, Transferable t) {
				try {
					final Element[] subElements = (Element[]) ObjectTransferable
							.getObject(t);

					session.lookup(UndoManager.class).compound(new Compound() {
						public void run() {
							List<Element> added = new ArrayList<Element>();

							for (Element element : subElements) {
								Element clone = element.clone();

								session.getOrgan().addElement(clone);

								added.add(clone);
							}

							session.lookup(ElementSelection.class)
									.setSelectedElements(added);
						}
					});

					return true;
				} catch (Exception noImport) {
					return false;
				}
			}
		});
		setContent(new JScrollPane(list));

		ButtonGroup sortGroup = new ButtonGroup() {
			@Override
			protected void onSelected(AbstractButton button) {
				setSession(session);
			}
		};
		config.get("sortByType").read(sortByTypeButton);
		sortGroup.add(sortByTypeButton);

		config.get("sortByName").read(sortByNameButton);
		sortGroup.add(sortByNameButton);

		new DuplicateAction();
	}

	@Override
	public boolean forPlay() {
		return false;
	}

	@Override
	protected void addTools(Docked docked) {

		docked.addTool(addAction);
		docked.addTool(new RemoveAction());

		docked.addToolSeparator();

		docked.addTool(sortByTypeButton);
		docked.addTool(sortByNameButton);
	}

	/**
	 * Get the edited organ.
	 * 
	 * @return organ
	 */
	public OrganSession getOrgan() {
		return session;
	}

	/**
	 * Set the organ to be edited.
	 * 
	 * @param session
	 *            organ to be edited
	 */
	public void setSession(OrganSession session) {

		if (this.session != null) {
			this.session.getOrgan().removeOrganListener(
					(OrganListener) Spin.over(selectionHandler));
			this.session.lookup(ElementProblems.class).removeListener(
					(ProblemListener) Spin.over(selectionHandler));
			this.session.lookup(ElementSelection.class).removeListener(
					selectionHandler);

			elements = new ArrayList<Element>();
			list.setModel(new ElementsModel());
		}

		this.session = session;

		if (this.session != null) {
			this.session.getOrgan().addOrganListener(
					(OrganListener) Spin.over(selectionHandler));
			this.session.lookup(ElementProblems.class).addListener(
					(ProblemListener) Spin.over(selectionHandler));
			this.session.lookup(ElementSelection.class).addListener(
					selectionHandler);

			elements = new ArrayList<Element>(this.session.getOrgan()
					.getElements());
			list.setModel(new ElementsModel());

			selectionHandler.selectionChanged();
		}

		addAction.newSession();

		if (transferable != null) {
			transferable.clear();
			transferable = null;
		}
	}

	/**
	 * The handler of selections.
	 */
	private class SelectionHandler extends OrganAdapter implements
			SelectionListener, ListSelectionListener, ProblemListener {

		private boolean updatingSelection = false;

		public void selectionChanged() {
			if (!updatingSelection) {
				updatingSelection = true;

				list.clearSelection();

				List<Element> selectedElements = session.lookup(
						ElementSelection.class).getSelectedElements();
				for (int e = 0; e < selectedElements.size(); e++) {
					Element element = selectedElements.get(e);

					int index = elements.indexOf(element);
					if (index != -1) {
						list.addSelectionInterval(index, index);

						if (e == 0) {
							list.scrollRectToVisible(list.getCellBounds(index,
									index));
						}
					}
				}

				updatingSelection = false;
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			if (!e.getValueIsAdjusting() && !updatingSelection) {
				updatingSelection = true;

				Object[] values = list.getSelectedValues();

				if (values.length == 1) {
					session.lookup(ElementSelection.class).setSelectedElement(
							(Element) values[0]);
				} else if (!elements.isEmpty()) {
					session.lookup(ElementSelection.class).setSelectedElements(
							Generics.asList(values, Element.class));
				}

				updatingSelection = false;
			}
		}

		public void problemAdded(Problem problem) {
			((ElementsModel) list.getModel()).update(problem.getElement());
		}

		public void problemRemoved(Problem problem) {
			((ElementsModel) list.getModel()).update(problem.getElement());
		}

		public void propertyChanged(Element element, String name) {
			((ElementsModel) list.getModel()).update(element);
		}

		public void elementAdded(Element element) {
			elements.add(element);

			list.setModel(new ElementsModel());

			selectionChanged();
		}

		public void elementRemoved(Element element) {
			elements.remove(element);

			list.setModel(new ElementsModel());
		}
	}

	/**
	 * Note that <em>Spin</em> ensures that the organListener methods are called
	 * on the EDT, although a change in the organ might be triggered by a change
	 * on a MIDI thread.
	 */
	private class ElementsModel extends AbstractListModel {

		public ElementsModel() {
			sort();
		}

		public void update(Element element) {
			int index = elements.indexOf(element);

			fireContentsChanged(this, index, index);
		}

		public Object getElementAt(int index) {
			return elements.get(index);
		}

		public int getSize() {
			return elements.size();
		}

		private void sort() {
			if (sortByNameButton.isSelected()) {
				Collections.sort(elements, ComparatorChain.of(
						new ElementNameComparator(),
						new ElementTypeComparator()));
			} else {
				Collections.sort(elements, ComparatorChain.of(
						new ElementTypeComparator(),
						new ElementNameComparator()));
			}
		}
	}

	private class AddAction extends BaseAction {

		private AddAction() {
			config.get("add").read(this);

			setEnabled(false);
		}

		public void newSession() {
			setEnabled(session != null);
		}

		public void actionPerformed(ActionEvent ev) {
			if (session != null) {
				CreateElementWizard.showInDialog(list, session);
			}
		}
	}

	private class DuplicateAction extends BaseAction implements
			ListSelectionListener, Compound {

		private DuplicateAction() {
			config.get("duplicate").read(this);

			list.addListSelectionListener(this);

			register(list);
			valueChanged(null);
		}

		public void actionPerformed(ActionEvent ev) {
			if (session != null) {
				session.lookup(UndoManager.class).compound(this);
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			setEnabled(list.getSelectedIndex() != -1);
		}

		public void run() {
			List<Element> duplicated = new ArrayList<Element>();
			for (Element element : new ArrayList<Element>(session.lookup(
					ElementSelection.class).getSelectedElements())) {
				duplicated.add(session.getOrgan().duplicate(element));
			}

			session.lookup(ElementSelection.class).setSelectedElements(
					duplicated);
		}
	}

	private class RemoveAction extends BaseAction implements
			ListSelectionListener, Compound {

		private RemoveAction() {
			config.get("remove").read(this);

			list.addListSelectionListener(this);
			valueChanged(null);
		}

		public void actionPerformed(ActionEvent ev) {
			session.lookup(UndoManager.class).compound(this);
		}

		public void valueChanged(ListSelectionEvent e) {
			setEnabled(list.getSelectedIndex() != -1);
		}

		public void run() {
			for (Element element : new ArrayList<Element>(session.lookup(
					ElementSelection.class).getSelectedElements())) {
				session.getOrgan().removeElement(element);
			}
		}
	}
}