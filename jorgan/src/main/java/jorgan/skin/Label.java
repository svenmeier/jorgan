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
package jorgan.skin;

import java.awt.Font;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * Label.
 */
public class Label {
  
  private Font      font        = new Font("Arial", Font.PLAIN, 12);
  private Color     color       = Color.black;
  private Rectangle bounds      = new Rectangle();
  private int       rotation    = 0; 
  private boolean   antialiased = false; 
 
  public Label() {
  }
  
  public Font getFont() {
    return font; 
  }
  
  public Color getColor() {
    return color; 
  }
  
  public Rectangle getBounds() {
    return bounds; 
  }
     
  public int getRotation() {
    return rotation; 
  }
     
  public boolean isAntialiased() {
    return antialiased;
  }

  public void setFont(Font font) {
    if (font == null) {
      throw new IllegalArgumentException("font of label cannot be null");
    }
    this.font = font; 
  }
  
  public void setColor(Color color) {
    if (color == null) {
      throw new IllegalArgumentException("color of label cannot be null");
    }
    this.color = color; 
  }
  
  public void setBounds(Rectangle bounds) {
    if (bounds == null) {
      throw new IllegalArgumentException("bounds of label cannot be null");
    }
    this.bounds = bounds; 
  }
  
  public void setRotation(int rotation) {
    rotation = rotation % 360;
    if (rotation < 0) {
      rotation += 360; 
    }

    this.rotation = rotation;
  }     

  public void setAntialiased(boolean b) {
    antialiased = b;
  }

}