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
package jorgan.fluidsynth.gui.dock;

import javax.swing.JScrollPane;

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.fluidsynth.disposition.FluidsynthSound;
import jorgan.gui.dock.OrganDockable;
import jorgan.gui.selection.ElementSelection;
import jorgan.gui.selection.SelectionListener;
import jorgan.session.OrganSession;
import spin.Spin;
import bias.Configuration;

public class EffectsDockable extends OrganDockable {

	private static Configuration config = Configuration.getRoot().get(
			EffectsDockable.class);

	private OrganSession session;

	private EffectsPanel panel;

	private EventHandler eventHandler = new EventHandler();

	public EffectsDockable() {
		config.read(this);
	}

	@Override
	public boolean forPlay() {
		return false;
	}

	@Override
	public void setSession(OrganSession session) {
		if (this.session != null) {
			this.session.getOrgan().removeOrganListener(
					(OrganListener) Spin.over(eventHandler));
			this.session.lookup(ElementSelection.class).removeListener(
					eventHandler);

			panel = null;
			setContent(null);
		}

		this.session = session;

		if (this.session != null) {
			this.session.getOrgan().addOrganListener(
					(OrganListener) Spin.over(eventHandler));
			this.session.lookup(ElementSelection.class).addListener(
					eventHandler);

			checkContent();
		}
	}

	private void checkContent() {
		Element element = session.lookup(ElementSelection.class)
				.getSelectedElement();
		if (element instanceof FluidsynthSound) {
			FluidsynthSound sound = (FluidsynthSound) element;

			panel = new EffectsPanel(sound);

			setContent(new JScrollPane(panel,
					JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
					JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		} else {
			setContent(null);
		}
	}

	private class EventHandler extends OrganAdapter implements
			SelectionListener {

		@Override
		public void propertyChanged(Element element, String name) {
			if (panel != null && element == panel.getSound()) {
				panel.read();
			}
		}

		@Override
		public void selectionChanged() {
			checkContent();
		}
	}
}
