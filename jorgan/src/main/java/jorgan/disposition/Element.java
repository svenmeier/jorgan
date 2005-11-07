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

import java.util.*;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;

/**
 * Abstract base class of all elements of an organ.
 */
public abstract class Element implements Cloneable, Serializable {

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
   * The set of elements that reference this element.
   */
  protected transient Set referrer = new HashSet();

  /**
   * The list of elements that are referenced by this element.
   */
  protected transient List references = new ArrayList();

  /**
   * Test if this element can reference the given element.
   * <br>
   * An element can be referenced if it is not identical to this,
   * is currently not referenced and its class can be referenced.
   * 
   * @param element the element to test
   * @return        <code>true</code> if element can be referenced
   * @see #canReference(Element)
   */
  public boolean canReference(Element element) {
    return element != this &&
           getReference(element) == null &&
           canReference(element.getClass());
  }
  
  /**
   * Test if this element can reference elements of the given class.
   * 
   * @param clazz   element class to test
   * @return        <code>true</code> if elements of the given class can be referenced
   */
  protected boolean canReference(Class clazz) {
    return false;
  }
  
  /**
   * Restore transient state on deserialization.
   *
   * @param in  stream to read from
   */
  private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
    in.defaultReadObject();
  
    referrer   = new HashSet();
    references = new ArrayList();
  } 

  /**
   * All elements are cloneable to support copying of elements.
   */
  public Object clone() {
      try {
          Element clone = (Element)super.clone();

          clone.referrer   = new HashSet();
          clone.references = new ArrayList();
          clone.organ      = null;
          
          return clone;
      } catch (CloneNotSupportedException ex) {
          throw new Error(ex);
      }
  }
  
  public Organ getOrgan() {
    return organ;
  }
  
  public void setOrgan(Organ organ) {
    if (this.organ != organ) {
      for (int r = references.size() - 1; r >= 0; r--) {
        Reference reference = (Reference)references.get(r);
        removeReference(reference);
      }

      // create copy of referrers, otherwise we'll get a
      // ConcurrentModification because unreference(Element)
      // will trigger a callback to referencedBy(Element, boolean)
      Iterator iterator = new ArrayList(referrer).iterator();
      while (iterator.hasNext()) {
        Element element = (Element)iterator.next();

        element.unreference(this);        
      }
      
      this.organ = organ;
    }    
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
      throw new IllegalArgumentException("cannot reference '"+ element + "'");
    }
    references.add(reference);
    reference.getElement().referencedBy(this, true);

    fireReferenceAdded(reference);
  }

  protected Reference createReference(Element element) {
    return new Reference(element);
  }
  
  public Reference getReference(Element element) {
    for (int r = 0; r < references.size(); r++) {
      Reference reference = (Reference)references.get(r);
      if (reference.getElement() == element) {
        return reference;
      }
    }
    return null;
  }
  
  public final void unreference(Element element) {

    removeReference(getReference(element));
  }

  public final void removeReference(Reference reference) {
    if (!references.contains(reference)) {
      throw new IllegalArgumentException("element not referenced");
    }

    references.remove(reference);
    reference.getElement().referencedBy(this, false);

    fireReferenceRemoved(reference);
  }

  public Reference getReference(int index) {
    return (Reference)references.get(index);
  }

  public int getReferenceCount() {
    return references.size();
  }

  public List getReferences() {
    return Collections.unmodifiableList(references);
  }
  
  /**
   * Get the name.
   *
   * @return  the name
   */
  public String getName() {
    return name;
  }

  /**
   * Set the name.
   *
   * @param name  name to set
   */
  public void setName(String name) {
    if (name == null) {
      name = "";
    }
    this.name = name;

    fireElementChanged(true);
  }

  /**
   * Get the description.
   *
   * @return  the description
   */
  public String getDescription() {
    return description;
  }

  /**
   * Set the description.
   *
   * @param description  description to set
   */
  public void setDescription(String description) {
    if (description == null) {
      description = "";
    }
    this.description = description;

    fireElementChanged(true);
  }

  /**
   * Convenience method to fire an event in response to a change
   * of this element.
   */
  protected void fireElementChanged(boolean dispositionChange) {
    if (organ != null) {
      organ.fireElementChanged(this, dispositionChange);
    }
  }

  /**
   * Convenience method to fire an event in response to a change
   * of this element.
   */
  protected void fireReferenceChanged(Reference reference, boolean dispositionChange) {
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

  /**
   * Test if this element is referenced by other elements.
   *
   * @return  <code>true</code> if at least on other element
   *          references this element
   */
  public boolean hasReferrer() {
    return !referrer.isEmpty();
  }

  /**
   * Notification that this element is referenced by the given
   * element.
   *
   * @param element   element that referres this element
   */
  protected void referencedBy(Element element, boolean referenced) {
    if (referenced) {
      referrer.add(element);
    } else {
      referrer.remove(element);
    }
  }
  
  public List referenced() {
    List referenced = new ArrayList();
      
    for (int r = 0; r < references.size(); r++) {
      Reference reference = (Reference)references.get(r);
      referenced.add(reference.getElement());
    }
    return referenced;
  }
    
  public Set referrer(Class clazz) {
    Set filtered = new HashSet();
    
    Iterator iterator = referrer.iterator();
    while (iterator.hasNext()){
      Element element = (Element)iterator.next();
      if (clazz.isAssignableFrom(element.getClass())) {
        filtered.add(element);    
      }
    }
    return filtered;
  }
  
  public Set getReferrer() {
    return Collections.unmodifiableSet(referrer);
  }

  public String toString() {
    return getName();
  }
  
  /**
   * @return
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

}