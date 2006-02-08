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
package jorgan.io.disposition;

import java.awt.event.KeyEvent;
import java.io.IOException;

import jorgan.disposition.Shortcut;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.handler.Handler;
import jorgan.xml.handler.IntegerHandler;
import jorgan.xml.handler.StringHandler;

import org.xml.sax.Attributes;

public class ShortcutHandler extends Handler {

    private Shortcut shortcut;

    private char character = KeyEvent.CHAR_UNDEFINED;

    private int code = KeyEvent.VK_UNDEFINED;

    private int modifiers = 0;

    private int location = KeyEvent.KEY_LOCATION_STANDARD;

    public ShortcutHandler(AbstractWriter writer, String tag, Shortcut shortcut) {
        super(writer, tag);

        this.shortcut = shortcut;
    }

    public ShortcutHandler(AbstractReader reader) {
        super(reader);
    }

    public Shortcut getShortcut() {
        return shortcut;
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if ("char".equals(qName)) {
            new StringHandler(getReader()) {
                public void finished() {
                    character = getString().charAt(0);
                }
            };
        } else if ("code".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    code = getInteger();
                }
            };
        } else if ("modifiers".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    modifiers = getInteger();
                }
            };
        } else if ("location".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    location = getInteger();
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    protected void finish() {
        shortcut = Shortcut
                .createShortcut(character, code, modifiers, location);

        finished();
    }

    public void children() throws IOException {
        if (shortcut.characterFallback()) {
            new StringHandler(getWriter(), "char", "" + shortcut.getCharacter())
                    .start();
        } else {
            new IntegerHandler(getWriter(), "code", shortcut.getCode()).start();

            if (shortcut.hasModifiers()) {
                new IntegerHandler(getWriter(), "modifiers", shortcut
                        .getModifiers()).start();
            }
            if (shortcut.hasLocation()) {
                new IntegerHandler(getWriter(), "location", shortcut
                        .getLocation()).start();
            }
        }
    }
}