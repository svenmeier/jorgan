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

import jorgan.App;
import jorgan.disposition.Activator;
import jorgan.disposition.Captor;
import jorgan.disposition.Combination;
import jorgan.disposition.Console;
import jorgan.disposition.Coupler;
import jorgan.disposition.Activation;
import jorgan.disposition.Element;
import jorgan.disposition.Keyboard;
import jorgan.disposition.Keyer;
import jorgan.disposition.Label;
import jorgan.disposition.Memory;
import jorgan.disposition.Organ;
import jorgan.disposition.Sequence;
import jorgan.disposition.SoundSource;
import jorgan.disposition.Incrementer;
import jorgan.disposition.Stop;
import jorgan.disposition.Swell;
import jorgan.disposition.Tremulant;
import jorgan.disposition.Variation;
import jorgan.xml.AbstractReader;
import jorgan.xml.AbstractWriter;
import jorgan.xml.XMLWriter;
import jorgan.xml.handler.Handler;

import org.xml.sax.Attributes;

public class OrganHandler extends Handler {

    private Organ organ;

    /**
     * Constructor.
     */
    public OrganHandler(AbstractReader reader) {
        super(reader);

        organ = new Organ();
    }

    public OrganHandler(AbstractWriter writer, String tag, Organ organ) {
        super(writer, tag);

        this.organ = organ;
    }

    public Organ getOrgan() {
        return organ;
    }

    public void startElement(String uri, String localName, String qName,
            Attributes attributes) {

        if ("console".equals(qName)) {
            new ConsoleHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getConsole());
                }
            };
        } else if ("label".equals(qName)) {
            new LabelHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getLabel());
                }
            };
        } else if ("keyboard".equals(qName)) {
            new KeyboardHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getKeyboard());
                }
            };
        } else if ("soundSource".equals(qName)) {
            new SoundSourceHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getSoundSource());
                }
            };
        } else if ("combination".equals(qName)) {
            new CombinationHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getCombination());
                }
            };
        } else if ("sequence".equals(qName)) {
            new SequenceHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getSequence());
                }
            };
        } else if ("tremulant".equals(qName)) {
            new TremulantHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getTremulant());
                }
            };
        } else if ("swell".equals(qName)) {
            new SwellHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getSwell());
                }
            };
        } else if ("activation".equals(qName)) {
            new ActivationHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getActivation());
                }
            };
        } else if ("activator".equals(qName)) {
            new ActivatorHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getActivator());
                }
            };
        } else if ("variation".equals(qName)) {
            new VariationHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getVariation());
                }
            };
        } else if ("stop".equals(qName)) {
            new StopHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getStop());
                }
            };
        } else if ("coupler".equals(qName)) {
            new CouplerHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getCoupler());
                }
            };
        } else if ("keyer".equals(qName)) {
            new KeyerHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getKeyer());
                }
            };
        } else if ("captor".equals(qName)) {
            new CaptorHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getCaptor());
                }
            };
        } else if ("memory".equals(qName)) {
            new MemoryHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getMemory());
                }
            };
        } else if ("incrementer".equals(qName)) {
            new IncrementerHandler(getReader(), attributes) {
                public void finished() {
                    organ.addElement(getIncrementer());
                }
            };
        } else {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void attributes(XMLWriter writer) throws IOException {
        writer.attribute("version", App.getVersion());

        super.attributes(writer);
    }

    public void children() throws IOException {
        super.children();

        for (int e = 0; e < organ.getElementCount(); e++) {
            Element element = organ.getElement(e);
            if (element instanceof Console) {
                new ConsoleHandler(getWriter(), "console", (Console) element)
                        .start();
            } else if (element instanceof Label) {
                new LabelHandler(getWriter(), "label", (Label) element).start();
            } else if (element instanceof Keyboard) {
                new KeyboardHandler(getWriter(), "keyboard", (Keyboard) element)
                        .start();
            } else if (element instanceof SoundSource) {
                new SoundSourceHandler(getWriter(), "soundSource",
                        (SoundSource) element).start();
            } else if (element instanceof Tremulant) {
                new TremulantHandler(getWriter(), "tremulant",
                        (Tremulant) element).start();
            } else if (element instanceof Swell) {
                new SwellHandler(getWriter(), "swell", (Swell) element).start();
            } else if (element instanceof Activation) {
                new ActivationHandler(getWriter(), "activation",
                        (Activation) element).start();
            } else if (element instanceof Variation) {
                new VariationHandler(getWriter(), "variation",
                        (Variation) element).start();
            } else if (element instanceof Combination) {
                new CombinationHandler(getWriter(), "combination",
                        (Combination) element).start();
            } else if (element instanceof Sequence) {
                new SequenceHandler(getWriter(), "sequence", (Sequence) element)
                        .start();
            } else if (element instanceof Stop) {
                new StopHandler(getWriter(), "stop", (Stop) element).start();
            } else if (element instanceof Coupler) {
                new CouplerHandler(getWriter(), "coupler", (Coupler) element)
                        .start();
            } else if (element instanceof Activator) {
                new ActivatorHandler(getWriter(), "activator",
                        (Activator) element).start();
            } else if (element instanceof Keyer) {
                new KeyerHandler(getWriter(), "keyer", (Keyer) element).start();
            } else if (element instanceof Captor) {
                new CaptorHandler(getWriter(), "captor", (Captor) element)
                        .start();
            } else if (element instanceof Memory) {
                new MemoryHandler(getWriter(), "memory", (Memory) element)
                        .start();
            } else if (element instanceof Incrementer) {
                new IncrementerHandler(getWriter(), "incrementer", (Incrementer) element)
                        .start();
            } else {
                throw new IOException("unable to write element '"
                        + element.getClass().getName() + "'");
            }
        }
    }
}