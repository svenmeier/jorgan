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
package jorgan.gui.construct.layout;

import java.util.*;
import javax.swing.ImageIcon;

import jorgan.gui.console.*;

public class SpreadVerticalLayout extends ViewLayout {

  private int y;
  private int height;
  private int count;
  
  public SpreadVerticalLayout() {
    super(resources.getString("view.spread.vertical"),
          new ImageIcon(View.class.getResource("/jorgan/gui/img/spreadVertical.gif")));
  }

  protected void init(View pressed, List views) {
    
    Collections.sort(views, new ViewComparator(false, true));
    count = views.size();

    View top    = (View)views.get(0); 
    View bottom = (View)views.get(views.size() - 1); 

    y      = top.getY()    + top.getHeight()/2;
    height = bottom.getY() + bottom.getHeight()/2 - y;      
  }
  
  protected void visit(View view, int index) {
    view.setPosition(view.getX(), y + (height * index / (count - 1)) - view.getHeight()/2);            
  }
}