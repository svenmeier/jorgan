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
package jorgan.gui.console;

import java.awt.*;

import jorgan.disposition.*;

/**
 * A view that shows a generic element.
 */
public class OtherView extends View {

  public OtherView(Element element) {
    super(element);
  }
    
  protected Dimension getNonStyleSize() {
    
    Dimension dim = getNameSize();

    dim.width  += 2*4;
    dim.height += 2*4;

    return dim;
  }
  
  protected void paintNonStyle(Graphics2D g) {
    Dimension dim = getNonStyleSize();

    g.setColor(Color.black);
    g.setFont(getNonStyleFont());
    paintName(g, 0, 0, dim.width, dim.height);
  }
  
  protected Font getNonStyleFont() {
    return Configuration.instance().getLabelFont();  
  }
}
