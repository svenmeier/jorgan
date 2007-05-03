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
package jorgan.gui.construct;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jorgan.disposition.Combination;
import jorgan.disposition.Console;
import jorgan.disposition.Coupler;
import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.disposition.Label;
import jorgan.disposition.SoundSource;
import jorgan.disposition.Stop;
import jorgan.disposition.Swell;
import jorgan.disposition.Tremulant;
import jorgan.disposition.Variation;

/**
 * Comparator of elements.
 */
public class ElementComparator implements Comparator<Element> {

    private static List<Class> types = new ArrayList<Class>();

    static {
        types.add(Console.class);
        types.add(Label.class);
        types.add(Keyboard.class);
        types.add(SoundSource.class);
        types.add(Stop.class);
        types.add(Coupler.class);
        types.add(Swell.class);
        types.add(Tremulant.class);
        types.add(Variation.class);
        types.add(Combination.class);
    }

    private boolean alphabet;

    public ElementComparator(boolean alphabet) {
        this.alphabet = alphabet;
    }

    public int compare(Element e1, Element e2) {

        if (alphabet) {
            int result = compareAlphabetical(e1, e2);
            if (result == 0) {
                result = compareType(e1, e2);
            }
            return result;
        } else {
            int result = compareType(e1, e2);
            if (result == 0) {
                result = compareAlphabetical(e1, e2);
            }
            return result;
        }
    }

    public int compareAlphabetical(Element e1, Element e2) {

        return e1.getName().compareTo(e2.getName());
    }

    public int compareType(Element e1, Element e2) {

        int index1 = types.indexOf(e1.getClass());
        int index2 = types.indexOf(e2.getClass());

        return index1 - index2;
    }
}