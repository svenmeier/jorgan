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

public class Memory extends Continuous {

    private String[] titles = new String[128];
    {
        for (int l = 0; l < titles.length; l++) {
            titles[l] = "";
        }
    }

    protected boolean canReference(Class clazz) {
        return Combination.class == clazz;
    }

    public String getTitle() {
        return titles[getPosition()];
    }

    public String getTitle(int index) {
        if (index < 0 || index > 127) {
            throw new IllegalArgumentException(
                    "index has to be between 0 and 127");
        }
        return titles[index];
    }

    public void setTitle(int index, String title) {
        if (index < 0 || index > 127) {
            throw new IllegalArgumentException(
                    "index has to be between 0 and 127");
        }
        if (title == null) {
            throw new IllegalArgumentException("level must not be null");
        }
        titles[index] = title;

        fireElementChanged(false);
    }

    public void clear(int level) {
        setTitle(level, "");

        for (int r = 0; r < getReferenceCount(); r++) {
            ((Combination) getReference(r).getElement()).clear(level);
        }
    }

    public void swap(int level1, int level2) {
        String title1 = getTitle(level1);
        String title2 = getTitle(level2);

        setTitle(level1, title2);
        setTitle(level2, title1);

        for (int r = 0; r < getReferenceCount(); r++) {
            ((Combination) getReference(r).getElement()).swap(level1, level2);
        }
    }

    public void copy(int level1, int level2) {
        String title = getTitle(level1);

        setTitle(level2, title);

        for (int r = 0; r < getReferenceCount(); r++) {
            ((Combination) getReference(r).getElement()).copy(level1, level2);
        }
    }
}