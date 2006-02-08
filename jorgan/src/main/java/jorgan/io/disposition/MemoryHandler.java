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

import java.io.IOException;

import jorgan.disposition.Continuous;
import jorgan.disposition.Memory;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.handler.StringHandler;

import org.xml.sax.Attributes;

public class MemoryHandler extends ContinuousHandler {

    private Memory memory;

    private int level = 0;

    public MemoryHandler(AbstractReader reader, Attributes attributes) {
        super(reader, attributes);

        memory = new Memory();
    }

    public MemoryHandler(AbstractWriter writer, String tag, Memory memory) {
        super(writer, tag);

        this.memory = memory;
    }

    public Memory getMemory() {
        return memory;
    }

    protected Continuous getContinuous() {
        return getMemory();
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if ("title".equals(qName)) {
            new StringHandler(getReader()) {
                public void finished() {
                    memory.setTitle(level, getString());
                    level++;
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void children() throws IOException {
        super.children();

        for (level = 0; level < 128; level++) {
            new StringHandler(getWriter(), "title", memory.getTitle(level))
                    .start();
        }
    }
}