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

import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.gui.imports.spi.Import;
import jorgan.io.DispositionStream;
import jorgan.swing.FileSelector;
import jorgan.swing.layout.DefinitionBuilder;
import jorgan.swing.layout.DefinitionBuilder.Column;
import bias.Configuration;
import bias.swing.MessageBox;

/**
 * An {@link Import} from a disposition.
 */
public class DispositionImport implements Import {

	private static Configuration config = Configuration.getRoot().get(
			DispositionImport.class);

	private OptionsPanel panel = new OptionsPanel();

	private String name;

	private String description;

	public DispositionImport() {
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
		File file = panel.fileSelector.getSelectedFile();

		return file != null && file.exists() && file.isFile();
	}

	public List<Element> getElements() {
		List<Element> elements = new ArrayList<Element>();

		File file = panel.fileSelector.getSelectedFile();
		if (file != null) {
			try {
				elements = readElements(file);
			} catch (IOException ex) {
				panel.showMessage("exception/general", file.getPath());
			} catch (Exception ex) {
				panel.showMessage("exception/invalid", file.getPath());
			}
		}

		return elements;
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

	/**
	 * A panel for options.
	 */
	public class OptionsPanel extends JPanel {

		/**
		 * Insets to use by subclasse for a standard spacing around components.
		 */
		protected Insets insets = new Insets(2, 2, 2, 2);

		private FileSelector fileSelector = new FileSelector();

		/**
		 * Constructor.
		 */
		public OptionsPanel() {
			DefinitionBuilder builder = new DefinitionBuilder(this);

			Column column = builder.column();

			column.term(config.get("options/file").read(new JLabel()));

			fileSelector.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					firePropertyChange("ranks", null, null);
				}
			});
			column.definition(fileSelector).fillHorizontal();
		}

		public void showMessage(String key, Object... args) {
			MessageBox box = config.get(key).read(
					new MessageBox(MessageBox.OPTIONS_OK));
			box.show(panel, args);
		}
	}
}