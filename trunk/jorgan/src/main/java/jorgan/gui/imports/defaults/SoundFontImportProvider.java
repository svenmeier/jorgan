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
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jorgan.disposition.Stop;
import jorgan.gui.imports.spi.ImportProvider;
import jorgan.io.riff.RiffChunk;
import jorgan.io.riff.RiffFormatException;
import jorgan.io.soundfont.Preset;
import jorgan.io.soundfont.SoundfontReader;
import jorgan.swing.FileSelector;
import jorgan.swing.GridBuilder;
import jorgan.util.I18N;

/**
 * A provider for an import from a SoundFont.
 */
public class SoundFontImportProvider implements ImportProvider {

	private static I18N i18n = I18N.get(SoundFontImportProvider.class);

	private OptionsPanel panel = new OptionsPanel();

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
		File file = panel.fileSelector.getSelectedFile();

		return file != null && file.exists() && file.isFile();
	}

	public List<Stop> getStops() {
		List<Stop> stops = new ArrayList<Stop>();

		File file = panel.fileSelector.getSelectedFile();
		if (file != null) {
			try {
				stops = readStops(file);
			} catch (RiffFormatException ex) {
				panel.showException("exception.invalid",
						new String[] { file.getPath() }, ex);
			} catch (IOException ex) {
				panel.showException("exception.general",
						new String[] { file.getPath() }, ex);
			}
		}

		return stops;
	}

	/**
	 * Read stops from the given soundfont file.
	 * 
	 * @param file
	 *            file to read from
	 * @return list of stops
	 * @throws IOException
	 * @throws RiffFormatException
	 */
	private List<Stop> readStops(File file) throws IOException, RiffFormatException {

		ArrayList<Stop> stops = new ArrayList<Stop>();

		InputStream input = null;
		try {
			input = new FileInputStream(file);

			RiffChunk riffChunk = new SoundfontReader(input).read();

			List<Preset> presets = SoundfontReader.getPresets(riffChunk);
			Collections.sort(presets);
			for (int p = 0; p < presets.size(); p++) {
				Preset preset = presets.get(p);

				Stop stop = new Stop();
				stop.setName(preset.getName());
				stop.setProgram(preset.getProgram());
				stops.add(stop);
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

			fileLabel.setText(i18n.getString("fileLabel/text"));
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

			message = MessageFormat.format(i18n.getString(message), args);

			JOptionPane.showMessageDialog(this, message, i18n
					.getString("exception/title"), JOptionPane.ERROR_MESSAGE);
		}
	}
}