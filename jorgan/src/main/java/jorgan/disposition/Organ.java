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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import jorgan.disposition.event.AbstractChange;
import jorgan.disposition.event.Change;
import jorgan.disposition.event.OrganListener;
import jorgan.disposition.event.OrganObserver;
import jorgan.gui.construct.ElementNameComparator;
import jorgan.util.ComparatorChain;
import jorgan.util.IdentityComparator;

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

	private String version = "";

	private List<Element> elements = new ArrayList<Element>();

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		if (version == null) {
			throw new IllegalArgumentException("version must not be null");
		}

		this.version = version;
	}

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

		if (!listeners.remove(listener)) {
			throw new IllegalArgumentException("unknown listener");
		}
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
		return new HashSet<Element>(elements);
	}

	public Iterable<Element> elements() {
		return elements;
	}

	public boolean containsElement(Element element) {
		return elements.contains(element);
	}

	private Long createId(Element element) {
		Long id = element.getId();

		long maxId = 0;
		for (Element other : elements) {
			if (other != element) {
				Long otherId = other.getId();
				if (otherId != null) {
					maxId = Math.max(maxId, otherId);

					if (id != null && id.equals(otherId)) {
						id = null;
					}
				}
			}
		}

		if (id != null) {
			return id;
		} else {
			return maxId + 1;
		}
	}

	/**
	 * This method is not part of this class' public API!
	 */
	public void bind(Element element) {
		element.id = createId(element);
		element.organ = this;
	}

	public void addElements(Collection<Element> elements) {
		for (Element element : elements) {
			if (this.elements.contains(element)) {
				throw new IllegalArgumentException("already added");
			}
		}

		// add elements in one go so they keep their references
		this.elements.addAll(elements);

		for (Element element : elements) {
			addElementImpl(element);
		}
	}

	public void addElement(final Element element) {
		if (this.elements.contains(element)) {
			throw new IllegalArgumentException("already added");
		}
		elements.add(element);

		addElementImpl(element);
	}

	private void addElementImpl(final Element element) {
		element.id = createId(element);

		element.setOrgan(this);

		fireChange(new AbstractChange() {
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

		fireChange(new AbstractChange() {
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
		if (observers != null) {
			// observer might remove itself when notified so work on copy
			for (OrganObserver observer : new ArrayList<OrganObserver>(
					observers)) {
				observer.onChange(change);
			}
		}

		if (listeners != null) {
			// listener might remove itself when notified so work on copy
			for (OrganListener listener : new ArrayList<OrganListener>(
					listeners)) {
				change.notify(listener);
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
	public Set<Element> getReferenceToCandidates(Element element) {

		Set<Element> candidates = new TreeSet<Element>(ComparatorChain.of(
				new ElementNameComparator(), new IdentityComparator<Element>()));

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
	public Set<Element> getReferencedFromCandidates(Element element) {

		Set<Element> candidates = new TreeSet<Element>(ComparatorChain.of(
				new ElementNameComparator(), new IdentityComparator<Element>()));

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
	 * @return elements sorted by name
	 */
	@SuppressWarnings("unchecked")
	public <E extends Element> Set<E> getElements(Class<E> clazz) {
		Set<E> set = new TreeSet<E>(ComparatorChain.of(
				new ElementNameComparator(), new IdentityComparator<Element>()));

		for (Element element : this.elements) {
			if (clazz.isInstance(element)) {
				set.add((E) element);
			}
		}

		return set;
	}

	/**
	 * Get element of the given class.
	 * 
	 * @param clazz
	 *            class to give element for
	 * @return element
	 */
	@SuppressWarnings("unchecked")
	public <E extends Element> E getElement(Class<E> clazz) {
		for (Element candidate : this.elements) {
			if (clazz.isInstance(candidate)) {
				return (E) candidate;
			}
		}

		return null;
	}

	public Element duplicate(Element element) {
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

		return clone;
	}

	public Element getElement(Long id) throws IllegalArgumentException {
		for (Element element : elements) {
			if (element.getId().equals(id)) {
				return element;
			}
		}

		throw new IllegalArgumentException("unkown id '" + id + "'");
	}
}