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
package jorgan.importer.gui.defaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jorgan.disposition.Element;
import jorgan.importer.gui.Import;
import jorgan.swing.wizard.Page;
import bias.Configuration;

/**
 * An {@link Import} from a patch list.
 */
public class PatchListImport implements Import {

	private static Configuration config = Configuration.getRoot().get(
			PatchListImport.class);

	private List<Element> elements = new ArrayList<Element>();

	private String name;

	private String description;

	public PatchListImport() {
		config.read(this);
	}

	@Override
	public List<Page> getPages() {
		return Collections.emptyList();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List<Element> getElements() {
		return elements;
	}
}