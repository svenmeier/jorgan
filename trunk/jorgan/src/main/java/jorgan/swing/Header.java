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
package jorgan.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

/**
 * A special label that can be used as a header.
 */
public class Header extends JLabel {

  /**
   * The header color.
   */
  private Color headerColor;
  
  /**
   * Constructor.
   */
  public Header() {
    super(" ");

    Font font = getFont();

    float fontSize = font.getSize2D() * 1.3f;   
    setFont(font.deriveFont(Font.BOLD, fontSize));

    int borderSize = (int)(font.getSize2D() * 0.3f);
    setBorder(BorderFactory.createEmptyBorder(borderSize, borderSize,
                                              borderSize, borderSize));    
  }

  /**
   * Set the color to be used for this header.
   * 
   * @param color   color to use
   */  
  public void setHeader(Color color) {
    this.headerColor = color;
  }
  
  /**
   * Get the color used by this header.
   * 
   * @return    color
   */
  public Color getHeader() {
    return headerColor;
  }
  
  /**
   * Overriden to replace empty or <code>null</code> text with a single
   * whitespace. This prevents this header to shrink.
   */
  public void setText(String text) {
    if (text == null || "".equals(text)) {
      text = " ";
    }
    super.setText(text);
  }
  
  public void paintComponent(Graphics g) {
    Graphics2D graphics = (Graphics2D)g;
    
    int   width      = getWidth();
    int   height     = getHeight();
    Color background = getBackground();

    graphics.setPaint(new GradientPaint(0, 0, headerColor, width, 0, background));
    graphics.fillRect(0, 0, width, height);
    
    super.paintComponent(g);    
  }
}