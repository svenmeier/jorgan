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

import java.io.Serializable;
import java.text.ParsePosition;

import jorgan.sound.midi.KeyFormat;

/**
 * A key is an immutable value object.
 */
public class Key implements Comparable, Serializable {

    public static final Key C0 = new Key(12);

    public static final Key C1 = new Key(24);

    public static final Key C2 = new Key(36);

    public static final Key C3 = new Key(48);

    public static final Key C4 = new Key(60);

    public static final Key C5 = new Key(72);

    public static final Key C6 = new Key(84);

    public static final Key C7 = new Key(96);

    public static final Key C8 = new Key(108);

    private int pitch;

    public Key(int pitch) {
        if (pitch < 0 || pitch > 127) {
            throw new IllegalArgumentException("pitch '" + pitch + "'");
        }
        this.pitch = pitch;
    }

    public Key(String name) {
        pitch = ((Integer) new KeyFormat().parseObject(name, new ParsePosition(
                0))).intValue();
    }

    public String getName() {
        return new KeyFormat().format(new Integer(pitch));
    }

    public Key halftoneUp() {
        if (pitch == 127) {
            return null;
        }
        return new Key(pitch + 1);
    }

    public Key halftoneDown() {
        if (pitch == 0) {
            return null;
        }
        return new Key(pitch - 1);
    }

    public int compareTo(Object object) {
        Key key = (Key) object;

        if (this.pitch < key.pitch) {
            return -1;
        }
        if (this.pitch > key.pitch) {
            return 1;
        }
        return 0;
    }

    public boolean lessEqual(Key key) {
        return compareTo(key) <= 0;
    }

    public boolean greaterEqual(Key key) {
        return compareTo(key) >= 0;
    }

    public boolean equals(Object key) {
        if (key == null || !(key instanceof Key)) {
            return false;
        }
        return this.pitch == ((Key) key).pitch;
    }

    public int hashCode() {
        return pitch;
    }
}
