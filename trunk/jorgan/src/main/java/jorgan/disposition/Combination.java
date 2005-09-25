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

import java.util.Iterator;

public class Combination extends Responsive {

  private boolean fixed = false;
  private boolean captureWithRecall = false;
  private Message captureMessage;
  private Message recallMessage;

  protected boolean canReference(Class clazz) {
    return Activateable.class.isAssignableFrom(clazz);  
  }

  protected Reference createReference(Element element) {
    return new CombinationReference((Activateable)element);
  }
  
  public boolean isFixed() {
    return fixed;
  }

  public void setFixed(boolean fixed) {
    this.fixed = fixed;

    fireElementChanged(true);
  }

  public boolean isCaptureWithRecall() {
    return captureWithRecall;
  }

  public void setCaptureWithRecall(boolean setWithGet) {
    this.captureWithRecall = setWithGet;

    fireElementChanged(true);
  }

  public void recall() {

    for (int e = 0; e < getReferenceCount(); e++) {
      CombinationReference reference = (CombinationReference)getReference(e);
      
      Activateable registratable = reference.getRegistratable();

      if (!reference.isActive()) {
        registratable.setActive(false);
      }
    }
    
    for (int e = 0; e < getReferenceCount(); e++) {
      CombinationReference reference = (CombinationReference)getReference(e);
        
      Activateable registratable = reference.getRegistratable();

      if (reference.isActive()) {
        registratable.setActive(true);
      }
    }

    Iterator iterator = referrer.iterator();
    while (iterator.hasNext()) {
      Element element = (Element)iterator.next();
      if (element instanceof Sequence) {
        ((Sequence)element).combinationGet(this);
      }
    }
  }

  public void capture() {

    for (int e = 0; e < getReferenceCount(); e++) {
      CombinationReference reference = (CombinationReference)getReference(e);
      
      Activateable registratable = (Activateable)reference.getElement();

      reference.setOn(registratable.isActive());

      fireReferenceChanged(reference, true);
    }
  }

  public Message getCaptureMessage() {
    return captureMessage;
  }

  public Message getRecallMessage() {
    return recallMessage;
  }

  public void setCaptureMessage(Message message) {
    this.captureMessage = message;

    fireElementChanged(true);
  }

  public void setRecallMessage(Message message) {
    this.recallMessage = message;

    fireElementChanged(true);
  }
  
  /**
   * A reference of a combination to another element.
   */
  public static class CombinationReference extends Reference {

    private boolean active;
    
    public CombinationReference(Activateable registratable) {
      super(registratable);  
    }
    
    public void setOn(boolean active) {
      this.active = active;
    }
    
    public boolean isActive() {
      return active;
    }

    public Activateable getRegistratable() {
      return (Activateable)getElement();
    }
  }
}