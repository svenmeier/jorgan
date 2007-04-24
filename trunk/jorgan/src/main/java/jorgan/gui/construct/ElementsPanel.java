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

import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.gui.ElementListCellRenderer;
import jorgan.gui.OrganSession;
import jorgan.gui.event.ElementSelectionEvent;
import jorgan.gui.event.ElementSelectionListener;
import jorgan.play.event.PlayEvent;
import jorgan.play.event.PlayListener;
import jorgan.util.Generics;
import jorgan.util.I18N;
import swingx.docking.DockedPanel;
import swingx.list.AbstractDnDListModel;
import swingx.list.DnDList;

/**
 * Panel shows all elements.
 */
public class ElementsPanel extends DockedPanel {

	private static final I18N i18n = I18N.get(ElementsPanel.class);

	private static final Icon sortNameIcon = new ImageIcon(ElementsPanel.class
			.getResource("/jorgan/gui/img/sortName.gif"));

	private static final Icon sortTypeIcon = new ImageIcon(ElementsPanel.class
			.getResource("/jorgan/gui/img/sortType.gif"));

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

	private DnDList list = new DnDList();

	private JToggleButton sortNameButton = new JToggleButton(sortNameIcon);

	private JToggleButton sortTypeButton = new JToggleButton(sortTypeIcon);

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
		sortNameButton.getModel().setGroup(sortGroup);
		sortNameButton.setToolTipText(i18n
				.getString("sortNameButton/toolTipText"));
		sortNameButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				setOrgan(session);
			}
		});
		addTool(sortNameButton);

		sortTypeButton.getModel().setGroup(sortGroup);
		sortTypeButton.setSelected(true);
		sortTypeButton.setToolTipText(i18n
				.getString("sortTypeButton/toolTipText"));
		sortTypeButton.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				setOrgan(session);
			}
		});
		addTool(sortTypeButton);

		list.setModel(elementsModel);
		list.setCellRenderer(new ElementListCellRenderer() {
			protected OrganSession getOrgan() {
				return session;
			}
		});
		list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		list.addListSelectionListener(selectionHandler);

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
			if (sortNameButton.isSelected()) {
				Collections.sort(elements, new ElementComparator(true));
			} else if (sortTypeButton.isSelected()) {
				Collections.sort(elements, new ElementComparator(false));
			}
			elementsModel.update();
		}

		removeAction.update();
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

			removeAction.update();
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

			removeAction.update();
		}
	}

	/**
	 * Note that <em>Spin</em> ensures that the organListener methods are
	 * called on the EDT, although a change in the organ might be triggered by a
	 * change on a MIDI thread.
	 */
	private class ElementsModel extends AbstractDnDListModel implements
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

		public int indexOf(Object element) {
			return elements.indexOf(element);
		}

		public int getSize() {
			size = elements.size();

			return size;
		}

		public int getDropActions(Transferable transferable, int index) {
			return 0;
		}

		protected void insertElementAt(Object element, int index) {
		}

		protected void removeElement(Object element) {
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

		public void elementChanged(final OrganEvent event) {
			Element element = event.getElement();
			int index = elements.indexOf(element);

			fireContentsChanged(this, index, index);
		}

		public void elementAdded(OrganEvent event) {
			elements.add(event.getElement());

			int index = elements.size() - 1;
			fireIntervalAdded(this, index, index);

			Collections.sort(elements, new ElementComparator(sortNameButton
					.isSelected()));
			fireContentsChanged(this, 0, index);

			selectionHandler.selectionChanged(null);
		}

		public void elementRemoved(OrganEvent event) {
			int index = elements.indexOf(event.getElement());

			elements.remove(event.getElement());

			fireIntervalRemoved(this, index, index);
		}

		public void referenceAdded(OrganEvent event) {
		}

		public void referenceChanged(OrganEvent event) {
		}

		public void referenceRemoved(OrganEvent event) {
		}
	}

	private class AddAction extends AbstractAction {

		private AddAction() {
			putValue(Action.NAME, i18n.getString("addAction/name"));
			putValue(Action.SHORT_DESCRIPTION, i18n
					.getString("addAction/shortDescription"));
			putValue(Action.SMALL_ICON, new ImageIcon(ElementsPanel.class
					.getResource("/jorgan/gui/img/add.gif")));
		}

		public void actionPerformed(ActionEvent ev) {
			if (session != null) {
				Element prototype = null;
				if (session.getSelectionModel().getSelectionCount() == 1) {
					prototype = session.getSelectionModel()
							.getSelectedElement();
				}
				CreateElementWizard.showInDialog(ElementsPanel.this, session
						.getOrgan(), prototype);
			}
		}
	}

	private class RemoveAction extends AbstractAction {

		private RemoveAction() {
			putValue(Action.NAME, i18n.getString("removeAction/name"));
			putValue(Action.SHORT_DESCRIPTION, i18n
					.getString("removeAction/shortDescription"));
			putValue(Action.SMALL_ICON, new ImageIcon(ElementsPanel.class
					.getResource("/jorgan/gui/img/remove.gif")));
		}

		public void actionPerformed(ActionEvent ev) {
			List selectedElements = session.getSelectionModel()
					.getSelectedElements();

			for (int e = selectedElements.size() - 1; e >= 0; e--) {
				session.getOrgan().removeElement(
						(Element) selectedElements.get(e));
			}
		}

		/**
		 * Update the enabled state.
		 */
		public void update() {
			setEnabled(session != null
					&& session.getSelectionModel().isElementSelected());
		}
	}
}