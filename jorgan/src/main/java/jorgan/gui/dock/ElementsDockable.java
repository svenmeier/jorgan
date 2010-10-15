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
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import jorgan.disposition.Element;
import jorgan.disposition.Group;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.ElementTreeCellRenderer;
import jorgan.gui.ElementTreeModel;
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
import jorgan.swing.tree.TreeUtils;
import jorgan.swing.tree.TreeUtils.Capture;
import jorgan.util.ComparatorChain;
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

	private JTree tree = new JTree();

	private ElementTreeModel model = new ElementTreeModel();

	private AddAction addAction = new AddAction();

	private CollapseAction collapseAction = new CollapseAction();

	private JToggleButton sortByNameButton = new JToggleButton();

	private JToggleButton sortByTypeButton = new JToggleButton();

	/**
	 * Create a tree panel.
	 */
	public ElementsDockable() {

		config.read(this);

		tree.setModel(model);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new ElementTreeCellRenderer() {
			@Override
			protected OrganSession getOrgan() {
				return session;
			}
		});
		tree.setExpandsSelectedPaths(true);
		tree.addTreeSelectionListener(selectionHandler);
		tree.setDragEnabled(true);
		tree.setTransferHandler(new TransferHandler() {

			@Override
			public int getSourceActions(JComponent c) {
				return DnDConstants.ACTION_LINK | DnDConstants.ACTION_COPY
						| DnDConstants.ACTION_MOVE;
			}

			@Override
			protected Transferable createTransferable(JComponent c) {
				return new ObjectTransferable(TreeUtils.getSelection(tree)
						.toArray());
			}

			@Override
			public void exportToClipboard(JComponent comp, Clipboard clip,
					int action) throws IllegalStateException {

				List<Element> selection = TreeUtils.getSelection(tree);
				if (!selection.isEmpty()) {
					for (Element element : selection) {
						if (action == DnDConstants.ACTION_MOVE) {
							session.getOrgan().removeElement(element);
						}
					}

					transferable = new ObjectTransferable(selection.toArray());
					clip.setContents(transferable, null);
				}
			}

			@Override
			public boolean importData(JComponent comp, Transferable t) {
				try {
					final Object[] subElements = (Object[]) ObjectTransferable
							.getObject(t);

					session.lookup(UndoManager.class).compound(new Compound() {
						public void run() {
							List<Element> added = new ArrayList<Element>();

							for (Object element : subElements) {
								Element clone = ((Element) element).clone();

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
		setContent(new JScrollPane(tree));

		ButtonGroup sortGroup = new ButtonGroup() {
			@Override
			protected void onSelected(AbstractButton button) {
				initTree();
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

		docked.addTool(collapseAction);

		docked.addToolSeparator();
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

			initTree();
		}

		this.session = session;

		TreeUtils.collapseAll(tree);

		if (this.session != null) {
			this.session.getOrgan().addOrganListener(
					(OrganListener) Spin.over(selectionHandler));
			this.session.lookup(ElementProblems.class).addListener(
					(ProblemListener) Spin.over(selectionHandler));
			this.session.lookup(ElementSelection.class).addListener(
					selectionHandler);

			initTree();
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
			SelectionListener, TreeSelectionListener, ProblemListener {

		private boolean updatingSelection = false;

		public void selectionChanged() {
			if (!updatingSelection) {
				updatingSelection = true;

				tree.clearSelection();

				boolean scrolled = false;
				for (Element selected : session.lookup(ElementSelection.class)
						.getSelectedElements()) {

					TreeUtils.addSelection(tree, selected);

					if (!scrolled) {
						scrolled = true;
						TreeUtils.scrollPathToVisible(tree, selected);
					}
				}

				updatingSelection = false;
			}
		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			if (!updatingSelection) {
				updatingSelection = true;

				List<Element> selection = TreeUtils.getSelection(tree);

				if (selection.size() == 1) {
					session.lookup(ElementSelection.class).setSelectedElement(
							selection.get(0));
				} else if (!selection.isEmpty()) {
					session.lookup(ElementSelection.class).setSelectedElements(
							selection);
				}

				updatingSelection = false;
			}
		}

		public void problemAdded(Problem problem) {
			model.fireNodeChanged(problem.getElement());
		}

		public void problemRemoved(Problem problem) {
			model.fireNodeChanged(problem.getElement());
		}

		public void propertyChanged(Element element, String name) {
			if ("name".equals(name)) {
				initTree();
			}
		}

		@Override
		public void indexedPropertyAdded(Element element, String name,
				Object value) {
			if (element instanceof Group) {
				initTree();

				TreeUtils.expand(tree, element);
			}
		}

		@Override
		public void indexedPropertyRemoved(Element element, String name,
				Object value) {
			if (element instanceof Group) {
				initTree();

				TreeUtils.expand(tree, element);
			}
		}

		public void elementAdded(Element element) {
			initTree();

			selectionChanged();
		}

		public void elementRemoved(Element element) {
			initTree();
		}
	}

	private void initTree() {
		if (this.session == null) {
			model.clearElements();
		} else {
			Capture<Element> capture = TreeUtils.expansion(tree);

			Comparator<Element> comparator;
			if (sortByNameButton.isSelected()) {
				comparator = ComparatorChain.of(new ElementNameComparator(),
						new ElementTypeComparator());
			} else {
				comparator = ComparatorChain.of(new ElementTypeComparator(),
						new ElementNameComparator());
			}

			model.setElements(session.getOrgan(), session.getOrgan()
					.getElements(), comparator);

			selectionHandler.selectionChanged();

			capture.restore();
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
				CreateElementWizard.showInDialog(tree, session);
			}
		}
	}

	private class CollapseAction extends BaseAction {

		private CollapseAction() {
			config.get("collapse").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			TreeUtils.collapseAll(tree);
		}
	}

	private class DuplicateAction extends BaseAction implements
			TreeSelectionListener, Compound {

		private DuplicateAction() {
			config.get("duplicate").read(this);

			tree.addTreeSelectionListener(this);

			register(tree);
			valueChanged(null);
		}

		public void actionPerformed(ActionEvent ev) {
			if (session != null) {
				session.lookup(UndoManager.class).compound(this);
			}
		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			setEnabled(tree.getSelectionCount() > 0);
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
			TreeSelectionListener, Compound {

		private RemoveAction() {
			config.get("remove").read(this);

			tree.addTreeSelectionListener(this);
			valueChanged(null);
		}

		public void actionPerformed(ActionEvent ev) {
			session.lookup(UndoManager.class).compound(this);
		}

		@Override
		public void valueChanged(TreeSelectionEvent e) {
			setEnabled(tree.getSelectionCount() > 0);
		}

		public void run() {
			for (Element element : new ArrayList<Element>(session.lookup(
					ElementSelection.class).getSelectedElements())) {
				session.getOrgan().removeElement(element);
			}
		}
	}
}