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
package jorgan.gui.config;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.border.*;

import jorgan.disposition.Organ;
import jorgan.docs.Documents;
import jorgan.gui.console.Configuration;
import jorgan.swing.font.*;
import jorgan.swing.color.*;

/**
 * A panel for the {@link jorgan.gui.console.Configuration}.
 */
public class GUIConsoleConfigPanel extends ConfigurationPanel {

  private JCheckBox interpolateCheckBox = new JCheckBox();
  private JPanel shortcutsPanel = new JPanel();
  private JCheckBox showShortcutCheckBox    = new JCheckBox();
  private JLabel shortcutColorLabel = new JLabel();
  private ColorSelector shortcutColorSelector = new ColorSelector();
  private JLabel shortcutFontLabel = new JLabel();
  private FontSelector shortcutFontSelector = new FontSelector();
  
  private JPanel fontsPanel = new JPanel();
  private Map fontSelectors = new HashMap();
  
  public GUIConsoleConfigPanel() {
    setLayout(new GridBagLayout());

    setName(resources.getString("config.console.name"));

    interpolateCheckBox.setText(resources.getString("config.console.interpolate"));
    add(interpolateCheckBox, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));

    shortcutsPanel.setLayout(new GridBagLayout());
    shortcutsPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), resources.getString("config.console.shortcuts")));
    add(shortcutsPanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));

      showShortcutCheckBox.setText(resources.getString("config.console.shortcuts.show"));
      showShortcutCheckBox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent ev) {
          shortcutColorSelector.setEnabled(showShortcutCheckBox.isSelected());
          shortcutFontSelector.setEnabled(showShortcutCheckBox.isSelected());
        }
      });
      shortcutsPanel.add(showShortcutCheckBox, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
  
      shortcutColorLabel.setText(resources.getString("config.console.shortcuts.color"));
      shortcutsPanel.add(shortcutColorLabel   , new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
      shortcutsPanel.add(shortcutColorSelector, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));

      shortcutFontLabel.setText(resources.getString("config.console.shortcuts.font"));
      shortcutsPanel.add(shortcutFontLabel   , new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
      shortcutsPanel.add(shortcutFontSelector, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));

    fontsPanel.setLayout(new GridBagLayout());
    fontsPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), resources.getString("config.console.font")));
    add(fontsPanel, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));

      Class[] classes = Organ.getElementClasses();
      for (int c = 0; c < classes.length; c++) {
        JLabel label = new JLabel(Documents.getInstance().getDisplayName(classes[c]));
        fontsPanel.add(label, new GridBagConstraints(0, c, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
        
        FontSelector fontSelector = new FontSelector();
        fontSelectors.put(classes[c], fontSelector);
        fontsPanel.add(fontSelector, new GridBagConstraints(1, c, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));
      }

    add(new JLabel(), new GridBagConstraints(0, GridBagConstraints.RELATIVE, 512, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, emptyInsets, 0, 0));
  }

  public void read() {
    Configuration config = (Configuration)getConfiguration();

    interpolateCheckBox.setSelected(config.getInterpolate());

    showShortcutCheckBox.setSelected      (config.getShowShortcut());
    shortcutColorSelector.setSelectedColor(config.getShortcutColor());
    shortcutColorSelector.setEnabled      (config.getShowShortcut());
    shortcutFontSelector.setSelectedFont  (config.getShortcutFont());
    shortcutFontSelector.setEnabled       (config.getShowShortcut());

    Class[] classes = Organ.getElementClasses();
    for (int c = 0; c < classes.length; c++) {
      FontSelector fontSelector = (FontSelector)fontSelectors.get(classes[c]);
      
      fontSelector.setSelectedFont(config.getFont(classes[c]));
    }    
  }

  /**
   * Write the configuration.
   */
  public void write() {
    Configuration config = (Configuration)getConfiguration();

    config.setInterpolate(interpolateCheckBox.isSelected());

    config.setShowShortcut(showShortcutCheckBox.isSelected());
    config.setShortcutColor(shortcutColorSelector.getSelectedColor());
    config.setShortcutFont(shortcutFontSelector.getSelectedFont());

    Class[] classes = Organ.getElementClasses();
    for (int c = 0; c < classes.length; c++) {
      FontSelector fontSelector = (FontSelector)fontSelectors.get(classes[c]);
      
      config.setFont(classes[c], fontSelector.getSelectedFont());
    }    
  }
}