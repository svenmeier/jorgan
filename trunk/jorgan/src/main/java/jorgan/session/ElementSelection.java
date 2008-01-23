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
package jorgan.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jorgan.disposition.Element;
import jorgan.session.event.ElementSelectionEvent;
import jorgan.session.event.ElementSelectionListener;

public class ElementSelection {

	private List<ElementSelectionListener> listeners = new ArrayList<ElementSelectionListener>();

	private ArrayList<Element> history = new ArrayList<Element>();

	private int historyIndex;

	/**
	 * The currently selected elements.
	 */
	private List<Element> selectedElements = new ArrayList<Element>();

	private String property;

	public ElementSelection() {
		clear();
	}

	public void clear() {
		history.clear();
		historyIndex = 0;

		selectedElements.clear();

		fireStateChanged();
	}

	public void clear(Element element) {
		selectedElements.remove(element);

		int index = history.indexOf(element);
		if (index != -1) {
			history.remove(element);
			if (historyIndex > index) {
				historyIndex--;
			}
		}

		fireStateChanged();
	}

	public String getSelectedProperty() {
		return property;
	}

	public void setSelectedProperty(String property) {
		if (this.property == null && property != null || this.property != null
				&& !this.property.equals(property)) {

			this.property = property;

			fireStateChanged();
		}
	}

	/**
	 * Is an element selected.
	 */
	public boolean isElementSelected() {
		return selectedElements.size() > 0;
	}

	/**
	 * Get the count of selected elements.
	 */
	public int getSelectionCount() {
		return selectedElements.size();
	}

	public boolean isSelected(Element element) {
		return selectedElements.contains(element);
	}

	/**
	 * Get the currently selected elements.
	 * 
	 * @return the currently selected elements
	 */
	public List<Element> getSelectedElements() {
		return Collections.unmodifiableList(selectedElements);
	}

	/**
	 * Get the currently selected element.
	 * 
	 * @return the currently selected element
	 */
	public Element getSelectedElement() {
		if (selectedElements.size() == 1) {
			return selectedElements.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Set the element to be selected.
	 * 
	 * @param element
	 *            element to select
	 */
	public void setSelectedElement(Element element) {

		setSelectedElement(element, null);
	}

	/**
	 * Set the element and property to be selected.
	 * 
	 * @param element
	 *            element to select
	 * @param property
	 *            property to select
	 */
	public void setSelectedElement(Element element, String property) {

		this.property = property;

		updateHistory(element);

		selectedElements.clear();
		if (element != null) {
			selectedElements.add(element);
		}
		fireStateChanged();
	}

	private void updateHistory(Element element) {
		while (historyIndex < history.size() - 1) {
			history.remove(history.size() - 1);
		}

		if (element == null) {
			if (historyIndex != history.size()) {
				historyIndex++;
			}
		} else {
			if (historyIndex == history.size()) {
				if (historyIndex > 0
						&& history.get(historyIndex - 1) == element) {
					historyIndex--;
				} else {
					history.add(historyIndex, element);
				}
			} else {
				if (history.get(historyIndex) != element) {
					historyIndex++;
					history.add(historyIndex, element);
				}
			}
		}
	}

	public boolean canBack() {
		return historyIndex > 0;
	}

	public void back() {
		if (historyIndex > 0) {
			historyIndex--;

			property = null;

			Element element = history.get(historyIndex);
			selectedElements.clear();
			selectedElements.add(element);

			fireStateChanged();
		}
	}

	public boolean canForward() {
		return historyIndex < history.size() - 1;
	}

	public void forward() {
		if (historyIndex < history.size() - 1) {
			historyIndex++;

			property = null;

			Element element = history.get(historyIndex);
			selectedElements.clear();
			selectedElements.add(element);

			fireStateChanged();
		}
	}

	/**
	 * Set the elements to be selected.
	 * 
	 * @param elements
	 *            elements to select
	 */
	public void setSelectedElements(List<Element> elements) {
		property = null;

		selectedElements.clear();
		selectedElements.addAll(elements);

		fireStateChanged();
	}

	/**
	 * Remove an element from being selected.
	 * 
	 * @param element
	 *            element to remove
	 */
	public void removeSelectedElement(Element element) {

		if (selectedElements.contains(element)) {
			property = null;

			selectedElements.remove(element);

			if (selectedElements.isEmpty()) {
				updateHistory(null);
			}

			fireStateChanged();
		}
	}

	/**
	 * Add an element to be selected.
	 * 
	 * @param element
	 *            element to add
	 */
	public void addSelectedElement(Element element) {
		if (element == null) {
			throw new IllegalArgumentException(
					"cannot add null element to selected elements");
		}

		property = null;

		updateHistory(element);

		if (!selectedElements.contains(element)) {
			selectedElements.add(element);
		}
		fireStateChanged();
	}

	/**
	 * Add a listener to selections.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addSelectionListener(ElementSelectionListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a listener to selections.
	 * 
	 * @param listener
	 *            listener to remove
	 */
	public void removeSelectionListener(ElementSelectionListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Fire a change to all registered change listeners.
	 */
	protected void fireStateChanged() {
		for (int l = 0; l < listeners.size(); l++) {
			ElementSelectionListener listener = listeners.get(l);
			listener.selectionChanged(new ElementSelectionEvent(this));
		}
	}
}
