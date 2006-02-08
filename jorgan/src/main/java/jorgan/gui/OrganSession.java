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
package jorgan.gui;

import jorgan.disposition.Console;
import jorgan.disposition.Organ;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganEvent;
import jorgan.disposition.event.OrganListener;
import jorgan.play.OrganPlay;
import spin.Spin;

public class OrganSession {

    private Organ organ;

    private OrganPlay play;

    private ElementSelectionModel selectionModel;

    public OrganSession() {
        this(createDefaultOrgan());
    }

    public OrganSession(Organ organ) {
        if (organ == null) {
            throw new IllegalArgumentException("organ must not be null");
        }
        this.organ = organ;

        this.play = new OrganPlay(organ);
        this.selectionModel = new ElementSelectionModel();
        this.organ.addOrganListener((OrganListener) Spin
                .over(new OrganAdapter() {
                    public void elementAdded(OrganEvent event) {

                        jorgan.disposition.Element element = event.getElement();

                        selectionModel.setSelectedElement(element);
                    }

                    public void elementRemoved(OrganEvent event) {

                        jorgan.disposition.Element element = event.getElement();

                        selectionModel.clear(element);
                    }
                }));
    }

    public Organ getOrgan() {
        return organ;
    }

    public OrganPlay getPlay() {
        return play;
    }

    public ElementSelectionModel getSelectionModel() {
        return selectionModel;
    }

    private static Organ createDefaultOrgan() {
        Organ organ = new Organ();

        organ.addElement(new Console());

        return organ;
    }
}