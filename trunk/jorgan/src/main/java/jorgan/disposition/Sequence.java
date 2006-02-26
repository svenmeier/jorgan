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
 * A sequence of {@link jorgan.disposition.Combination}s.
 */
public class Sequence extends Continuous implements Combination.Observer {

    protected boolean canReference(Class clazz) {
        return Combination.class == clazz;
    }
    
    protected boolean canReferenceDuplicates() {
        return true;
    }

    public void initiated(Combination combination) {
        if (getCombination() != combination) {
            setPosition(references.indexOf(getReference(combination)));
        }
    }

    private Combination getCombination() {
        int position = getPosition();
        
        if (getReferenceCount() == 0) {
            return null;
        } else {
            Reference reference = getReference(Math.min(position, getReferenceCount() - 1));
            return ((Combination) reference.getElement());
        }
    }
    
    public void setPosition(int position) {
        int oldPosition = getPosition();
        
        super.setPosition(position);
        
        if (oldPosition != position) {
            Combination combination = getCombination();
            if (combination != null) {
                combination.recall();
            }
        }
    }
    
    protected int limitIncrement(int position) {
        if (position > getReferenceCount()) {
            position = Math.max(0, getReferenceCount() - 1);
        }
        return position;
    }
}