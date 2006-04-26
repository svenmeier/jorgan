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
package jorgan.xml.handler;

import java.io.IOException;

import jorgan.xml.*;

/**
 * A handler for double values.
 */
public class DoubleHandler extends Handler {

    private double dd;

    public DoubleHandler(AbstractWriter writer, String tag, double dd) {
        super(writer, tag);

        this.dd = dd;
    }

    /**
     * Constructor.
     */
    public DoubleHandler(AbstractReader reader) {
        super(reader);
    }

    public double getDouble() {
        return dd;
    }

    public void characters(XMLWriter writer) throws IOException {

        writer.characters(Double.toString(dd));
    }

    protected void finish() {
        dd = Double.parseDouble(getCharacters());

        finished();
    }
}