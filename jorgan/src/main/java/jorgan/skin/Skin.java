/*
 * jOrgan - Java Virtual Pipe Organ
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

import java.util.*;

/**
 * Style.
 */
public class Skin {

  private ArrayList styles = new ArrayList();

  public Skin() {
  }
  
  public Skin getSkin() {
    
    return this;
  }

  public int getStyleCount() {
    return styles.size();
  }
  
  public Style getStyle(String styleName) {

    for (int s = 0; s < styles.size(); s++) {
      Style style = (Style)styles.get(s);
      if (style.getName().equals(styleName)) {
        return style;
      }
    }
    return null;  
  }
 

  public Style getStyle(int index) {
    return (Style)styles.get(index);
  }
  
  public void addStyle(Style style) {
    styles.add(style);
    style.setSkin(this);
  }  

  public void removeStyle(Style style) {
    styles.remove(style);
    style.setSkin(null);
  }  
}