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
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Selector of a font.
 */
public class FontSelector extends JPanel {

  /**
   * The resource bundle.
   */
  protected static ResourceBundle resources = ResourceBundle.getBundle("jorgan.swing.resources");

  /**
   * The selected font.
   */
  private Font font;

  /**
   * The button used to edit the selected font.
   */
  private JButton button = new JButton();

  /**
   * Create a new selector.
   */
  public FontSelector() {
    super(new BorderLayout());

    button.setHorizontalAlignment(JButton.LEFT);
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent ev) {
        FontDialog dialog;
        Window window = SwingUtilities.getWindowAncestor(FontSelector.this);
        if (window instanceof JFrame) {
          dialog = new FontDialog((JFrame)window);
        } else {
          dialog = new FontDialog((JDialog)window);
        }
        dialog.setSelectedFont(font);
        dialog.start();

        setSelectedFont(dialog.getSelectedFont());
      }
    });
    add(button, BorderLayout.CENTER);

    setSelectedFont(new Font("Arial", Font.PLAIN, 12));
  }

  public void setEnabled(boolean enabled) {
    button.setEnabled(enabled);
  }

  /**
   * Set the selected font.
   *
   * @param font  the font to select
   */
  public void setSelectedFont(Font font) {
    this.font = font;
    
    button.setText(format(font));
  }

  /**
   * Get the selected font.
   *
   * @return  the selected font
   */
  public Font getSelectedFont() {
    return font;
  }

  /**
   * Utility method for formatting of a font.
   * 
   * @param font    font to format
   * @return        formatted font
   */
  public static String format(Font font) {
    if (font == null) {
      return "-";
    } else {
      String name  = font.getName();
      int    size  = font.getSize();
      String style = resources.getString("font.style." + font.getStyle());
      return (name + " " + size + " " + style);
    }
  }
}