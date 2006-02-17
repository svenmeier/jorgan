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
import java.util.Set;

public class Combination extends Initiator {

    protected boolean canReference(Class clazz) {
        return Activateable.class.isAssignableFrom(clazz);
    }

    protected Reference createReference(Element element) {
        return new CombinationReference((Activateable) element);
    }

    public void initiate() {
        recall();
    }

    public void recall() {

        Iterator captors = getReferrer(Captor.class).iterator();
        while (captors.hasNext()) {
            Captor captor = (Captor) captors.next();
            if (captor.isActive()) {
                capture();
                return;
            }
        }

        int level = getLevel();

        for (int e = 0; e < getReferenceCount(); e++) {
            CombinationReference reference = (CombinationReference) getReference(e);

            Activateable registratable = reference.getRegistratable();

            if (!reference.isActive(level)) {
                registratable.setActive(false);
            }
        }

        for (int e = 0; e < getReferenceCount(); e++) {
            CombinationReference reference = (CombinationReference) getReference(e);

            Activateable registratable = reference.getRegistratable();

            if (reference.isActive(level)) {
                registratable.setActive(true);
            }
        }

        notifyObservers();
    }

    protected int getLevel() {
        Set memories = getReferrer(Memory.class);
        if (memories.size() > 0) {
            Memory memory = (Memory) memories.iterator().next();
            return memory.getPosition();
        } else {
            return 0;
        }
    }

    public void capture() {

        int level = getLevel();

        for (int e = 0; e < getReferenceCount(); e++) {
            CombinationReference reference = (CombinationReference) getReference(e);

            Activateable registratable = (Activateable) reference.getElement();

            reference.setActive(level, registratable.isActive());

            fireReferenceChanged(reference, false);
        }

        notifyObservers();
    }

    public void clear(int level) {
        for (int e = 0; e < getReferenceCount(); e++) {
            CombinationReference reference = (CombinationReference) getReference(e);

            reference.setActive(level, false);

            fireReferenceChanged(reference, false);
        }
    }

    public void swap(int level1, int level2) {
        for (int e = 0; e < getReferenceCount(); e++) {
            CombinationReference reference = (CombinationReference) getReference(e);

            boolean value1 = reference.isActive(level1);
            boolean value2 = reference.isActive(level1);

            reference.setActive(level1, value2);
            reference.setActive(level2, value1);

            fireReferenceChanged(reference, false);
        }
    }

    public void copy(int level1, int level2) {
        for (int e = 0; e < getReferenceCount(); e++) {
            CombinationReference reference = (CombinationReference) getReference(e);

            boolean value = reference.isActive(level1);

            reference.setActive(level2, value);

            fireReferenceChanged(reference, false);
        }
    }

    /**
     * A reference of a combination to another element.
     */
    public static class CombinationReference extends Reference {

        private boolean[] activated = new boolean[128];

        public CombinationReference(Activateable registratable) {
            super(registratable);
        }

        public void setActive(int level, boolean active) {
            if (level < 0 || level > 127) {
                throw new IllegalArgumentException("level");
            }
            activated[level] = active;
        }

        public boolean isActive(int level) {
            if (level < 0 || level > 127) {
                throw new IllegalArgumentException("level");
            }
            return activated[level];
        }

        public Activateable getRegistratable() {
            return (Activateable) getElement();
        }
    }
}