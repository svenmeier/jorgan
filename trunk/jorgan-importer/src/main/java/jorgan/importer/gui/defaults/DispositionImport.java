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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JComponent;

import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.importer.gui.Import;
import jorgan.io.DispositionStream;
import jorgan.swing.wizard.AbstractPage;
import jorgan.swing.wizard.Page;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * An {@link Import} from a disposition.
 */
public class DispositionImport implements Import {

	private static Configuration config = Configuration.getRoot().get(
			DispositionImport.class);

	private DispositionOptionsPanel panel = new DispositionOptionsPanel();

	private String name;

	private String description;

	private List<Element> elements;

	public DispositionImport() {
		config.read(this);
	}

	@Override
	public List<Page> getPages() {
		return Collections.<Page> singletonList(new OptionsPage());
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

	public List<Element> getElements() {
		return elements;
	}

	public void showMessage(String key, Object... args) {
		MessageBox box = config.get(key).read(
				new MessageBox(MessageBox.OPTIONS_OK));
		box.show(panel, args);
	}

	/**
	 * Read ranks from the given disposition file.
	 * 
	 * @param file
	 *            file to read from
	 * @return list of ranks
	 * @throws IOException
	 * @throws XMLFormatException
	 */
	private List<Element> readElements(File file) throws IOException {
		Organ organ = new DispositionStream().read(file);

		List<Element> elements = new ArrayList<Element>(organ.getElements());

		for (Element element : elements) {
			organ.removeElement(element);
		}

		return elements;
	}

	private class OptionsPage extends AbstractPage {

		@Override
		public String getDescription() {
			return DispositionImport.this.getDescription();
		}

		@Override
		protected JComponent getComponentImpl() {
			return panel;
		}

		@Override
		public boolean leavingToPrevious() {
			elements = null;

			return true;
		}

		@Override
		public boolean allowsNext() {
			File file = panel.getDisposition();

			return file != null && file.exists() && file.isFile();
		}

		@Override
		public boolean leavingToNext() {
			File file = panel.getDisposition();
			try {
				elements = readElements(file);
			} catch (IOException ex) {
				showMessage("exception/general", file.getPath());
				return false;
			} catch (Exception ex) {
				showMessage("exception/invalid", file.getPath());
				return false;
			}
			return true;
		}
	}
}