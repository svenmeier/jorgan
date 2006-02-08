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

import jorgan.disposition.Message;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.handler.Handler;
import jorgan.xml.handler.IntegerHandler;

import org.xml.sax.Attributes;

public class MessageHandler extends Handler {

    private Message message;

    private int status = -1;

    private int data1 = -1;

    private int data2 = -1;

    public MessageHandler(AbstractWriter writer, String tag, Message message) {
        super(writer, tag);

        this.message = message;
    }

    public MessageHandler(AbstractReader reader) {
        super(reader);
    }

    public Message getMessage() {
        return message;
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if ("status".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    status = getInteger();
                }
            };
        } else if ("data1".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    data1 = getInteger();
                }
            };
        } else if ("data2".equals(qName)) {
            new IntegerHandler(getReader()) {
                public void finished() {
                    data2 = getInteger();
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    protected void finish() {
        message = new Message(status, data1, data2);

        finished();
    }

    public void children() throws IOException {
        if (message.getStatus() != -1) {
            new IntegerHandler(getWriter(), "status", message.getStatus())
                    .start();
        }
        if (message.getData1() != -1) {
            new IntegerHandler(getWriter(), "data1", message.getData1())
                    .start();
        }
        if (message.getData2() != -1) {
            new IntegerHandler(getWriter(), "data2", message.getData2())
                    .start();
        }
    }
}
