/*
 * jOrgan - Java Virtual  Organ
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
package jorgan.swing.color;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

/**
 * Selector of a color.
 */
public class ColorSelector extends JPanel {

  /**
   * The resource bundle.
   */
  protected static ResourceBundle resources = ResourceBundle.getBundle("jorgan.swing.resources");

  /**
   * The selected color.
   */
  private Color color;

  /**
   * The button used to edit the selected font.
   */
  private JButton button = new JButton();
  
  /**
   * Should color be shown as an icon.
   */
  private boolean showIcon;

  /**
   * Create a new selector.
   */
  public ColorSelector() {
    this(true);
  }
  
  /**
   * Create a new selector.
   */
  public ColorSelector(boolean showIcon) {
    super(new BorderLayout());
    
    this.showIcon = showIcon;

    button.setHorizontalAlignment(JButton.LEFT);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        String title = resources.getString("color.title");
        Color newColor = JColorChooser.showDialog(ColorSelector.this, title, color);
        if (newColor != null) {
          setSelectedColor(newColor);
        }
      }
    });
    add(button, BorderLayout.CENTER);

    setSelectedColor(Color.black);
  }

  public void setEnabled(boolean enabled) {
    button.setEnabled(enabled);
  }

  /**
   * Set the selected color.
   *
   * @param color  the color to select
   */
  public void setSelectedColor(Color color) {
    this.color = color;
    if (color == null) {
      button.setText("-");
      button.setIcon(null);
    } else {
      if (showIcon) {
        button.setText(null);
        button.setIcon(new ColorIcon(color));
      } else {
        button.setText(format(color));
        button.setIcon(null);
      }
    }
  }

  /**
   * Get the selected color.
   *
   * @return  the selected color
   */
  public Color getSelectedColor() {
    return color;
  }

  /**
   * Utility method for formatting of a color.
   * 
   * @param color   color to format
   * @return        formatted color
   */
  public static String format(Color color) {
    if (color == null) {
      return "-";
    } else {
      return (color.getRed() + ", " + color.getGreen() + ", " + color.getBlue());
    }
  }
 }