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
import javax.swing.border.*;

import jorgan.gui.console.Configuration;
import jorgan.swing.font.*;
import jorgan.swing.color.*;

/**
 * A panel for the {@link jorgan.swing.view.Configuration}.
 */
public class SwingViewConfigPanel extends ConfigurationPanel {

  private JCheckBox interpolateCheckBox = new JCheckBox();
  private JPanel shortcutsPanel = new JPanel();
  private JCheckBox showShortcutCheckBox    = new JCheckBox();
  private JLabel shortcutColorLabel = new JLabel();
  private ColorSelector shortcutColorSelector = new ColorSelector();
  private JLabel shortcutFontLabel = new JLabel();
  private FontSelector shortcutFontSelector = new FontSelector();
  
  private JPanel fontsPanel = new JPanel();
  private JLabel labelFontLabel = new JLabel();
  private JLabel pistonFontLabel = new JLabel();
  private JLabel stopFontLabel = new JLabel();
  private JLabel couplerFontLabel = new JLabel();
  private JLabel tremulantFontLabel = new JLabel();
  private JLabel variationFontLabel = new JLabel();
  private FontSelector labelFontSelector  = new FontSelector();
  private FontSelector pistonFontSelector    = new FontSelector();
  private FontSelector stopFontSelector      = new FontSelector();
  private FontSelector couplerFontSelector   = new FontSelector();
  private FontSelector tremulantFontSelector = new FontSelector();
  private FontSelector variationFontSelector = new FontSelector();

  public SwingViewConfigPanel() {
    setLayout(new GridBagLayout());

    setName(resources.getString("config.view.name"));

    interpolateCheckBox.setText(resources.getString("config.view.interpolate"));
    add(interpolateCheckBox, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));

    shortcutsPanel.setLayout(new GridBagLayout());
    shortcutsPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), resources.getString("config.view.shortcuts")));
    add(shortcutsPanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));

      showShortcutCheckBox.setText(resources.getString("config.view.shortcuts.show"));
      showShortcutCheckBox.addItemListener(new ItemListener() {
        public void itemStateChanged(ItemEvent ev) {
          shortcutColorSelector.setEnabled(showShortcutCheckBox.isSelected());
          shortcutFontSelector.setEnabled(showShortcutCheckBox.isSelected());
        }
      });
      shortcutsPanel.add(showShortcutCheckBox, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
  
      shortcutColorLabel.setText(resources.getString("config.view.shortcuts.color"));
      shortcutsPanel.add(shortcutColorLabel   , new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
      shortcutsPanel.add(shortcutColorSelector, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));

      shortcutFontLabel.setText(resources.getString("config.view.shortcuts.font"));
      shortcutsPanel.add(shortcutFontLabel   , new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
      shortcutsPanel.add(shortcutFontSelector, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));

    fontsPanel.setLayout(new GridBagLayout());
    fontsPanel.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), resources.getString("config.view.font")));
    add(fontsPanel, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, emptyInsets, 0, 0));

      labelFontLabel.setText(resources.getString("config.view.font.label"));
      fontsPanel.add(labelFontLabel,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
      fontsPanel.add(labelFontSelector,   new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));

      pistonFontLabel.setText(resources.getString("config.view.font.piston"));
      fontsPanel.add(pistonFontLabel,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
      fontsPanel.add(pistonFontSelector,   new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));

      stopFontLabel.setText(resources.getString("config.view.font.stop"));
      fontsPanel.add(stopFontLabel,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
      fontsPanel.add(stopFontSelector,   new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));

      couplerFontLabel.setText(resources.getString("config.view.font.coupler"));
      fontsPanel.add(couplerFontLabel,    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
      fontsPanel.add(couplerFontSelector,   new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));

      tremulantFontLabel.setText(resources.getString("config.view.font.tremulant"));
      fontsPanel.add(tremulantFontLabel,    new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
      fontsPanel.add(tremulantFontSelector,   new GridBagConstraints(1, 5, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));

      variationFontLabel.setText(resources.getString("config.view.font.variation"));
      fontsPanel.add(variationFontLabel,    new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, standardInsets, 0, 0));
      fontsPanel.add(variationFontSelector,   new GridBagConstraints(1, 6, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, standardInsets, 0, 0));

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

    labelFontSelector    .setSelectedFont(config.getLabelFont());
    pistonFontSelector   .setSelectedFont(config.getPistonFont());
    stopFontSelector     .setSelectedFont(config.getStopFont());
    couplerFontSelector  .setSelectedFont(config.getCouplerFont());
    tremulantFontSelector.setSelectedFont(config.getTremulantFont());
    variationFontSelector.setSelectedFont(config.getVariationFont());
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

    config.setLabelFont    (labelFontSelector .getSelectedFont());
    config.setPistonFont   (pistonFontSelector   .getSelectedFont());
    config.setStopFont     (stopFontSelector     .getSelectedFont());
    config.setCouplerFont  (couplerFontSelector  .getSelectedFont());
    config.setTremulantFont(tremulantFontSelector.getSelectedFont());
    config.setVariationFont(variationFontSelector.getSelectedFont());
  }
}