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
package jorgan.gui.dock.spi;

import java.util.ArrayList;
import java.util.List;

import jorgan.gui.dock.DescriptionDockable;
import jorgan.gui.dock.ElementsDockable;
import jorgan.gui.dock.KeyboardDockable;
import jorgan.gui.dock.MemoryDockable;
import jorgan.gui.dock.MessagesDockable;
import jorgan.gui.dock.MonitorDockable;
import jorgan.gui.dock.ProblemsDockable;
import jorgan.gui.dock.PropertiesDockable;
import jorgan.gui.dock.ReferencesDockable;
import swingx.docking.Dockable;

public class DefaultDockableProvider implements DockableProvider {

	public List<? extends Dockable> getDockables() {
		List<Dockable> dockables = new ArrayList<Dockable>();

		dockables.add(new ElementsDockable());
		dockables.add(new PropertiesDockable());
		dockables.add(new DescriptionDockable());
		dockables.add(new MonitorDockable());
		dockables.add(new MemoryDockable());
		dockables.add(new ProblemsDockable());
		dockables.add(new ReferencesDockable());
		dockables.add(new MessagesDockable());
		dockables.add(new KeyboardDockable());

		return dockables;
	}
}
