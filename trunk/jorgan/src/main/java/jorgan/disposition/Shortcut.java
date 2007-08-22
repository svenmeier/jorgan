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

import java.awt.event.KeyEvent;

/**
 * A shortcut is an immutable value object.
 */
public class Shortcut {

    private char character = 0x0000;

    private int code = KeyEvent.VK_UNDEFINED;

    private int modifiers = 0;

    private int location = KeyEvent.KEY_LOCATION_STANDARD;

    public Shortcut(char character, int modifiers, int location) {
        if (character == KeyEvent.CHAR_UNDEFINED) {
            throw new IllegalArgumentException(
                    "character must not be undefined");
        }
        this.character = character;
        this.modifiers = modifiers;
        this.location = location;
    }

    public Shortcut(int code, int modifiers, int location) {
        if (code == KeyEvent.VK_UNDEFINED) {
            throw new IllegalArgumentException("code must not be undefined");
        }
        this.code = code;
        this.modifiers = modifiers;
        this.location = location;
    }

    public boolean characterFallback() {
        return code == KeyEvent.VK_UNDEFINED;
    }

    public boolean hasModifiers() {
        return modifiers != 0;
    }

    public boolean hasLocation() {
        return location != KeyEvent.KEY_LOCATION_STANDARD;
    }

    public char getCharacter() {
        return character;
    }

    public int getCode() {
        return code;
    }

    public int getModifiers() {
        return modifiers;
    }

    public int getLocation() {
        return location;
    }

    public boolean match(KeyEvent ev) {
        char character = ev.getKeyChar();
        int code = ev.getKeyCode();
        int modifiers = ev.getModifiers();
        int location = ev.getKeyLocation();

    	if (this.modifiers != modifiers
                || this.location != location) {
    		return false;
    	}
    		
        if (characterFallback()) {
            return this.character == character;
        } else {
            return this.code == code;
        }
    }

    public String toString() {
        String string = KeyEvent.getKeyModifiersText(modifiers);
        if (string.length() > 0) {
            string += "+";
        }
        
        if (characterFallback()) {
            string += Character.toUpperCase(character);
        } else {
            string += KeyEvent.getKeyText(code);
        }

        return string;
    }

    public static Shortcut createShortCut(KeyEvent ev) {
        char character = ev.getKeyChar();
        int code = ev.getKeyCode();
        int modifiers = ev.getModifiers();
        int location = ev.getKeyLocation();

        if (!isModifier(code)) {
	        if (code != KeyEvent.VK_UNDEFINED) {
	            return new Shortcut(code, modifiers, location);
	        } else if (character != KeyEvent.CHAR_UNDEFINED) {
                return new Shortcut(character, modifiers, location);
	        }
        }
        return null;
    }

    public static boolean isModifier(int code) {
        return code == KeyEvent.VK_CONTROL || code == KeyEvent.VK_SHIFT
                || code == KeyEvent.VK_META || code == KeyEvent.VK_ALT_GRAPH
                || code == KeyEvent.VK_ALT;
    }

	public static boolean maybeShortcut(KeyEvent e) {
		return !isModifier(e.getKeyCode()) && e.getID() != KeyEvent.KEY_TYPED;
	}
}