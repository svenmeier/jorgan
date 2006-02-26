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

import jorgan.disposition.Continuous;
import jorgan.disposition.Activation;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;

import org.xml.sax.Attributes;

public class ActivationHandler extends ContinuousHandler {

    private Activation activation;

    public ActivationHandler(AbstractReader reader, Attributes attributes) {
        super(reader, attributes);

        activation = new Activation();
    }

    public ActivationHandler(AbstractWriter writer, String tag,
            Activation activation) {
        super(writer, tag);

        this.activation = activation;
    }

    public Activation getActivation() {
        return activation;
    }

    protected Continuous getContinuous() {
        return getActivation();
    }
}
