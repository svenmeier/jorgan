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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jorgan.disposition.event.AbstractUndoableChange;
import jorgan.disposition.event.Change;
import jorgan.disposition.event.OrganListener;
import jorgan.disposition.event.UndoableChange;
import jorgan.util.Null;

/**
 * Abstract base class of all elements of an organ.
 */
public abstract class Element implements Cloneable {

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

	/**
	 * The references to other elements.
	 * 
	 * TODO protected for {@link Console#toFront(Element)} and
	 * {@link Console#toBack(Element)} only
	 */
	protected List<Reference<? extends Element>> references = new ArrayList<Reference<? extends Element>>();

	private List<Message> messages = new ArrayList<Message>();

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
		if (organ == null) {
			// we keep references to other elements so this element can be
			// re-added (eventually cloned) to the previous organ
		} else {
			if (this.organ != null) {
				throw new IllegalStateException("already added");
			}

			// work on copy of references to avoid concurrent modification
			for (Reference<? extends Element> reference : new ArrayList<Reference<? extends Element>>(
					references)) {
				if (!organ.containsElement(reference.getElement())) {
					references.remove(reference);
				}
			}
		}

		this.organ = organ;
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

	public void addReference(final Reference<? extends Element> reference) {
		addReference(reference, this.references.size());
	}

	public void addReference(final Reference<? extends Element> reference,
			final int index) {
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
		if (this.references.size() < index) {
			throw new IllegalArgumentException("index '" + index + "'");
		}

		references.add(index, reference);

		fireChange(new AbstractUndoableChange() {
			public void notify(OrganListener listener) {
				listener.referenceAdded(Element.this, reference);
			}

			public void undo() {
				removeReference(reference);
			}

			public void redo() {
				addReference(reference, index);
			}
		});
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

		if (filter == null) {
			throw new IllegalArgumentException("unkown element");
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

	public void removeReference(final Reference<? extends Element> reference) {
		if (!references.contains(reference)) {
			throw new IllegalArgumentException("element not referenced");
		}

		final int index = references.indexOf(reference);

		references.remove(reference);

		fireChange(new AbstractUndoableChange() {
			public void notify(OrganListener listener) {
				listener.referenceRemoved(Element.this, reference);
			}

			public void undo() {
				addReference(reference, index);
			}

			public void redo() {
				removeReference(reference);
			}
		});
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
			String oldName = this.name;

			if (name == null) {
				name = "";
			}
			this.name = name.trim();

			fireChange(new UndoablePropertyChange(oldName, this.name));
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
		if (!Null.safeEquals(this.description, description)) {
			String oldDescription = this.description;

			if (description == null) {
				description = "";
			}
			this.description = description;

			fireChange(new UndoablePropertyChange(oldDescription, this.description));
		}
	}

	protected void fireChange(Change change) {
		if (organ != null) {
			organ.fireChange(change);
		}
	}

	@Override
	public String toString() {
		return getName();
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

	public Set<Class<? extends Message>> getMessageClasses() {
		return new HashSet<Class<? extends Message>>();
	}

	public List<Message> getMessages() {
		return Collections.unmodifiableList(messages);
	}

	public boolean hasMessages() {
		return !messages.isEmpty();
	}

	public void addMessage(final Message message) {
		addMessage(message, this.messages.size());
	}

	public void addMessage(final Message message, final int index) {
		Set<Class<? extends Message>> messageClasses = getMessageClasses();
		if (!messageClasses.contains(message.getClass())) {
			throw new IllegalArgumentException("illegal message '" + message
					+ "'");
		}
		if (this.messages.size() < index) {
			throw new IllegalArgumentException("index '" + index + "'");
		}

		this.messages.add(index, message);

		fireChange(new AbstractUndoableChange() {
			public void notify(OrganListener listener) {
				listener.messageAdded(Element.this, message);
			}

			public void undo() {
				removeMessage(message);
			}

			public void redo() {
				addMessage(message, index);
			}
		});
	}

	public void removeMessage(final Message message) {
		if (!this.messages.contains(message)) {
			throw new IllegalArgumentException("unkown message");
		}

		final int index = messages.indexOf(message);

		this.messages.remove(message);

		fireChange(new AbstractUndoableChange() {
			public void notify(OrganListener listener) {
				listener.messageRemoved(Element.this, message);
			}

			public void undo() {
				addMessage(message, index);
			}

			public void redo() {
				removeMessage(message);
			}
		});
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

	public void changeMessage(final Message message, final String status,
			final String data1, final String data2) {
		if (!this.messages.contains(message)) {
			throw new IllegalArgumentException("unkown message");
		}

		final String oldStatus = message.getStatus();
		final String oldData1 = message.getData1();
		final String oldData2 = message.getData2();

		message.change(status, data1, data2);

		fireChange(new AbstractUndoableChange() {
			public void notify(OrganListener listener) {
				listener.messageChanged(Element.this, message);
			}

			public void undo() {
				changeMessage(message, oldStatus, oldData1, oldData2);
			}

			public void redo() {
				changeMessage(message, status, data1, data2);
			}
		});
	}

	public boolean hasReference(Reference<? extends Element> reference) {
		return references.contains(reference);
	}

	protected void moveReference(final Reference<? extends Element> reference,
			final int index) {
		final int oldIndex = references.indexOf(reference);
		references.remove(reference);

		references.add(oldIndex < index ? index - 1 : index, reference);

		fireChange(new AbstractUndoableChange() {
			public void notify(OrganListener listener) {
				listener.referenceChanged(Element.this, reference);
			}

			public void undo() {
				moveReference(reference, oldIndex);
			}

			public void redo() {
				moveReference(reference, index);
			}
		});
	}

	public class PropertyChange implements Change {
		private String methodName;

		public PropertyChange() {
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();
			for (StackTraceElement element : stack) {
				if (element.getMethodName().startsWith("set")) {
					this.methodName = element.getMethodName();
					return;
				}
			}
			throw new UnsupportedOperationException();
		}

		public void notify(OrganListener listener) {
			listener.propertyChanged(Element.this, getName());
		}

		protected String getName() {
			return Character.toLowerCase(methodName.charAt("set".length()))
					+ methodName.substring("set".length() + 1);
		}
		
		protected Method getMethod() throws Exception {
			for (Method method : Element.this.getClass().getMethods()) {
				if (method.getName().equals(methodName)) {
					return method;
				}
			}
			throw new UnsupportedOperationException(methodName);
		}
	}

	public class UndoablePropertyChange extends PropertyChange implements UndoableChange {

		private Object oldValue;

		private Object newValue;

		public UndoablePropertyChange(Object oldValue, Object newValue) {
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		public void undo() {
			invoke(oldValue);
		}

		public void redo() {
			invoke(newValue);
		}

		private void invoke(Object value) {
			try {
				getMethod().invoke(Element.this, value);
			} catch (Exception ex) {
				throw new IllegalStateException(ex);
			}
		}
		
		private Element getElement() {
			return Element.this;
		}
		
		public boolean replaces(UndoableChange change) {
			if (change instanceof UndoablePropertyChange) {
				UndoablePropertyChange other = (UndoablePropertyChange)change;
				if (this.getElement() == other.getElement() && this.getName().equals(other.getName())) {
						this.newValue = other.newValue;
						
						return true;
				}
			}
			
			return false;
		}
	}

	public class ReferenceChange implements Change {
		private Reference<?> reference;

		public ReferenceChange(Reference<?> reference) {
			this.reference = reference;
		}

		public Reference<?> getReference() {
			return reference;
		}
		
		public void notify(OrganListener listener) {
			listener.referenceChanged(Element.this, reference);
		}
	}
}