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
package jorgan.gui.dock;

import java.util.ArrayList;
import java.util.List;

import jorgan.gui.dock.spi.ViewProvider;

public class DefaultViewProvider implements ViewProvider {

	public List<AbstractView> getViews() {
		List<AbstractView> views = new ArrayList<AbstractView>();

		// construct only
		views.add(new ElementsView());
		views.add(new PropertiesView());
		views.add(new ReferencesView());
		views.add(new DescriptionView());
		views.add(new MessagesView());
		views.add(new SkinView());

		views.add(new MonitorView());
		views.add(new ProblemsView());

		return views;
	}
}
