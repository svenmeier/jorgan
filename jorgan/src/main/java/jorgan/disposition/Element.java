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

/**
 * Abstract base class of all elements of an organ.
 */
public abstract class Element implements Cloneable {

	/**
	 * The organ this element belongs to.
	 */
	private transient Organ organ;

	/**
	 * The name of this element.
	 */
	private String name = "";

	/**
	 * The description of this element.
	 */
	private String description = "";

	private String style;

	/**
	 * The references to other elements.
	 */
	protected List<Reference> references = new ArrayList<Reference>();

	/**
	 * Test if this element can reference the given element. <br>
	 * An element can be referenced if it is not identical to this, is currently
	 * not referenced and its class can be referenced.
	 * 
	 * @param element
	 *            the element to test
	 * @return <code>true</code> if element can be referenced
	 * @see #canReference(Element)
	 */
	public boolean canReference(Element element) {
		return element != this
				&& (canReferenceDuplicates() || getReference(element) == null)
				&& canReference(element.getClass());
	}

	/**
	 * Test if this element can reference elements of the given class.
	 * 
	 * @param clazz
	 *            element class to test
	 * @return <code>true</code> if elements of the given class can be
	 *         referenced
	 */
	protected boolean canReference(Class<? extends Element> clazz) {
		return false;
	}

	protected boolean canReferenceDuplicates() {
		return false;
	}

	public Organ getOrgan() {
		return organ;
	}

	protected void setOrgan(Organ organ) {
		if (this.organ != organ) {
			for (Reference reference : new ArrayList<Reference>(references)) {
				removeReference(reference);
			}
		}
		this.organ = organ;
	}

	public List<Reference> getReferences() {
		return Collections.unmodifiableList(references);
	}

	public void reference(Element element) {
		if (element == null) {
			throw new IllegalArgumentException("element must not be null");
		}
		addReference(createReference(element));
	}

	public void addReference(Reference reference) {
		Element element = reference.getElement();
		if (!canReference(element)) {
			throw new IllegalArgumentException("cannot reference '" + element
					+ "'");
		}
		references.add(reference);

		fireReferenceAdded(reference);
	}

	protected Reference createReference(Element element) {
		return new Reference(element);
	}

	/**
	 * Get the reference to the given element.
	 * 
	 * @param element
	 *            element to get reference for
	 * @return reference or <code>null</code> if element is not referenced
	 */
	public Reference getReference(Element element) {
		for (Reference reference : references) {
			if (reference.getElement() == element) {
				return reference;
			}
		}
		return null;
	}

	/**
	 * Get all references to the given element.
	 * 
	 * @param element
	 *            element to get references for
	 * @return references
	 */
	public List<Reference> getReferences(Element element) {
		List<Reference> filtered = new ArrayList<Reference>();

		for (Reference reference : references) {
			if (reference.getElement() == element) {
				filtered.add(reference);
			}
		}

		return filtered;
	}

	public final void unreference(Element element) {

		// work on copy of references to avoid concurrent modification
		for (Reference reference : new ArrayList<Reference>(references)) {
			if (reference.getElement() == element) {
				removeReference(reference);
			}
		}
	}

	public void removeReference(Reference reference) {
		if (!references.contains(reference)) {
			throw new IllegalArgumentException("element not referenced");
		}

		references.remove(reference);

		fireReferenceRemoved(reference);
	}

	public Reference getReference(int index) {
		return references.get(index);
	}

	public int getReferenceCount() {
		return references.size();
	}

	/**
	 * Get the name.
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name.
	 * 
	 * @param name
	 *            name to set
	 */
	public void setName(String name) {
		if (name == null) {
			name = "";
		}
		this.name = name.trim();

		fireElementChanged(true);
	}

	/**
	 * Get the description.
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Set the description.
	 * 
	 * @param description
	 *            description to set
	 */
	public void setDescription(String description) {
		if (description == null) {
			description = "";
		}
		this.description = description.trim();

		fireElementChanged(true);
	}

	/**
	 * Convenience method to fire an event in response to a change of this
	 * element.
	 */
	protected void fireElementChanged(boolean dispositionChange) {
		if (organ != null) {
			organ.fireElementChanged(this, dispositionChange);
		}
	}

	/**
	 * Convenience method to fire an event in response to a change of this
	 * element.
	 */
	protected void fireReferenceChanged(Reference reference,
			boolean dispositionChange) {
		if (organ != null) {
			organ.fireReferenceChanged(this, reference, dispositionChange);
		}
	}

	protected void fireReferenceAdded(Reference reference) {
		if (organ != null) {
			organ.fireReferenceAdded(this, reference);
		}
	}

	protected void fireReferenceRemoved(Reference reference) {
		if (organ != null) {
			organ.fireReferenceRemoved(this, reference);
		}
	}

	public String toString() {
		return getName();
	}

	/**
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * @param string
	 */
	public void setStyle(String string) {
		style = string;

		fireElementChanged(true);
	}

	/**
	 * All elements are cloneable for prototyping support.
	 */
	public Object clone() {
		try {
			Element clone = (Element) super.clone();

			clone.references = new ArrayList<Reference>();
			clone.organ = null;

			return clone;
		} catch (CloneNotSupportedException ex) {
			throw new Error(ex);
		}
	}

	public boolean references(Element element) {
		return getReference(element) != null;
	}

	@SuppressWarnings("unchecked")
	public <E> Set<E> getReferrer(Class<E> clazz) {
		Set<E> set = new HashSet<E>();

		for (Element candidate : organ.getElements()) {
			if (clazz.isAssignableFrom(candidate.getClass())
					&& candidate.references(this)) {
				set.add((E) candidate);
			}
		}
		return set;
	}

	public Set<Element> getReferrer() {
		return getReferrer(Element.class);
	}
}