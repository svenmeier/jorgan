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

public class Piston extends Active {

  private boolean fixed      = false;
  private boolean setWithGet = false;
  private Message setMessage;
  private Message getMessage;

  protected boolean canReference(Class clazz) {
    return Registratable.class.isAssignableFrom(clazz);  
  }

  protected Reference createReference(Element element) {
    return new RegistrationReference(element);
  }
  
  public boolean isFixed() {
    return fixed;
  }

  public void setFixed(boolean fixed) {
    this.fixed = fixed;

    fireElementChanged(true);
  }

  public boolean isSetWithGet() {
    return setWithGet;
  }

  public void setSetWithGet(boolean setWithGet) {
    this.setWithGet = setWithGet;

    fireElementChanged(true);
  }

  public void get() {

    for (int e = 0; e < getReferencesCount(); e++) {
      RegistrationReference reference = (RegistrationReference)getReference(e);
      
      Registratable registratable = reference.getRegistratable();

      if (!reference.isOn()) {
        registratable.setOn(false);
      }
    }
    
    for (int e = 0; e < getReferencesCount(); e++) {
        RegistrationReference reference = (RegistrationReference)getReference(e);
        
        Registratable registratable = reference.getRegistratable();

        if (reference.isOn()) {
          registratable.setOn(true);
        }
      }
  }

  public void set() {

    for (int e = 0; e < getReferencesCount(); e++) {
      RegistrationReference reference = (RegistrationReference)getReference(e);
      
      Registratable registratable = (Registratable)reference.getElement();

      reference.setOn(registratable.isOn());

      fireReferenceChanged(reference, true);
    }
  }

  public Message getSetMessage() {
    return setMessage;
  }

  public Message getGetMessage() {
    return getMessage;
  }

  public void setSetMessage(Message message) {
    this.setMessage = message;

    fireElementChanged(true);
  }

  public void setGetMessage(Message message) {
    this.getMessage = message;

    fireElementChanged(true);
  }
  
  /**
   * A reference of a piston to another element.
   */
  public static class RegistrationReference extends Reference {

    private boolean on;
    
    public RegistrationReference(Element element) {
      super(element);  
    }
    
    public void setOn(boolean on) {
      this.on = on;
    }
    
    public boolean isOn() {
      return on;
    }

    public Registratable getRegistratable() {
      return (Registratable)getElement();
    }
  }
}