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

import jorgan.disposition.event.*;

/**
 * The container for all elements of an organ.
 */
public class Organ {

  private static Class[] elementClasses = new Class[]{Console.class,
                                               Label.class,
                                               Keyboard.class,
                                               SoundSource.class,
                                               Stop.class,
                                               Coupler.class,
                                               Combination.class,
                                               Swell.class,
                                               Tremulant.class,
                                               Variation.class,
                                               Sequence.class,
                                               Activator.class,
                                               Crescendo.class,
                                               Keyer.class,
                                               Memory.class}; 
  
  /**
   * Registered listeners.
   */
  private transient List listeners;

  private List elements = new ArrayList();

  public static Class[] getElementClasses() {
    Class[] copy = new Class[elementClasses.length];
    System.arraycopy(elementClasses, 0, copy, 0, elementClasses.length);
    return copy;
  }
  
  public void addOrganListener(OrganListener listener) {
    if (listeners == null){
      listeners = new ArrayList();
    }
    listeners.add(listener);
  }

  public void removeOrganListener(OrganListener listener) {
    if (listeners == null){
      listeners = new ArrayList();
    }
    listeners.remove(listener);
  }

  public int getElementCount() {
      return elements.size();
  }
  
  public Element getElement(int index) {
      return (Element)elements.get(index);
  }

  public List getElements() {
      return Collections.unmodifiableList(elements);
  }

  public void addElement(Element element) {
      addElement(elements.size(), element);
  }
  
  public void addElement(int index, Element element) {
      elements.add(index, element);
      element.setOrgan(this);

      fireElementAdded(element);
  }

  public void removeElement(Element element) {

      elements.remove(element);
      element.setOrgan(null);

      fireElementRemoved(element);
  }
  
  protected void fireElementChanged(Element element, boolean dispositionChange) {
    if (listeners != null) {
      OrganEvent event = new OrganEvent(this, element, dispositionChange);
      for (int l = 0; l < listeners.size(); l++) {
        OrganListener listener = (OrganListener)listeners.get(l);

        listener.elementChanged(event);
      }
    }
  }

  protected void fireElementAdded(Element element) {
    if (listeners != null) {
      OrganEvent event = new OrganEvent(this, element, true);
      for (int l = 0; l < listeners.size(); l++) {
        OrganListener listener = (OrganListener)listeners.get(l);

        listener.elementAdded(event);
      }
    }
  }

  protected void fireElementRemoved(Element element) {
    if (listeners != null) {
      OrganEvent event = new OrganEvent(this, element, true);
      for (int l = 0; l < listeners.size(); l++) {
        OrganListener listener = (OrganListener)listeners.get(l);

        listener.elementRemoved(event);
      }
    }
  }
  
  protected void fireReferenceChanged(Element element, Reference reference, boolean dispositionChange) {
    if (listeners != null) {
      OrganEvent event = new OrganEvent(this, element, reference, dispositionChange);
      for (int l = 0; l < listeners.size(); l++) {
        OrganListener listener = (OrganListener)listeners.get(l);

        listener.referenceChanged(event);
      }
    }
  }

  protected void fireReferenceAdded(Element element, Reference reference) {
    if (listeners != null) {
      OrganEvent event = new OrganEvent(this, element, reference, true);
      for (int l = 0; l < listeners.size(); l++) {
        OrganListener listener = (OrganListener)listeners.get(l);

        listener.referenceAdded(event);
      }
    }
  }

  protected void fireReferenceRemoved(Element element, Reference reference) {
    if (listeners != null) {
      OrganEvent event = new OrganEvent(this, element, reference, true);
      for (int l = 0; l < listeners.size(); l++) {
        OrganListener listener = (OrganListener)listeners.get(l);

        listener.referenceRemoved(event);
      }
    }
  }

  public List getCandidates(Class clazz) {
      List candidates = new ArrayList();
      
      for (int e = 0; e < elements.size(); e++) {
          Element element = (Element)elements.get(e);
          if (clazz.isInstance(element)) {
              candidates.add(element);
          }
      }
      
      return candidates;
  }
  
  public List getReferenceToCandidates(List elements) {

    List candidates = new ArrayList();

    candidates:
    for (int c = 0; c < this.elements.size(); c++) {
      Element candidate = (Element)this.elements.get(c);     

      for (int e = 0; e < elements.size(); e++) {
        Element element = (Element)elements.get(e);
        
        if (!element.canReference(candidate)) {
          continue candidates;
        }
      }
      candidates.add(candidate);
    }
    return candidates;
  }

  /**
   * Get candidates which can reference all given elements.
   * 
   * @param elements    elements to find candidates for 
   * @return            candidates, never null
   */
  public List getReferencedFromCandidates(List elements) {

    List candidates = new ArrayList();

    candidates:
    for (int c = 0; c < this.elements.size(); c++) {
      Element candidate = (Element)this.elements.get(c);
        
      for (int e = 0; e < elements.size(); e++) {
        Element element = (Element)elements.get(e);
        if (!candidate.canReference(element)) {
          continue candidates;
        }
      }
      candidates.add(candidate);
    }    
    return candidates;
  }

  /**
   * Get elements of the given class.
   * 
   * @param clazz   class to give elements for
   * @return    elements
   */
  public List getElements(Class clazz) {
      List list = new ArrayList();

      for (int c = 0; c < this.elements.size(); c++) {
        Element element = (Element)this.elements.get(c);

        if (clazz.isInstance(element)) {
            list.add(element);
        }
      }
      
      return list;
  }
}