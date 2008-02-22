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
import jorgan.util.Null;

/**
 * Abstract base class of all elements of an organ.
 */
public abstract class Element implements Cloneable {

	/**
	 * The maximum supported zoom.
	 */
	public static final float MAX_ZOOM = 1.5f;

	/**
	 * The minimum supported zoom.
	 */
	public static final float MIN_ZOOM = 0.5f;

	/**
	 * The organ this element belongs to.
	 */
	private Organ organ;

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
	 * 
	 * TODO protected for {@link Console#toFront(Element)} and
	 * {@link Console#toBack(Element)} only
	 */
	protected List<Reference<? extends Element>> references = new ArrayList<Reference<? extends Element>>();

	private List<Message> messages = new ArrayList<Message>();

	/**
	 * The zoom.
	 */
	private float zoom = 1.0f;

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
		if (element == this) {
			return false;
		}

		if (!canReferenceDuplicates() && references(element)) {
			return false;
		}

		return canReference(element.getClass());
	}

	protected boolean canReference(Class<? extends Element> clazz) {
		return false;
	}

	protected boolean canReferenceDuplicates() {
		return false;
	}

	protected boolean validReference(Reference<? extends Element> reference) {
		return reference.getClass() == Reference.class;
	}

	public Organ getOrgan() {
		return organ;
	}

	protected void setOrgan(Organ organ) {
		this.organ = organ;

		if (this.organ != null) {
			// work on copy of references to avoid concurrent modification
			for (Reference<? extends Element> reference : new ArrayList<Reference<? extends Element>>(
					references)) {
				if (reference.getElement().getOrgan() != organ) {
					references.remove(reference);
				}
			}
		}
	}

	public List<Reference<? extends Element>> getReferences() {
		return Collections.unmodifiableList(references);
	}

	public void reference(Element element) {
		if (element == null) {
			throw new IllegalArgumentException("element must not be null");
		}
		addReference(createReference(element));
	}

	public void addReference(Reference<? extends Element> reference) {
		if (references.contains(reference)) {
			throw new IllegalArgumentException("duplicate reference '"
					+ reference + "'");
		}
		if (!canReference(reference.getElement())) {
			throw new IllegalArgumentException("cannot reference '"
					+ reference.getElement() + "'");
		}
		if (!validReference(reference)) {
			throw new IllegalArgumentException("invalid reference '"
					+ reference + "'");
		}
		references.add(reference);

		if (organ != null) {
			organ.fireAdded(new OrganEvent(organ, this, reference, true));
		}
	}

	protected Reference<? extends Element> createReference(Element element) {
		return new Reference<Element>(element);
	}

	/**
	 * Get the reference to the given element.
	 * 
	 * @param element
	 *            element to get reference for
	 * @return reference or <code>null</code> if element is not referenced
	 */
	public Reference<? extends Element> getReference(Element element) {
		Reference<? extends Element> filter = null;

		for (Reference<? extends Element> reference : references) {
			if (reference.getElement() == element) {
				if (filter == null) {
					filter = reference;
				} else {
					throw new IllegalStateException(
							"element is referenced more than once");
				}
			}
		}

		return filter;
	}

	public List<Reference<? extends Element>> getReferences(Element element) {
		List<Reference<? extends Element>> filter = new ArrayList<Reference<? extends Element>>();

		for (Reference<? extends Element> reference : this.references) {
			if (reference.getElement() == element) {
				filter.add(reference);
			}
		}
		return filter;
	}

	@SuppressWarnings("unchecked")
	public <E extends Element> List<E> getReferenced(Class<E> clazz) {
		List<E> filter = new ArrayList<E>();

		for (Reference reference : this.references) {
			if (clazz.isInstance(reference.getElement())) {
				filter.add((E) reference.getElement());
			}
		}
		return filter;
	}

	public int getReferencedIndex(Element element) {
		for (int r = 0; r < references.size(); r++) {
			Reference<? extends Element> reference = references.get(r);

			if (reference.getElement() == element) {
				return r;
			}
		}
		throw new IllegalArgumentException("element not referenced");
	}

	@SuppressWarnings("unchecked")
	public <R extends Reference> List<R> getReferences(Class<R> clazz) {
		List<R> filter = new ArrayList<R>();

		for (Reference reference : this.references) {
			if (clazz.isInstance(reference)) {
				filter.add((R) reference);
			}
		}
		return filter;
	}

	public final void unreference(Element element) {

		// work on copy of references to avoid concurrent modification
		for (Reference<? extends Element> reference : new ArrayList<Reference<? extends Element>>(
				references)) {
			if (reference.getElement() == element) {
				removeReference(reference);
			}
		}
	}

	public void removeReference(Reference<? extends Element> reference) {
		if (!references.contains(reference)) {
			throw new IllegalArgumentException("element not referenced");
		}

		references.remove(reference);

		if (organ != null) {
			organ.fireRemoved(new OrganEvent(organ, this, reference, true));
		}
	}

	public Reference<? extends Element> getReference(int index) {
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
		if (!Null.safeEquals(this.name, name)) {
			if (name == null) {
				name = "";
			}
			this.name = name.trim();

			fireChanged(true);
		}
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

		fireChanged(true);
	}

	protected void fireChanged(boolean dispositionChange) {
		if (organ != null) {
			organ.fireChanged(new OrganEvent(organ, this, dispositionChange));
		}
	}

	protected void fireChanged(Reference<? extends Element> reference,
			boolean dispositionChange) {
		if (organ != null) {
			organ.fireChanged(new OrganEvent(organ, this, reference,
					dispositionChange));
		}
	}

	protected void fireChanged(Message message, boolean dispositionChange) {
		if (organ != null) {
			organ.fireChanged(new OrganEvent(organ, this, message,
					dispositionChange));
		}
	}

	@Override
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
	 * TODO move into {@link Console.Reference} ?
	 * 
	 * @param style
	 */
	public void setStyle(String style) {
		if (!Null.safeEquals(this.style, style)) {
			this.style = style;

			fireChanged(true);
		}
	}

	/**
	 * All elements are cloneable.
	 */
	@Override
	public Element clone() {
		try {
			Element clone = (Element) super.clone();

			clone.references = new ArrayList<Reference<? extends Element>>();
			for (Reference<? extends Element> reference : references) {
				clone.references.add(reference.clone());
			}
			clone.messages = new ArrayList<Message>();
			for (Message message : messages) {
				clone.messages.add(message.clone());
			}
			clone.organ = null;

			return clone;
		} catch (CloneNotSupportedException ex) {
			throw new Error(ex);
		}
	}

	public boolean references(Element element) {
		for (Reference<? extends Element> reference : references) {
			if (reference.getElement() == element) {
				return true;
			}
		}
		return false;
	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		if (zoom < MIN_ZOOM) {
			zoom = MIN_ZOOM;
		}
		if (zoom > MAX_ZOOM) {
			zoom = MAX_ZOOM;
		}

		this.zoom = zoom;

		fireChanged(true);
	}

	/**
	 * Notification that a referrer has changed. <br>
	 * This default implementation does nothing.
	 */
	public void referrerChanged(Element element) {
	}

	public Set<Class<? extends Message>> getMessageClasses() {
		return new HashSet<Class<? extends Message>>();
	}

	public List<Message> getMessages() {
		return Collections.unmodifiableList(messages);
	}

	public boolean hasMessages() {
		return !messages.isEmpty();
	}

	public void addMessage(Message message) {
		Set<Class<? extends Message>> messageClasses = getMessageClasses();
		if (!messageClasses.contains(message.getClass())) {
			throw new IllegalArgumentException("illegal message '" + message
					+ "'");
		}
		this.messages.add(message);

		if (organ != null) {
			organ.fireAdded(new OrganEvent(organ, this, message, true));
		}
	}

	public void removeMessage(Message message) {
		this.messages.remove(message);

		if (organ != null) {
			organ.fireRemoved(new OrganEvent(organ, this, message, true));
		}
	}

	@SuppressWarnings("unchecked")
	public <M extends Message> List<M> getMessages(Class<M> clazz) {
		List<M> messages = new ArrayList<M>();

		for (Message message : this.messages) {
			if (clazz.isAssignableFrom(message.getClass())) {
				messages.add((M) message);
			}
		}
		return messages;
	}

	public void changeMessage(Message message, String status, String data1,
			String data2) {
		if (!this.messages.contains(message)) {
			throw new IllegalArgumentException("unkown message");
		}
		message.change(status, data1, data2);

		fireChanged(message, true);
	}

	public boolean hasReference(Reference<? extends Element> reference) {
		return references.contains(reference);
	}
}