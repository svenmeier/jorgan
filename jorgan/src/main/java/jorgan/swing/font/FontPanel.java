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
package jorgan.swing.font;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.*;
import javax.swing.*;

import jorgan.swing.EditableList;

/**
 * A panel for a font selection.
 */ 
class FontPanel extends JPanel {

  private static Logger logger = Logger.getLogger(FontPanel.class.getName());
    
  /**
   * The resource bundle.
   */
  private static ResourceBundle resources = ResourceBundle.getBundle("jorgan.swing.resources");
  
  private static String[] sizes = new String[]{"8", "10", "12", "14", "16", "18", "24"};
     
  private JLabel familyLabel = new JLabel();
  private JLabel sizeLabel = new JLabel();
  private JLabel styleLabel = new JLabel();
  private EditableList familyList = new EditableList();
  private EditableList sizeList = new EditableList();
  private EditableList styleList = new EditableList();

  private Font font;

  public FontPanel() {
    super(new GridBagLayout());
        
    familyLabel.setText(resources.getString("font.family"));
    sizeLabel.setText  (resources.getString("font.size"));
    styleLabel.setText (resources.getString("font.style"));
    
    Insets insets = new Insets(2, 2, 2, 2);
    add(familyLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    add(sizeLabel  , new GridBagConstraints(1, 0, 1, 1, 0.5, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    add(styleLabel , new GridBagConstraints(2, 0, 1, 1, 0.5, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    add(familyList , new GridBagConstraints(0, 1, 1, 1, 0.5, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, insets, 0, 0));
    add(sizeList   , new GridBagConstraints(1, 1, 1, 1, 0.5, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, insets, 0, 0));
    add(styleList  , new GridBagConstraints(2, 1, 1, 1, 0.5, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, insets, 0, 0));    

    familyList.setValues(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());

    sizeList.setValues(sizes);

    String[] styles = new String[]{resources.getString("font.style.0"),
                                   resources.getString("font.style.1"),
                                   resources.getString("font.style.2"),
                                   resources.getString("font.style.3")};
    styleList.setValues(styles);
  }

  public void setSelectedFont(Font font) {
    this.font = font;
 
    if (font != null) {
      familyList.setSelectedValue("" + font.getFamily());
      sizeList  .setSelectedValue("" + font.getSize());
      styleList .setSelectedValue(resources.getString("font.style." + font.getStyle()));      
    }
  }

  public Font getSelectedFont() {
    try {
      int size      = Integer.parseInt(sizeList.getSelectedValue());
      int style     = styleList.getSelectedIndex();
      String family = familyList.getSelectedValue();
      
      font = new Font(family, style, size); 
    } catch (Exception ex) {
      logger.log(Level.FINE, "font construction failed", ex);
    }
    return font;
  }
}