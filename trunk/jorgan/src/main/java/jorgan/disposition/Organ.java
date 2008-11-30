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
package jorgan.disposition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jorgan.disposition.event.Change;
import jorgan.disposition.event.OrganListener;
import jorgan.disposition.event.OrganObserver;
import jorgan.disposition.event.UndoableChange;

/**
 * The container for all elements of an organ.
 */
public class Organ {

	/**
	 * Registered listeners.
	 */
	private transient List<OrganListener> listeners;

	/**
	 * Registered observers.
	 */
	private transient List<OrganObserver> observers;
	
	private Set<Element> elements = new HashSet<Element>();

	/**
	 * Add a listener to this organ.
	 * 
	 * @param listener
	 *            listener to add
	 */
	public void addOrganListener(OrganListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<OrganListener>();
		}
		listeners.add(listener);
	}

	/**
	 * Remove the given listener.
	 * 
	 * @param listener
	 *            listener to remove
	 * @see #addOrganListener(OrganListener)
	 */
	public void removeOrganListener(OrganListener listener) {
		if (listeners == null) {
			listeners = new ArrayList<OrganListener>();
		}
		listeners.remove(listener);
	}

	/**
	 * Add a observer to this organ.
	 * 
	 * @param observer
	 *            observer to add
	 */
	public void addOrganObserver(OrganObserver observer) {
		if (observers == null) {
			observers = new ArrayList<OrganObserver>();
		}
		observers.add(observer);
	}

	/**
	 * Remove the given observer.
	 * 
	 * @param observer
	 *            observer to remove
	 * @see #addOrganObserver(OrganListener)
	 */
	public void removeOrganObserver(OrganObserver observer) {
		if (observers == null) {
			observers = new ArrayList<OrganObserver>();
		}
		observers.remove(observer);
	}

	public Set<Element> getElements() {
		return Collections.unmodifiableSet(elements);
	}

	public boolean containsElement(Element element) {
		return elements.contains(element);
	}

	public void addElements(Collection<Element> elements) {
		this.elements.addAll(elements);

		for (Element element : elements) {
			addElement(element);
		}
	}

	public void addElement(final Element element) {
		
		elements.add(element);
		element.setOrgan(this);

		fireChange(new UndoableChange() {
			public void notify(OrganListener listener) {
				listener.elementAdded(element);
			}
			public void undo() {
				removeElement(element);
			}
			public void redo() {
				addElement(element);
			}
		});
	}
	
	public void removeElement(final Element element) {

		if (element.getOrgan() != this) {
			throw new IllegalArgumentException("unkown element "
					+ element.getName() + "'");
		}

		for (Element referrer : getReferrer(element)) {
			referrer.unreference(element);
		}

		elements.remove(element);
		element.setOrgan(null);

		fireChange(new UndoableChange() {
			public void notify(OrganListener listener) {
				listener.elementRemoved(element);
			}
			public void undo() {
				addElement(element);
			}
			public void redo() {
				removeElement(element);
			}
		});
	}

	protected void fireChange(Change change) {
		if (listeners != null) {
			// listener might remove itself when notified so work on copy
			for (OrganListener listener : new ArrayList<OrganListener>(
					listeners)) {
				change.notify(listener);
			}
		}
		
		if (observers != null) {
			// observer might remove itself when notified so work on copy
			for (OrganObserver observer : new ArrayList<OrganObserver>(
					observers)) {
				observer.onChange(change);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <E> Set<E> getReferrer(Element element, Class<E> clazz) {
		Set<E> set = new HashSet<E>();

		for (Element candidate : elements) {
			if (clazz.isAssignableFrom(candidate.getClass())
					&& candidate.references(element)) {
				set.add((E) candidate);
			}
		}
		return set;
	}

	public Set<Element> getReferrer(Element element) {
		return getReferrer(element, Element.class);
	}

	/**
	 * Get candidates to reference from the given element.
	 * 
	 * @param element
	 *            element to get candidates for
	 * @return candidates
	 */
	public List<Element> getReferenceToCandidates(Element element) {

		List<Element> candidates = new ArrayList<Element>();

		for (Element candidate : elements) {
			if (element.canReference(candidate)) {
				candidates.add(candidate);
			}
		}

		return candidates;
	}

	/**
	 * Get candidates which can reference the given elements.
	 * 
	 * @param element
	 *            element to find candidates for
	 * @return candidates, never null
	 */
	public List<Element> getReferencedFromCandidates(Element element) {

		List<Element> candidates = new ArrayList<Element>();

		for (Element candidate : elements) {
			if (candidate.canReference(element)) {
				candidates.add(candidate);
			}
		}

		return candidates;
	}

	/**
	 * Get elements of the given class.
	 * 
	 * @param clazz
	 *            class to give elements for
	 * @return elements
	 */
	@SuppressWarnings("unchecked")
	public <E> Set<E> getElements(Class<E> clazz) {
		Set<E> set = new HashSet<E>();

		for (Element element : this.elements) {
			if (clazz.isInstance(element)) {
				set.add((E) element);
			}
		}

		return set;
	}

	public void duplicate(Element element) {
		if (element.getOrgan() != this) {
			throw new IllegalArgumentException("unkown element "
					+ element.getName() + "'");
		}

		Element clone = element.clone();

		addElement(clone);

		for (Element referrer : getReferrer(element)) {
			for (Reference<? extends Element> reference : referrer
					.getReferences(element)) {
				if (referrer.canReference(clone)) {
					referrer.addReference(reference.clone(clone));
				}
			}
		}
	}
}