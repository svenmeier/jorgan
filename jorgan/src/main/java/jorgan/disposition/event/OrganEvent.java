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
package jorgan.disposition.event;

import java.util.*;

import jorgan.disposition.*;

/**
 * Event describing the change of an organ.
 */
public class OrganEvent extends EventObject {

  /**
   * Does this event indicate a disposition change.
   */
  private boolean dispositionChange;

  /**
   * The the element that was changed, added or removed.
   */
  private Element element;

  /**
   * The reference that was changed, added or removed.
   */
  private Reference reference;
  
  /**
   * Create a new event in case of a change of an element.
   *
   * @param organ   the organ that is the source of this event
   * @param element the changed element
   */
  public OrganEvent(Organ organ, Element element, boolean dispositionChange) {
    this(organ, element, null, dispositionChange);
  }

  /**
   * Create a new event in case of a change of a reference.
   *
   * @param organ     the organ that is the source of this event
   * @param element   the owning element of the reference
   * @param reference the changed reference
   */
  public OrganEvent(Organ organ, Element element, Reference reference, boolean dispositionChange) {
    super(organ);
    
    this.element           = element;
    this.reference         = reference;
    this.dispositionChange = dispositionChange;
  }

  /**
   * Get the element.
   *
   * @return  the element
   */
  public Element getElement() {
    return element;
  }
 
  /**
   * Get the reference
   *
   * @return  the reference
   */
  public Reference getReference() {
    return reference;
  }
 
  public boolean isDispositionChange() {
    return dispositionChange;
  }
}