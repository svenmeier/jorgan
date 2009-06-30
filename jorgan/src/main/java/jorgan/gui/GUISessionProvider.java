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

import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.gui.selection.ElementSelection;
import jorgan.gui.selection.SelectionListener;
import jorgan.gui.undo.UndoManager;
import jorgan.session.OrganSession;
import jorgan.session.spi.SessionProvider;

public class GUISessionProvider implements SessionProvider {

	public Object create(final OrganSession session, Class<?> clazz) {
		if (clazz == UndoManager.class) {
			final UndoManager undoManager = new UndoManager(session.getOrgan());
			
			session.getOrgan().addOrganListener(new OrganAdapter() {
				@Override
				public void elementRemoved(Element element) {
					undoManager.compound();
				}

				@Override
				public void elementAdded(Element element) {
					undoManager.compound();
				}
			});
			
			return undoManager;
		} else if (clazz == ElementSelection.class) {
			final ElementSelection selection = new ElementSelection();
			
			selection.addListener(new SelectionListener() {
				public void selectionChanged() {
					session.get(UndoManager.class).compound();
				}
			});
			session.getOrgan().addOrganListener(new OrganAdapter() {
				@Override
				public void elementRemoved(Element element) {
					selection.clear(element);
				}

				@Override
				public void elementAdded(Element element) {
					selection.setSelectedElement(element);
				}
			});
			
			return selection;
		}
		return null;
	}
}