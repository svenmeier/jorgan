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

public class Sequence extends Responsive {

  private Message previousMessage;
  private Message nextMessage;

  protected boolean canReference(Class clazz) {
    return Combination.class == clazz;  
  }

  protected Reference createReference(Element element) {
    return new SequenceReference((Combination)element);
  }
  
  public Message getNextMessage() {
    return nextMessage;
  }

  public void setNextMessage(Message message) {
    this.nextMessage = message;

    fireElementChanged(true);
  }
  
  public Message getPreviousMessage() {
    return previousMessage;
  }

  public void setPreviousMessage(Message message) {
    this.previousMessage = message;

    fireElementChanged(true);
  }
    
  public void combinationGet(Combination combination) {
    for (int r = 0; r < getReferenceCount(); r++) {
      SequenceReference reference = (SequenceReference)getReference(r);
      reference.setCurrent(reference.getCombination() == combination);
    }
  }

  public void next() {
    
    SequenceReference next = null;
    
    for (int r = 0; r < getReferenceCount(); r++) {
      SequenceReference reference = (SequenceReference)getReference(r);
      if (reference.isCurrent()) {
        reference.setCurrent(false);
        
        if (r == getReferenceCount() - 1) {
          next = (SequenceReference)getReference(0);
        } else {
          next = (SequenceReference)getReference(r + 1);
        }
      }
    }
    
    if (next == null && getReferenceCount() > 0) {
      next = (SequenceReference)getReference(0);
    }
    
    if (next != null) {
      next.getCombination().recall();
      next.setCurrent(true);
    }
  }
  
  public void previous() {

    SequenceReference previous = null;
      
    for (int r = 0; r < getReferenceCount(); r++) {
      SequenceReference reference = (SequenceReference)getReference(r);
      if (reference.isCurrent()) {
        reference.setCurrent(false);
          
        if (r == 0) {
          previous = (SequenceReference)getReference(getReferenceCount() - 1);
        } else {
            previous = (SequenceReference)getReference(r - 1);
        }
      }
    }
      
    if (previous == null && getReferenceCount() > 0) {
      previous = (SequenceReference)getReference(0);
    }
      
    if (previous != null) {
      previous.getCombination().recall();
      previous.setCurrent(true);
    }
  }
  
  /**
   * A reference of a sequence to another element.
   */
  public static class SequenceReference extends Reference {

    private boolean current;
    
    public SequenceReference(Combination combination) {
      super(combination);  
    }
    
    public void setCurrent(boolean current) {
      this.current = current;
    }
    
    public boolean isCurrent() {
      return current;
    }

    public Combination getCombination() {
      return (Combination)getElement();
    }
  }
}