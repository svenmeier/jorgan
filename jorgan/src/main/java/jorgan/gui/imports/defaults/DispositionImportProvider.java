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

import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jorgan.disposition.Element;
import jorgan.disposition.Organ;
import jorgan.disposition.Stop;
import jorgan.gui.imports.spi.ImportProvider;
import jorgan.io.DispositionReader;
import jorgan.io.riff.RiffFormatException;
import jorgan.swing.FileSelector;
import jorgan.swing.GridBuilder;
import jorgan.xml.XMLFormatException;

/**
 * A provider for an import from a disposition.
 */
public class DispositionImportProvider implements ImportProvider {

	/**
	 * The resource bundle.
	 */
	protected static ResourceBundle resources = ResourceBundle
			.getBundle("jorgan.gui.i18n");

	private OptionsPanel panel = new OptionsPanel();

	public JPanel getOptionsPanel() {
		return panel;
	}

	public String getName() {
		return resources.getString("import.disposition.name");
	}

	public String getDescription() {
		return resources.getString("import.disposition.description");
	}

	public boolean hasStops() {
		File file = panel.fileSelector.getSelectedFile();

		return file != null && file.exists() && file.isFile();
	}

	public List getStops() {
		List stops = new ArrayList();

		File file = panel.fileSelector.getSelectedFile();
		if (file != null) {
			try {
				stops = readStops(file);
			} catch (XMLFormatException ex) {
				panel.showException("import.disposition.exception.invalid",
						new String[] { file.getPath() }, ex);
			} catch (IOException ex) {
				panel.showException("import.disposition.exception",
						new String[] { file.getPath() }, ex);
			}
		}

		return stops;
	}

	/**
	 * Read stops from the given disposition file.
	 * 
	 * @param file
	 *            file to read from
	 * @return list of stops
	 * @throws IOException
	 * @throws XMLFormatException
	 */
	private List readStops(File file) throws IOException, RiffFormatException {

		List stops;

		InputStream input = null;
		try {
			input = new FileInputStream(file);

			DispositionReader reader = new DispositionReader(
					new FileInputStream(file));

			Organ organ = (Organ) reader.read();

			stops = organ.getElements(Stop.class);

			for (int s = 0; s < stops.size(); s++) {
				organ.removeElement((Element) stops.get(s));
			}
		} finally {
			if (input != null) {
				input.close();
			}
		}

		return stops;
	}

	/**
	 * A panel for options.
	 */
	public class OptionsPanel extends JPanel {

		/**
		 * Insets to use by subclasse for a standard spacing around components.
		 */
		protected Insets insets = new Insets(2, 2, 2, 2);

		private JLabel fileLabel = new JLabel();

		private FileSelector fileSelector = new FileSelector();

		/**
		 * Constructor.
		 */
		public OptionsPanel() {
			super(new GridBagLayout());

			GridBuilder builder = new GridBuilder(new double[] { 0.0d, 1.0d });

			builder.nextRow(1.0d);

			fileLabel.setText(resources.getString("import.soundfont.file"));
			add(fileLabel, builder.nextColumn());

			fileSelector.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					firePropertyChange("stops", null, null);
				}
			});
			add(fileSelector, builder.nextColumn().fillHorizontal());
		}

		/**
		 * Show an exception.
		 * 
		 * @param message
		 *            message of exception
		 * @param args
		 *            arguments of message
		 * @param exception
		 *            the exception
		 */
		public void showException(String message, Object[] args,
				Exception exception) {

			message = MessageFormat.format(resources.getString(message), args);

			JOptionPane.showMessageDialog(this, message, resources
					.getString("exception.title"), JOptionPane.ERROR_MESSAGE);
		}
	}
}