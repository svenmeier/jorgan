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

public class InitiatorSequence extends Continuous implements Combination.Observer {

    protected boolean canReference(Class clazz) {
        return Combination.class == clazz;
    }

    public void initiated(Initiator initiator) {
        if (getInitiator() != initiator) {
            setPosition(references.indexOf(getReference(initiator)));
        }
    }

    public Initiator getInitiator() {
        int position = getPosition();
        
        if (position < getReferenceCount()) {
            Reference reference = getReference(position);
            return ((Initiator) reference.getElement());
        }
        
        return null;
    }
    
    public void setPosition(int position) {
        int oldPosition = getPosition();
        
        super.setPosition(position);
        
        if (oldPosition != position) {
            Initiator initiator = getInitiator();
            if (initiator != null) {
                initiator.initiate();
            }
        }
    }
    
    protected int getIncrementMax() {
        return getReferenceCount();
    }
}