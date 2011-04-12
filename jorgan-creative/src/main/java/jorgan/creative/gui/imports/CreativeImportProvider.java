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
package jorgan.creative.gui.imports;

import java.util.ArrayList;
import java.util.List;

import jorgan.creative.SoundFontManager;
import jorgan.importer.gui.Import;
import jorgan.importer.gui.spi.ImportProvider;

/**
 * A provider of {@link Import}s from Creative soundcards.
 * 
 * @see jorgan.creative.SoundFontManager
 */
public class CreativeImportProvider implements ImportProvider {

	public List<Import> getImports() {
		List<Import> imports = new ArrayList<Import>();

		try {
			// trigger creative failure early
			SoundFontManager.class.getName();

			imports.add(new CreativeImport());
		} catch (UnsatisfiedLinkError error) {
		}

		return imports;
	}
}