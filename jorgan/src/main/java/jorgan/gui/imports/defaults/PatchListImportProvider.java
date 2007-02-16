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
package jorgan.gui.imports.defaults;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import jorgan.disposition.Stop;
import jorgan.gui.imports.spi.ImportProvider;
import jorgan.util.I18N;

/**
 * A provider for an import from a patch list.
 */
public class PatchListImportProvider implements ImportProvider {

	private static I18N i18n = I18N.get(PatchListImportProvider.class);
	
	private PatchListPanel panel = new PatchListPanel();

	private List<Stop> stops = new ArrayList<Stop>();

	public JPanel getOptionsPanel() {
		return panel;
	}

	public String getName() {
		return i18n.getString("name");
	}

	public String getDescription() {
		return i18n.getString("description");
	}

	public boolean hasStops() {
		return false;
	}

	public List<Stop> getStops() {
		return stops;
	}

	/**
	 * A panel for options of a patchList.
	 */
	public class PatchListPanel extends JPanel {
	}
}