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
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.ElementListCellRenderer;
import jorgan.gui.OrganAware;
import jorgan.gui.OrganSession;
import jorgan.gui.event.ElementSelectionEvent;
import jorgan.gui.event.ElementSelectionListener;
import jorgan.play.event.PlayEvent;
import jorgan.play.event.PlayListener;
import jorgan.swing.BaseAction;
import jorgan.util.Generics;
import swingx.dnd.ObjectTransferable;
import swingx.docking.DockedPanel;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * Panel shows all elements.
 */
public class ElementsPanel extends DockedPanel implements OrganAware {

	private static Configuration config = Configuration.getRoot().get(
			ElementsPanel.class);

	/**
	 * The edited organ.
	 */
	private OrganSession session;

	/**
	 * The handler of selection changes.
	 */
	private SelectionHandler selectionHandler = new SelectionHandler();

	private AddAction addAction = new AddAction();

	private RemoveAction removeAction = new RemoveAction();

	private JList list = new JList();

	private JToggleButton sortByNameButton = new JToggleButton();

	private JToggleButton sortByTypeButton = new JToggleButton();

	private ElementsModel elementsModel = new ElementsModel();

	private List<Element> elements = new ArrayList<Element>();

	/**
	 * Create a tree panel.
	 */
	public ElementsPanel() {

		addTool(addAction);
		addTool(removeAction);

		addToolSeparator();

		ButtonGroup sortGroup = new ButtonGroup();
		config.get("sortByName").read(sortByNameButton);
		sortByNameButton.getModel().setGroup(sortGroup);
		sortByNameButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				setOrgan(session);
			}
		});
		addTool(sortByNameButton);

		config.get("sortByType").read(sortByTypeButton);
		sortByTypeButton.getModel().setGroup(sortGroup);
		sortByTypeButton.setSelected(true);
		sortByTypeButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				setOrgan(session);
			}
		});
		addTool(sortByTypeButton);

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

					clip.setContents(new ObjectTransferable(subElements), null);
				}
			}

			@Override
			public boolean importData(JComponent comp, Transferable t) {
				try {
					Element[] subElements = (Element[]) ObjectTransferable
							.getObject(t);
					for (Element element : subElements) {
						session.getOrgan().addElement(element.clone());
					}

					return true;
				} catch (Exception noImport) {
					return false;
				}
			}
		});
		list.addListSelectionListener(removeAction);

		setScrollableBody(list, true, false);
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
	public void setOrgan(OrganSession session) {

		if (this.session != null) {
			this.session.removeOrganListener(elementsModel);
			this.session.removePlayerListener(elementsModel);
			this.session.removeSelectionListener(selectionHandler);

			elements = new ArrayList<Element>();
			elementsModel.update();
		}

		this.session = session;

		if (this.session != null) {
			this.session.addOrganListener(elementsModel);
			this.session.addPlayerListener(elementsModel);
			this.session.getSelectionModel().addSelectionListener(
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
	}

	/**
	 * The handler of selections.
	 */
	private class SelectionHandler implements ElementSelectionListener,
			ListSelectionListener {

		private boolean updatingSelection = false;

		public void selectionChanged(ElementSelectionEvent ev) {
			if (!updatingSelection) {
				updatingSelection = true;

				list.clearSelection();

				java.util.List selectedElements = session.getSelectionModel()
						.getSelectedElements();
				for (int e = 0; e < selectedElements.size(); e++) {
					Element element = (Element) selectedElements.get(e);

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
					session.getSelectionModel().setSelectedElement(
							(Element) values[0]);
				} else {
					session.getSelectionModel().setSelectedElements(
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
			PlayListener, OrganListener {

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

		public void outputProduced() {
		}

		public void inputAccepted() {
		}

		public void playerAdded(PlayEvent ev) {
		}

		public void playerRemoved(PlayEvent ev) {
		}

		public void problemAdded(PlayEvent ev) {
			updateProblem(ev);
		}

		public void problemRemoved(PlayEvent ev) {
			updateProblem(ev);
		}

		public void opened() {
		}

		public void closed() {
		}

		private void updateProblem(PlayEvent ev) {

			Element element = ev.getElement();
			int index = elements.indexOf(element);

			fireContentsChanged(this, index, index);
		}

		public void changed(final OrganEvent event) {
			if (event.self()) {
				Element element = event.getElement();
				int index = elements.indexOf(element);

				fireContentsChanged(this, index, index);
			}
		}

		public void added(OrganEvent event) {
			if (event.self()) {
				elements.add(event.getElement());

				int index = elements.size() - 1;
				fireIntervalAdded(this, index, index);

				Collections.sort(elements, new ElementComparator(
						sortByNameButton.isSelected()));
				fireContentsChanged(this, 0, index);

				selectionHandler.selectionChanged(null);
			}
		}

		public void removed(OrganEvent event) {
			if (event.self()) {
				int index = elements.indexOf(event.getElement());

				elements.remove(event.getElement());

				fireIntervalRemoved(this, index, index);
			}
		}
	}

	private class AddAction extends BaseAction {

		private AddAction() {
			config.get("add").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			if (session != null) {
				CreateElementWizard.showInDialog(ElementsPanel.this, session
						.getOrgan());
			}
		}
	}

	private class RemoveAction extends BaseAction implements
			ListSelectionListener {

		private RemoveAction() {
			config.get("remove").read(this);
		}

		public void actionPerformed(ActionEvent ev) {
			if (config.get("remove/confirm").read(
					new MessageBox(MessageBox.OPTIONS_OK_CANCEL)).show(
					ElementsPanel.this) != MessageBox.OPTION_OK) {
				return;
			}

			List selectedElements = session.getSelectionModel()
					.getSelectedElements();

			for (int e = selectedElements.size() - 1; e >= 0; e--) {
				session.getOrgan().removeElement(
						(Element) selectedElements.get(e));
			}
		}

		public void valueChanged(ListSelectionEvent e) {
			setEnabled(list.getSelectedIndex() != -1);
		}
	}
}