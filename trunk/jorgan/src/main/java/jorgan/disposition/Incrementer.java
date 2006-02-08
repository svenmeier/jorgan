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

/**
 * An incrementer of continuous elements.
 */
public class Incrementer extends Initiator {

    private int delta = 1;

    protected boolean canReference(Class clazz) {
        return Continuous.class.isAssignableFrom(clazz);
    }

    public void initiate() {
        Iterator iterator = getReferences().iterator();
        while (iterator.hasNext()) {
            Reference reference = (Reference) iterator.next();
            Continuous continuous = (Continuous) reference.getElement();

            continuous.setPosition(increment(continuous.getPosition()));
        }
    }

    private int increment(int position) {
        if (delta > 0) {
            position += delta;
            position -= (position % delta);
        } else {
            position += delta - 1;
            if (position >= 0) {
                position -= delta + (position % delta);
            }
        }
        return position;
    }

    public int getDelta() {
        return delta;
    }

    public void setDelta(int delta) {
        this.delta = delta;
    }
}