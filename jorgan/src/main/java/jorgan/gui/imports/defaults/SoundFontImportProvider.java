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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

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

/**
 * A provider of an import.
 */
public class SoundFontImportProvider implements ImportProvider {

  /**
   * The resource bundle.
   */
  protected static ResourceBundle resources = ResourceBundle.getBundle("jorgan.gui.resources");

  private OptionsPanel panel = new OptionsPanel();

  public JPanel getOptionsPanel() {
      return panel;
  }
  
  public String getName() {
    return resources.getString("import.soundfont.name");
  }

  public String getDescription() {
    return resources.getString("import.soundfont.description");
  }

  public boolean hasStops() {
    File file = panel.fileSelector.getSelectedFile();
    
    return file != null  &&
           file.exists() &&
           file.isFile();
  }
  
  public List getStops() {      
    List stops = new ArrayList();

    File file = panel.fileSelector.getSelectedFile();
    if (file != null) {
      try {
        stops = readStops(file);
      } catch (RiffFormatException ex) {
        panel.showException("import.soundfont.exception.invalid", new String[]{file.getPath()}, ex);
      } catch (IOException ex) {
        panel.showException("import.soundfont.exception", new String[]{file.getPath()}, ex);
      }
    }

    return stops;    
  }
  
  /**
   * Read stops from the given soundfont file.
   * 
   * @param file    file to read from
   * @return        list of stops
   * @throws IOException
   * @throws RiffFormatException
   */  
  private List readStops(File file) throws IOException, RiffFormatException {

    ArrayList stops = new ArrayList();

    RiffChunk riffChunk = new SoundfontReader(new FileInputStream(file)).read();
    
    java.util.List presets = SoundfontReader.getPresets(riffChunk);
    Collections.sort(presets);
    for (int p = 0; p < presets.size(); p++) {
      Preset preset = (Preset)presets.get(p);
    
      Stop stop = new Stop();
      stop.setName(preset.getName());
      stop.setProgram(preset.getProgram()); 
      stops.add(stop);
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
    protected Insets insets = new Insets(2,2,2,2);

    private JLabel       fileLabel        = new JLabel();
    private FileSelector fileSelector     = new FileSelector();
   
    public OptionsPanel() {
      setLayout(new GridBagLayout());

      fileLabel.setText(resources.getString("import.soundfont.file"));
      add(fileLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));

      fileSelector.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          firePropertyChange("stops", null, null);
        }
      });
      add(fileSelector, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

      add(new JLabel(), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, insets, 0, 0));    
    }
  
    /**
     * Show an exception.
     *
     * @param message   message of exception
     * @param args      arguments of message
     * @param exception the exception
     */
    public void showException(String message, Object[] args, Exception exception) {

      message = MessageFormat.format(resources.getString(message), args);

      JOptionPane.showMessageDialog(this,
                                    message,
                                    resources.getString("exception.title"),
                                    JOptionPane.ERROR_MESSAGE);
    }
  }
}