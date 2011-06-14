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
package jorgan.exporter.gui.defaults;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import jorgan.disposition.Element;
import jorgan.exporter.gui.Export;
import bias.Configuration;

/**
 * An {@link Export} to a list.
 */
public class ListExport implements Export {

	private static Configuration config = Configuration.getRoot().get(
			ListExport.class);

	private OptionsPanel panel = new OptionsPanel();

	private List<Element> elements = new ArrayList<Element>();

	private String name;

	private String description;

	public ListExport() {
		config.read(this);
	}

	public JPanel getOptionsPanel() {
		return panel;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean hasElements() {
		return false;
	}

	public List<Element> getElements() {
		return elements;
	}

	/**
	 * A panel for options of a patchList.
	 */
	public class OptionsPanel extends JPanel {
	}
}