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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;

/**
 * The container for all elements of an organ.
 */
public class Organ {

	/**
	 * Registered listeners.
	 */
	private transient List<OrganListener> listeners;

	private Set<Element> elements = new HashSet<Element>();

	private static List<Class<? extends Element>> elementClasses;

	/**
	 * Get all known element classes.
	 * 
	 * @return element classes
	 */
	public static List<Class<? extends Element>> getElementClasses() {
		if (elementClasses == null) {
			List<Class<? extends Element>> classes = new ArrayList<Class<? extends Element>>();
			classes.add(Console.class);
			classes.add(Label.class);
			classes.add(Keyboard.class);
			classes.add(Coupler.class);
			classes.add(Stop.class);
			classes.add(Rank.class);
			classes.add(SwitchFilter.class);
			classes.add(ContinuousFilter.class);
			classes.add(Keyer.class);
			classes.add(Activator.class);
			classes.add(Regulator.class);
			classes.add(Combination.class);
			classes.add(Captor.class);
			classes.add(Sequence.class);
			classes.add(Incrementer.class);
			classes.add(Memory.class);

			elementClasses = classes;
		}

		return new ArrayList<Class<? extends Element>>(elementClasses);
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
		listeners.remove(listener);
	}

	public Set<Element> getElements() {
		return Collections.unmodifiableSet(elements);
	}

	public void addElement(Element element) {
		elements.add(element);
		element.setOrgan(this);

		fireAdded(new OrganEvent(this, element, true));
	}

	public void removeElement(Element element) {

		if (element.getOrgan() != this) {
			throw new IllegalArgumentException("unkown element " + element.getName() + "'");
		}

		for (Element referrer : getReferrer(element)) {
			referrer.unreference(element);
		}
		
		elements.remove(element);
		element.setOrgan(null);

		fireRemoved(new OrganEvent(this, element, true));
	}

	protected void fireChanged(OrganEvent event) {
		if (listeners != null) {
			for (OrganListener listener : listeners) {
				listener.changed(event);
			}
		}
	}

	protected void fireAdded(OrganEvent event) {
		if (listeners != null) {
			// listener might add itself when notified so work on copy 
			for (OrganListener listener : new ArrayList<OrganListener>(listeners)) {
				listener.added(event);
			}
		}
	}

	protected void fireRemoved(OrganEvent event) {
		if (listeners != null) {
			// listener might remove itself when notified so work on copy 
			for (OrganListener listener : new ArrayList<OrganListener>(listeners)) {
				listener.removed(event);
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
		Element clone = element.clone();
		
		addElement(clone);
		
		for (Element referrer : getReferrer(element)) {
			Reference reference = referrer.getReference(element);
			if (referrer.canReference(clone)) {
				referrer.addReference(reference.clone(clone));
			}
		}
	}
}