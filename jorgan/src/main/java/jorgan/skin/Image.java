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

import java.awt.Point;

/**
 * Image.
 */
public class Image {
  
  private String file = "";
  private Point  location = new Point(0, 0);
 
  public Image() {
  }
  
  public String getFile() {
    return file; 
  }
  
  public void setFile(String file) {
    if (file == null) {
      throw new IllegalArgumentException("file of image cannot be null");
    }
    this.file = file; 
  }
    
  public Point getLocation() {
    return location; 
  }
  
  public void setLocation(Point location) {
    if (location == null) {
      throw new IllegalArgumentException("location of image cannot be null");
    }
    this.location = location; 
  }  
}