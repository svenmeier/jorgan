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
import jorgan.disposition.Message;
import jorgan.disposition.Reference;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.ElementListCellRenderer;
import jorgan.gui.construct.CreateElementWizard;
import jorgan.gui.construct.ElementComparator;
import jorgan.session.OrganSession;
import jorgan.session.problem.Problem;
import jorgan.session.problem.ProblemListener;
import jorgan.session.selection.SelectionEvent;
import jorgan.session.selection.SelectionListener;
import jorgan.session.undo.Compound;
import jorgan.swing.BaseAction;
import jorgan.swing.button.ButtonGroup;
import jorgan.util.Generics;
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

	private ElementsModel elementsModel = new ElementsModel();

	private List<Element> elements = new ArrayList<Element>();

	/**
	 * Create a tree panel.
	 */
	public ElementsDockable() {

		config.read(this);

		list.setModel(elementsModel);
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
					
					session.getUndoManager().compound(new Compound() {
						public void run() {
							List<Element> added = new ArrayList<Element>();
							
							for (Element element : subElements) {
								Element clone = element.clone();
								
								session.getOrgan().addElement(clone);
								
								added.add(clone);
							}

							session.getSelection().setSelectedElements(added);
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
	public void docked(Docked docked) {
		super.docked(docked);

		docked.addTool(new AddAction());
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
			this.session.removeOrganListener(elementsModel);
			this.session.removeProblemListener(elementsModel);
			this.session.removeSelectionListener(selectionHandler);

			elements = new ArrayList<Element>();
			elementsModel.update();
		}

		this.session = session;

		if (this.session != null) {
			this.session.addOrganListener(elementsModel);
			this.session.addProblemListener(elementsModel);
			this.session.getSelection().addSelectionListener(
					selectionHandler);

			elements = new ArrayList<Element>(this.session.getOrgan()
					.getElements());
			if (sortByNameButton.isSelected()) {
				Collections.sort(elements, new ElementComparator(true));
			} else if (sortByTypeButton.isSelected()) {
				Collections.sort(elements, new ElementComparator(false));
			}
			elementsModel.update();
		}
		
		if (transferable != null) {
			transferable.clear();
			transferable = null;
		}		
	}

	/**
	 * The handler of selections.
	 */
	private class SelectionHandler implements SelectionListener,
			ListSelectionListener {

		private boolean updatingSelection = false;

		public void selectionChanged(SelectionEvent ev) {
			if (!updatingSelection) {
				updatingSelection = true;

				list.clearSelection();

				List<Element> selectedElements = session.getSelection()
						.getSelectedElements();
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
					session.getSelection().setSelectedElement(
							(Element) values[0]);
				} else {
					session.getSelection().setSelectedElements(
							Generics.asList(values, Element.class));
				}

				updatingSelection = false;
			}
		}
	}

	/**
	 * Note that <em>Spin</em> ensures that the organListener methods are
	 * called on the EDT, although a change in the organ might be triggered by a
	 * change on a MIDI thread.
	 */
	private class ElementsModel extends AbstractListModel implements
			ProblemListener, OrganListener {

		private int size = -1;

		private void update() {
			if (size != -1) {
				if (elements.size() > size) {
					fireIntervalAdded(this, size, elements.size() - 1);
				} else if (elements.size() < size) {
					fireIntervalRemoved(this, elements.size(), size - 1);
				}
				size = elements.size();
			}
		}

		public Object getElementAt(int index) {
			return elements.get(index);
		}

		public int getSize() {
			size = elements.size();

			return size;
		}

		public void problemAdded(Problem problem) {
			updateProblem(problem);
		}

		public void problemRemoved(Problem problem) {
			updateProblem(problem);
		}

		private void updateProblem(Problem problem) {

			Element element = problem.getElement();
			int index = elements.indexOf(element);

			fireContentsChanged(this, index, index);
		}

		public void propertyChanged(Element element, String name) {
			if ("name".equals(name) || "description".equals(name)) {
				int index = elements.indexOf(element);

				fireContentsChanged(this, index, index);
			}
		}

		public void elementAdded(Element element) {
			elements.add(element);

			int index = elements.size() - 1;
			fireIntervalAdded(this, index, index);

			Collections.sort(elements, new ElementComparator(sortByNameButton
					.isSelected()));
			fireContentsChanged(this, 0, index);

			selectionHandler.selectionChanged(null);
		}

		public void elementRemoved(Element element) {
			int index = elements.indexOf(element);

			elements.remove(element);

			fireIntervalRemoved(this, index, index);
		}

		public void messageAdded(Element element, Message reference) {
		}

		public void messageChanged(Element element, Message reference) {
		}

		public void messageRemoved(Element element, Message reference) {
		}

		public void referenceAdded(Element element, Reference<?> reference) {
		}

		public void referenceChanged(Element element, Reference<?> reference) {
		}

		public void referenceRemoved(Element element, Reference<?> reference) {
		}
	}

	private class AddAction extends BaseAction {

		private AddAction() {
			config.get("add").read(this);
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
		}

		public void actionPerformed(ActionEvent ev) {
			if (session != null) {
				session.getUndoManager().compound(this);
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			setEnabled(list.getSelectedIndex() != -1);
		}
		
		public void run() {
			List<Element> duplicated = new ArrayList<Element>();
			for (Element element : new ArrayList<Element>(session
					.getSelection().getSelectedElements())) {
				duplicated.add(session.getOrgan().duplicate(element));
			}
			
			session.getSelection().setSelectedElements(duplicated);
		}
	}

	private class RemoveAction extends BaseAction implements
			ListSelectionListener, Compound {

		private RemoveAction() {
			config.get("remove").read(this);

			list.addListSelectionListener(this);
			setEnabled(false);
		}

		public void actionPerformed(ActionEvent ev) {
			session.getUndoManager().compound(this);
		}

		public void valueChanged(ListSelectionEvent e) {
			setEnabled(list.getSelectedIndex() != -1);
		}
		
		public void run() {
			for (Element element : new ArrayList<Element>(session
					.getSelection().getSelectedElements())) {
				session.getOrgan().removeElement(element);
			}
		}
	}
}