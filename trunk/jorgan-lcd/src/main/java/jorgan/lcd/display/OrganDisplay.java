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
import jorgan.lcd.display.spi.DisplayerRegistry;
import jorgan.lcd.disposition.Display;
import jorgan.lcd.lcdproc.Client;
import jorgan.lcd.lcdproc.Screen;

public class OrganDisplay {

	private Map<Display, ScreenWrapper> wrappers = new HashMap<Display, ScreenWrapper>();

	public OrganDisplay(Organ organ) {
		organ.addOrganListener(new OrganAdapter() {
			@Override
			public void elementAdded(Element element) {
				if (element instanceof Display) {
					add((Display) element);
				}
			}

			@Override
			public void elementRemoved(Element element) {
				if (element instanceof Display) {
					remove((Display) element);
				}
			}

			public void propertyChanged(Element element, String name) {

			}
		});

		for (Display display : organ.getElements(Display.class)) {
			add(display);
		}
	}

	private void add(Display display) {
		wrappers.put(display, new ScreenWrapper(display));
	}

	protected void remove(Display display) {
		ScreenWrapper wrapper = wrappers.remove(display);

		wrapper.close();
	}

	public void destroy() {
		for (Display display : new ArrayList<Display>(wrappers.keySet())) {
			remove(display);
		}
	}

	private class ScreenWrapper {

		private Client client;

		private Screen screen;

		private Map<Element, ElementDisplayer<?>> displayers = new HashMap<Element, ElementDisplayer<?>>();

		public ScreenWrapper(Display display) {
			try {
				this.client = new Client(display.getHost(), display.getPort());

				this.screen = client.addScreen();

				int row = 1;
				for (Element element : display.getReferenced(Element.class)) {
					ElementDisplayer<?> displayer = new DisplayerRegistry()
							.getDisplayer(screen, row, element);
					if (displayer != null) {
						displayers.put(element, displayer);
						row++;
					}
				}
			} catch (Exception ex) {
				close();
			}
		}

		public void close() {
			if (this.client != null) {
				try {
					client.close();
				} catch (IOException ignore) {
				}
				client = null;
			}
		}
	}
}