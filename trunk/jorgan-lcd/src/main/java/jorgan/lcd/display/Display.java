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
package jorgan.lcd.display;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.disposition.event.OrganAdapter;
import jorgan.lcd.disposition.Screen;

public class Display {

	private Map<Screen, ScreenDisplay> screenClients = new HashMap<Screen, ScreenDisplay>();

	public Display(Organ organ) {
		organ.addOrganListener(new OrganAdapter() {
			@Override
			public void elementAdded(Element element) {
				if (element instanceof Screen) {
					add((Screen) element);
				}
			}

			@Override
			public void elementRemoved(Element element) {
				if (element instanceof Screen) {
					remove((Screen) element);
				}
			}

			public void propertyChanged(Element element, String name) {

			}
		});

		for (Screen screen : organ.getElements(Screen.class)) {
			add(screen);
		}
	}

	private void add(Screen screen) {
		try {
			screenClients.put(screen, new ScreenDisplay(screen));
		} catch (IOException ex) {
			// TODO
			ex.printStackTrace();
		}
	}

	protected void remove(Screen screen) {
		ScreenDisplay screenClient = screenClients.remove(screen);

		try {
			screenClient.destroy();
		} catch (IOException ex) {
			// TODO
			ex.printStackTrace();
		}
	}

	public void destroy() {
		for (Screen screen : new ArrayList<Screen>(screenClients.keySet())) {
			remove(screen);
		}
	}
}