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

/**
 * A crescendo.
 */
public class Crescendo extends Slider {

  protected boolean canReference(Class clazz) {
    return Activateable.class.isAssignableFrom(clazz);  
  }

  public void setPosition(int position) {
    super.setPosition(position);

    if (getReferencesCount() > 0) {
      int current = (getPosition() * getReferencesCount() / 128);
    
      Reference reference = getReference(current);
      ((Activateable)reference.getElement()).setActive(true);

      for (int r = 0; r < getReferencesCount(); r++) {
        reference = getReference(r);
        if (r != current) {
          ((Activateable)reference.getElement()).setActive(false);
        }
      }
    }
  }
}